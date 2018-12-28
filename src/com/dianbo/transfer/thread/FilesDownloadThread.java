package com.dianbo.transfer.thread;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import com.dianbo.transfer.multidownload.DownloadConstants;
import com.dianbo.transfer.multidownload.BatchDownloadThread;
import com.dianbo.transfer.multidownload.DownloadCallback;
import com.dianbo.transfer.multidownload.DownloadInfo;
import com.dianbo.transfer.multidownload.LogUtils;

public class FilesDownloadThread extends Thread {
	
	private CopyOnWriteArrayList<String> downloadUrls = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> downloadingUrls = new CopyOnWriteArrayList<String>();//包括下面的downloadingBigUrls
	private CopyOnWriteArrayList<String> downloadingBigUrls = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> downloadFailUrls = new CopyOnWriteArrayList<String>();
	
	public synchronized void addDownload(String downloadUrl){
		if(!downloadUrls.contains(downloadUrl)){
			downloadUrls.add(downloadUrl);
		}
	}
	
	private BufferedWriter outputForErrorUrl;
	private void addError2File(String url){
		if(outputForErrorUrl == null){
			File errorFile = new File(DownloadConstants.MULTI_DOWNLOAD_ERROR_PATH);
			
			if (errorFile.exists()) {
				System.out.print("文件存在");
			} else {
				System.out.print("文件不存在");
				try {
					errorFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}// 不存在则创建
			}
			
			try {
				outputForErrorUrl = new BufferedWriter(new FileWriter(errorFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			outputForErrorUrl.write(url+"\n");
			outputForErrorUrl.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		
		File downloadDirectory = new File(DownloadConstants.MULTI_DOWNLOAD_PATH);
		if(downloadDirectory.exists() == false){
			downloadDirectory.mkdir();
		}
		
		while(true){
			if(downloadUrls.size() > 0 && downloadingUrls.size() < DownloadConstants.MAX_DOWNLOAD_FILE_COUNT_THE_SAME_TIME){
				int downloadFileNum = DownloadConstants.MAX_DOWNLOAD_FILE_COUNT_THE_SAME_TIME - downloadingUrls.size();
				
				for(int index = 0; index < downloadFileNum; index++){
					
					if(index >= downloadUrls.size()){
						break;
					}
					
					String downloadUrl = downloadUrls.get(index);
					LogUtils.log("开始下载"+downloadUrl);
					
					downloadUrls.remove(downloadUrl);
					downloadingUrls.add(downloadUrl);
					
					DownloadInfo downloadInfo = new DownloadInfo(downloadUrl);
					BatchDownloadThread batchDownloadThread = new BatchDownloadThread(downloadInfo);
					batchDownloadThread.setCallback(new DownloadCallback() {
						
						@Override
						public boolean willStartDownloadFile(String url, long byteSize) {
							
							downloadingBigUrls.add(url);
							return true;
						}
						
						@Override
						public void finishDownload(String url, boolean downloadSuccess) {
							if(downloadSuccess == false){
								downloadFailUrls.add(url);
								addError2File(url);
							}
							
							downloadingUrls.remove(url);
							downloadingBigUrls.remove(url);
							
							LogUtils.log("结束下载" + downloadSuccess + " "+url);
						}
					});
					batchDownloadThread.start();
				}
			}
			
//			if(downloadUrls.size() == 0){
//				LogUtils.log("当前没有需要下载的文件");
//			}
			
			LogUtils.log("FilesDownloadThread is running " + hashCode());
			
			try {
				sleep(DownloadConstants.DOWNLOAD_THREAD_SLEEPING_MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
