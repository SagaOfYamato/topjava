<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru">

<head>
    <title>Add Meal</title>
</head>

Add Meal

<body>
<form action = "${pageContext.request.contextPath}/meals" method="post">
    <input required type="datetime-local" name="dateTime" placeholder="DateTime">

    <input required type="text" name="description" placeholder="Description">

    <input required type="text" name="calories" placeholder="Calories">
    <input type="submit" value="Save">

</form>
</body>
</html>
