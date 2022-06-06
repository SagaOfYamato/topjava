<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru">

<head>
    <title>Edit Meal</title>
</head>
<body>

Edit Meal

<form action="${pageContext.request.contextPath}/meals/${param.id}" method="post">
    <input type="hidden" name = "id" value="${param.id}">
    <input type="datetime-local" name="dateTime" value="${param.dateTime}" placeholder=${param.dateTime}>
    <input type="text" name="description" value="${param.description}" placeholder=${param.description}>
    <input type="text" name="calories" value="${param.calories}" placeholder=${param.calories}>
    <input type="hidden" name="_method" value="put">
    <input type="submit" value="Save">
    <input type="submit" value="Cancel">
</form>

</body>
</html>
