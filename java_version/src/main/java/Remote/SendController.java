package Remote;

import Client.EdgeChooser;
import Client.User;
import Edge.EdgeServer;
import Remote.OriginalServer;
import tools.MyConf;
import tools.MyLog;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;



public class SendController
{
	public static HashMap<Long, User> userHashMap = new HashMap<>();

	private int last_update_workload_timestamp = 1431792000;
	private int workload_update_period_index = 0;

	public void readFileAndSent(String requestFile)
	{
		ArrayList<ArrayList<Double>> hit_rate_periods = new ArrayList<>();
		//read in the corresponding type info
		BufferedReader typeReader;
		HashMap<String, Integer> typeMapping = new HashMap<>();
		try
		{
			int maxTypeId = 0;
			typeReader = new BufferedReader(new FileReader(MyConf.typeFile));
			String oneLine = typeReader.readLine();
			while (oneLine != null)
			{
				if (oneLine.length() == 0)
				{
					oneLine = typeReader.readLine();
					continue;
				}
				String[] tmp = oneLine.split(":");
				int typeId = Integer.parseInt(tmp[1]);
				typeMapping.put(tmp[0], typeId);
				if (typeId > maxTypeId)
					maxTypeId = typeId;
				oneLine = typeReader.readLine();
			}
			MyConf.typeNum = maxTypeId + 1;
			System.out.println(
					"typeNum:%d,neighborNum:%d,MEC_connection_period:%d".formatted(MyConf.typeNum, MyConf.neighborNum,
							MyConf.MEC_CONNECTION_PERIOD));

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		BufferedReader bufferedReader;
		try
		{
			bufferedReader = new BufferedReader(new FileReader(requestFile));
			String oneLine = bufferedReader.readLine();
			//check the first line
			if (oneLine.startsWith("user"))
			{
				oneLine = bufferedReader.readLine();
			}
			//for debug use
			int test = 0;
			int t0 = 1431792000;
			int period = 3600;
			int t1 = t0 + period;

//			var x = tqdm(100, "iterating");

			ArrayList<Integer> hit_count_period = new ArrayList<>();
			int total_count_period = 0;
			reset_hit_count_period(hit_count_period);

			while (oneLine != null)
			{
				if (oneLine.length() == 0)
				{
					oneLine = bufferedReader.readLine();
					continue;
				}

				total_count_period += 1;

				String[] tmp = oneLine.split(",");
				long uid = Long.parseLong(tmp[0]);
				int timestamp = Integer.parseInt(tmp[1]);
				double lon = Double.parseDouble(tmp[2]);
				double lat = Double.parseDouble(tmp[3]);
				String vid = oneLine.split(",")[4];

				//update_wordload

				if (timestamp - last_update_workload_timestamp > EdgeServer.WORKLOAD_UPDATE_PERIOD)
				{
					for (EdgeServer edge : OriginalServer.getInstance().edges.values())
					{
						edge.update_workload(workload_update_period_index);
					}
					last_update_workload_timestamp = timestamp;
					workload_update_period_index++;
				}

				//1.find the user
				User user = userHashMap.get(uid);
				if (user == null)
				{
					user = new User(uid, lat, lon);
					userHashMap.put(uid, user);
				} else
					//caution: user may change its requesting location
					//2.check if this user's location has changed
					if (Math.abs(user.getLat() - lat) > 0.1 || Math.abs(user.getLon() - lon) > 0.1)
					{
						user.updateLocation(lat, lon);
					}

				//3.send request
				//								user.requestWithoutType(timestamp, vid);
				//				int tid = typeMapping.get(vid) != null ? typeMapping.get(vid) : new Random().nextInt(MyConf.typeNum);
				int tid = typeMapping.get(vid) != null ? typeMapping.get(vid) : MyConf.WITHOUT_TYPE;
				int hit_result = user.request(timestamp, tid, vid);

				//deal with hit result
				hit_count_period.set(hit_result, hit_count_period.get(hit_result) + 1);

				if (timestamp >= t1)
				{
					t0 = t1;
					t1 = t0 + period;
					for (int i = 0; i < 3; i++)
					{
						int accumulated_hit = 0;
						for (int j = 0; j < i + 1; j++)
						{
							accumulated_hit += hit_count_period.get(j);
						}
						if(hit_rate_periods.size()<i+1){
							hit_rate_periods.add(new ArrayList<Double>());
						}
						hit_rate_periods.get(i).add(accumulated_hit * 1.0 / total_count_period);

					}
					reset_hit_count_period(hit_count_period);
					total_count_period = 0;
				}

				oneLine = bufferedReader.readLine();

				//for test use
				if (MyConf.DEBUG)
				{
					test++;
					//					if (test > 5000)
					if (test % 10000 == 0)
					{
						//						System.out.println("finish debug number!");
						System.out.println("debug number%d".formatted(test));
						break;
					}

				}
			}

			//add the last hour result
			for (int i = 0; i < 3; i++)
			{
				int accumulated_hit = 0;
				for (int j = 0; j < i + 1; j++)
				{
					accumulated_hit += hit_count_period.get(j);
				}
				hit_rate_periods.get(i).add(accumulated_hit * 1.0 / total_count_period);

			}

			//at last, update
			for (EdgeServer edge : OriginalServer.getInstance().edges.values())
			{
				edge.update_workload(workload_update_period_index);
			}

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		for (int i = 0; i < 3; i++)
		{
			MyLog.resultDoubleListAdder("hit rate", hit_rate_periods.get(i));
		}
		OriginalServer.getInstance().reportAllStat();
	}

	private void reset_hit_count_period(ArrayList<Integer> hit_count_period)
	{
		hit_count_period.clear();
		for (int i = 0; i < 3; i++)
		{
			hit_count_period.add(0);
		}
	}

	public void cleanUsers()
	{
		userHashMap.clear();
	}
}
