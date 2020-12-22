package smartform.widget.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: WidgetUpload
 * @Description: 上传控件
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgetUpload extends WidgetBase implements Serializable {


	private static final long serialVersionUID = 1L;

	/**
	 * 上传内容列表
	 */
	private List<UploadItem> defValue;
	
	/**
	 * 上传文件类型，用于业务标记
	 */
	private Integer uploadType;
	/**
	 * 最小上传数量
	 */
	private Integer minCount;
	/**
	 * 最大上传数量
	 */
	private Integer maxCount;
	/**
	 * 上传大小限定(kb)
	 */
	private Integer size;
	/**
	 * 上传格式限定
	 */
	private String suffixd;

	public Integer getMinCount() {
		return minCount;
	}

	public void setMinCount(Integer minCount) {
		this.minCount = minCount;
	}

	public Integer getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(Integer maxCount) {
		this.maxCount = maxCount;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getSuffixd() {
		return suffixd;
	}

	public void setSuffixd(String suffixd) {
		this.suffixd = suffixd;
	}

    public List<UploadItem> getDefValue() {
		return defValue;
	}

	public void setDefValue(List<UploadItem> defValue) {
		this.defValue = defValue;
	}
	@Override
	public String toString() {
		return "WidgetUpload [defValue=" + defValue + ", uploadType=" + uploadType + ", minCount=" + minCount
				+ ", maxCount=" + maxCount + ", size=" + size + ", suffixd=" + suffixd + "]";
	}

	public Integer getUploadType() {
		return uploadType;
	}

	public void setUploadType(Integer uploadType) {
		this.uploadType = uploadType;
	}

}