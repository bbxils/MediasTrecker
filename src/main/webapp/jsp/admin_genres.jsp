<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<t:admin_layout title="Жанры">
  <div class="container mt-4">
    <h2 class="mb-4">Жанры</h2>

    <form action="${pageContext.request.contextPath}/admin/genres" method="post" class="mb-4 d-flex gap-2 align-items-center">
      <input type="hidden" name="action" value="create">
      <input type="text" class="form-control" name="name" placeholder="Новый жанр" maxlength="50" required style="max-width: 300px;">
      <button type="submit" class="btn btn-success">Добавить</button>
    </form>

    <div class="card p-3 shadow-sm">
      <table class="table table-striped table-hover align-middle mb-0">
        <thead>
        <tr><th>ID</th><th>Название</th><th>Действия</th></tr>
        </thead>
        <tbody>
        <c:forEach var="g" items="${genres}">
          <tr>
            <td>${g.id}</td>
            <td>
              <form action="${pageContext.request.contextPath}/admin/genres" method="post" class="d-flex gap-2">
                <input type="hidden" name="action" value="rename">
                <input type="hidden" name="id" value="${g.id}">
                <input type="text" class="form-control form-control-sm" name="name" maxlength="50" required value="<c:out value='${g.name}'/>" style="max-width: 250px;">
                <button type="submit" class="btn btn-sm btn-outline-primary">Сохранить</button>
              </form>
            </td>
            <td>
              <form action="${pageContext.request.contextPath}/admin/genres" method="post"
                    onsubmit="return confirm('Удалить жанр?');">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="${g.id}">
                <button type="submit" class="btn btn-sm btn-outline-danger">Удалить</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</t:admin_layout>