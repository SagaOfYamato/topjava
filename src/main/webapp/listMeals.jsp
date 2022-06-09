<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html lang="ru">
<head>
    <title>Meals</title>
</head>

<body>
<h3><a href="index.html">Home</a></h3>

<h2>Meals</h2>

<ul>
    <a href="meals?action=add">AddMeal</a>
</ul>


    <table border="1" cellspacing="0" cellpadding="0">
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th></th>
        <th></th>
    </tr>

    <c:forEach var="mealTo" items="${mealsTo}">
        <tr style="color:${mealTo.excess ? 'red' : 'green'}">
        <td>${mealTo.date} ${mealTo.time}</td>
        <td>${mealTo.description}</td>
        <td>${mealTo.calories}</td>
            <td><a href="meals?action=edit&id=<c:out value = "${mealTo.id}"/>">Update</a></td>
            <td><a href="meals?action=delete&id=<c:out value = "${mealTo.id}"/>">Delete</a></td>
        <tr>
    </c:forEach>
</table>


</body>
</html>
