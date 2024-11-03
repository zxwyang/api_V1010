package com.lemon.api.auto.pojo;

/**该对象封装的是响应校验信息（验证规则）
 * 数据为[{"jsonPath":"$.msg","expected":"密码不能为空"}]样式
 * jsonPath：代表实际的msg数据
 * expected：期望的msg数据
 * @author Administrator
 *
 */
public class ResponseValidator {

	private String jsonPath;
	private String expected;
	
	public ResponseValidator() {
		super();
	}
	public ResponseValidator(String jsonPath, String expected) {
		super();
		this.jsonPath = jsonPath;
		this.expected = expected;
	}
	public String getJsonPath() {
		return jsonPath;
	}
	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}
	public String getExpected() {
		return expected;
	}
	public void setExpected(String expected) {
		this.expected = expected;
	}
	
}
