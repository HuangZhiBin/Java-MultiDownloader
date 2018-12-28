package com.dianbo.transfer.multidownload;

public class DownloadConstants {
	public static final boolean IS_DEBUG = true;//是否输出与下载相关的sysout log
	
	public static final String MULTI_DOWNLOAD_PATH = "/Users/bin/Desktop/downloading/";//下载目录
	public static final String MULTI_DOWNLOAD_ERROR_PATH = MULTI_DOWNLOAD_PATH + "DownloadErrorUrls.log";//记录所有下载失败的url
	
	public static final int DOWNLOAD_FILE_STATE_PENDING = 0;//等待下载
	public static final int DOWNLOAD_FILE_STATE_DOWNLOADING = 1;//下载中
	public static final int DOWNLOAD_FILE_STATE_FAIL = 2;//下载失败
	public static final int DOWNLOAD_FILE_STATE_SUCCESS = 3;//下载成功
	
	public static final int DOWNLOAD_THREAD_SLEEPING_MILLISECONDS = 1000;//线程检测新下载任务的时间间隔
	public static final int MAX_THREAD_DOWNLOAD_COUNT_EACH_FILE = 5;//每个文件最多5个进程在同时下载
	public static final int DOWNLOAD_BLOCK_SIZE_EACH_PIECE = 1 * 128 * 1024;//下载分块大小
	
	public static final int DOWNLOAD_WAITING_TIMEOUT = 5 * 1000;//下载超时时间,5s
	
	public static final int MAX_DOWNLOAD_FILE_COUNT_THE_SAME_TIME = 5;//最多同时下载5个文件

}
