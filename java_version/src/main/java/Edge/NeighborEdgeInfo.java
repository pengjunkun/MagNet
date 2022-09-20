package Edge;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/2/25.
 */
public class NeighborEdgeInfo
{
	int id;
//	String ip;
	int latency;
//	double lat;
//	double lon;

//	public NeighborEdgeInfo(int id, String ip, int latency, double lat, double lon)
	public NeighborEdgeInfo(int id,  int latency)
	{
		this.id = id;
//		this.ip = ip;
		this.latency = latency;
//		this.lat = lat;
//		this.lon = lon;
	}
}
