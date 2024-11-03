package com.lemon.api.auto.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;


/**QueryResult实体类中储存需要返回的是QueryResultBefore(接口执行前数据库查询结果)
 * 数据返回格式：[{"order":"1","result":{"totalNum":1}}]
 * order：SQL编号
 * result：SQL查询后得到的结果集，结果集储存为map集合格式
 * @author Administrator
 *
 */
public class QueryResult {
	//属性的定义取决json中有哪些key
	private String order;
	private Map<String , Object> result;
	
	public QueryResult() {
		super();
	}
	public QueryResult(String order, Map<String, Object> result) {
		super();
		this.order = order;
		this.result = result;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public Map<String, Object> getResult() {
		return result;
	}
	public void setResult(Map<String, Object> result) {
		this.result = result;
	}	
	
	public static void main(String[] args) {
		//准备集合用于储存多个对象
		List<QueryResult> list = new ArrayList<QueryResult>();
		
		//第一个对象，序列化后，输出json字符串格式
		QueryResult queryResult = new QueryResult();
		String order = "1";
		queryResult.setOrder(order);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("totalname", 1);
		queryResult.setResult(map);
		//将queryResult对象序列化为json格式
		String jsonstring = JSONObject.toJSONString(queryResult);
		System.out.println(jsonstring);
		//加入list集合
		list.add(queryResult);
		System.out.println("==================================");
		
		////第一个对象，序列化后，输出json字符串格式
		QueryResult queryResult2 = new QueryResult();
		String order2 = "1";
		queryResult2.setOrder(order2);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("totalname", 1);
		queryResult2.setResult(map2);
		//将queryResult2对象序列化为json格式
		String jsonstring2 = JSONObject.toJSONString(queryResult2);
		System.out.println(jsonstring2);
		//加入list集合
		list.add(queryResult2);
		System.out.println("==================================");
		
		//将list集合序列化为json格式
		String jsonArrayString = JSONObject.toJSONString(list);
		System.out.println(jsonArrayString);
		
	}

}
