package com.lemon.api.auto.util;

import java.util.ArrayList;
import java.util.List;

import org.testng.ITestContext;

import com.lemon.api.auto.pojo.Api;
import com.lemon.api.auto.pojo.Case;

public class DataUtil {

	/**该工具类用于取出相应的接口信息和用例信息
	 * @param apiId：接口编号 
	 * @param context：测试上下文对象====在该对象内获取接口需要的字段，并拼接
	 */
	public static Object [][] getProviderDatas(String apiId, ITestContext context) {
		//取出所有满足接口的用例
		List<Case> cases = getWantedCaseList(apiId, context);
		//根据apiId取到对应的接口
		Api api = getWantedApi(apiId, context);
		//准备一个二维数组，用于存放case和api的对象数据
		Object [][] datas = new Object[cases.size()][2];
		//循环数组，对接口参数进行赋值
		for (int i = 0; i < cases.size(); i++) {
			Case cs = cases.get(i);	
			//将其存入二维数组datas中
			datas[i][0] = cs;     //根据传入的用接口编号apiId来获取需要的用例信息（用例有多条）
			datas[i][1] = api;    //根据传入的用接口编号apiId来获取需要的接口信息（对应的接口信息只有一条）
		}		
		return datas;	
	}

	//取出ApiId为*的用例信息,当测试类传入的ApiId为多少，就调用相应的用例信息
	public static List<Case> getWantedCaseList(String apiId, ITestContext context) {
		//从上下文对象中使用getAttribute("cases")，取出集合cases中满足条件的用例
		List<Case> cases = (List<Case>) context.getAttribute("cases");
		//准备一个集合，用于保存我们想要的数据
		List<Case> wantedCaseList = new ArrayList<Case>();
		//根据接口编号过滤出满足条件的用例
		for (Case cs : cases) {
			//判断传入的apiId和从cs中获取的apiid是否相同
			if (apiId.equals(cs.getApiId())) {
				//将符合的用例加入wantedCaseList集合中
				wantedCaseList.add(cs);
			}
		}
		return wantedCaseList;
		
	}
	
	//获取ApiId为*的接口信息，当测试类传入的ApiId为多少，就调用相应的接口信息
	public static Api getWantedApi(String apiId,ITestContext context){
		//从上下文对象中使用getAttribute("apis")，取出集合apis中满足条件的用例
		List<Api> apis = (List<Api>) context.getAttribute("apis");
		//根据接口编号过滤出满足条件的用例
		for (Api api : apis) {
			//判断传入的apiId和从cs中获取的apiid是否相同
			if(apiId.equals(api.getApiId())){
				return api;
			}
		}
		return null;
	}

}
