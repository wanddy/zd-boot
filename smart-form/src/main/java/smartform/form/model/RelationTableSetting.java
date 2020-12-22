package smartform.form.model;

import java.io.Serializable;

public class RelationTableSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 父关联表表名
	 */
	private String table;

	/**
	 * lineId别名
	 */
	private String alias;

	/**
	 * 关联下拉控件标题
	 */
	private String selectTitle;

	/**
	 * SQL筛选条件
	 */
	private String where;
	
	/**
	* 是否自动删除数据
	*/
	private Boolean autoDel;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getSelectTitle() {
		return selectTitle;
	}

	public void setSelectTitle(String selectTitle) {
		this.selectTitle = selectTitle;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	@Override
	public String toString() {
		return "RelationTableSetting [table=" + table + ", alias=" + alias + ", selectTitle=" + selectTitle + ", where="
				+ where + ", autoDel=" + autoDel + "]";
	}

	public Boolean getAutoDel() {
		return autoDel;
	}

	public void setAutoDel(Boolean autoDel) {
		this.autoDel = autoDel;
	}
}
