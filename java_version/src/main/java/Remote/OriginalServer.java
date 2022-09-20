package Remote;

import Client.User;
import Edge.EdgeServer;
import tools.MyConf;
import tools.MyLog;
import tools.MyTool;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/29.
 * <p>
 * this class is used to simulate the original content provider server which contains all the requested content.
 */
public class OriginalServer
{
	private static OriginalServer originalServer;
	//<Eid,EdgeServer>
	public HashMap<Integer, EdgeServer> edges = new HashMap<>();
	ArrayList<Integer> max_workload_eids = new ArrayList<>();
	ArrayList<Integer> max_workloads = new ArrayList<>();
	ArrayList<Integer> min_workloads = new ArrayList<>();

	public int[][] workloads = new int[240][400];

	//use singleton model
	private OriginalServer()
	{
		cleanAndUpdateEdges();
	}

	public void cleanAndUpdateEdges()
	{
		clean();
		for (String[] edge : MyConf.edgesInfo)
		{
			int id = Integer.parseInt(edge[0]);
			double lat = Double.parseDouble(edge[1]);
			double lon = Double.parseDouble(edge[2]);
			int size = Integer.parseInt(edge[3]);
			if (MyConf.ENOUGH_CAPACITY)
			{
				size *= 100000;
			}
			edges.put(id, new EdgeServer(id, lat, lon, size));
		}
	}

	public static OriginalServer getInstance()
	{
		if (originalServer == null)
		{
			originalServer = new OriginalServer();
		}
		return originalServer;
	}

	public int getContent(String id)
	{
		// TODO: 2021/1/29
		return 100;
	}

	public int getEdgeByDistance(double lat, double log)
	{
		//as all edges has been read above
		//calculate the nearest one
		float minDistance = Integer.MAX_VALUE;
		int eid = -1;
		for (EdgeServer edge : edges.values())
		{
			float temp = calDistance(edge, lat, log);
			if (temp < minDistance)
			{
				eid = edge.getEid();
				minDistance = temp;
			}
		}
		return eid;
	}

	private float calDistance(EdgeServer edge, double lat, double log)
	{
		//use lat and log to calculate the distance
		return (float) MyTool.distance(edge.getLat(), edge.getLon(), lat, log, "K");

	}

	public EdgeServer getEdge(int eid)
	{
		return edges.get(eid);
	}

	public long[] reportAllStat()
	{
		long totalReq = 0;
		long missReq = 0;
		long hitReq = 0;
		long total_matched_request = 0;
		ArrayList<Double> edgeRatio = new ArrayList<>();

		//		MyLog.initTagWriter("totalStatic_typesNumber%d_neighborNumber%d_period%d"
		//				.formatted(MyConf.typeNum, MyConf.neighborNum, MyConf.MEC_CONNECTION_PERIOD));

		int[] edge_requests = new int[400];

		for (EdgeServer edge : edges.values())
		{
			//            return new long[] { totalRequest, hitRequest, missRequest };
			long[] tmp = edge.getRequestStat();
			double ratio = 0;
			if (tmp[0] != 0)
				ratio = 1.0 * tmp[1] / tmp[0];
			totalReq += tmp[0];
			hitReq += tmp[1];
			missReq += tmp[2];

			total_matched_request += edge.matchedRequest;
			edgeRatio.add(ratio);
			//			MyLog.tagWriter("Eid:" + edge.getEid());
			//			MyLog.tagWriter("Size:" + edge.getCacheSize());
			//			MyLog.tagWriter("Matched request:" + edge.matchedRequest);
			//			MyLog.tagWriter("Total request:" + tmp[0]);
			edge_requests[edge.getEid() - 1] = (int) tmp[0];
			//			MyLog.tagWriter("Hit request:" + tmp[1]);
			//			MyLog.tagWriter("Hit ratio:" + ratio);
			HashMap<Integer, Integer> requestMap = edge.getRequestTypeSize();
			ArrayList<Integer> requestInfo = new ArrayList<>();
			for (int i = 0; i < MyConf.typeNum; i++)
			{
				requestInfo.add(requestMap.getOrDefault(i, 0));
			}

			//			MyLog.tagWriter("categories cache   size:" + typeInfo.toString());
			//			MyLog.tagWriter("categories request size:" + requestInfo.toString());
			//			MyLog.tagWriter("fromMEC:");
			//			printSqure(edge.getEid(), edge.fromMEC, edge.fromMEC_hit);
			//			MyLog.tagWriter("----------------");
			//			MyLog.tagWriter(" ");
		}

		//		MyLog.tagWriter("----------------total----------------");
		//		MyLog.tagWriter("matched request:" + total_matched_request);
		//		MyLog.tagWriter("Total request:" + totalReq);
		//		MyLog.tagWriter("Hit request:" + hitReq);
		double totalHitRatio = 1.0 * hitReq / totalReq;
		double total_matched_Ratio = 1.0 * total_matched_request / totalReq;
		//		MyLog.tagWriter("Hit ratio:" + totalHitRatio);
		//		MyLog.tagWriter("Matched ratio:" + total_matched_Ratio);

		//calculate the SD of workloads
		ArrayList<Double> SDs = calculate_SDs_get_max_workload();

		//write in -----------------------------allResult.txt--------------------------------------------------
		MyLog.jack_debug("std_dev:%f".formatted(MyTool.POP_SD_with_min_max_normalization(edge_requests)));
		MyLog.resultAdder("typeNumber:%d".formatted(MyConf.typeNum));
		MyLog.resultAdder("neighborNumber:%d".formatted(MyConf.neighborNum));
		MyLog.resultAdder("MEC_connection_period:%d".formatted(MyConf.MEC_CONNECTION_PERIOD));
		//		MyLog.resultAdder("user_MEC_distance_threshold:%d".formatted(MyConf.USER_MEC_threshold));
		MyLog.resultAdder("penalty_factor:%f".formatted(MyConf.PENALTY_FACTOR));
		MyLog.resultAdder("matched ratio:" + total_matched_Ratio);
		MyLog.resultAdder("Hit ratio:" + totalHitRatio);
		//calculate Traffic
		MyLog.resultAdder("Traffic Cost:" + get_average_traffic());
		MyLog.resultAdder("Average Latency:" + get_average_latency());
		MyLog.resultAdder("std_dev:%f".formatted(MyTool.POP_SD_with_min_max_normalization(edge_requests)));
		MyLog.resultDoubleListAdder("SDs", SDs);
		MyLog.resultIntListAdder("max_workload_eids", max_workload_eids);
		MyLog.resultIntListAdder("max_workloads", max_workloads);
		MyLog.resultIntListAdder("min_workloads", min_workloads);
		MyLog.resultAdder("classification:%s".formatted(MyConf.type_prefix));
		MyLog.resultAdder("Capacity:%s".formatted(MyConf.edgesInfo[0][3]));
		MyLog.resultAdder("---------------------------------------");
		//		MyLog.closeTagWriter();
		return new long[] { totalReq, hitReq, missReq };
	}

	private double get_average_traffic()
	{
		double traffic = 0;
		int count = 0;
		for (User u : SendController.userHashMap.values())
		{
			traffic += u.total_traffic;
			count += u.requst_count;
		}
		return traffic / count;
	}

	private double get_average_latency()
	{
		double latency = 0;
		int count = 0;
		for (User u : SendController.userHashMap.values())
		{
			latency += u.total_latency;
			count += u.requst_count;
		}
		return latency / count;
	}

	private ArrayList<Double> calculate_SDs_get_max_workload()
	{
		int expected_periods_count = 24;
		ArrayList<Double> result_SDs = new ArrayList<>();
		int periods_count = 240;
		int combine_count = periods_count / expected_periods_count;
		int max_workload_eid = -1;

		for (int period_index = 0; period_index < expected_periods_count; period_index++)
		{
			int[] request_in_one_period = new int[400];
			for (int i = combine_count * period_index; i < combine_count * period_index + combine_count; i++)
			{
				int[] adder = workloads[i];
				for (int j = 0; j < 400; j++)
				{
					request_in_one_period[j] += adder[j];
				}
			}
			result_SDs.add(MyTool.POP_SD_with_min_max_normalization(request_in_one_period));
			OptionalInt max_workload = Arrays.stream(request_in_one_period).max();
			OptionalInt min_workload = Arrays.stream(request_in_one_period).min();
			max_workload_eid = Arrays.stream(request_in_one_period).boxed().collect(Collectors.toList())
					.indexOf(max_workload.getAsInt());
			max_workload_eids.add(max_workload_eid + 1);
			max_workloads.add(max_workload.getAsInt());
			min_workloads.add(min_workload.getAsInt());
		}
		return result_SDs;
	}

	public void clean()
	{
		for (EdgeServer edge : edges.values())
		{
			edge.clear();
		}
		edges.clear();
	}

}
