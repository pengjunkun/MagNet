package remote;

import Client.User;
import Remote.OriginalServer;
import org.junit.jupiter.api.Test;
import tools.MyConf;
import tools.MyLog;
import tools.MyTool;

import static org.junit.jupiter.api.Assertions.*;

public class OriginalServerTest {
    OriginalServer server = null;
    User user = new User(0, 39, 116);

    @Test
    void testOri() {
//
//        MyConf.edgesInfo = new String[][]{{"0", "39.1", "116.1", "1000"}, {"1", "40.1", "116.1", "1005"}};
//        server=OriginalServer.getInstance();
//        int e1 = server.getEdgeByDistance(29F, 116F);
//        int e2 = server.getEdgeByDistance(59F, 116F);
//        assertEquals(0, e1);
//        assertEquals(1, e2);
//
//        user.request(0, 0, 0);
//        user.request(0, 0, 1);
//        user.request(0, 0, 2);
//        user.request(0, 0, 3);
//        user.request(0, 0, 4);
//        user.request(0, 1, 10);
//        user.request(0, 1, 11);
//        user.request(0, 1, 12);
//        user.request(0, 1, 13);
//        user.request(0, 1, 14);
//        //for now, totalReq=10,hit=0
//        long[] tmp = server.reportAllStat();
//        assertEquals(10, tmp[0]);
//        assertEquals(0, tmp[1]);
//
//        user.request(0, 0, 0);
//        user.request(0, 0, 1);
//        user.request(0, 0, 2);
//        user.request(0, 0, 3);
//        user.request(0, 0, 4);
//        user.request(0, 1, 10);
//        user.request(0, 1, 11);
//        //for now, totalReq=17,hit=7
//        tmp = server.reportAllStat();
//        assertEquals(17, tmp[0]);
//        assertEquals(7, tmp[1]);

    }


    @Test
    void testDistance() {
        //Distance Beijing, CHN -> Tianjing, Tianjin, CHN
        //Distance: 67.26 mi (108.25 km)
        double dis = MyTool.distance(39.9075, 116.39723, 39.14222, 117.17667, "K");
        double diff = Math.abs(108.25 - dis);
        //108.20866758335683
        double dis2 = MyTool.distance(39.8075, 116.39723, 39.14222, 117.17667, "K");
        //99.73455057528393, which shows 0.1 diff in lat causes about 10KM distance
        assertTrue(diff < 0.1);
    }
}
