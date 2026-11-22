<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" %>
<html lang="ru" data-bs-theme="dark"> <%-- Включаем темную тему Bootstrap --%>
<head>
  <title>${title}</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
  <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-dark text-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark border-bottom border-secondary mb-4">
  <div class="container-fluid">
    <div class="navbar-nav me-auto gap-2">
      <a class="nav-link" href="${pageContext.request.contextPath}/admin/media">Каталог</a>
      <a class="nav-link" href="${pageContext.request.contextPath}/admin/moderation">Модерация</a>
      <a class="nav-link" href="${pageContext.request.contextPath}/admin/genres">Жанры</a>
      <a class="nav-link" href="${pageContext.request.contextPath}/admin/users">Пользователи</a>
    </div>
    <div class="d-flex align-items-center gap-3">
      <a class="nav-link" href="${pageContext.request.contextPath}/myLibrary">Моя библиотека</a>
      <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
        <button type="submit" class="btn btn-outline-danger btn-sm">Выйти</button>
      </form>
    </div>
  </div>
</nav>

<c:if test="${not empty sessionScope.flashSuccess}">
  <div class="container">
    <div class="alert alert-success"><c:out value="${sessionScope.flashSuccess}"/></div>
  </div>
  <c:remove var="flashSuccess" scope="session"/>
</c:if>
<c:if test="${not empty sessionScope.flashError}">
  <div class="container">
    <div class="alert alert-danger"><c:out value="${sessionScope.flashError}"/></div>
  </div>
  <c:remove var="flashError" scope="session"/>
</c:if>

<main>
  <jsp:doBody/>
</main>

<script src="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>