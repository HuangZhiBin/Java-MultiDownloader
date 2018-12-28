package com.dianbo.transfer.multidownload;

//import java.io.DataInputStream;
//import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
 
public class BatchDownloadThread extends Thread{
    //下载文件信息 
    private DownloadInfo downloadInfo;
    //一组开始下载位置
    private long[] startPos;
    //一组结束下载位置
    private long[] endPos;
    //休眠时间
    //private static final int SLEEP_SECONDS = 500;
    //子线程下载
    private DownloadFileThread[] fileItem;
    //文件长度
    private int length;
    //是否停止下载
    private boolean shouldStopDownload = false;
    //临时文件信息
//    private File tempFile;
    
    private DownloadCallback callback;
    
    public DownloadInfo getDownloadInfo() {
		return downloadInfo;
	}

	public void setDownloadInfo(DownloadInfo downloadInfo) {
		this.downloadInfo = downloadInfo;
	}

	public void setCallback(DownloadCallback callback) {
		this.callback = callback;
	}

	public BatchDownloadThread(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;   
    }
    
	@Override
    public void run() {
        //首次下载，获取下载文件长度
		length = this.getFileSize();//获取文件长度
		
		int retryTimes = 0;
		final int MAX_RETRY_TIMES = 3;
		while(length <= 0 && retryTimes < MAX_RETRY_TIMES){
			length = this.getFileSize();
			retryTimes++;
		}
		
        if (length == -1) {
            LogUtils.log("file length is not found!");
            shouldStopDownload = true;
        } else if (length == -2) {
            LogUtils.log("read file length is error!");
            shouldStopDownload = true;
        } else if (length > 0) {
        	boolean willContinueDownload = callback.willStartDownloadFile(downloadInfo.getUrl(), length);
        	
        	if(willContinueDownload == false){
        		shouldStopDownload = true;
            	return;
        	}
        	
        	int blockNum = length / DownloadConstants.DOWNLOAD_BLOCK_SIZE_EACH_PIECE;
        	if(length % DownloadConstants.DOWNLOAD_BLOCK_SIZE_EACH_PIECE > 0){
        		blockNum++;
        	}
        	
        	//数组的长度就要分成多少段的数量
            startPos = new long[blockNum];
            endPos = new long[blockNum];
        	
            for (int blockIndex = 0; blockIndex < blockNum; blockIndex++) {
                int size = blockIndex * DownloadConstants.DOWNLOAD_BLOCK_SIZE_EACH_PIECE;
                startPos[blockIndex] = size;
                
                //设置最后一个结束点的位置
                if (blockIndex == blockNum - 1) {
                    endPos[blockIndex] = length;
                } else {
                    size = (blockIndex + 1) * DownloadConstants.DOWNLOAD_BLOCK_SIZE_EACH_PIECE;
                    endPos[blockIndex] = size;
                }
                LogUtils.log("start-end Position[" + blockIndex + "]: " + startPos[blockIndex] + "-" + endPos[blockIndex]);
            }
        } else {
            LogUtils.log("get file length is error, download is stop!");
            shouldStopDownload = true;
        }
        
        //子线程开始下载
        if (!shouldStopDownload) {
            //创建单线程下载对象数组
            fileItem = new DownloadFileThread[startPos.length];//startPos.length = downloadInfo.getSplitter()
            
            int downloadSuccessCount = 0;
            do{
            	downloadSuccessCount = 0;
            	int currentDownloadThreadCount = 0;
            	for (int index = 0; index < startPos.length; index++) {
            		DownloadFileThread downloadFileThread = fileItem[index];
            		if(downloadFileThread != null){
            			if(downloadFileThread.getDownloadState() == DownloadConstants.DOWNLOAD_FILE_STATE_DOWNLOADING){
                        	currentDownloadThreadCount++;
                        }
                        else if(downloadFileThread.getDownloadState() == DownloadConstants.DOWNLOAD_FILE_STATE_SUCCESS){
                        	downloadSuccessCount++;
                        }
            		}
                }
            	
            	for (int index = 0; index < startPos.length; index++) {
            		DownloadFileThread downloadFileThread = fileItem[index];
            		if(downloadFileThread == null || downloadFileThread.getDownloadState() == DownloadConstants.DOWNLOAD_FILE_STATE_PENDING || downloadFileThread.getDownloadState() == DownloadConstants.DOWNLOAD_FILE_STATE_FAIL){
            			if(currentDownloadThreadCount < DownloadConstants.MAX_THREAD_DOWNLOAD_COUNT_EACH_FILE){
//            				if(downloadFileThread != null){
//            					fileItem[index].closeWriteFile();
//            				}
            				
            				startDownloadFileItem(index);
    						currentDownloadThreadCount++;
        				}
        			}
            	}
            	
            	try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
            while(downloadSuccessCount < startPos.length);
            
            LogUtils.log("下载所有子线程结束");
        }
        else{
        	callback.finishDownload(this.downloadInfo.getUrl(),false);
        }
    }
	
	private int downloadSuccessNum = 0;
	
	private synchronized void addDownloadSuccessNum(){
		downloadSuccessNum++;
		
		LogUtils.log("文件下载进度 " + downloadSuccessNum + "/" + startPos.length);
		
		if(downloadSuccessNum == startPos.length){
			callback.finishDownload(downloadInfo.getUrl(), true);
		}
	}
	
	//开始或者重新下载子文件
	private void startDownloadFileItem(int index){
		//创建指定个数单线程下载对象，每个线程独立完成指定块内容的下载
    	DownloadFileThread downloadFileThread = new DownloadFileThread(
                downloadInfo.getUrl(), 
                DownloadConstants.MULTI_DOWNLOAD_PATH/* + File.separator */ + downloadInfo.getFileName(), 
                startPos[index], endPos[index], index
            );
    	downloadFileThread.setCallback(new DownloadItemCallback() {
			@Override
			public void finishItemDownload(int itemIndex, boolean downloadSuccess, long lastDownloadPos) {
				// TODO Auto-generated method stub
				if(downloadSuccess){
//					downloadSuccessNum++;
					addDownloadSuccessNum();
					
					
				}
				else{
					//重试下载
					startPos[itemIndex] = lastDownloadPos;
					LogUtils.log("Thread"+itemIndex+"下载失败，即将重试下载");
				}
			}
		});
        fileItem[index] = downloadFileThread;
        
        try {
        	fileItem[index].start();//启动线程，开始下载
		} catch (Exception e) {
			LogUtils.log("线程启动失败");
			downloadFileThread.closeWriteFile();
		}
        
        //LogUtils.log("Thread: " + index + ", startPos: " + startPos[index] + ", endPos: " + endPos[index]);
	}
    
    public void stopAllThreads(){
    	for (int i = 0; i < startPos.length; i++) {
    		if(fileItem != null && fileItem[i] != null ){
    			fileItem[i].setForceStop(true);
    		}
    	}
    }
    
    /**
     * <b>function:</b> 获取下载文件的长度
     * @author hoojo
     * @createDate 2011-9-26 下午12:15:08
     * @return
     */
    private int getFileSize() {
        int fileLength = -1;
        try {
            URL url = new URL(this.downloadInfo.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            //System.out.println("url="+this.downloadInfo.getUrl());
            conn.setConnectTimeout(DownloadConstants.DOWNLOAD_WAITING_TIMEOUT);
            conn.setReadTimeout(DownloadConstants.DOWNLOAD_WAITING_TIMEOUT);
            DownloadFileThread.setHeader(conn);
 
            int stateCode = conn.getResponseCode();
            //判断http status是否为HTTP/1.1 206 Partial Content或者200 OK
            if (stateCode != HttpURLConnection.HTTP_OK && stateCode != HttpURLConnection.HTTP_PARTIAL) {
                LogUtils.log("Error Code: " + stateCode);
                return -2;
            }
            else {
                //获取长度
                fileLength = conn.getContentLength();
                LogUtils.log("FileLength: " + fileLength);
            }
            
            //DownloadFileThread.printHeader(conn);
        } catch (MalformedURLException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
            LogUtils.log("找不到网络文件");
        }
        return fileLength;
    }
}