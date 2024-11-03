package com.lemon.api.auto.cases;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.log4j.Logger;
import org.apache.log4j.jmx.LoggerDynamicMBean;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.lemon.api.auto.pojo.Api;
import com.lemon.api.auto.pojo.Case;
import com.lemon.api.auto.util.AuthorizationUtil;
import com.lemon.api.auto.util.DBUtil;
import com.lemon.api.auto.util.ExcelUtil;
import com.lemon.api.auto.util.HttpUtil;
import com.lemon.api.auto.util.ResponseValidateUtil;



/**父类（基类）
 * 在Register和LoginCase中iniData方法和test1方法是相同的
 * 所以这里将这两个方法封装为父类
 * 减少代码的复用性
 * @author Administrator
 */
public class BaseCase {
	//声明一个私有对象logger，用于日志编辑
	private Logger logger = Logger.getLogger(BaseCase.class);

	// 在套件执行之前执行，@BeforeSuite是基于测试套件的，所以只会执行一次
	@BeforeSuite
	// ITestContext：测试上下文对象；存活于整个测试的周期，可以作为一个缓存对象
	// 相当于将两个集合存入该上下文中
	// 只有有testng注解的方法才能直接引用上下文ITestContext
	public void iniData(ITestContext context) {
		logger.info("*************开始执行测试套件*************");
		// 解析第一个表单对象
		List<Api> apis = ExcelUtil.readPojos(Api.class, 0);
		logger.info("读取所有接口信息，保存到上下文");
		// 解析第二个表单对象
		List<Case> cases = ExcelUtil.readPojos(Case.class, 1);
		logger.info("读取所有用例信息，保存到上下文");
		// 获取上下文对象
		context.setAttribute("apis", apis);
		context.setAttribute("cases", cases);
		// 加载数据库配置
		DBUtil.loadDBconfig();
		logger.info("加载数据库配置");
	}

	/**
	 * call方法用于接口参数的调用 调用call方法，获取参数
	 * 
	 * @Test是基于测试类的，所以会随着类的执行而执行
	 */
	@Test(dataProvider = "datas")
	public void test1(Case cs, Api api) {
		logger.info("===========开始执行测试方法===========");
		logger.info("开始查询待验证字段的数据");
		// 查询待验证字段的数据（接口调用前的表数据验证）
		String dbQueryResult = DBUtil.queryValidateFields(cs);

		// 判断表验证数据是否为空，不为空的话；再将得到的查询待验证字段回写到Excel的用例表单中
		if (dbQueryResult != null) {
			ExcelUtil.saveWriteBackData(cs.getCaseId(), "QueryResultBefore", dbQueryResult);
		}

		// 获取每条用例返回的状态码、响应头、响应报文
		Map<String, Object> resultMap = HttpUtil.call(cs, api);

		// 判断当前正在执行的用例是否为登录接口的正向用例
		if ("2".equals(api.getApiId()) && "1".equals(cs.getIsPositive())) {
			// 如果满足条件，就获取登陆认证的鉴权数据
			AuthorizationUtil.storeAuthorization(resultMap);
		}

		// 接口响应报文验证，判断该接口是否通过
		String validateResult = ResponseValidateUtil.validateResponseData(cs, resultMap);
		logger.info("接口响应数据的验证结果为：【"+validateResult+"】");

		// 调用保存回写数据的方法saveWriteBackData
		// 保存接口验证断言的回写数据
		ExcelUtil.saveWriteBackData(cs.getCaseId(), "ResponseValidationResult", validateResult);

		// 回写接口响应报文的方法
		// 获取响应报文
		String result = (String) resultMap.get("result");
		// 保存接口响应报文的回写数据
		ExcelUtil.saveWriteBackData(cs.getCaseId(), "ActualResponseData", result);

		// 查询待验证字段的数据（接口调用后的表数据验证） 复用前面的变量dbQueryResult
		// 相当于 String a = "hello"; a = "word"; a的值变为word
		dbQueryResult = DBUtil.queryValidateFields(cs);
		// 再将得到的查询待验证字段回写到Excel的用例表单中
		if (dbQueryResult != null) {
			ExcelUtil.saveWriteBackData(cs.getCaseId(), "QueryResultAfter", dbQueryResult);
		}
		logger.info("===========测试方法执行完毕===========");
	}

	// 准备一个finish方法，在套件执行完后，将集合中的数据全部回写到Excel表中
	@AfterSuite
	public void finish() {
		// 批量回写数据
		ExcelUtil.batchWriteBackDatas();
		logger.info("*************测试套件执行完毕*************");
	}

}
