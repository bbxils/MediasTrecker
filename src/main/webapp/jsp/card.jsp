<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru" data-bs-theme="dark">
<head>
  <title><c:out value="${item.title}"/></title>
  <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<t:header/>

<div class="container mt-5">
  <div class="row">
    <!-- Левая колонка: Обложка и основные данные -->
    <div class="col-md-4">
      <c:if test="${not empty item.coverUrl}">
        <img src="${pageContext.request.contextPath}/${item.coverUrl}" class="img-fluid rounded shadow mb-3" alt="Обложка">
      </c:if>

      <ul class="list-group mb-4">
        <li class="list-group-item bg-dark text-light border-secondary">
          <strong>Тип:</strong> <c:out value="${item.typeName}"/>
        </li>
        <c:if test="${not empty item.releaseYear}">
          <li class="list-group-item bg-dark text-light border-secondary">
            <strong>Год:</strong> ${item.releaseYear}
          </li>
        </c:if>
        <c:if test="${not empty item.genres}">
          <li class="list-group-item bg-dark text-light border-secondary">
            <strong>Жанры:</strong> <c:out value="${item.genres}"/>
          </li>
        </c:if>
      </ul>
    </div>

    <!-- Правая колонка: Описание и Личные данные -->
    <div class="col-md-8">
      <h1 class="mb-3"><c:out value="${item.title}"/></h1>
      <p class="lead text-muted"><c:out value="${item.description}"/></p>

      <hr class="my-4 border-secondary">

      <!-- Мои данные -->
      <h3 class="mb-3">Мои данные</h3>

      <div class="card p-3 bg-dark border-secondary mb-4">
        <div class="row">
          <div class="col-md-6 mb-2">
            <strong>Статус:</strong>
            <span class="badge bg-secondary ms-2">
                            <c:choose>
                              <c:when test="${item.status == 'planned'}">Запланировано</c:when>
                              <c:when test="${item.status == 'in_progress'}">В процессе</c:when>
                              <c:when test="${item.status == 'completed'}">Завершено</c:when>
                              <c:when test="${item.status == 'dropped'}">Заброшено</c:when>
                            </c:choose>
                        </span>
          </div>
          <div class="col-md-6 mb-2">
            <strong>Моя оценка:</strong>
            <c:choose>
              <c:when test="${not empty item.rating}">
                <span class="text-warning ms-2">${item.rating} / 10 ★</span>
              </c:when>
              <c:otherwise>
                <span class="text-muted ms-2">Нет оценки</span>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <!-- Прогресс в зависимости от типа -->
        <c:if test="${item.typeName == 'Книга'}">
          <div class="mt-2">
            <strong>Прочитано страниц:</strong>
              ${item.pagesRead != null ? item.pagesRead : 0}
            <c:if test="${not empty item.pageCount}">из ${item.pageCount}</c:if>
          </div>
        </c:if>
        <c:if test="${item.typeName == 'Сериал'}">
          <div class="mt-2">
            <strong>Просмотрено:</strong>
              ${item.seasonsWatched != null ? item.seasonsWatched : 0} сезонов,
              ${item.episodesWatched != null ? item.episodesWatched : 0} эпизодов
          </div>
        </c:if>
        <c:if test="${item.typeName == 'Игра'}">
          <div class="mt-2">
            <strong>Пройдено глав:</strong> ${item.chaptersDone != null ? item.chaptersDone : 0}
          </div>
        </c:if>

        <c:if test="${not empty item.note}">
          <div class="mt-3">
            <strong>Моя заметка:</strong>
            <p class="text-muted mb-0 mt-1"><c:out value="${item.note}"/></p>
          </div>
        </c:if>
      </div>

      <!-- Кнопки управления -->
      <div class="d-flex gap-2 flex-wrap">
        <!-- Редактировать прогресс (статус, оценка, заметки) - всегда доступно -->
        <a href="${pageContext.request.contextPath}/myLibrary/card?edit=true&id=${item.id}" class="btn btn-primary">
          Редактировать прогресс
        </a>

        <!-- Редактировать саму карточку (название, жанр, обложка) - только если она приватная -->
        <c:if test="${item.visibility == 'private'}">
          <a href="${pageContext.request.contextPath}/myLibrary/card/edit-media?id=${item.mediaId}" class="btn btn-warning">
            Редактировать карточку
          </a>
        </c:if>

        <!-- Удалить из библиотеки -->
        <form action="${pageContext.request.contextPath}/myLibrary/card" method="post" onsubmit="return confirm('Удалить из библиотеки?');" class="m-0">
          <input type="hidden" name="action" value="delete">
          <input type="hidden" name="id" value="${item.id}">
          <button type="submit" class="btn btn-danger">Удалить</button>
        </form>
      </div>

    </div>
  </div>
</div>
</body>
</html>