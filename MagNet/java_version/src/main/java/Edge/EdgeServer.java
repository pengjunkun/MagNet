package Edge;

import Client.EdgeChooser;
import Remote.OriginalServer;
import tools.LRUCache;
import tools.MyConf;
import tools.MyLog;
import tools.MyTool;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/29.
 */
public class EdgeServer
{
	public ArrayList<EdgeServer> mySlaves;
	public EdgeServer myMaster;
	private static final int EDGECHOOSER_UPDATE_PERIOD = 60;
	//for now, match without temporal constrain
	private int EXIST_PERIOD_THRESHOLD = Integer.MAX_VALUE;
	private HashMap<Integer, Integer> vid_latest_access_record_map = new HashMap<>();
	public int matchedRequest = 0;

	private int id;
	private double lat;
	private double lon;
	private int size;
	private int usedSize;
	public int[] fromMEC = new int[MyConf.edgesInfo.length];
	public int[] fromMEC_hit = new int[MyConf.edgesInfo.length];

	private ConcurrentHashMap<Integer, Integer> cacheTypeSize;
	private HashMap<Integer, Integer> requestTypeSize;
	private ConcurrentHashMap<Integer, Integer> oldCacheTypeSize;
	private long lastCaheTypeTimestamp = -1;
	// in LRUCache <vid,tid>
	private LRUCache lruCache;
	private long totalRequest;
	private long hitRequest;
	private long missRequest;

	private HashMap<Integer, NeighborEdgeInfo> neighbors;

	private EdgeChooser edgeChooser;
	private int edgeChoose_update_latest;

	public static final int WORKLOAD_UPDATE_PERIOD = 360;
	private int workload_count_period = 0;
	private HashMap<Integer, Integer> workload_typeSize_period;
	private MyConf.Workload_status workload_status = MyConf.Workload_status.NORMAL;

	public EdgeServer(int id, double lat, double lon, int size)
	{
		this.id = id;
		this.lat = lat;
		this.lon = lon;
		this.size = size;
		lruCache = new LRUCache(id, size);
		cacheTypeSize = new ConcurrentHashMap<>();
		requestTypeSize = new HashMap<>();
		oldCacheTypeSize = new ConcurrentHashMap<>();
		neighbors = new HashMap<>();

		//every MEC knows its top K neighbors
		addNearnestK_neigbors(MyConf.neighborNum, this.lat, this.lon);

		//create a EdgeChooser for this edge
		edgeChooser = new EdgeChooser(id, lat, lon);
		edgeChoose_update_latest = 0;

		workload_typeSize_period = new HashMap<>();
		myMaster = null;
		mySlaves = new ArrayList<>();

	}

	public void update_workload(int workload_update_period_index)
	{
		int[] workload_one_period = OriginalServer.getInstance().workloads[workload_update_period_index];
		workload_one_period[id - 1] += workload_count_period;
		//reset period param
		if (mySlaves.size() > 0)
		{
			int average_count = workload_count_period / (1 + mySlaves.size());
			workload_one_period[id - 1] -= average_count * mySlaves.size();
			totalRequest -= average_count * mySlaves.size();
			for (EdgeServer slave : mySlaves)
			{
				slave.totalRequest += average_count;
				workload_one_period[slave.id - 1] += average_count;
			}
		}

		update_workload_status();
		//to do next
		//if I'm free edge
		//(somedays later)if I write some above, I will never be free for it
		if (MyConf.type_prefix != "classic" && !MyConf.HOME_MEC && myMaster == null)
		{
			modifyWorkload();
		}

		workload_count_period = 0;
		workload_typeSize_period.clear();

	}

	public int getCacheSize()
	{
		return size;
	}

	private void addNearnestK_neigbors(int k, double lat, double lon)
	{
		PriorityQueue<Map.Entry<Integer, Double>> neighborsPQ = new PriorityQueue<>(
				new Comparator<Map.Entry<Integer, Double>>()
				{
					@Override public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2)
					{
						return o1.getValue().compareTo(o2.getValue());
					}
				});
		for (String[] edge : MyConf.edgesInfo)
		{
			int nid = Integer.parseInt(edge[0]);
			double nlat = Double.parseDouble(edge[1]);
			double nlon = Double.parseDouble(edge[2]);
			double dist = MyTool.distance(lat, lon, nlat, nlon, "K");
			if (id == nid)
			{
				continue;
			}
			neighborsPQ.add(new AbstractMap.SimpleEntry<Integer, Double>(nid, dist));
		}
		for (int i = 0; i < k; i++)
		{
			Map.Entry<Integer, Double> entry = neighborsPQ.poll();
			//for now, we suppose the latency is the distence*10
			neighbors.put(entry.getKey(), new NeighborEdgeInfo(entry.getKey(), (int) (10 * entry.getValue())));
		}
	}

	public int getEid()
	{
		return id;
	}

	public double getLat()
	{
		return lat;
	}

	public double getLon()
	{
		return lon;
	}

	/**
	 * get content from edge, if edge don't has it, edge will fetch from remote server and trigger the caching mechanism
	 * may evict old ones
	 * update the different type saving info
	 *
	 * @param timestamp
	 * @param tid:if    =-1, means get without type
	 * @param vid
	 * @return the latency caused in this request
	 */
	public boolean getContent(long timestamp, int tid, String vid, int homeMEC)
	{
		boolean hit = false;
		//turn all requests to master
		if (myMaster != null)
		{
			return myMaster.getContent(timestamp, tid, vid, homeMEC);
		}

		workload_count_period++;
		//for matched pairs statics
		//		checkMatch(timestamp, vid);

		fromMEC[homeMEC - 1]++;
		totalRequest++;
		requestTypeSize.put(tid, requestTypeSize.getOrDefault(tid, 0) + 1);
		workload_typeSize_period.put(tid, workload_typeSize_period.getOrDefault(tid, 0) + 1);
		//hit in this edge
		if (lruCache.get(vid) != null)
		{
			hit = true;
			hitRequest++;
			fromMEC_hit[homeMEC - 1]++;
		} else
		{
			//missed in this edge, will get from remote server
			missRequest++;
			if (checkAdmission(timestamp, tid, vid))
			{
				//if pass the admission
				//use LRU now
				int preTid = lruCache.putContentGetReplacedType(vid, tid);
				if (preTid != MyConf.NOT_REPLACEMENT)
				{
					//minus old type info
					int size = cacheTypeSize.getOrDefault(preTid, 0) - MyConf.FILE_SIZE;
					if (size == 0)
					{
						cacheTypeSize.remove(preTid);
					} else
					{
						cacheTypeSize.put(preTid, size);
					}
				}

				//add new added type info
				cacheTypeSize.put(tid, MyConf.FILE_SIZE + cacheTypeSize.getOrDefault(tid, 0));
			}
		}

		if (timestamp - lastCaheTypeTimestamp > MyConf.MEC_CONNECTION_PERIOD)
		{
			oldCacheTypeSize.clear();
			oldCacheTypeSize.putAll(cacheTypeSize);
			lastCaheTypeTimestamp = timestamp;
		}

		return hit;
	}

	private void insert_for_capacity_change(String vid, int tid)
	{
		int preTid = lruCache.putContentGetReplacedType(vid, tid);
		if (preTid != MyConf.NOT_REPLACEMENT)
		{
			//minus old type info
			int size = cacheTypeSize.getOrDefault(preTid, 0) - MyConf.FILE_SIZE;
			if (size == 0)
			{
				cacheTypeSize.remove(preTid);
			} else
			{
				cacheTypeSize.put(preTid, size);
			}
		}

		//add new added type info
		cacheTypeSize.put(tid, MyConf.FILE_SIZE + cacheTypeSize.getOrDefault(tid, 0));
	}

	private void change_lru_capacity()
	{
		LRUCache oldOne = lruCache;
		lruCache = new LRUCache(id, size);
		cacheTypeSize.clear();
		oldCacheTypeSize.clear();

		if (size != 0)
		{
			for (Map.Entry<String, Integer> one : oldOne.entrySet())
			{
				insert_for_capacity_change(one.getKey(), one.getValue());
			}
		}
		oldOne.clear();
		oldOne = null;

		oldCacheTypeSize.putAll(cacheTypeSize);

	}

	//	private void checkMatch(long current_timestamp, String new_vid)
	//	{
	//		int request_vid = Integer.parseInt(new_vid);
	//		if (vid_latest_access_record_map.get(request_vid) != null)
	//		{
	//			int last_access_timestamp = vid_latest_access_record_map.get(request_vid);
	//			//find the matched
	//			if (current_timestamp - last_access_timestamp < EXIST_PERIOD_THRESHOLD)
	//			{
	//				matchedRequest++;
	//			}
	//		}
	//		// insert this one
	//		vid_latest_access_record_map.put(request_vid, (int) current_timestamp);
	//
	//	}

	public HashMap getRequestTypeSize()
	{
		return requestTypeSize;
	}

	public ConcurrentHashMap<Integer, Integer> getCacheTypeSize()
	{
		return cacheTypeSize;
	}

	public ConcurrentHashMap<Integer, Integer> getOldCacheTypeSize()
	{
		return oldCacheTypeSize;
	}

	public HashMap<Integer, NeighborEdgeInfo> getNeighbors()
	{
		return neighbors;
	}

	/**
	 * this is the admission hook for a Cache Algorithm
	 *
	 * @param timestamp
	 * @param tid
	 * @param vid
	 * @return
	 */
	private boolean checkAdmission(long timestamp, int tid, String vid)
	{
		//use LRU now, so cache all
		//		if (tid==MyConf.WITHOUT_TYPE)
		//			return false;
		return true;
	}

	/**
	 * report the request statistics
	 *
	 * @return { totalRequest, hitRequest, missRequest }
	 */
	public long[] getRequestStat()
	{
		return new long[] { totalRequest, hitRequest, missRequest };
	}

	//    public int getContentWithoutType(long timestamp, String vid) {
	//        return getContent(timestamp, MyConf.WITHOUT_TYPE, vid);
	//    }

	/**
	 * used to clear all the cached files in Edge
	 */
	public void clear()
	{
		cacheTypeSize.clear();
		requestTypeSize.clear();
		oldCacheTypeSize.clear();
		lruCache.clear();
		neighbors.clear();
		lastCaheTypeTimestamp = -1;
	}

	public int getAllContentSize()
	{
		return lruCache.size();
	}

	public double getDistanceFromUser(double lat, double lon)
	{
		return MyTool.distance(lat, lon, this.lat, this.lon);
	}

	/**
	 * test if this request is hited in homeEdge
	 * if not, return the optimal edge
	 * -1 stands for hiting
	 *
	 * @param tid
	 * @param timestamp
	 * @return
	 */
	public int hit_or_getOptimalEdge(String vid, int tid, int timestamp)
	{

		if (timestamp - edgeChoose_update_latest > EDGECHOOSER_UPDATE_PERIOD)
		{
			edgeChooser.updateEdgeCandidates(getNeighbors(), timestamp);
		}

		int result = -2;
		// test if hit in this home edge
		// this request ended in this edge
		if (lruCache.get(vid) != null)
		{
			result = -1;
			hitRequest++;
			totalRequest++;
			return result;
		} else
		{
			return edgeChooser.getEdge(tid, timestamp);
		}
	}

	private void update_workload_status()

	{
		int counts_in_one_hour = workload_count_period * (3600 / WORKLOAD_UPDATE_PERIOD);
		if (counts_in_one_hour > size * MyConf.WORKLOAD_SIZE_FACTOR)
		{
			workload_status = MyConf.Workload_status.BUSY;
		} else if (counts_in_one_hour < size / MyConf.WORKLOAD_SIZE_FACTOR)
		{
			workload_status = MyConf.Workload_status.IDLE;
		} else
		{
			workload_status = MyConf.Workload_status.NORMAL;
		}
	}

	public void modifyWorkload()
	{
		//		if (id == 188)
		//		{
		//			System.out.println("188");
		//		}
		switch (workload_status)
		{
		case BUSY:
			//1. check how many big types here
			int biggest_type = -1;
			int biggest_type_size = -1;
			int smallest_type = -1;
			int smallest_type_size = Integer.MAX_VALUE;
			for (int tid : workload_typeSize_period.keySet())
			{
				if (workload_typeSize_period.get(tid) * 1.0 / workload_count_period > 0.3)
				{
					if (workload_typeSize_period.get(tid) > biggest_type_size)
					{
						biggest_type = tid;
						biggest_type_size = workload_typeSize_period.get(tid);
					} else if (workload_typeSize_period.get(tid) < smallest_type_size)
					{
						smallest_type = tid;
						smallest_type = workload_typeSize_period.get(tid);
					}
				}
			}
			if (biggest_type != Integer.MAX_VALUE && smallest_type != -1 && biggest_type != smallest_type)
			{
				//clean weak type
				cacheTypeSize.put(smallest_type, 0);
				break;
			} else
			{
				//incorporate more edges
				findResource();

			}

			break;
		case IDLE:
			if (mySlaves.size() > 0)
			{
				freeResource();
			}
			break;
		case NORMAL:
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + workload_status);
		}
	}

	private void freeResource()
	{

		int aveSize = size / (1 + mySlaves.size());
		//free my slaves, thanks for your work
		Iterator<EdgeServer> iterator = mySlaves.iterator();
		while (iterator.hasNext())
		{
			EdgeServer neighbor = iterator.next();

			neighbor.myMaster = null;
			neighbor.size = aveSize;
			neighbor.change_lru_capacity();

			size -= aveSize;
			iterator.remove();
			MyLog.jack_debug("edge%d freed one slave%d!".formatted(id, neighbor.id));

			update_workload_status();
			if (workload_status != MyConf.Workload_status.IDLE)
			{
				break;
			}
		}
		//update myself
		change_lru_capacity();

	}

	private void findResource()
	{
		PriorityQueue<Map.Entry<Integer, Integer>> neighborsPQ_totalRequestNow = new PriorityQueue<>(
				new Comparator<Map.Entry<Integer, Integer>>()
				{
					@Override public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2)
					{
						return o1.getValue().compareTo(o2.getValue());
					}
				});
		for (int nid : neighbors.keySet())
		{
			int totalRequestNow = (int) OriginalServer.getInstance().getEdge(nid).totalRequest;
			if (id == nid)
			{
				continue;
			}
			neighborsPQ_totalRequestNow.add(new AbstractMap.SimpleEntry<Integer, Integer>(nid, totalRequestNow));
		}

		Map.Entry<Integer, Integer> candidate = neighborsPQ_totalRequestNow.poll();
		while (candidate != null)
		{
			int nid = candidate.getKey();
			EdgeServer neighbor = OriginalServer.getInstance().getEdge(nid);
			if (neighbor.myMaster == null && neighbor.workload_status == MyConf.Workload_status.IDLE)
			{
				//update myself
				size += neighbor.size;
				change_lru_capacity();
				mySlaves.add(neighbor);

				//update neighbor
				neighbor.sell_to_master(id);
			}
			update_workload_status();
			if (mySlaves.size() > 5 || workload_status != MyConf.Workload_status.BUSY)
			{
				break;
			}
			//			if (more_idles == 0)
			//			{
			//				break;
			//			}
			candidate = neighborsPQ_totalRequestNow.poll();
		}
		//		MyLog.jack_debug("edge %d has %d slaves".formatted(id, mySlaves.size()));
	}

	private void sell_to_master(int id)
	{
		myMaster = OriginalServer.getInstance().getEdge(id);
		size = 0;
		change_lru_capacity();
		cacheTypeSize.clear();
		oldCacheTypeSize.clear();
	}
}
