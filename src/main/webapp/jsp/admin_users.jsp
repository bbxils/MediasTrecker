<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<t:admin_layout title="Пользователи">
  <div class="container mt-4">
    <h2 class="mb-4">Пользователи</h2>

    <div class="card p-3 shadow-sm">
      <table class="table table-striped table-hover align-middle mb-0">
        <thead>
        <tr><th>ID</th><th>Логин</th><th>Email</th><th>Роль</th><th>Действия</th></tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${users}">
          <tr>
            <td>${u.id}</td>
            <td><c:out value="${u.username}"/></td>
            <td><c:out value="${u.email}"/></td>
            <td>
              <c:choose>
                <c:when test="${u.id == sessionScope.userId}">
                  <span class="badge bg-secondary">${u.role}</span> (это вы)
                </c:when>
                <c:otherwise>
                  <form action="${pageContext.request.contextPath}/admin/users" method="post" class="d-flex gap-2 align-items-center">
                    <input type="hidden" name="action" value="role">
                    <input type="hidden" name="id" value="${u.id}">
                    <select name="role" class="form-select form-select-sm" style="width: 100px;">
                      <option value="user" ${u.role == 'user' ? 'selected' : ''}>user</option>
                      <option value="admin" ${u.role == 'admin' ? 'selected' : ''}>admin</option>
                    </select>
                    <button type="submit" class="btn btn-sm btn-outline-primary">Сменить</button>
                  </form>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <c:if test="${u.id != sessionScope.userId}">
                <form action="${pageContext.request.contextPath}/admin/users" method="post"
                      onsubmit="return confirm('Удалить пользователя вместе с его библиотекой?');">
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="id" value="${u.id}">
                  <button type="submit" class="btn btn-sm btn-outline-danger">Удалить</button>
                </form>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</t:admin_layout>