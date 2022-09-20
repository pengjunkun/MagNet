package Client;

import Edge.NeighborEdgeInfo;
import Remote.OriginalServer;
import tools.MyConf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this class is a part of User. It's used to maintain the edges' info and choose a optimal edge for the user
 * <p>
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/29.
 */
public class EdgeChooser
{
	private static EdgeChooser instance;
	//id,capacity,timestamp
	private HashMap<Integer, int[]> type2edgeIdCapacityTimestamp_hashMap;

	private int belongToEdge_Id = -1;
	private double lat;
	private double lon;

	public EdgeChooser(int eid, double lat, double lon)
	{
		this.lat = lat;
		this.lon = lon;
		type2edgeIdCapacityTimestamp_hashMap = new HashMap<>();
		belongToEdge_Id = eid;
	}

	public void cleanCache()
	{
		type2edgeIdCapacityTimestamp_hashMap.clear();
	}

	public int getEdge(int tid, int timestamp)
	{
		if (MyConf.HOME_MEC)
			return getBelongToEdge_IdEdgeId();
		if (tid == MyConf.WITHOUT_TYPE)
			return getBelongToEdge_IdEdgeId();
		if (type2edgeIdCapacityTimestamp_hashMap.get(tid) != null
				&& timestamp - type2edgeIdCapacityTimestamp_hashMap.get(tid)[2] < MyConf.EXPIRE_TIME)
		{
			return type2edgeIdCapacityTimestamp_hashMap.get(tid)[0];
		}
		return getBelongToEdge_IdEdgeId();
	}

	public int getBelongToEdge_IdEdgeId()
	{
		return belongToEdge_Id;
	}

	private void updateTypeInfo(int eid, ConcurrentHashMap<Integer, Integer> typeSizePairs, int timestamp)
	{
		//if the MEC is too far for the user, we do not consider it
		int user_MEC_distance = (int) OriginalServer.getInstance().getEdge(eid).getDistanceFromUser(lat, lon);
		if (user_MEC_distance > MyConf.USER_MEC_threshold || typeSizePairs == null)
		{
			return;
		}

		//compare each capacity with current
		for (int typeId : typeSizePairs.keySet())
		{
			if (typeSizePairs.get(typeId) == null)
				continue;
			int size = typeSizePairs.get(typeId);

			//farther with a discount penalty
			//			size -= user_MEC_distance * MyConf.PENALTY_FACTOR;
			int[] edgeStatus = type2edgeIdCapacityTimestamp_hashMap.get(typeId);
			if (edgeStatus == null || size > edgeStatus[1] || timestamp - edgeStatus[2] > MyConf.EXPIRE_TIME)
			{
				type2edgeIdCapacityTimestamp_hashMap.put(typeId, new int[] { eid, size, timestamp });
			}
		}
	}

	public void updateEdgeCandidates(HashMap<Integer, NeighborEdgeInfo> candidates, int timestamp)
	{
		for (int nid : candidates.keySet())
		{
			ConcurrentHashMap<Integer, Integer> edgeTypeSize = null;
			if (nid != belongToEdge_Id)
			{
				edgeTypeSize = OriginalServer.getInstance().getEdge(nid).getOldCacheTypeSize();
			} else
			{
				edgeTypeSize = OriginalServer.getInstance().getEdge(nid).getCacheTypeSize();
			}
			updateTypeInfo(nid, edgeTypeSize, timestamp);
		}

	}
}
