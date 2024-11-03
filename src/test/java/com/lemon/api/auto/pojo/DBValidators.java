package com.lemon.api.auto.pojo;

/**该对象封装的是表数据验证的信息
 * 数据样式：[{"order":"1","sql":"select count(*) as totalNum from member where mobilephone='18813989202'"}]
 * order：sql编号
 * sql：SQL内容
 * @author Administrator
 *
 */
public class DBValidators {

	private String order;
	private String sql;
	
	public DBValidators() {
		super();
	}
	public DBValidators(String order, String sql) {
		super();
		this.order = order;
		this.sql = sql;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
}
