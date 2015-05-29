package com.ericyl.themedemo.util;

import java.util.Random;

public class CodeUtils {
	
	public static String getRandomCode(int nums){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < nums; i++) {
			Random random = new Random();
			int j = random.nextInt(10);
			sb.append(j);
		}
		String code = sb.toString();
		return code;
	}

}
