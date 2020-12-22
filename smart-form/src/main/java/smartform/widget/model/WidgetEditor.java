package smartform.widget.model;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: WidgetEditor
 * @Description: 富文本框
 * @author hou
 * @date 2018年9月16日 下午15:00:00
 * 
 */
public class WidgetEditor extends WidgetBase implements Serializable {


	private static final long serialVersionUID = 1L;

	/**
	 * 默认值
	 */
	private String defValue;
	/**
	 * 开启图片上传
	 */
	private Boolean imgUpload;
	/**
	 * 上传大小限定(kb)
	 */
	private Integer imgSize;
	/**
	 * 上传格式限定
	 */
	private String imgSuffix;
	/**
	 * 开启视频上传
	 */
	private Boolean videoUpload;
	/**
	 * 视频上传大小(kb)
	 */
	private Integer videoSize;
	/**
	 * 视频上传格式
	 */
	private String videoSuffix;
	
	/** 
	* @Fields defHeight : 富文本默认高度 
	*/ 
	private Integer defHeight;
	
	/** 
	* @Fields imgLimit : 图片数量限制
	*/ 
	private String imgLimit;
	
	/** 
	* @Fields toolbarButtons : 富文本工具栏列表数据
	*/ 
	private List<Object> toolbarButtons;
	
	/** 
	* @Fields toolbarType : 富文本工具栏列表状态 0基础 1全部 2自定义
	*/ 
	private Integer toolbarType;
	
	/** 
	* @Fields isTextIndent : 首行缩进
	*/ 
	private Boolean isTextIndent;
	
	
    public String getDefValue() {
		/*if(null!=super.getDefValue()){
			defValue = (String) super.getDefValue();
			super.setDefValue(null);
		}*/
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	public Boolean getImgUpload() {
		return imgUpload;
	}

	public void setImgUpload(Boolean imgUpload) {
		this.imgUpload = imgUpload;
	}

	public Integer getImgSize() {
		return imgSize;
	}

	public void setImgSize(Integer imgSize) {
		this.imgSize = imgSize;
	}

	public String getImgSuffix() {
		return imgSuffix;
	}

	public void setImgSuffix(String imgSuffix) {
		this.imgSuffix = imgSuffix;
	}

	public Boolean getVideoUpload() {
		return videoUpload;
	}

	public void setVideoUpload(Boolean videoUpload) {
		this.videoUpload = videoUpload;
	}

	public Integer getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(Integer videoSize) {
		this.videoSize = videoSize;
	}

	public String getVideoSuffix() {
		return videoSuffix;
	}

	public void setVideoSuffix(String videoSuffix) {
		this.videoSuffix = videoSuffix;
	}

	public Integer getDefHeight() {
		return defHeight;
	}

	public void setDefHeight(Integer defHeight) {
		this.defHeight = defHeight;
	}

	public String getImgLimit() {
		return imgLimit;
	}

	public void setImgLimit(String imgLimit) {
		this.imgLimit = imgLimit;
	}

	public List<Object> getToolbarButtons() {
		return toolbarButtons;
	}

	public void setToolbarButtons(List<Object> toolbarButtons) {
		this.toolbarButtons = toolbarButtons;
	}

	public Integer getToolbarType() {
		return toolbarType;
	}

	public void setToolbarType(Integer toolbarType) {
		this.toolbarType = toolbarType;
	}

	public Boolean getIsTextIndent() {
		return isTextIndent;
	}

	public void setIsTextIndent(Boolean isTextIndent) {
		this.isTextIndent = isTextIndent;
	}

	@Override
	public String toString() {
		return "WidgetEditor [defValue=" + defValue + ", imgUpload=" + imgUpload + ", imgSize=" + imgSize
				+ ", imgSuffix=" + imgSuffix + ", videoUpload=" + videoUpload + ", videoSize=" + videoSize
				+ ", videoSuffix=" + videoSuffix + ", defHeight=" + defHeight + ", imgLimit=" + imgLimit
				+ ", toolbarButtons=" + toolbarButtons + ", toolbarType=" + toolbarType + ", isTextIndent=" + isTextIndent + "]";
	}

}