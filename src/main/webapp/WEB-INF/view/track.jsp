<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<html>
<head>
    <link href='http://fonts.googleapis.com/css?family=Bitter' rel='stylesheet' type='text/css'>
    <link href="<c:url value="/resources/css/main.css"/>" rel="stylesheet">
    <title>Track</title>
</head>
<body>

<c:url var="addUrl" value="/image/add"/>

<form:form class="desktop" modelAttribute="track" method="POST" action="${saveUrl}" enctype="multipart/form-data">

    <div class="section">
        <c:choose>
            <c:when test="${type=='edit'}">
                <c:url var="saveUrl" value="/track/edit?id=${track.id}"/>
                Track
            </c:when>
            <c:when test="${type=='add'}">
                <c:url var="saveUrl" value="/track/add"/>
                new Track
            </c:when>
        </c:choose>
    </div>

    <div class="form-elements">
        <form:label path="name">
            Name
            <form:input type="text" path="name"/>
        </form:label>
    </div>

    <div>
        <div class="file">
            <input type="file" name="file" accept=".gpx">
        </div>
        <input type="submit" value="Save"/>
    </div>

    <div class="menu">
        <a href="${addUrl}">Add image</a>
    </div>

    <table class="list">
        <thead>
        <tr>
            <td>Name</td>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${images}" var="image">
            <c:url var="downloadUrl" value="/image/download?id=${track.id}"/>
            <c:url var="deleteUrl" value="/image/delete?id=${track.id}"/>
            <tr>
                <td><c:out value="${track.name}"/></td>
                <td class="button"><a href="${downloadUrl}"><img src="${viewImgUrl}"/></a></td>
                <td class="button"><a href="${deleteUrl}"><img src="${deleteImgUrl}"/></a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <c:if test="${empty images}">
        No images available
    </c:if>

    <br/>
    <br/>

</form:form>

</body>
</html>
