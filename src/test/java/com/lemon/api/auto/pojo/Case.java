package com.lemon.api.auto.pojo;

/**Excel中第二张表单：接口信息表中的属性值
 *  caseId：用例编号
	desc：用例描述
	apiId：接口编号
	params：参数
	isPositive：是否为正向用例
	responseValidators：响应校验信息
	actualResponseData：接口实际响应
	responseValidationResult：响应断言结果
	DbValidators：表数据验证结果
	queryResultBefore：接口执行前数据库查询结果
	queryResultAfter：接口执行后数据库查询结果
 * @author Administrator
 *
 */
public class Case {
	private String caseId;
	private String desc;
	private String apiId;
	private String params;
	private String isPositive;
	private String responseValidators;
	private String actualResponseData;
	private String responseValidationResult;
	private String DbValidators;
	private String queryResultBefore;
	private String queryResultAfter;
	public String getDbValidators() {
		return DbValidators;
	}
	public void setDbValidators(String dbValidators) {
		DbValidators = dbValidators;
	}
	public String getQueryResultBefore() {
		return queryResultBefore;
	}
	public void setQueryResultBefore(String queryResultBefore) {
		this.queryResultBefore = queryResultBefore;
	}
	public String getQueryResultAfter() {
		return queryResultAfter;
	}
	public void setQueryResultAfter(String queryResultAfter) {
		this.queryResultAfter = queryResultAfter;
	}
	public String getResponseValidators() {
		return responseValidators;
	}
	public void setResponseValidators(String responseValidators) {
		this.responseValidators = responseValidators;
	}
	public String getActualResponseData() {
		return actualResponseData;
	}
	public void setActualResponseData(String actualResponseData) {
		this.actualResponseData = actualResponseData;
	}
	public String getResponseValidationResult() {
		return responseValidationResult;
	}
	public void setResponseValidationResult(String responseValidationResult) {
		this.responseValidationResult = responseValidationResult;
	}	
	public String getIsPositive() {
		return isPositive;
	}
	public void setIsPositive(String isPositive) {
		this.isPositive = isPositive;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getApiId() {
		return apiId;
	}
	public void setApiId(String apiId) {
		this.apiId = apiId;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	@Override
	public String toString() {
		return "Case [caseId=" + caseId + ", desc=" + desc + ", apiId=" + apiId + ", params=" + params + ", isPositive="
				+ isPositive + ", responseValidators=" + responseValidators + ", actualResponseData="
				+ actualResponseData + ", responseValidationResult=" + responseValidationResult + ", DbValidators="
				+ DbValidators + ", queryResultBefore=" + queryResultBefore + ", queryResultAfter=" + queryResultAfter
				+ "]";
	}

}
