package com.dianbo.transfer.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dianbo.transfer.Singleton;

public class StartupListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("web app context is initialized =>");
		
		Singleton.getInstance();//借助单例启动下载线程监听下载请求
	}

}
