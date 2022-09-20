package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.*;

/**
 * Created by JackPeng(pengjunkun@gmail.com) on 2021/1/12.
 */
public class MyLog
{
	private static String MYTAG = "JackTag";
	private static String RESULT_FILE = "";
	public static Logger logger;
	public static BufferedWriter tagWriter;

	public static void set_resultFile(String name)
	{
		RESULT_FILE = "result_now/"+MyConf.type_prefix;
		RESULT_FILE += "/";
		RESULT_FILE += name;
		if (!new File(RESULT_FILE).exists())
		{
			new File(RESULT_FILE).mkdirs();
		}
	}

	public static void resultAdder(String content)
	{
		try
		{
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(RESULT_FILE + "/allResult.txt", true));
			bufferedWriter.write(content);
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

//	public static void initTagWriter(String str)
//	{
//		MYTAG = str;
//		try
//		{
//			tagWriter = new BufferedWriter(new FileWriter(RESULT_FILE + "/" + MYTAG + ".txt"));
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//
//	}

//	public static void tagWriter(String cont)
//	{
//		try
//		{
//			tagWriter.write(cont);
//			tagWriter.newLine();
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//	}

//	public static void tagSquareWriter(int homeMEC, int[] arrays, int[] arrays_hit)
//	{
//		try
//		{
//			int lineSize = (int) Math.sqrt(arrays.length);
//			for (int a = 0; a < lineSize; a++)
//			{
//				String line = "";
//				for (int b = lineSize * a; b < lineSize * a + lineSize; b++)
//				{
//					String tmp = "";
//					if (arrays[b] != 0)
//					{
//						tmp += ("%2$d/%1$d".formatted(arrays[b], arrays_hit[b]));
//					} else
//					{
//						tmp += ("%1$d".formatted(arrays[b]));
//					}
//
//					if (b + 1 == homeMEC)
//						tmp += "H";
//
//					int append = 8 - tmp.length();
//					if (append < 0)
//						append = 0;
//					for (int i = 0; i < append; i++)
//					{
//						tmp += " ";
//					}
//					line += tmp;
//				}
//				tagWriter.write(line);
//				tagWriter.newLine();
//			}
//		} catch (IOException e)
//
//		{
//			e.printStackTrace();
//		}
//
//	}
//
//	public static void closeTagWriter()
//	{
//		try
//		{
//			tagWriter.close();
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//	}

	public static void jack(String s)
	{
		System.out.println(s);
	}

	public static void jack_debug(String s)
	{
		if (MyConf.DEBUG)
			System.out.println(s);
	}

	public static void jackJoint(String s)
	{
		System.out.print(s);
	}

	public static void resultIntListAdder(String tag,ArrayList<Integer> SDs)
	{
		try
		{
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(RESULT_FILE + "/allResult.txt", true));
			StringBuffer sb = new StringBuffer();
			sb.append(tag+":");
			for (Integer SD : SDs)
			{
				sb.append(SD);
				sb.append(",");
			}
			bufferedWriter.write(sb.toString());
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public static void resultDoubleListAdder(String tag,ArrayList<Double> SDs)
	{
		try
		{
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(RESULT_FILE + "/allResult.txt", true));
			StringBuffer sb = new StringBuffer();
			sb.append(tag+":");
			for (Double SD : SDs)
			{
				sb.append(SD);
				sb.append(",");
			}
			bufferedWriter.write(sb.toString());
			bufferedWriter.newLine();
			bufferedWriter.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
