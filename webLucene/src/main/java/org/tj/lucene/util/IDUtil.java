package org.tj.lucene.util;

import java.util.UUID;

/**
 * ID工具类
 * 
 * @author 唐靖
 *
 * @date 2016年6月18日下午11:56:40
 *
 */
public class IDUtil {
	/**
	 * 32位随机id
	 * 
	 * @author 唐靖
	 *
	 * @date 2016年6月18日下午11:56:50
	 *
	 * @return
	 */
	public static String getId() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}

}
