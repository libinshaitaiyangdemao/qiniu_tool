package com.bingo.qiniu.model;

public class QUploadSubjectModel {

	public static final int UPLOAD_STATUS_PRE = 0;

	public static final int UPLOAD_STATUS_ING = 1;

	public static final int UPLOAD_STATUS_FINISH_SUCCESS = 2;

	public static final int UPLOAD_STATUS_FINISH_FAILED = 3;

	private float persent;

	private String fileSize;

	private String location;

	private String url;

	private int status;

	public float getPersent() {
		return persent;
	}

	public void setPersent(float persent) {
		this.persent = persent;
	}

	public String persentText() {
		return String.format("%.2f", persent * 100) + "%";
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getLocationUrl() {
		return location;
	}

	public void setLocationUrl(String location) {
		this.location = location;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
