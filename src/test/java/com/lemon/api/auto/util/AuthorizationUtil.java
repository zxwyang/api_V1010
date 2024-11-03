package com.lemon.api.auto.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpRequest;

/**认证工具类
 * 定义一个cookie认证的工具类（相当于缓存作用）
 * 用户缓存获取的cookie信息
 * @author Administrator
 *
 */
public class AuthorizationUtil {
	//定义一个cookie认证的工具类（相当于缓存作用）
	public static Map<String, Object> cache = new HashMap<String, Object>();
	
	//定义一个cookie数据传入的方法addAuthorizationInfoBeforeRequest
	//当满足鉴权条件后，将数据传入到相应的http请求中
	//因为HttpRequest类是httppost和httpget的父类，所以该方法中的请求参数设置为HttpRequest httpRequest
	public static void addAuthorizationInfoBeforeRequest(HttpRequest httpRequest) {
		//获取cache集合中的value值，并转为字符串形式
		String auth = AuthorizationUtil.cache.get("auth").toString();
		//添加cookie请求头
		httpRequest.addHeader("Cookie",auth);
	}
	
	//定义一个鉴权的方法storeAuthorization
	public static void storeAuthorization(Map<String, Object> resultMap) {
		//从登录接口的响应头中取出set-cookie值（票）
		//先取去map集合中的headers值
		Header [] headers = (Header[]) resultMap.get("headers");
		//循环header数组
		for (Header header : headers) {
			//header数组的值类似于key：value形式
			//得到的cookie值满足header.getName()为Set-Cookie  header.getValue()包含 JSESSIONID
			if ("Set-Cookie".equals(header.getName())&&header.getValue().contains("JSESSIONID")) {
				//取出header的value值
				String cookieValue = header.getValue();
				//按照;对获取的cookieValue进行切割,得到一个字符串的数组
				//String parts = cookieValue.split(";")[1];   //不建议用索引来获取，因为你不确定获取的是否正确
				String [] parts = cookieValue.split(";");
				for (String part : parts) {
					if (part.contains("JSESSIONID")) {
						//System.out.println("JSESSIONID"+part);
						//在cache数组中存入认证用的cookie信息
						AuthorizationUtil.cache.put("auth", part);
					}
				}
			}
		}
	}
}
