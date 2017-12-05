package de.domisum.lib.auxilium.util;

import de.domisum.lib.auxilium.util.java.annotations.API;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil
{

	@API public static String replaceLast(String string, String from, String to)
	{
		int pos = string.lastIndexOf(from);
		if(pos > -1)
			return string.substring(0, pos)+to+string.substring(pos+from.length(), string.length());

		return string;
	}

	@API public static String repeat(String string, int repeats)
	{
		StringBuilder result = new StringBuilder();
		for(int i = 0; i < repeats; i++)
			result.append(string);

		return result.toString();
	}


	@API public static String getCommonPrefix(String s1, String s2)
	{
		StringBuilder common = new StringBuilder();

		for(int ci = 0; ci < Math.min(s1.length(), s2.length()); ci++)
		{
			char c1 = s1.charAt(ci);
			char c2 = s2.charAt(ci);

			if(c1 != c2)
				break;

			common.append(c1);
		}

		return common.toString();
	}

}