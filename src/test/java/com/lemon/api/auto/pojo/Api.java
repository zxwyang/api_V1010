package com.lemon.api.auto.pojo;

/**Excel中第一张表单：接口信息表中的属性值
 *  apiId：接口编号
	apiName：接口名称
	type：接口提交方式
	apiType：接口类型
	url：接口地址
	isAccessControled：是否做权限控制
 * @author Administrator
 */
public class Api {
	private String apiId;
	private String apiName;
	private String type;
	private String apiType;
	private String url;
	private String isAccessControled;
	
	public String getIsAccessControled() {
		return isAccessControled;
	}
	public void setIsAccessControled(String isAccessControled) {
		this.isAccessControled = isAccessControled;
	}
	public String getApiId() {
		return apiId;
	}
	public void setApiId(String apiId) {
		this.apiId = apiId;
	}
	public String getApiName() {
		return apiName;
	}
	public void setApiName(String apiName) {
		this.apiName = apiName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getApiType() {
		return apiType;
	}
	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "Api [apiId=" + apiId + ", apiName=" + apiName + ", type=" + type + ", apiType=" + apiType + ", url="
				+ url + ", isAccessControled=" + isAccessControled + "]";
	}
	
}
