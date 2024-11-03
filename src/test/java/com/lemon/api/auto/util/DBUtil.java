package com.lemon.api.auto.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;
import com.lemon.api.auto.pojo.Case;
import com.lemon.api.auto.pojo.DBValidators;
import com.lemon.api.auto.pojo.QueryResult;


/**数据库工具类，封装对数据库进行操作的方法
 * queryValidateFields：接口调用前的表数据验证，并转化为json字符串格式
 * @author Administrator
 *
 */
public class DBUtil {
	//静态变量只能用静态方法进行访问，设置为全局变量，不然会导致queryValidateFields方法中无法获取到数据库连接的所有数据信息
	static Properties properties = new Properties();
	//加载数据库连接
	public static void loadDBconfig() {
		try {
			File file = new File("src/test/resources/jdbc.properties");
			InputStream inputStream = new FileInputStream(file);
			properties.load(inputStream);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
    //查询验证字段（接口调用的表数据验证）
	public static String queryValidateFields(Case cs) {
		//接口调用前的表数据验证
		//取出表数据验证结果：[{"order":"1","sql":"select count(*) as totalNum from member where mobilephone='18813989202'"}]
		String dbValidators = cs.getDbValidators();
		
		//判断表数据验证结果是否为空，如果不为空就去获取结果集
		if (dbValidators!=null&&dbValidators.trim().length()>0) {
			//获取的表数据结果是包含json格式的数组
			//[{"order":"1","sql":"select count(*) as totalNum from member where mobilephone='18813989202'"}]
			//所以需要对其进行解析，使用JSONObject.parseArray()方法，返回是个list集合
			//第一个参数是需要解析的json字符串对象
			//第二个参数传入字节码的作用是反射解析DBValidators类中的order和sql对象
			List<DBValidators> validators = JSONObject.parseArray(dbValidators, DBValidators.class);		
			
			//创建一个集合，用于将QueryResult对象添加到集合
			List<QueryResult> queryResults = new ArrayList<QueryResult>();
			//循环遍历validators集合，获取到每条SQL的order和sql
			for (DBValidators dbValidator : validators) {
				//取出SQL编号
				String order = dbValidator.getOrder();
				//取出SQL脚本
				String sql = dbValidator.getSql();
				//执行SQL要先获取jdbc完成数据库查询
				//准备一个map集合用于储存执行SQL获取到的结果集
				Map<String, Object> map = new HashMap<String, Object>();
				try {
					//获取数据库连接的所有数据信息
					String url = properties.getProperty("jdbc.url");
					String user = properties.getProperty("jdbc.username");
					String password = properties.getProperty("jdbc.password");
					//1.创建数据库连接
					Connection connection = DriverManager.getConnection(url, user, password);
					//2.获取一个statement对象，将需要执行的SQL脚本封装到此对象中
					PreparedStatement statement = connection.prepareStatement(sql);
					//3.返回一个结果集：执行SQL后得到的数据
					ResultSet resultSet = statement.executeQuery();	
					//获取结果集的元数据（查询的信息：查询的字段名，查询字段名的内容）
					ResultSetMetaData metaData = resultSet.getMetaData();
					//获取SQL查询字段的个数，如果SQL中有三个字段，就会查询出3列结果，那么count=3
					int count = metaData.getColumnCount();
					//从结果集取数据，resultSet.next():判断结果集是否有数据
					while (resultSet.next()) {
						//循环count
						for (int i = 1; i <= count; i++) {
							//获取到字段的列名
							String columnName = metaData.getColumnName(i);	
							
							//获取到字段的列值
							//resultSet.get：获取列字段的类型；SQL查询出来的列名有不同的类型
							//比如int、string类型等；当不知道是什么类型时，就用getObject
							Object value = resultSet.getObject(i);
							//将结果集存入map集合中
							map.put(columnName, value);
							
						}
						
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				//再将获取到的结果集存入QueryResult对象中
				QueryResult queryResult = new QueryResult();
				queryResult.setOrder(order);
				queryResult.setResult(map);
				
				//将queryResult对象添加到queryResults集合中
				queryResults.add(queryResult);
			}
			//将queryResults集合序列化成json字符串（放在for循环外面，不然会在每添加一个对象的时候就序列化一次）
			String jsonArrayString = JSONObject.toJSONString(queryResults);
			//返回获取到的json字符串
			return jsonArrayString;
		}
		return null;
	}

}
