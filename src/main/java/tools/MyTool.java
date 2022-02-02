package tools;

import java.util.Arrays;
import java.util.stream.IntStream;

public class MyTool
{
	//    'M' is statute miles (default)
	//                  'K' is kilometers
	//                  'N' is nautical miles

	/**
	 * Copyright: this method from: https://www.geodatasource.com/developers/java
	 *
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @param unit
	 * @return
	 */
	public static double distance(double lat1, double lon1, double lat2, double lon2, String unit)
	{
		if ((lat1 == lat2) && (lon1 == lon2))
		{
			return 0;
		} else
		{
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2))
					+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			if (unit.equals("K"))
			{
				dist = dist * 1.609344;
			} else if (unit.equals("N"))
			{
				dist = dist * 0.8684;
			}
			return (dist);
		}
	}

	public static String genMultiString(String oneBlock, int times)
	{
		StringBuffer result = new StringBuffer();
		for (int i : IntStream.range(0, times).toArray())
		{
			result.append(oneBlock);
		}
		return result.toString();
	}

	public static double distance(double lat, double lon, double lat1, double lon1)
	{
		return distance(lat, lon, lat1, lon1, "K");
	}

	static double Sum(double[] data)
	{
		double sum = 0;
		for (int i = 0; i < data.length; i++)
			sum = sum + data[i];
		return sum;
	}

	static double Mean(double[] data)
	{
		double mean = 0;
		mean = Sum(data) / data.length;
		return mean;
	}

	public static double POP_Variance(double[] data)
	{
		double variance = 0;
		for (int i = 0; i < data.length; i++)
		{
			variance = variance + (Math.pow((data[i] - Mean(data)), 2));
		}
		variance = variance / data.length;
		return variance;
	}

//	public static double POP_STD_dev(int[] data)
//	{
//		double std_dev;
//		std_dev = Math.sqrt(POP_Variance(data));
//		return std_dev;
//	}

	public static double POP_SD_with_min_max_normalization(int[] data)
	{
		int count=data.length;
		int min= Arrays.stream(data).min().getAsInt();
		int max= Arrays.stream(data).max().getAsInt();
		int gap=max-min;
		double std_dev;
		double[] data_double = new double[count];
		for (int i = 0; i < count; i++)
		{
			data_double[i] = (data[i]-min) * 100.0 / gap;
		}
		std_dev = Math.sqrt(POP_Variance(data_double));
		return std_dev;
	}
//	int[] test = new int[400];
//		for (int i = 0; i < 400; i++)
//	{
//		test[i] = 100 + i;
//	}
//		System.out.println(MyTool.POP_SD_with_min_max_normalization(test));


}
