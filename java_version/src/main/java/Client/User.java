package Client;

import Edge.EdgeServer;
import Remote.OriginalServer;
import tools.MyConf;
import tools.MyTool;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/29.
 */
public class User
{
	long uid;
	double lat;
	double lon;
	EdgeServer homeEdge;
	public double total_traffic = 0;
	public double total_latency = 0;
	public int requst_count = 0;

	public User(long uid, double lat, double lon)
	{
		this.uid = uid;
		this.lat = lat;
		this.lon = lon;
		int defaultEdgeId = OriginalServer.getInstance().getEdgeByDistance(lat, lon);
		homeEdge = OriginalServer.getInstance().getEdge(defaultEdgeId);
	}

	public long getUid()
	{
		return uid;
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
	 *
	 * @param timestamp
	 * @param tid
	 * @param vid
	 * @return
	 * 0: local hit
	 * 1: neighbor hit
	 * 2: cloud hit
	 */
	public int request(int timestamp, int tid, String vid)
	{
		requst_count++;
		//1. from home Edge, get the optimal edge
		//everytime, user ask edgechooser, edgechooser will check whether update is needed?
		int optimalEdgeId = homeEdge.hit_or_getOptimalEdge(vid, tid, timestamp);

		//use to gathering all request to edge1
		if (MyConf.GATHER_TO_EDGE1)
		{
			optimalEdgeId = 1;
		}

		//get hit in home edge
		if (optimalEdgeId == -1)
		{
			return 0;
		} else
		{
			EdgeServer optimalEdge = OriginalServer.getInstance().getEdge(optimalEdgeId);
			//2. use this edge to request
			boolean hit = optimalEdge.getContent(timestamp, tid, vid, homeEdge.getEid());

			double latency;
			double traffic;
			latency = get_latency(homeEdge, optimalEdge);
			traffic = get_traffic(homeEdge, optimalEdge);
			if (!hit)
			{
				latency += MyConf.CDN_LATENCY;
				traffic += MyConf.CDN_TRAFFIC;
			}

			//if only request Home MEC
			//		if (MyConf.HOME_MEC)
			//			return latency;

			total_latency += latency;
			total_traffic += traffic;
			//		return latency;
			if(hit){
				return 1;
			}
			else {
				return 2;
			}
		}
	}

	private double get_traffic(EdgeServer homeEdge, EdgeServer optimalEdge)
	{
		if (homeEdge.equals(optimalEdge))
		{
			return MyConf.Basic_TRAFFIC;
		} else
		{
			return 1 + Math.pow(
					MyTool.distance(homeEdge.getLat(), homeEdge.getLon(), optimalEdge.getLat(), optimalEdge.getLon()),
					1.3);
		}
	}

	private double get_latency(EdgeServer homeEdge, EdgeServer optimalEdge)
	{
		if (homeEdge.equals(optimalEdge))
		{
			return MyConf.Basic_LATENCY;
		} else
		{
			return 10 + 10 * MyTool.distance(homeEdge.getLat(), homeEdge.getLon(), optimalEdge.getLat(),
					optimalEdge.getLon());
		}
	}

	public void updateLocation(double lat, double lon)
	{
		int defaultEdgeId = OriginalServer.getInstance().getEdgeByDistance(lat, lon);
		homeEdge = OriginalServer.getInstance().getEdge(defaultEdgeId);
	}
}
