<%@ page import="java.util.List" %>
<%@ page import="ru.javawebinar.topjava.model.MealTo" %>
<%@ page import="ru.javawebinar.topjava.util.MealsUtil" %>
<%@ page import="java.time.LocalTime" %>
<%@ page import="ru.javawebinar.topjava.model.ModelSingletone" %>
<%@ page import="ru.javawebinar.topjava.model.Meal" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE HTML>
<html lang="ru">
<head>
    <title>Meals</title>
</head>

<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>

<ul>
    <a href="${pageContext.request.contextPath}/addMeal.jsp">AddMeal</a>
</ul>

<h1>
    <%
        List<Meal> meals = (List<Meal>) request.getAttribute("meals");
        List<MealTo> mealsTo = MealsUtil.filteredByStreams(meals, LocalTime.of(0, 0), LocalTime.of(23, 59), ModelSingletone.CALORIES_PER_DAY);
        request.setAttribute("mealsTo", mealsTo);
    %>

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
        <td>${mealTo.date}${" "}${mealTo.time}</td>
        <td>${mealTo.description}</td>
        <td>${mealTo.calories}</td>
            <td><a href="${pageContext.request.contextPath}/updateMeal.jsp">Update</a></td>
            <td><a href="${pageContext.request.contextPath}/deleteMeal.jsp">Delete</a></td>
        <tr>
    </c:forEach>
</table>
</h1>

</body>
</html>
