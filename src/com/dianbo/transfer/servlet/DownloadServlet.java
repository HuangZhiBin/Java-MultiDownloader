package com.dianbo.transfer.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dianbo.transfer.Singleton;

public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doGet");

		//doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("doPost");
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");

		String downloadUrl = (String) request.getParameter("downloadUrl");
		Singleton.getInstance().getFilesDownloadThread().addDownload(downloadUrl);

		print(response, "success, download task is added");
	}

	public static void print(HttpServletResponse response, String str) {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out;
		try {
			out = response.getWriter();
			out.print(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
