package com.dianbo.transfer.multidownload;

public class DownloadConstants {
	public static final boolean IS_DEBUG = true;
	
	public static final String MULTI_DOWNLOAD_PATH = "/Users/bin/Desktop/downloading/";
	public static final String MULTI_DOWNLOAD_ERROR_PATH = MULTI_DOWNLOAD_PATH + "DownloadErrorUrls.log";
//	public static final String MULTI_DOWNLOAD_BIG_PATH = MULTI_DOWNLOAD_PATH + "DownloadSuperBigUrls.txt";
	
	public static final int DOWNLOAD_FILE_STATE_PENDING = 0;
	public static final int DOWNLOAD_FILE_STATE_DOWNLOADING = 1;
	public static final int DOWNLOAD_FILE_STATE_FAIL = 2;
	public static final int DOWNLOAD_FILE_STATE_SUCCESS = 3;
	
	public static final int DOWNLOAD_THREAD_SLEEPING_MILLISECONDS = 1000;
	public static final int MAX_THREAD_DOWNLOAD_COUNT_EACH_FILE = 5;//每个文件最多5个进程在同时下载
	public static final int DOWNLOAD_BLOCK_SIZE_EACH_PIECE = 1 * 128 * 1024;//下载分块大小
	
	public static final int DOWNLOAD_WAITING_TIMEOUT = 5 * 1000;//下载超时时间
	
	public static final int MAX_DOWNLOAD_FILE_COUNT_THE_SAME_TIME = 5;//最多同时下载5个文件

}
