package com.dianbo.transfer.multidownload;


public abstract class LogUtils {
    
    public static void log(Object message) {
        if(DownloadConstants.IS_DEBUG)System.err.println(message);
    }
    
    public static void log(String message) {
    	if(DownloadConstants.IS_DEBUG)System.err.println(message);
    }
    
    public static void log(int message) {
    	if(DownloadConstants.IS_DEBUG)System.err.println(message);
    }
    
    public static void info(Object message) {
    	if(DownloadConstants.IS_DEBUG)System.out.println(message);
    }
    
    public static void info(String message) {
    	if(DownloadConstants.IS_DEBUG)System.out.println(message);
    }
    
    public static void info(int message) {
    	if(DownloadConstants.IS_DEBUG)System.out.println(message);
    }
}