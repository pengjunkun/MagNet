package tools;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/28.
 */
public class MyConf
{
	//	public static final int WORKLOAD_SIZE_FACTOR = 2;
	public static final double WORKLOAD_SIZE_FACTOR = 1.5;
	public static final double CDN_LATENCY = 100;
	public static final double CDN_TRAFFIC = 50;
	public static int USER_MEC_threshold = 7;

	public enum Workload_status
	{
		IDLE, NORMAL, BUSY;
	}

	//latency(ms)
	public static final int Basic_LATENCY = 10;
	public static final int Basic_TRAFFIC = 1;
	//	public static final int MISS_LATENCY = 500;
	//MiB
	public static final int FILE_SIZE = 1;
	public static final boolean DEBUG = false;
	//	public static final boolean DEBUG = true;
	public static final boolean HOME_MEC = false;
	public static final boolean ENOUGH_CAPACITY = false;
	public static final boolean GATHER_TO_EDGE1 = false;
	public static double PENALTY_FACTOR = 0.0;

	public static String typeFile = "";
	public static final int EXPIRE_TIME = 200 * 60;
	public static int typeNum = 621;
	public static int neighborNum = 30;
	public static int MEC_CONNECTION_PERIOD = Integer.MAX_VALUE;
	//	public static int USER_MEC_threshold = 15;
	public static String type_prefix = "";

	public static String type_files_address = "";

	public static void set_type_method(TYPE_METHOD method)
	{
		switch (method)
		{
		case KMEANS:
			type_prefix = "KMeans";
			break;
		case RANDOM:
			type_prefix = "randomClass";
			break;
		case FURTHEST:
			type_prefix = "furthestClass";
			break;
		case HOTBALANCE:
			type_prefix = "hotBalance";
			break;
		case CLASSIC:
			type_prefix = "classic";
			break;
		}
		MyLog.jack("switch to %s".formatted(type_prefix));
		type_files_address = "./classifications/%s/typeResult/".formatted(type_prefix);
	}

	public enum TYPE_METHOD
	{KMEANS, RANDOM, FURTHEST, HOTBALANCE, CLASSIC}

	public static TYPE_METHOD current_type_method = TYPE_METHOD.KMEANS;
	//edges:{id,latitude,longitude,size(MiB)}
	//    public static String[][] edgesInfo = { {"1", "40.1", "116.1", "108"}};
	public static String[][] edgesInfo = { { "1", "39.853188", "116.242630", "135" },
			{ "2", "39.853188", "116.254288", "135" }, { "3", "39.853188", "116.265945", "135" },
			{ "4", "39.853188", "116.277603", "135" }, { "5", "39.853188", "116.289261", "135" },
			{ "6", "39.853188", "116.300919", "135" }, { "7", "39.853188", "116.312576", "135" },
			{ "8", "39.853188", "116.324234", "135" }, { "9", "39.853188", "116.335892", "135" },
			{ "10", "39.853188", "116.347549", "135" }, { "11", "39.853188", "116.359207", "135" },
			{ "12", "39.853188", "116.370865", "135" }, { "13", "39.853188", "116.382523", "135" },
			{ "14", "39.853188", "116.394180", "135" }, { "15", "39.853188", "116.405838", "135" },
			{ "16", "39.853188", "116.417496", "135" }, { "17", "39.853188", "116.429153", "135" },
			{ "18", "39.853188", "116.440811", "135" }, { "19", "39.853188", "116.452469", "135" },
			{ "20", "39.853188", "116.464127", "135" }, { "21", "39.861946", "116.242630", "135" },
			{ "22", "39.861946", "116.254288", "135" }, { "23", "39.861946", "116.265945", "135" },
			{ "24", "39.861946", "116.277603", "135" }, { "25", "39.861946", "116.289261", "135" },
			{ "26", "39.861946", "116.300919", "135" }, { "27", "39.861946", "116.312576", "135" },
			{ "28", "39.861946", "116.324234", "135" }, { "29", "39.861946", "116.335892", "135" },
			{ "30", "39.861946", "116.347549", "135" }, { "31", "39.861946", "116.359207", "135" },
			{ "32", "39.861946", "116.370865", "135" }, { "33", "39.861946", "116.382523", "135" },
			{ "34", "39.861946", "116.394180", "135" }, { "35", "39.861946", "116.405838", "135" },
			{ "36", "39.861946", "116.417496", "135" }, { "37", "39.861946", "116.429153", "135" },
			{ "38", "39.861946", "116.440811", "135" }, { "39", "39.861946", "116.452469", "135" },
			{ "40", "39.861946", "116.464127", "135" }, { "41", "39.870705", "116.242630", "135" },
			{ "42", "39.870705", "116.254288", "135" }, { "43", "39.870705", "116.265945", "135" },
			{ "44", "39.870705", "116.277603", "135" }, { "45", "39.870705", "116.289261", "135" },
			{ "46", "39.870705", "116.300919", "135" }, { "47", "39.870705", "116.312576", "135" },
			{ "48", "39.870705", "116.324234", "135" }, { "49", "39.870705", "116.335892", "135" },
			{ "50", "39.870705", "116.347549", "135" }, { "51", "39.870705", "116.359207", "135" },
			{ "52", "39.870705", "116.370865", "135" }, { "53", "39.870705", "116.382523", "135" },
			{ "54", "39.870705", "116.394180", "135" }, { "55", "39.870705", "116.405838", "135" },
			{ "56", "39.870705", "116.417496", "135" }, { "57", "39.870705", "116.429153", "135" },
			{ "58", "39.870705", "116.440811", "135" }, { "59", "39.870705", "116.452469", "135" },
			{ "60", "39.870705", "116.464127", "135" }, { "61", "39.879463", "116.242630", "135" },
			{ "62", "39.879463", "116.254288", "135" }, { "63", "39.879463", "116.265945", "135" },
			{ "64", "39.879463", "116.277603", "135" }, { "65", "39.879463", "116.289261", "135" },
			{ "66", "39.879463", "116.300919", "135" }, { "67", "39.879463", "116.312576", "135" },
			{ "68", "39.879463", "116.324234", "135" }, { "69", "39.879463", "116.335892", "135" },
			{ "70", "39.879463", "116.347549", "135" }, { "71", "39.879463", "116.359207", "135" },
			{ "72", "39.879463", "116.370865", "135" }, { "73", "39.879463", "116.382523", "135" },
			{ "74", "39.879463", "116.394180", "135" }, { "75", "39.879463", "116.405838", "135" },
			{ "76", "39.879463", "116.417496", "135" }, { "77", "39.879463", "116.429153", "135" },
			{ "78", "39.879463", "116.440811", "135" }, { "79", "39.879463", "116.452469", "135" },
			{ "80", "39.879463", "116.464127", "135" }, { "81", "39.888222", "116.242630", "135" },
			{ "82", "39.888222", "116.254288", "135" }, { "83", "39.888222", "116.265945", "135" },
			{ "84", "39.888222", "116.277603", "135" }, { "85", "39.888222", "116.289261", "135" },
			{ "86", "39.888222", "116.300919", "135" }, { "87", "39.888222", "116.312576", "135" },
			{ "88", "39.888222", "116.324234", "135" }, { "89", "39.888222", "116.335892", "135" },
			{ "90", "39.888222", "116.347549", "135" }, { "91", "39.888222", "116.359207", "135" },
			{ "92", "39.888222", "116.370865", "135" }, { "93", "39.888222", "116.382523", "135" },
			{ "94", "39.888222", "116.394180", "135" }, { "95", "39.888222", "116.405838", "135" },
			{ "96", "39.888222", "116.417496", "135" }, { "97", "39.888222", "116.429153", "135" },
			{ "98", "39.888222", "116.440811", "135" }, { "99", "39.888222", "116.452469", "135" },
			{ "100", "39.888222", "116.464127", "135" }, { "101", "39.896980", "116.242630", "135" },
			{ "102", "39.896980", "116.254288", "135" }, { "103", "39.896980", "116.265945", "135" },
			{ "104", "39.896980", "116.277603", "135" }, { "105", "39.896980", "116.289261", "135" },
			{ "106", "39.896980", "116.300919", "135" }, { "107", "39.896980", "116.312576", "135" },
			{ "108", "39.896980", "116.324234", "135" }, { "109", "39.896980", "116.335892", "135" },
			{ "110", "39.896980", "116.347549", "135" }, { "111", "39.896980", "116.359207", "135" },
			{ "112", "39.896980", "116.370865", "135" }, { "113", "39.896980", "116.382523", "135" },
			{ "114", "39.896980", "116.394180", "135" }, { "115", "39.896980", "116.405838", "135" },
			{ "116", "39.896980", "116.417496", "135" }, { "117", "39.896980", "116.429153", "135" },
			{ "118", "39.896980", "116.440811", "135" }, { "119", "39.896980", "116.452469", "135" },
			{ "120", "39.896980", "116.464127", "135" }, { "121", "39.905739", "116.242630", "135" },
			{ "122", "39.905739", "116.254288", "135" }, { "123", "39.905739", "116.265945", "135" },
			{ "124", "39.905739", "116.277603", "135" }, { "125", "39.905739", "116.289261", "135" },
			{ "126", "39.905739", "116.300919", "135" }, { "127", "39.905739", "116.312576", "135" },
			{ "128", "39.905739", "116.324234", "135" }, { "129", "39.905739", "116.335892", "135" },
			{ "130", "39.905739", "116.347549", "135" }, { "131", "39.905739", "116.359207", "135" },
			{ "132", "39.905739", "116.370865", "135" }, { "133", "39.905739", "116.382523", "135" },
			{ "134", "39.905739", "116.394180", "135" }, { "135", "39.905739", "116.405838", "135" },
			{ "136", "39.905739", "116.417496", "135" }, { "137", "39.905739", "116.429153", "135" },
			{ "138", "39.905739", "116.440811", "135" }, { "139", "39.905739", "116.452469", "135" },
			{ "140", "39.905739", "116.464127", "135" }, { "141", "39.914497", "116.242630", "135" },
			{ "142", "39.914497", "116.254288", "135" }, { "143", "39.914497", "116.265945", "135" },
			{ "144", "39.914497", "116.277603", "135" }, { "145", "39.914497", "116.289261", "135" },
			{ "146", "39.914497", "116.300919", "135" }, { "147", "39.914497", "116.312576", "135" },
			{ "148", "39.914497", "116.324234", "135" }, { "149", "39.914497", "116.335892", "135" },
			{ "150", "39.914497", "116.347549", "135" }, { "151", "39.914497", "116.359207", "135" },
			{ "152", "39.914497", "116.370865", "135" }, { "153", "39.914497", "116.382523", "135" },
			{ "154", "39.914497", "116.394180", "135" }, { "155", "39.914497", "116.405838", "135" },
			{ "156", "39.914497", "116.417496", "135" }, { "157", "39.914497", "116.429153", "135" },
			{ "158", "39.914497", "116.440811", "135" }, { "159", "39.914497", "116.452469", "135" },
			{ "160", "39.914497", "116.464127", "135" }, { "161", "39.923256", "116.242630", "135" },
			{ "162", "39.923256", "116.254288", "135" }, { "163", "39.923256", "116.265945", "135" },
			{ "164", "39.923256", "116.277603", "135" }, { "165", "39.923256", "116.289261", "135" },
			{ "166", "39.923256", "116.300919", "135" }, { "167", "39.923256", "116.312576", "135" },
			{ "168", "39.923256", "116.324234", "135" }, { "169", "39.923256", "116.335892", "135" },
			{ "170", "39.923256", "116.347549", "135" }, { "171", "39.923256", "116.359207", "135" },
			{ "172", "39.923256", "116.370865", "135" }, { "173", "39.923256", "116.382523", "135" },
			{ "174", "39.923256", "116.394180", "135" }, { "175", "39.923256", "116.405838", "135" },
			{ "176", "39.923256", "116.417496", "135" }, { "177", "39.923256", "116.429153", "135" },
			{ "178", "39.923256", "116.440811", "135" }, { "179", "39.923256", "116.452469", "135" },
			{ "180", "39.923256", "116.464127", "135" }, { "181", "39.932014", "116.242630", "135" },
			{ "182", "39.932014", "116.254288", "135" }, { "183", "39.932014", "116.265945", "135" },
			{ "184", "39.932014", "116.277603", "135" }, { "185", "39.932014", "116.289261", "135" },
			{ "186", "39.932014", "116.300919", "135" }, { "187", "39.932014", "116.312576", "135" },
			{ "188", "39.932014", "116.324234", "135" }, { "189", "39.932014", "116.335892", "135" },
			{ "190", "39.932014", "116.347549", "135" }, { "191", "39.932014", "116.359207", "135" },
			{ "192", "39.932014", "116.370865", "135" }, { "193", "39.932014", "116.382523", "135" },
			{ "194", "39.932014", "116.394180", "135" }, { "195", "39.932014", "116.405838", "135" },
			{ "196", "39.932014", "116.417496", "135" }, { "197", "39.932014", "116.429153", "135" },
			{ "198", "39.932014", "116.440811", "135" }, { "199", "39.932014", "116.452469", "135" },
			{ "200", "39.932014", "116.464127", "135" }, { "201", "39.940773", "116.242630", "135" },
			{ "202", "39.940773", "116.254288", "135" }, { "203", "39.940773", "116.265945", "135" },
			{ "204", "39.940773", "116.277603", "135" }, { "205", "39.940773", "116.289261", "135" },
			{ "206", "39.940773", "116.300919", "135" }, { "207", "39.940773", "116.312576", "135" },
			{ "208", "39.940773", "116.324234", "135" }, { "209", "39.940773", "116.335892", "135" },
			{ "210", "39.940773", "116.347549", "135" }, { "211", "39.940773", "116.359207", "135" },
			{ "212", "39.940773", "116.370865", "135" }, { "213", "39.940773", "116.382523", "135" },
			{ "214", "39.940773", "116.394180", "135" }, { "215", "39.940773", "116.405838", "135" },
			{ "216", "39.940773", "116.417496", "135" }, { "217", "39.940773", "116.429153", "135" },
			{ "218", "39.940773", "116.440811", "135" }, { "219", "39.940773", "116.452469", "135" },
			{ "220", "39.940773", "116.464127", "135" }, { "221", "39.949532", "116.242630", "135" },
			{ "222", "39.949532", "116.254288", "135" }, { "223", "39.949532", "116.265945", "135" },
			{ "224", "39.949532", "116.277603", "135" }, { "225", "39.949532", "116.289261", "135" },
			{ "226", "39.949532", "116.300919", "135" }, { "227", "39.949532", "116.312576", "135" },
			{ "228", "39.949532", "116.324234", "135" }, { "229", "39.949532", "116.335892", "135" },
			{ "230", "39.949532", "116.347549", "135" }, { "231", "39.949532", "116.359207", "135" },
			{ "232", "39.949532", "116.370865", "135" }, { "233", "39.949532", "116.382523", "135" },
			{ "234", "39.949532", "116.394180", "135" }, { "235", "39.949532", "116.405838", "135" },
			{ "236", "39.949532", "116.417496", "135" }, { "237", "39.949532", "116.429153", "135" },
			{ "238", "39.949532", "116.440811", "135" }, { "239", "39.949532", "116.452469", "135" },
			{ "240", "39.949532", "116.464127", "135" }, { "241", "39.958290", "116.242630", "135" },
			{ "242", "39.958290", "116.254288", "135" }, { "243", "39.958290", "116.265945", "135" },
			{ "244", "39.958290", "116.277603", "135" }, { "245", "39.958290", "116.289261", "135" },
			{ "246", "39.958290", "116.300919", "135" }, { "247", "39.958290", "116.312576", "135" },
			{ "248", "39.958290", "116.324234", "135" }, { "249", "39.958290", "116.335892", "135" },
			{ "250", "39.958290", "116.347549", "135" }, { "251", "39.958290", "116.359207", "135" },
			{ "252", "39.958290", "116.370865", "135" }, { "253", "39.958290", "116.382523", "135" },
			{ "254", "39.958290", "116.394180", "135" }, { "255", "39.958290", "116.405838", "135" },
			{ "256", "39.958290", "116.417496", "135" }, { "257", "39.958290", "116.429153", "135" },
			{ "258", "39.958290", "116.440811", "135" }, { "259", "39.958290", "116.452469", "135" },
			{ "260", "39.958290", "116.464127", "135" }, { "261", "39.967049", "116.242630", "135" },
			{ "262", "39.967049", "116.254288", "135" }, { "263", "39.967049", "116.265945", "135" },
			{ "264", "39.967049", "116.277603", "135" }, { "265", "39.967049", "116.289261", "135" },
			{ "266", "39.967049", "116.300919", "135" }, { "267", "39.967049", "116.312576", "135" },
			{ "268", "39.967049", "116.324234", "135" }, { "269", "39.967049", "116.335892", "135" },
			{ "270", "39.967049", "116.347549", "135" }, { "271", "39.967049", "116.359207", "135" },
			{ "272", "39.967049", "116.370865", "135" }, { "273", "39.967049", "116.382523", "135" },
			{ "274", "39.967049", "116.394180", "135" }, { "275", "39.967049", "116.405838", "135" },
			{ "276", "39.967049", "116.417496", "135" }, { "277", "39.967049", "116.429153", "135" },
			{ "278", "39.967049", "116.440811", "135" }, { "279", "39.967049", "116.452469", "135" },
			{ "280", "39.967049", "116.464127", "135" }, { "281", "39.975807", "116.242630", "135" },
			{ "282", "39.975807", "116.254288", "135" }, { "283", "39.975807", "116.265945", "135" },
			{ "284", "39.975807", "116.277603", "135" }, { "285", "39.975807", "116.289261", "135" },
			{ "286", "39.975807", "116.300919", "135" }, { "287", "39.975807", "116.312576", "135" },
			{ "288", "39.975807", "116.324234", "135" }, { "289", "39.975807", "116.335892", "135" },
			{ "290", "39.975807", "116.347549", "135" }, { "291", "39.975807", "116.359207", "135" },
			{ "292", "39.975807", "116.370865", "135" }, { "293", "39.975807", "116.382523", "135" },
			{ "294", "39.975807", "116.394180", "135" }, { "295", "39.975807", "116.405838", "135" },
			{ "296", "39.975807", "116.417496", "135" }, { "297", "39.975807", "116.429153", "135" },
			{ "298", "39.975807", "116.440811", "135" }, { "299", "39.975807", "116.452469", "135" },
			{ "300", "39.975807", "116.464127", "135" }, { "301", "39.984566", "116.242630", "135" },
			{ "302", "39.984566", "116.254288", "135" }, { "303", "39.984566", "116.265945", "135" },
			{ "304", "39.984566", "116.277603", "135" }, { "305", "39.984566", "116.289261", "135" },
			{ "306", "39.984566", "116.300919", "135" }, { "307", "39.984566", "116.312576", "135" },
			{ "308", "39.984566", "116.324234", "135" }, { "309", "39.984566", "116.335892", "135" },
			{ "310", "39.984566", "116.347549", "135" }, { "311", "39.984566", "116.359207", "135" },
			{ "312", "39.984566", "116.370865", "135" }, { "313", "39.984566", "116.382523", "135" },
			{ "314", "39.984566", "116.394180", "135" }, { "315", "39.984566", "116.405838", "135" },
			{ "316", "39.984566", "116.417496", "135" }, { "317", "39.984566", "116.429153", "135" },
			{ "318", "39.984566", "116.440811", "135" }, { "319", "39.984566", "116.452469", "135" },
			{ "320", "39.984566", "116.464127", "135" }, { "321", "39.993324", "116.242630", "135" },
			{ "322", "39.993324", "116.254288", "135" }, { "323", "39.993324", "116.265945", "135" },
			{ "324", "39.993324", "116.277603", "135" }, { "325", "39.993324", "116.289261", "135" },
			{ "326", "39.993324", "116.300919", "135" }, { "327", "39.993324", "116.312576", "135" },
			{ "328", "39.993324", "116.324234", "135" }, { "329", "39.993324", "116.335892", "135" },
			{ "330", "39.993324", "116.347549", "135" }, { "331", "39.993324", "116.359207", "135" },
			{ "332", "39.993324", "116.370865", "135" }, { "333", "39.993324", "116.382523", "135" },
			{ "334", "39.993324", "116.394180", "135" }, { "335", "39.993324", "116.405838", "135" },
			{ "336", "39.993324", "116.417496", "135" }, { "337", "39.993324", "116.429153", "135" },
			{ "338", "39.993324", "116.440811", "135" }, { "339", "39.993324", "116.452469", "135" },
			{ "340", "39.993324", "116.464127", "135" }, { "341", "40.002083", "116.242630", "135" },
			{ "342", "40.002083", "116.254288", "135" }, { "343", "40.002083", "116.265945", "135" },
			{ "344", "40.002083", "116.277603", "135" }, { "345", "40.002083", "116.289261", "135" },
			{ "346", "40.002083", "116.300919", "135" }, { "347", "40.002083", "116.312576", "135" },
			{ "348", "40.002083", "116.324234", "135" }, { "349", "40.002083", "116.335892", "135" },
			{ "350", "40.002083", "116.347549", "135" }, { "351", "40.002083", "116.359207", "135" },
			{ "352", "40.002083", "116.370865", "135" }, { "353", "40.002083", "116.382523", "135" },
			{ "354", "40.002083", "116.394180", "135" }, { "355", "40.002083", "116.405838", "135" },
			{ "356", "40.002083", "116.417496", "135" }, { "357", "40.002083", "116.429153", "135" },
			{ "358", "40.002083", "116.440811", "135" }, { "359", "40.002083", "116.452469", "135" },
			{ "360", "40.002083", "116.464127", "135" }, { "361", "40.010841", "116.242630", "135" },
			{ "362", "40.010841", "116.254288", "135" }, { "363", "40.010841", "116.265945", "135" },
			{ "364", "40.010841", "116.277603", "135" }, { "365", "40.010841", "116.289261", "135" },
			{ "366", "40.010841", "116.300919", "135" }, { "367", "40.010841", "116.312576", "135" },
			{ "368", "40.010841", "116.324234", "135" }, { "369", "40.010841", "116.335892", "135" },
			{ "370", "40.010841", "116.347549", "135" }, { "371", "40.010841", "116.359207", "135" },
			{ "372", "40.010841", "116.370865", "135" }, { "373", "40.010841", "116.382523", "135" },
			{ "374", "40.010841", "116.394180", "135" }, { "375", "40.010841", "116.405838", "135" },
			{ "376", "40.010841", "116.417496", "135" }, { "377", "40.010841", "116.429153", "135" },
			{ "378", "40.010841", "116.440811", "135" }, { "379", "40.010841", "116.452469", "135" },
			{ "380", "40.010841", "116.464127", "135" }, { "381", "40.019600", "116.242630", "135" },
			{ "382", "40.019600", "116.254288", "135" }, { "383", "40.019600", "116.265945", "135" },
			{ "384", "40.019600", "116.277603", "135" }, { "385", "40.019600", "116.289261", "135" },
			{ "386", "40.019600", "116.300919", "135" }, { "387", "40.019600", "116.312576", "135" },
			{ "388", "40.019600", "116.324234", "135" }, { "389", "40.019600", "116.335892", "135" },
			{ "390", "40.019600", "116.347549", "135" }, { "391", "40.019600", "116.359207", "135" },
			{ "392", "40.019600", "116.370865", "135" }, { "393", "40.019600", "116.382523", "135" },
			{ "394", "40.019600", "116.394180", "135" }, { "395", "40.019600", "116.405838", "135" },
			{ "396", "40.019600", "116.417496", "135" }, { "397", "40.019600", "116.429153", "135" },
			{ "398", "40.019600", "116.440811", "135" }, { "399", "40.019600", "116.452469", "135" },
			{ "400", "40.019600", "116.464127", "135" } };

	//define some special video type in Integer
	public final static int NOT_REPLACEMENT = -101;
	public final static int WITHOUT_TYPE = -102;
}
