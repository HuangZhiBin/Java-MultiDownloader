# Java-MultiDownloader
MultiDownloader多线程下载

![](http://wxtopik.oss-cn-shanghai.aliyuncs.com/app/images/kaka.png)

### 使用说明
- 1.配置DownloadConstants.java
> 一般只需只需配置MULTI_DOWNLOAD_PATH，即下载目录，其他参数可根据实际情况修改
```Java
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
```
- 2.开启web项目,打开首页http://localhost:8080/Multi-Downloader/index.jsp 输入下载链接即可

### 技术说明
- 1.开启tomcat环境的web项目后，会触发StartupListener，StartupListener初始化Singleton，由Singleton开启下载总线程，监听随时发过来的新下载请求。当检测到有新的下载请求，便会加入待下载队列进行下载。
- 2.断点下载的核心：在HTTP/1.1 中，很明确的声明了一个响应头部 Access-Ranges 来标记是否支持范围。请求对大型文件，开启多个线程，每个线程下载其中的某一段，最后下载完成之后，在本地拼接成一个完整的文件。