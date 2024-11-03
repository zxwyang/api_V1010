package com.lemon.api.auto.cases;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.lemon.api.auto.util.DataUtil;

/**登陆接口测试类
 * @author Administrator
 *
 */
public class RechargeCase extends BaseCase{
	
	//只有有testng注解的方法才能直接引用上下文ITestContext
	//该方法中的“3”代表的是接口信息表和用例表中的ApiId=3
	@DataProvider
	public Object [][] datas(ITestContext context) {
		return DataUtil.getProviderDatas("3",context);
	}
	
}
