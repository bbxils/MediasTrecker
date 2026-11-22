<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru" data-bs-theme="dark">
<head>
  <title>Вход</title>
  <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-6 col-lg-4">
      <div class="card p-4 shadow-sm">
        <h2 class="mb-4 text-center">Вход</h2>

        <c:if test="${param.registered == '1'}">
          <div class="alert alert-success">Регистрация прошла успешно. Войдите в аккаунт.</div>
        </c:if>
        <c:if test="${not empty errors.form}">
          <div class="alert alert-danger">${errors.form}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/signIn" method="post">
          <div class="mb-3">
            <label for="login" class="form-label">Логин или email:</label>
            <input type="text" class="form-control" name="login" id="login" required value="<c:out value='${login}'/>">
          </div>

          <div class="mb-4">
            <label for="password" class="form-label">Пароль:</label>
            <input type="password" class="form-control" name="password" id="password" required>
          </div>
          <button type="submit" class="btn btn-danger w-100 py-2">Войти</button>
        </form>

        <p class="text-center mt-3 mb-0">Нет аккаунта? <a href="${pageContext.request.contextPath}/signUp" class="text-warning">Зарегистрироваться</a></p>
      </div>
    </div>
  </div>
</div>
</body>
</html>