package com.dianbo.transfer.multidownload;

public class DownloadInfo {
	//下载文件url
    private String url;
    //下载文件名称
    private String fileName;
    
    public DownloadInfo() {
        super();
    }
    
    /**
     * @param url 下载地址
     */
    public DownloadInfo(String url) {
        this(url, null);
    }
    
    /**
     * @param url 下载地址url
     * @param splitter 分成多少段或是多少个线程下载
     */
    public DownloadInfo(String url, int splitter) {
        this(url, null);
    }
    
    /***
     * @param url 下载地址
     * @param fileName 文件名称
     */
    public DownloadInfo(String url, String fileName) {
        super();
        if (url == null || "".equals(url)) {
            throw new RuntimeException("url is not null!");
        }
        this.url =  url;
        this.fileName = (fileName == null || "".equals(fileName)) ? getFileName(url) : fileName;
    }
    
    /**
     * <b>function:</b> 通过url获得文件名称
     * @param url
     * @return
     */
    private String getFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1, url.length());
    }
    
    public String getUrl() {
        return url;
    }
 
    public void setUrl(String url) {
        if (url == null || "".equals(url)) {
            throw new RuntimeException("url is not null!");
        }
        this.url = url;
    }
 
    public String getFileName() {
        return fileName;
    }
 
    public void setFileName(String fileName) {
        this.fileName = (fileName == null || "".equals(fileName)) ? getFileName(url) : fileName;
    }
}
