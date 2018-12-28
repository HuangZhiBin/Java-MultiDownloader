package com.dianbo.transfer.multidownload;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
 
/**
 * 单线程下载
 */
public class DownloadFileThread extends Thread {
    
    //下载文件url
    private String url;
    //下载文件起始位置  
    private long startPos;
    //下载文件结束位置
    private long endPos;
    //线程id
    private int threadId;
    
    private String name;
    
    //下载状态
    private int downloadState = DownloadConstants.DOWNLOAD_FILE_STATE_PENDING;
 
    public int getDownloadState() {
		return downloadState;
	}

    //存储文件
    private RandomAccessFile randomAccessFile;
    
    private static final int BUFF_LENGTH = 1024;
    
    private DownloadItemCallback callback;
    
    public DownloadItemCallback getCallback() {
		return callback;
	}

	public void setCallback(DownloadItemCallback callback) {
		this.callback = callback;
	}

	/**
     * @param url 下载文件url
     * @param name 文件名称
     * @param startPos 下载文件起点
     * @param endPos 下载文件结束点
     * @param threadId 线程id
     * @throws IOException
     */
    public DownloadFileThread(String url, String name, long startPos, long endPos, int threadId) {
        super();
        this.downloadState = DownloadConstants.DOWNLOAD_FILE_STATE_PENDING;
        this.url = url;
        this.startPos = startPos;
        this.endPos = endPos;
        this.threadId = threadId;
        this.name = name;
    }
 
    private boolean forceStop;
    
    public void setForceStop(boolean forceStop) {
		this.forceStop = forceStop;
	}

	@Override
    public void run() {
		this.downloadState = DownloadConstants.DOWNLOAD_FILE_STATE_DOWNLOADING;
        if (endPos > startPos) {
            
        	long lastDownloadPos = startPos;
        	
        	//获取文件输入流，读取文件内容
            InputStream is = null;
            boolean downloadSuccess = false;
            
            try {
            	//注意，必须写在run里面，否则下载的视频播放有问题
            	//分块下载写入文件内容
                randomAccessFile = new RandomAccessFile(name, "rwd");
                //在指定的pos位置开始写入数据
            	randomAccessFile.seek(startPos);
            	
            	URL url = new URL(this.url);
            	HttpURLConnection conn = (HttpURLConnection) url.openConnection();	
				conn.setRequestMethod("GET");
             // 设置连接超时时间为10000ms
                conn.setConnectTimeout(DownloadConstants.DOWNLOAD_WAITING_TIMEOUT);
                // 设置读取数据超时时间为10000ms
                conn.setReadTimeout(DownloadConstants.DOWNLOAD_WAITING_TIMEOUT);
                
                setHeader(conn);
                
                String property = "bytes=" + startPos + "-"+endPos;
                conn.setRequestProperty("RANGE", property);
                
                //输出log信息
                //LogUtils.log("开始 " + threadId + "：" + property + endPos);
                //printHeader(conn);
            	is = conn.getInputStream();
            	
                byte[] buff = new byte[BUFF_LENGTH];
                int length = -1;
                LogUtils.log(">>>>>start Thread: " + threadId + ", startPos: " + startPos + ", endPos: " + endPos);
                while (!forceStop && (length = is.read(buff, 0, BUFF_LENGTH)) != -1 && lastDownloadPos < endPos) {
                	lastDownloadPos += length;
                	randomAccessFile.write(buff, 0, length);
                    buff = new byte[BUFF_LENGTH];
                }
                LogUtils.log("<<<<<< end Thread: " + threadId + " lastDownloadPos="+lastDownloadPos + " endPos="+endPos);
                downloadState = DownloadConstants.DOWNLOAD_FILE_STATE_SUCCESS;
                
                downloadSuccess = true;
            } catch (Exception e) {
                //e.printStackTrace();
            	LogUtils.log("下载失败Thread: " + threadId);
                forceStop = true;
                downloadState = DownloadConstants.DOWNLOAD_FILE_STATE_FAIL;
                
            } finally {
                try {
                    if (randomAccessFile != null) {
                    	randomAccessFile.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                    //conn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                callback.finishItemDownload(this.threadId, downloadSuccess, lastDownloadPos);
            }
            
            
        }
    }
	
	public void closeWriteFile(){
		if(randomAccessFile != null){
			try {
				randomAccessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
    
    /**
     * <b>function:</b> 设置URLConnection的头部信息，伪装请求信息
     * @author hoojo
     * @createDate 2011-9-28 下午05:29:43
     * @param con
     */
    public static void setHeader(URLConnection conn) {
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
        conn.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
        conn.setRequestProperty("Accept-Encoding", "utf-8");
        conn.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        conn.setRequestProperty("Keep-Alive", "300");
        conn.setRequestProperty("connnection", "keep-alive");
        conn.setRequestProperty("If-Modified-Since", "Fri, 02 Jan 2009 17:00:05 GMT");
        conn.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
        conn.setRequestProperty("Cache-conntrol", "max-age=0");
        conn.setRequestProperty("Referer", "http://www.baidu.com");
    }
    
    public long getStartPos() {
        return startPos;
    }
 
    public long getEndPos() {
        return endPos;
    }
}