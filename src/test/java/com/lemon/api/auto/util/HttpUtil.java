package com.lemon.api.auto.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.lemon.api.auto.cases.BaseCase;
import com.lemon.api.auto.pojo.Api;
import com.lemon.api.auto.pojo.Case;

/**发包的工具类
 * @author Administrator
 *
 */
public class HttpUtil {
	//声明一个私有对象logger，用于日志编辑
	private static Logger logger = Logger.getLogger(BaseCase.class);
	/**
	 * @param TestData中参数如下：
	 * mothed:接口的提交方式（post、get）
	 * call方法：调用接口参数
	 * 因为需要对获取的参数进行处理，所以要返回相应的get、post接口参数
	 */
	public static Map<String, Object> call(Case cs,Api api) {
		//获取请求方法get和post，用于比较
		String method = api.getType();
		//判断请求的方法，并调用相应的接口访问类型
		if("get".equalsIgnoreCase(method)){
			return doGet(cs,api);
		}else if("post".equalsIgnoreCase(method)){
			return doPost(cs,api);
		}
		return null;
	}
	
	/**get请求方法接口的调用
	 * @param url 接口地址
	 * @param jsonString/params 测试数据
	 * 需要返回状态码、响应头、响应报文的信息，所以要改为返回map集合
	 */
	public static Map<String, Object> doGet(Case cs,Api api){
		//创建数组对象用于保存状态码、响应头、响应报文的信息
		Map<String , Object> resultMap = new HashMap<String, Object>();
		//获取URL地址
		String url = api.getUrl();
		//获取params参数，因为获取的参数是json格式的，需要转换成key：value形式
		String jsonString = cs.getParams();
		//将json格式转换成key：value格式    调用generateQueryParam()方法
		String params = generateQueryParam(jsonString);
		try {
			//创建HttpGet对象
			HttpGet httpGet = new HttpGet(url+"?"+params);
			//准备HttpClient客户端
			CloseableHttpClient httpClient = HttpClients.createDefault();
			//判断请求是否需要鉴权，设置1为需要鉴权的接口
			if ("1".equals(api.getIsAccessControled())) {
				AuthorizationUtil.addAuthorizationInfoBeforeRequest(httpGet);
			}
			
			//发送请求
			CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			//取出响应状态码
			int code = httpResponse.getStatusLine().getStatusCode();
			System.out.println("状态码为："+code);
			//取出响应头
			Header [] headers = httpResponse.getAllHeaders();
			System.out.println("响应头："+Arrays.toString(headers));
			//取出响应报文
			String result = EntityUtils.toString(httpResponse.getEntity());
			System.out.println("响应报文："+result);
			resultMap.put("code", code);
			resultMap.put("headers", headers);
			resultMap.put("result", result);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
	
	/**
	 * post请求方法接口的调用(判断请求的提交方式是json还是form), 再传入相应的参数形式
	 * @param url 接口地址
	 * @param jsonString/params 接口参数
	 * 需要返回状态码、响应头、响应报文的信息，所以要改为返回map集合
	 */
	public static Map<String, Object> doPost(Case cs,Api api){
		Map<String , Object> resultMap = new HashMap<String, Object>();
		//获取URL地址
		String url = api.getUrl();
		logger.info("调用的接口地址为：【"+url+"】");
		//获取params参数，因为获取的参数是json格式的，需要转换成key：value形式
		String jsonString = cs.getParams();
		//获取apiType值：用户判断post请求是form格式还是json格式
		String apiType = api.getApiType();
		logger.info("接口参数的提交类型：【"+apiType+"】");
		try {
			//创建HttpPost对象
			HttpPost httpPost = new HttpPost(url);
			//判断post请求的提交方式是哪种
			if ("form".equals(apiType)) {
				//添加请求头（指定以表单方式来提交参数）
				httpPost.addHeader(new BasicHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8"));
				//将json格式转换成key：value格式    调用generateQueryParam()方法
				String params = generateBodyParam(jsonString);
				logger.info("接口参数为：【"+params+"】");
				//添加参数到请求体
				httpPost.setEntity(new StringEntity(params,"UTF-8"));
			}else if ("post".equals(apiType)) {
				//添加请求头（指定以json方式来提交参数）
				httpPost.addHeader(new BasicHeader("Content-Type","application/json;charset=UTF-8"));
				//添加参数到请求体，直接使用jsonString
				httpPost.setEntity(new StringEntity(jsonString,"UTF-8"));
			}	
			//准备客户端
			CloseableHttpClient httpClient = HttpClients.createDefault();
			//判断请求是否需要鉴权,设置1为需要鉴权的接口
			if ("1".equals(api.getIsAccessControled())) {
				AuthorizationUtil.addAuthorizationInfoBeforeRequest(httpPost);
			}
			//发送请求
			CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
			//取出响应状态码
			int code = httpResponse.getStatusLine().getStatusCode();
			logger.info("状态码为：【"+code+"】");
			//取出响应头
			Header [] headers = httpResponse.getAllHeaders();
			logger.info("响应头为：【"+headers+"】");
			//取出响应报文
			String result = EntityUtils.toString(httpResponse.getEntity());
			logger.info("响应报文为：【"+result+"】");
			resultMap.put("code", code);
			resultMap.put("headers", headers);
			resultMap.put("result", result);
			return resultMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    
	//用于get请求
	public static String generateQueryParam(String jsonString) {
		return generate(jsonString);
	}

    //用于post请求的form表单数据
	public static String generateBodyParam(String jsonString) {
		return generate(jsonString);
	}
	/**当Excel中传入的数据为json格式时
	 * 使用fastjson解析json数据，并将其存入map数组中
	 * 也就是将json格式转化为key：value格式
	 * 便于get方法和post方法的form表单格式调用
	 * @param jsonString
	 * @return
	 */
	private static String generate(String jsonString) {
		// 通过fastjson框架，将json字符串反序列化为一个map对象  需要向下强制转型
		Map<String, Object> map = (Map<String, Object>) JSONObject.parse(jsonString);
		//遍历map的key，然后在对json进行拼
		Set<String> keys = map.keySet();
		//String为字符串常量，而StringBuilder和StringBuffer均为字符串变量
		//即String对象一旦创建之后该对象是不可更改的，但后两者的对象是变量，是可以更改的
		//用于字符串的拼接
		StringBuffer sBuffer = new StringBuffer();
		//定义一个空的数组，大小和keys集合的大小一致
		String [] keyarr = new String[keys.size()];
		//将set类型转换为string数组==>keys集合转化为keyarr数组
		keys.toArray(keyarr);
		//循环数组
		for (int i = 0; i < keyarr.length; i++) {
			//获取每一个数组的值
			String paramName = keyarr[i];
			//拼接字符串该方式获取的字符串格式为：mobilephone=19000000000&pwd=123456& (多出末尾的&符号)，需要切割
			sBuffer.append(paramName).append("=").append(map.get(paramName)).append("&");
		}
		//截取返回的字符串从第一个到最后第二个，排除了“&”符号
		return sBuffer.substring(0,sBuffer.lastIndexOf("&"));
	}
	
}
