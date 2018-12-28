package com.dianbo.transfer.multidownload;

public interface DownloadItemCallback {
	public void finishItemDownload(int itemIndex, boolean downloadSuccess, long lastDownloadPos);
}
