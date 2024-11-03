package com.lemon.api.auto.pojo;

/**需要回写的数据对象
 * @author Administrator
 *caseId：用例编号————用来确定行
 *cellName：列名————用来确定列
 *content：需要写入的数据
 */
public class WriteBackData {

	private String caseId;
	private String cellName;
	private String content;
	
	public WriteBackData() {
		super();
	}
	public WriteBackData(String caseId, String cellName, String content) {
		super();
		this.caseId = caseId;
		this.cellName = cellName;
		this.content = content;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getCellName() {
		return cellName;
	}
	public void setCellName(String cellName) {
		this.cellName = cellName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
