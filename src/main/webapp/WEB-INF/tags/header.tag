<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="false" %>

<!DOCTYPE html>
<html lang="ru" data-bs-theme="dark">
<head>
    <title>${title != null ? title : 'Моя библиотека'}</title>
    <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-expand bg-body-tertiary px-3">
    <c:if test="${sessionScope.role != 'admin'}">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/myLibrary">Моя библиотека</a>
    </c:if>
    <a class="navbar-brand" href="${pageContext.request.contextPath}/catalog">Каталог</a>

    <div class="navbar-nav me-auto">
        <c:if test="${sessionScope.role == 'admin'}">
            <a class="nav-link" href="${pageContext.request.contextPath}/admin/media">Админка</a>
        </c:if>
    </div>

    <span class="navbar-text me-3"><c:out value="${sessionScope.username}"/></span>

    <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
        <button type="submit" class="btn btn-outline-danger btn-sm">Выйти</button>
    </form>
</nav>