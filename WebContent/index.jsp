<%@ page contentType="text/html; charset=utf-8" language="java"
	import="java.sql.*" errorPage=""%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>多线程下载</title>
</head>

<body>
	<div align="center">
		<div align="center" style="margin:20px;font-size: 20px;font-weight: bold;">多线程下载</div>
		<form id="login" action="download" method="post" onsubmit="return true;">
			<div>
				<div class="boxinner">
					<input style="width:80%;padding:10px;border-radius:10px;font-size: 14px;" type="text" name="downloadUrl" placeholder="请输入下载的url"
					 id="downloadUrl" />
				</div>
			</div>

			<button type="submit" style="margin:20px;width:150px;height:40px;padding:10px;border-radius:10px;font-size: 14px;" style="color:#ffffff;">下载</button>

		</form>

	</div>
</body>

</html>