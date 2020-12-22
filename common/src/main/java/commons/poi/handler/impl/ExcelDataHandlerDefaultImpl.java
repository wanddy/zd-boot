package commons.poi.handler.impl;

import java.util.Map;

import commons.poi.handler.inter.IExcelDataHandler;

/**
 * 数据处理默认实现,返回空
 *
 * @author JEECG
 * @date 2014年6月20日 上午12:11:52
 */
public abstract class ExcelDataHandlerDefaultImpl implements IExcelDataHandler {
	/**
	 * 需要处理的字段
	 */
	private String[] needHandlerFields;

	@Override
	public Object exportHandler(Object obj, String name, Object value) {
		return value;
	}

	@Override
	public String[] getNeedHandlerFields() {
		return needHandlerFields;
	}

	@Override
	public Object importHandler(Object obj, String name, Object value) {
		return value;
	}

	@Override
	public void setNeedHandlerFields(String[] needHandlerFields) {
		this.needHandlerFields = needHandlerFields;
	}

	@Override
	public void setMapValue(Map<String, Object> map, String originKey, Object value) {
		map.put(originKey, value);
	}

}
