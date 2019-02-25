<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://webLucene/jst/c"%>
<html>
<body>
	<form action="toSearchUI.do" method="post">
		关键字：<input type="text" name="key" value="${key}"> <input
			type="submit" value="搜索">
	</form>
	<table width="50%">
		<tr>
			<td>id</td>
			<td>title</td>
			<td>content</td>
		</tr>
		<c:if test="${contents != null}">
			<c:forEach items="${contents}" var="content">
				<tr>
					<td>${content.id}</td>
					<td>${content.title}</td>
					<td>${content.content}</td>
				</tr>
			</c:forEach>
		</c:if>
	</table>
</body>

</html>