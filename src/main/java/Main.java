import Remote.OriginalServer;
import Remote.SendController;
import tools.MyConf;
import tools.MyLog;

import java.io.File;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/28.
 */
public class Main
{
	static SendController sendController;
	static File default_typeFile;
	static File classic_typeFile;
	static File[] typeFiles;
	static int default_NEN = 154;
	static int default_EUP = 360;

	public static void main(String[] args)
	{
		MyLog.jack("good luck!");
		String classification_str = args[0];
		String job_str = args[1];
		String arg_str = args[2];
		MyConf.TYPE_METHOD classificatio_method;
		switch (classification_str)
		{
		case "h":
			classificatio_method = MyConf.TYPE_METHOD.HOTBALANCE;
			break;
		case "f":
			classificatio_method = MyConf.TYPE_METHOD.FURTHEST;
			break;
		case "k":
			classificatio_method = MyConf.TYPE_METHOD.KMEANS;
			break;
		case "r":
			classificatio_method = MyConf.TYPE_METHOD.RANDOM;
			break;
		case "c":
			classificatio_method = MyConf.TYPE_METHOD.CLASSIC;
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + args[0]);
		}

		MyConf.set_type_method(classificatio_method);

		File typeBaseDir = new File(MyConf.type_files_address);
		typeFiles = typeBaseDir.listFiles();
		default_typeFile = new File(MyConf.type_files_address + "nodes621.csv");
		classic_typeFile = new File(MyConf.type_files_address + "nodes39765.csv");

		switch (job_str)
		{
		case "type":
			if (arg_str.isEmpty())
			{
				test_typeNumAll();
			} else
			{
				test_typeNum(arg_str);
			}
			break;
		case "period":
			if (arg_str.isEmpty())
			{
				test_period_all();
			} else
			{
				test_period(arg_str);
			}
			break;
		case "neighbor":
			if (arg_str.isEmpty())
			{
				test_neighborNumber_all();
			} else
			{
				test_neighborNumber(arg_str);
			}
			break;
		case "test":
			test_temporal("test");
			break;
		case "CAP":
			if (arg_str.isEmpty())
			{
				//todo
			} else
			{
				test_capacity(arg_str);
			}
			break;
		case "all":
			test_typeNumAll();
			test_period_all();
			test_neighborNumber_all();
			break;
		case "classic_NEN":
			classic_test_NEN(arg_str);
			break;
		case "classic_EUP":
			classic_test_EUP(arg_str);
			break;
		case "classic_CAP":
			classic_test_capacity(arg_str);
			break;
		default:
			throw new IllegalStateException("Unexpected value: " + args[1]);
		}

	}

	private static void classic_test_NEN(String arg)
	{
		MyLog.set_resultFile("NEN");
		int neighNum = Integer.parseInt(arg);
		go(classic_typeFile, neighNum, default_EUP * 64);
	}

	private static void classic_test_EUP(String arg)
	{
		MyLog.set_resultFile("EUP");
		int MEC_connection_period = Integer.parseInt(arg);
		go(classic_typeFile, default_NEN / 64, MEC_connection_period);
	}

	private static void test_temporal(String result_file_name)
	{
		MyLog.set_resultFile(result_file_name);
		go(default_typeFile, default_NEN, default_EUP);

	}

	private static void test_period_all()
	{
		MyLog.set_resultFile("EUP");
		//every 3min to 30min
		for (int MEC_connection_period = 0; MEC_connection_period < 30 * 60 + 1; MEC_connection_period += 60 * 3)
			//			System.out.print("\"%d\" ".formatted(MEC_connection_period));
			go(default_typeFile, default_NEN, MEC_connection_period);
	}

	private static void test_period(String arg)
	{
		MyLog.set_resultFile("EUP");
		//every 3min to 30min
		int MEC_connection_period = Integer.parseInt(arg);
		go(default_typeFile, default_NEN, MEC_connection_period);
	}

	private static void test_neighborNumber_all()
	{
		MyLog.set_resultFile("NEN");
		for (int neighNum = 0; neighNum < 301; neighNum += 30)
			//			System.out.print("\"%d\" ".formatted(neighNum));
			go(default_typeFile, neighNum, default_EUP);
	}

	private static void test_neighborNumber(String arg)
	{
		MyLog.set_resultFile("NEN");
		int neighNum = Integer.parseInt(arg);
		go(default_typeFile, neighNum, default_EUP);
	}

	private static void test_typeNumAll()
	{
		MyLog.set_resultFile("NGT");
		for (File typeFile : typeFiles)
			go(typeFile, default_NEN, default_EUP);
	}

	private static void test_typeNum(String arg)
	{
		File typeFile = new File(MyConf.type_files_address + "nodes%s.csv".formatted(arg));
		MyLog.set_resultFile("NGT");
		go(typeFile, default_NEN, default_EUP);
	}

	private static void test_capacity(String c)
	{
		MyLog.set_resultFile("CAP");
		for (String[] edgeInfo_one : MyConf.edgesInfo)
		{
			edgeInfo_one[3] = c;
		}

		go(default_typeFile, default_NEN, default_EUP);
	}

	private static void classic_test_capacity(String c)
	{
		MyLog.set_resultFile("CAP");
		go(classic_typeFile, default_NEN / 64, default_EUP);
	}

	public static void go(File typeFile, int NEN, int EUP)
	{
		MyConf.neighborNum = NEN;
		MyConf.MEC_CONNECTION_PERIOD = EUP;
		MyConf.typeFile = typeFile.getAbsolutePath();

		OriginalServer.getInstance().cleanAndUpdateEdges();
		sendController = new SendController();
		//		sendController.readFileAndSent("/home/pjk/data/iqiyi/lastDay_target.csv");
		sendController.readFileAndSent("./res/lastDay_target.csv");
		sendController.cleanUsers();

		OriginalServer.getInstance().clean();
	}

}
