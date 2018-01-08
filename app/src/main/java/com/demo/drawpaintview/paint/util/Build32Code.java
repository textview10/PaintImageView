package com.demo.drawpaintview.paint.util;

import java.util.Random;

/**
 * 生成32位二进制码
 * 
 * @author 张迎迎
 * 
 */
public class Build32Code {
	/**
	 * 随机生成36位id
	 *
	 * @return
	 */
	public static String createGUID() {
		String result = "00000000-0000-0000-0000-000000000000";
		try {
			char[] content = { 'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2',
					'3', '4', '5', '6', '7', '8', '9' };
			Random random = new Random();
			char[] charArray = result.toCharArray();
			for (int i = 0; i < charArray.length; i++) {
				if (charArray[i] == '0') {
					charArray[i] = content[random.nextInt(16)];
				}
			}
			result = String.valueOf(charArray);
		} catch (Exception ex) {
		}
		return result;
	}
}
