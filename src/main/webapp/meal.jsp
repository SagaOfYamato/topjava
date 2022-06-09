<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
    <title>Add new user</title>
</head>
<body>

<h1> Add or update meal </h1>

<form method="POST" action="<c:url value="/meals"/>" name="frmAddUser">
    DateTime : <input
        type="datetime-local" name="dateTime"
        value="<c:out value="${meal.dateTime}" />" /> <br />
    Discription : <input
        type="text" name="description"
        value="<c:out value="${meal.description}" />" /> <br />
    Calories : <input
        type="number" name="calories"
        value="<c:out value="${meal.calories}" />" /> <br />
        <input type="submit" value="Submit" name="subm"/>
    <input type="submit" value="Cancel" name="canc"/>
</form>
</body>
</html>
