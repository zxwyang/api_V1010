package com.lemon.api.auto.cases;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.lemon.api.auto.util.DataUtil;

/**注册接口测试类
 * @author Administrator
 *
 */
public class RegisterCase extends BaseCase{
	
	//只有有testng注解的方法才能直接引用上下文ITestContext
	//该方法中的“1”代表的是接口信息表和用例表中的ApiId=1
	@DataProvider
	public Object [][] datas(ITestContext context) {
		return DataUtil.getProviderDatas("1",context);
	}
	
}
