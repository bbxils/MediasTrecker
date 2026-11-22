<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru" data-bs-theme="dark">
<head>
  <title>Регистрация</title>
  <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
      <div class="card p-4 shadow-sm">
        <h2 class="mb-4 text-center">Регистрация</h2>

        <form action="${pageContext.request.contextPath}/signUp" method="post">
          <div class="mb-3">
            <label for="username" class="form-label">Логин:</label>
            <input type="text" class="form-control" name="username" id="username" required maxlength="30"
                   value="<c:out value='${form.username}'/>">
            <c:if test="${not empty errors.username}">
              <div class="text-danger small mt-1">${errors.username}</div>
            </c:if>
          </div>

          <div class="mb-3">
            <label for="email" class="form-label">Email:</label>
            <input type="email" class="form-control" name="email" id="email" required maxlength="200"
                   value="<c:out value='${form.email}'/>">
            <c:if test="${not empty errors.email}">
              <div class="text-danger small mt-1">${errors.email}</div>
            </c:if>
          </div>

          <div class="mb-3">
            <label for="password" class="form-label">Пароль:</label>
            <input type="password" class="form-control" name="password" id="password" required minlength="8">
            <c:if test="${not empty errors.password}">
              <div class="text-danger small mt-1">${errors.password}</div>
            </c:if>
          </div>

          <div class="mb-4">
            <label for="confirmPassword" class="form-label">Повторите пароль:</label>
            <input type="password" class="form-control" name="confirmPassword" id="confirmPassword" required>
            <c:if test="${not empty errors.confirmPassword}">
              <div class="text-danger small mt-1">${errors.confirmPassword}</div>
            </c:if>
          </div>

          <button type="submit" class="btn btn-danger w-100 py-2">Зарегистрироваться</button>
        </form>

        <p class="text-center mt-3 mb-0">Уже есть аккаунт? <a href="${pageContext.request.contextPath}/signIn" class="text-warning">Войти</a></p>
      </div>
    </div>
  </div>
</div>
</body>
</html>