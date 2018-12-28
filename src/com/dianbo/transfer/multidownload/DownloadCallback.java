package com.dianbo.transfer.multidownload;

public interface DownloadCallback {
	public boolean willStartDownloadFile(String url, long biteSize);
	public void finishDownload(String url, boolean downloadSuccess);
}
