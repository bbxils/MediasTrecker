<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru" data-bs-theme="dark">
<head>
  <title><c:out value="${media.title}"/></title>
  <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<t:header/>

<div class="container mt-5">
  <div class="row">
    <div class="col-md-4">
      <c:if test="${not empty media.coverUrl}">
        <img src="${pageContext.request.contextPath}/${media.coverUrl}" class="img-fluid rounded shadow mb-3" alt="Обложка">
      </c:if>

      <ul class="list-group mb-4">
        <li class="list-group-item bg-dark text-light border-secondary">
          <strong>Тип:</strong> <c:out value="${media.typeName}"/>
        </li>
        <c:if test="${not empty media.releaseYear}">
          <li class="list-group-item bg-dark text-light border-secondary">
            <strong>Год:</strong> ${media.releaseYear}
          </li>
        </c:if>
        <c:if test="${not empty media.pageCount}">
          <li class="list-group-item bg-dark text-light border-secondary">
            <strong>Страниц:</strong> ${media.pageCount}
          </li>
        </c:if>
        <c:if test="${not empty media.seasonsCount}">
          <li class="list-group-item bg-dark text-light border-secondary">
            <strong>Сезонов:</strong> ${media.seasonsCount}
          </li>
        </c:if>
        <c:if test="${not empty media.episodesCount}">
          <li class="list-group-item bg-dark text-light border-secondary">
            <strong>Эпизодов:</strong> ${media.episodesCount}
          </li>
        </c:if>
        <c:if test="${not empty media.runtime}">
          <li class="list-group-item bg-dark text-light border-secondary">
            <strong>Длительность:</strong> ${media.runtime} мин.
          </li>
        </c:if>
      </ul>
    </div>

    <div class="col-md-8">
      <h1 class="mb-3"><c:out value="${media.title}"/></h1>
      <p class="lead text-muted"><c:out value="${media.description}"/></p>

      <hr class="my-4 border-secondary">

      <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger">${errorMessage}</div>
      </c:if>

      <!-- ПРОВЕРКА НА АДМИНА -->
      <c:choose>
        <%-- Если пользователь - обычный юзер, показываем форму добавления --%>
        <c:when test="${sessionScope.role != 'admin'}">
          <h3 class="mb-3">Добавить в мою библиотеку</h3>

          <form action="${pageContext.request.contextPath}/myLibrary/add" method="post">
            <input type="hidden" name="mediaId" value="${media.id}">

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Статус:</label>
                <select name="status" class="form-select" required>
                  <option value="planned">Запланировано</option>
                  <option value="in_progress">В процессе</option>
                  <option value="completed">Завершено</option>
                  <option value="dropped">Заброшено</option>
                </select>
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label d-block">Оценка:</label>
                <div class="rating-10">
                  <c:forEach var="i" begin="1" end="10">
                    <input type="radio" id="star_${i}" name="rating" value="${11 - i}">
                    <label for="star_${i}" title="${11 - i}">★</label>
                  </c:forEach>
                </div>
              </div>
            </div>

            <c:if test="${not empty media.pageCount}">
              <div class="mb-3">
                <label class="form-label">Прочитано страниц (из ${media.pageCount}):</label>
                <input type="number" name="pagesRead" class="form-control" min="0" max="${media.pageCount}">
              </div>
            </c:if>

            <c:if test="${not empty media.seasonsCount}">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">Просмотрено сезонов (из ${media.seasonsCount}):</label>
                  <input type="number" name="seasonsWatched" class="form-control" min="0" max="${media.seasonsCount}">
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">Просмотрено эпизодов (из ${media.episodesCount}):</label>
                  <input type="number" name="episodesWatched" class="form-control" min="0" max="${media.episodesCount}">
                </div>
              </div>
            </c:if>

            <div class="mb-3">
              <label class="form-label">Заметка:</label>
              <textarea name="note" class="form-control" rows="3"></textarea>
            </div>

            <button type="submit" class="btn btn-success w-100 mt-3">Сохранить в библиотеку</button>
          </form>
        </c:when>

        <c:otherwise>
          <div class="alert alert-info mt-4">
            <h5>Вы вошли как администратор</h5>
            <p class="mb-0">Администраторы не могут добавлять карточки в личную библиотеку.</p>
            <a href="${pageContext.request.contextPath}/admin/media/form?id=${media.id}" class="btn btn-primary mt-3">
              Редактировать эту карточку
            </a>
          </div>
        </c:otherwise>
      </c:choose>

    </div>
  </div>
</div>
</body>
</html>