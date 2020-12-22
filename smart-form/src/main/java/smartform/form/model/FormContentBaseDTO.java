package smartform.form.model;

import smartform.query.Query;

import java.util.List;

/** 
* @ClassName: FormContentBaseDTO 
* @Description: 用于数据存储传输
* @author quhanlin
* @date 2018年11月7日 下午7:30:07 
*  
*/
public class FormContentBaseDTO<T> {
	
	/**
	 * 要存储的具体数据
	 */
	private T data;
	
	/**
	 * 数据库名
	 */
	private String dbName;
	
	/**
	 * 数据存储到的表名
	 */
	private String dbTable;
	
	/**
	 * 数据存储的业务类型，用于区分同名表的组件
	 */
	private Integer workType;
	
	/**
	 * 行号类型存储
	 */
	private Integer lineNum;
	
	/**
	 * 上传类型存储
	 */
	private Integer uploadType;
	
	/**
	 * 自定义字段的数据
	 */
	private Query content;
	
	/**
	 * 用于存储查询到的数据
	 */
	private Object queryData;
	
	/**
	 * 上下文信息列表，目前用于复杂规则上下文判定
	 */
	private List<Object> contexts;

	@Override
	public String toString() {
		return "FormContentBaseDTO [data=" + data + ", dbName=" + dbName + ", dbTable=" + dbTable + ", workType="
				+ workType + ", lineNum=" + lineNum + ", uploadType=" + uploadType + ", content=" + content
				+ ", queryData=" + queryData + ", contexts=" + contexts + "]";
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getDbTable() {
		return dbTable;
	}

	public void setDbTable(String dbTable) {
		this.dbTable = dbTable;
	}

	public Query getContent() {
		return content;
	}

	public void setContent(Query content) {
		this.content = content;
	}

	public Integer getWorkType() {
		return workType;
	}

	public void setWorkType(Integer workType) {
		this.workType = workType;
	}

	public Integer getLineNum() {
		return lineNum;
	}

	public void setLineNum(Integer lineNum) {
		this.lineNum = lineNum;
	}

	public Integer getUploadType() {
		return uploadType;
	}

	public void setUploadType(Integer uploadType) {
		this.uploadType = uploadType;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public Object getQueryData() {
		return queryData;
	}

	public void setQueryData(Object queryData) {
		this.queryData = queryData;
	}

	public List<Object> getContexts() {
		return contexts;
	}

	public void setContexts(List<Object> contexts) {
		this.contexts = contexts;
	}
}
