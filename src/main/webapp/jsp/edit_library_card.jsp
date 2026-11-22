<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru" data-bs-theme="dark">
<head>
  <title>Редактирование: <c:out value="${item.title}"/></title>
  <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<t:header/>

<div class="container mt-5">
  <h2 class="mb-4">Редактировать прогресс: <c:out value="${item.title}"/></h2>

  <div class="card p-4 shadow-sm">
    <form action="${pageContext.request.contextPath}/myLibrary/card" method="post">
      <input type="hidden" name="id" value="${item.id}">

      <div class="row">
        <div class="col-md-6 mb-3">
          <label class="form-label">Статус:</label>
          <select name="status" class="form-select" required>
            <option value="planned" ${userMedia.status == 'planned' ? 'selected' : ''}>Запланировано</option>
            <option value="in_progress" ${userMedia.status == 'in_progress' ? 'selected' : ''}>В процессе</option>
            <option value="completed" ${userMedia.status == 'completed' ? 'selected' : ''}>Завершено</option>
            <option value="dropped" ${userMedia.status == 'dropped' ? 'selected' : ''}>Заброшено</option>
          </select>
        </div>

        <div class="col-md-6 mb-3">
          <label class="form-label d-block">Оценка:</label>
          <div class="rating-10">
            <c:forEach var="i" begin="1" end="10">
              <input type="radio" id="star_${i}" name="rating" value="${11 - i}"
                ${userMedia.rating == (11 - i) ? 'checked' : ''}>
              <label for="star_${i}" title="${11 - i}">★</label>
            </c:forEach>
          </div>
        </div>
      </div>
      <c:if test="${item.typeName == 'Книга'}">
        <div class="mb-3">
          <label class="form-label">Прочитано страниц <c:if test="${not empty item.pageCount}">(из ${item.pageCount})</c:if>:</label>
          <input type="number" name="pagesRead" class="form-control" min="0"
                 <c:if test="${not empty item.pageCount}">max="${item.pageCount}"</c:if>
                 value="${userMedia.pagesRead}">
        </div>
      </c:if>

      <c:if test="${item.typeName == 'Сериал'}">
        <div class="row">
          <div class="col-md-6 mb-3">
            <label class="form-label">Просмотрено сезонов <c:if test="${not empty item.seasonsCount}">(из ${item.seasonsCount})</c:if>:</label>
            <input type="number" name="seasonsWatched" class="form-control" min="0"
                   <c:if test="${not empty item.seasonsCount}">max="${item.seasonsCount}"</c:if>
                   value="${userMedia.seasonsWatched}">
          </div>
          <div class="col-md-6 mb-3">
            <label class="form-label">Просмотрено эпизодов <c:if test="${not empty item.episodesCount}">(из ${item.episodesCount})</c:if>:</label>
            <input type="number" name="episodesWatched" class="form-control" min="0"
                   <c:if test="${not empty item.episodesCount}">max="${item.episodesCount}"</c:if>
                   value="${userMedia.episodesWatched}">
          </div>
        </div>
      </c:if>

      <c:if test="${item.typeName == 'Игра'}">
        <div class="mb-3">
          <label class="form-label">Пройдено глав:</label>
          <input type="number" name="chaptersDone" class="form-control" min="0" value="${userMedia.chaptersDone}">
        </div>
      </c:if>

      <div class="mb-3">
        <label class="form-label">Заметка:</label>
        <textarea name="note" class="form-control" rows="3">${userMedia.note}</textarea>
      </div>

      <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">Сохранить изменения</button>
        <a href="${pageContext.request.contextPath}/myLibrary" class="btn btn-secondary">Отмена</a>
      </div>
    </form>
  </div>
</div>
</body>
</html>