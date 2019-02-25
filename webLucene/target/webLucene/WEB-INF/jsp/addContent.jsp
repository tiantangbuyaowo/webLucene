<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<body>
	<form action="addContent.do" method="post">
		名称：<input type="text" name="title"><br /> 内容：
		<textarea rows="5" cols="20" name="content"></textarea>
		<input type="submit" value="提交">
	</form>
	<a href="toSearchUI.do">搜索</a>
</body>

</html>