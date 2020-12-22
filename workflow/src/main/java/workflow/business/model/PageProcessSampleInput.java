package workflow.business.model;

public class PageProcessSampleInput {

	private String name;
	
	private boolean onlyLatestVersion;
	
	private int pageNum;
	
	private int pageSize;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOnlyLatestVersion() {
		return onlyLatestVersion;
	}

	public void setOnlyLatestVersion(boolean onlyLatestVersion) {
		this.onlyLatestVersion = onlyLatestVersion;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
	
}
