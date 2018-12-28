package com.dianbo.transfer;

import com.dianbo.transfer.thread.FilesDownloadThread;

public class Singleton {
	
	private static Singleton instance;
	
	private FilesDownloadThread filesDownloadThread;
	
	public FilesDownloadThread getFilesDownloadThread() {
		return filesDownloadThread;
	}

	private Singleton() {
		
		filesDownloadThread = new FilesDownloadThread();
		filesDownloadThread.start();
	}

	public static Singleton getInstance() {
		if (instance == null) {
			instance = new Singleton();
		}
		else{
			if(instance.filesDownloadThread.isInterrupted() || (instance.filesDownloadThread.isAlive() == false)){
				System.out.println("filesDownloadThread已失效，重新启动");
				instance.filesDownloadThread = null;
				instance.filesDownloadThread = new FilesDownloadThread();
				instance.filesDownloadThread.start();
			}
		}
		return instance;
	}
}
