<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<t:admin_layout title="Модерация">
  <div class="container mt-4">
    <h2 class="mb-4">Предложения пользователей</h2>
    <div id="msg"></div>

    <div class="card p-3 shadow-sm">
      <table class="table table-striped table-hover mb-0">
        <thead>
        <tr>
          <th>Обложка</th>
          <th>Название</th>
          <th>Тип</th>
          <th>Год</th>
          <th>Автор</th>
          <th>Описание</th>
          <th>Действия</th>
        </tr>
        </thead>
        <tbody id="rows">
        <c:forEach var="m" items="${items}">
          <tr id="row-${m.id}">
            <td>
              <c:choose>
                <c:when test="${not empty m.coverUrl}">
                  <img src="${pageContext.request.contextPath}/${m.coverUrl}"
                       alt="Обложка"
                       style="width: 60px; height: 80px; object-fit: cover;"
                       class="rounded shadow-sm">
                </c:when>
                <c:otherwise>
                  <span class="text-muted small">Нет</span>
                </c:otherwise>
              </c:choose>
            </td>

            <td><c:out value="${m.title}"/></td>
            <td><c:out value="${m.typeName}"/></td>
            <td>${m.releaseYear}</td>
            <td><c:out value="${m.authorName}"/></td>
            <td><c:out value="${m.description}"/></td>
            <td>
              <button type="button" class="btn btn-sm btn-success" data-action="approve" data-id="${m.id}">Одобрить</button>
              <button type="button" class="btn btn-sm btn-danger" data-action="reject" data-id="${m.id}">Отклонить</button>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
      <p id="empty" class="text-muted mt-3 mb-0" style="display:none">Новых заявок нет.</p>
    </div>
  </div>

  <script>
    const ctx = '${pageContext.request.contextPath}';
    const msg = document.getElementById('msg');

    function showMessage(text, ok) {
      msg.className = ok ? 'alert alert-success' : 'alert alert-danger';
      msg.textContent = text;
    }

    function checkEmpty() {
      if (!document.querySelector('#rows tr')) {
        document.getElementById('empty').style.display = 'block';
      }
    }

    document.querySelectorAll('[data-action]').forEach(btn => {
      btn.addEventListener('click', async () => {
        const id = btn.dataset.id;
        const action = btn.dataset.action;
        const response = await fetch(ctx + '/admin/moderation', {
          method: 'POST',
          headers: {'X-Requested-With': 'XMLHttpRequest'},
          body: new URLSearchParams({action: action, id: id})
        });
        if (response.ok) {
          document.getElementById('row-' + id).remove();
          showMessage(action === 'approve' ? 'Карточка одобрена' : 'Заявка отклонена', true);
          checkEmpty();
        } else {
          showMessage(response.status === 409 ? await response.text() : 'Ошибка ' + response.status, false);
        }
      });
    });

    checkEmpty();
  </script>
</t:admin_layout>