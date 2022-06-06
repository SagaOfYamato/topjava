<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru">

<head>
    <title>Удалить еду</title>
</head>
<body>

Вы действительно хотите удалить еду ${param.id}?

<form action="${pageContext.request.contextPath}/meals/${param.id}" method="post">
<input type="hidden" name="id" value="${param.id}">
<input type="hidden" name="_method" value="delete">
<input type="submit" value="Удалить">
</form>

</body>
</html>
