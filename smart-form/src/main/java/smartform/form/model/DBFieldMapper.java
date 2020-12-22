package smartform.form.model;

import java.io.Serializable;

/** 
* @ClassName: DBFieldMapper 
* @Description: 表单内容表字段映射，用于内容填报时，映射到主表的数据配置
* @author quhanlin
* @date 2018年10月30日 下午6:18:15 
*  
*/
public class DBFieldMapper implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	private String id;

	/**
	 * 对应字段的数据类型：1，字符串；2，Long 
	 */
	private Integer dataType;
	
	/**
	 * 此数据是否必填
	 */
	private Boolean required;

	/**
	 * 映射类型：1，参数；2，字段别名（表名.别名）；3，组件状态关系表；4，上传列表
	 */
	private Integer mapperType;

	/**
	 * 参数名/别名/关系表模式为空
	 */
	private String alias;

	/**
	 * 数据库主表别名/关系表名
	 */
	private String dbAlias;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getMapperType() {
		return mapperType;
	}

	public void setMapperType(Integer mapperType) {
		this.mapperType = mapperType;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDbAlias() {
		return dbAlias;
	}

	public void setDbAlias(String dbAlias) {
		this.dbAlias = dbAlias;
	}

	@Override
	public String toString() {
		return "DBFieldMapper [id=" + id + ", dataType=" + dataType + ", required=" + required + ", mapperType="
				+ mapperType + ", alias=" + alias + ", dbAlias=" + dbAlias + "]";
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}
}
