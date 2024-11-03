package com.lemon.api.auto.util;

import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.lemon.api.auto.pojo.Case;
import com.lemon.api.auto.pojo.ResponseValidator;

/**验证工具类：验证用例是否通过
 * @author Administrator
 *
 */
public class ResponseValidateUtil {

	public static String validateResponseData(Case cs, Map<String, Object> resultMap) {
		//取出接口的响应报文
		String result = (String) resultMap.get("result");
		
		//获取Excel表中的响应校验信息（验证规则）
		//取出的数据为[{"jsonPath":"$.msg","expected":"密码不能为空"}]样式
		String responseValidators = cs.getResponseValidators();
		
		//为了能分别取出jsonPath和expected的值，需要用到面向对象思想
		//创建一个对象用于储存jsonPath和expected的值
		//因为传入的是数组，所以需要对其进行解析，使用JSONObject.parseArray()方法，返回是个list集合
		//第一个参数是需要解析的json字符串对象
		//第二个参数传入字节码的作用是反射解析ResponseValidator类中的jsonPath和expected对象
		List<ResponseValidator> validators = JSONObject.parseArray(responseValidators, ResponseValidator.class);
		//设置默认值为通过状态
		String validateResult = "通过";
		//循环遍历ResponseValidator集合
		for (ResponseValidator responseValidator : validators) {
			//取出jsonPath表达式：$msg
			String jsonPathExpression = responseValidator.getJsonPath();
			//根据表达式从响应报文中取出实际的msg数据
			//第一个参数是响应结果，第二个参数是jsonPath的表达式
			String actual = (String) JSONPath.read(result, jsonPathExpression);
			//取出期望值，"expected":"密码不能为空"
			String expected = responseValidator.getExpected();
			try {
				//将实际的msg数据和期望的msg数据进行断言
				Assert.assertEquals(actual, expected);	
			} catch (Error e) {//AssertionError断言错误的时候，其报错要使用Error
				//当断言不通过时，输出不通过
				validateResult = "不通过";
				break;
			}	
		}
		return validateResult;
	}
	
}
