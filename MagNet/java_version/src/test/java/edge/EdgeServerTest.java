package edge;

import Edge.EdgeServer;
import org.junit.jupiter.api.Test;
import tools.MyConf;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/29.
 */
public class EdgeServerTest
{
	private EdgeServer edgeServer = new EdgeServer(0, 39.1F, 116.1F, 5);

//	@Test
//	void testEdge()
//	{
//		//must miss 5 times
//		assertEquals(MyConf.MISS_LATENCY, edgeServer.getContent(-1,0, 1, 1));
//		assertEquals(MyConf.MISS_LATENCY, edgeServer.getContent(-1,0, 2, 2));
//		assertEquals(MyConf.MISS_LATENCY, edgeServer.getContent(-1,0, 3, 3));
//		assertEquals(MyConf.MISS_LATENCY, edgeServer.getContent(-1,0, 4, 4));
//		assertEquals(MyConf.MISS_LATENCY, edgeServer.getContent(-1,0, 5, 5));
//		//for now,save 5 files in this edge, check its type info
//		ConcurrentHashMap typeInfo=edgeServer.getCacheTypeSize();
//		assertEquals(1,typeInfo.get(1));
//		assertEquals(1,typeInfo.get(2));
//		assertEquals(1,typeInfo.get(3));
//		assertEquals(1,typeInfo.get(4));
//		assertEquals(1,typeInfo.get(5));
//
//		//add one for type3, this should replace the oldest entry--> tid=1,has 0; tid=3,has 2;
//		edgeServer.getContent(-1,1,3,6);
//		typeInfo=edgeServer.getCacheTypeSize();
//		assertEquals(0,typeInfo.get(1));
//		assertEquals(2,typeInfo.get(3));
//
//		//for request:6 in which, hit:0,miss:6
//		long[] stat=edgeServer.getRequestStat();
//		assertEquals(6,stat[0]);
//		assertEquals(0,stat[1]);
//		assertEquals(6,stat[2]);
//
//		edgeServer.getContent(-1,1,2,2);
//		edgeServer.getContent(-1,1,3,6);
//		//for request:8 in which, hit:2,miss:6
//		long[] stat2=edgeServer.getRequestStat();
//		assertEquals(8,stat2[0]);
//		assertEquals(2,stat2[1]);
//		assertEquals(6,stat2[2]);
//
//	}
//
}
