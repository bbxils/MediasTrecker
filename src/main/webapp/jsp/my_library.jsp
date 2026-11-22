<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru" data-bs-theme="dark">
<head>
  <title>Моя библиотека</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
  <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<t:header/>

<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-10 col-lg-8">

      <h2 class="mb-4">Добавить произведение</h2>

      <c:if test="${not empty sessionScope.flashSuccess}">
        <div class="alert alert-success"><c:out value="${sessionScope.flashSuccess}"/></div>
        <c:remove var="flashSuccess" scope="session"/>
      </c:if>
      <c:if test="${not empty errors}">
        <div class="alert alert-danger">Проверьте введённые данные</div>
      </c:if>

      <div class="card p-4 shadow-sm">
        <form action="${pageContext.request.contextPath}/myLibrary" method="post" enctype="multipart/form-data">

          <div class="mb-3 form-check">
            <input type="checkbox" class="form-check-input" name="suggestToCatalog" id="suggestToCatalog" value="true">
            <label class="form-check-label" for="suggestToCatalog">
              Предложить в общий каталог (на проверку админу)
            </label>
          </div>

          <div class="mb-3">
            <label for="title" class="form-label">Название:</label>
            <input type="text" class="form-control" name="title" id="title" value="<c:out value='${form.title}'/>">
            <c:if test="${not empty errors.title}"><div class="text-danger small mt-1">${errors.title}</div></c:if>
          </div>

          <div class="mb-3">
            <label for="cover" class="form-label">Обложка:</label>
            <input type="file" class="form-control" name="cover" id="cover" accept="image/*">
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label for="type" class="form-label">Тип:</label>
              <select class="form-select" name="type" id="type" onchange="updateFields()">
                <option value="">-- Выберите тип --</option>
                <c:forEach var="item" items="${mediaTypeList}">
                  <option value="${item.id}" data-name="<c:out value='${item.name}'/>"
                    ${form.typeId == item.id.toString() ? 'selected' : ''}><c:out value="${item.name}"/></option>
                </c:forEach>
              </select>
              <c:if test="${not empty errors.type}"><div class="text-danger small mt-1">${errors.type}</div></c:if>
            </div>

            <div class="col-md-6 mb-3">
              <label for="release" class="form-label">Год выпуска:</label>
              <input type="number" class="form-control" name="release_year" id="release" value="${form.releaseYear}">
              <c:if test="${not empty errors.releaseYear}"><div class="text-danger small mt-1">${errors.releaseYear}</div></c:if>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label d-block">Жанры:</label>
            <c:forEach var="item" items="${genreList}">
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="checkbox" name="genre" id="genre_${item.id}" value="${item.id}"
                       <c:if test="${form.genreIds.contains(item.id.toString())}">checked</c:if>>
                <label class="form-check-label" for="genre_${item.id}"><c:out value="${item.name}"/></label>
              </div>
            </c:forEach>
            <c:if test="${not empty errors.genre}"><div class="text-danger small mt-1">${errors.genre}</div></c:if>
          </div>

          <div class="mb-3">
            <label for="description" class="form-label">Описание:</label>
            <textarea class="form-control" name="description" id="description" rows="4"><c:out value="${form.description}"/></textarea>
            <c:if test="${not empty errors.description}"><div class="text-danger small mt-1">${errors.description}</div></c:if>
          </div>

          <div class="mb-3">
            <label for="status" class="form-label">Статус:</label>
            <select class="form-select" name="status" id="status">
              <option value="">-- Выберите статус --</option>
              <option value="planned" ${form.status == 'planned' ? 'selected' : ''}>Запланировано</option>
              <option value="in_progress" ${form.status == 'in_progress' ? 'selected' : ''}>В процессе</option>
              <option value="completed" ${form.status == 'completed' ? 'selected' : ''}>Завершено</option>
              <option value="dropped" ${form.status == 'dropped' ? 'selected' : ''}>Заброшено</option>
            </select>
            <c:if test="${not empty errors.status}"><div class="text-danger small mt-1">${errors.status}</div></c:if>
          </div>

          <%-- Поля ниже показываются в зависимости от типа (см. скрипт) --%>
          <div class="conditional-field mb-3" id="field-pages">
            <label for="page_count" class="form-label">Количество страниц:</label>
            <input type="number" class="form-control" name="page_count" id="page_count" value="${form.pageCount}">
            <c:if test="${not empty errors.pageCount}"><div class="text-danger small mt-1">${errors.pageCount}</div></c:if>
          </div>

          <div class="conditional-field mb-3" id="field-seasons">
            <label for="seasons_count" class="form-label">Количество сезонов:</label>
            <input type="number" class="form-control" name="seasons_count" id="seasons_count" value="${form.seasonsCount}">
            <c:if test="${not empty errors.seasonsCount}"><div class="text-danger small mt-1">${errors.seasonsCount}</div></c:if>
          </div>

          <div class="conditional-field mb-3" id="field-episodes">
            <label for="episodes_count" class="form-label">Количество эпизодов:</label>
            <input type="number" class="form-control" name="episodes_count" id="episodes_count" value="${form.episodesCount}">
            <c:if test="${not empty errors.episodesCount}"><div class="text-danger small mt-1">${errors.episodesCount}</div></c:if>
          </div>

          <div class="conditional-field mb-3" id="field-runtime">
            <label for="runtime" class="form-label">Длительность (в минутах):</label>
            <input type="number" class="form-control" name="runtime" id="runtime" value="${form.runtime}">
            <c:if test="${not empty errors.runtime}"><div class="text-danger small mt-1">${errors.runtime}</div></c:if>
          </div>

          <div class="conditional-field mb-3" id="field-chapters">
            <label for="chapters_done" class="form-label">Пройдено глав:</label>
            <input type="number" class="form-control" name="chapters_done" id="chapters_done" value="${form.chaptersDone}">
            <c:if test="${not empty errors.chaptersDone}"><div class="text-danger small mt-1">${errors.chaptersDone}</div></c:if>
          </div>

          <div class="conditional-field mb-3" id="field-seasons-watched">
            <label for="seasons_watched" class="form-label">Просмотрено сезонов:</label>
            <input type="number" class="form-control" name="seasons_watched" id="seasons_watched" value="${form.seasonsWatched}">
            <c:if test="${not empty errors.seasonsWatched}"><div class="text-danger small mt-1">${errors.seasonsWatched}</div></c:if>
          </div>

          <div class="conditional-field mb-3" id="field-episodes-watched">
            <label for="episodes_watched" class="form-label">Просмотрено эпизодов:</label>
            <input type="number" class="form-control" name="episodes_watched" id="episodes_watched" value="${form.episodesWatched}">
            <c:if test="${not empty errors.episodesWatched}"><div class="text-danger small mt-1">${errors.episodesWatched}</div></c:if>
          </div>

          <div class="conditional-field mb-3" id="field-pages-read">
            <label for="pages_read" class="form-label">Прочитано страниц:</label>
            <input type="number" class="form-control" name="pages_read" id="pages_read" value="${form.pagesRead}">
            <c:if test="${not empty errors.pagesRead}"><div class="text-danger small mt-1">${errors.pagesRead}</div></c:if>
          </div>

          <hr class="my-4">
          <small class="text-muted">Необязательные поля:</small>
          <br><br>

          <div class="mb-3">
            <label class="form-label d-block">Ваша оценка:</label>
            <div class="rating-10">
              <c:forEach var="i" begin="1" end="10">
                <input type="radio" id="s${i}" name="rating" value="${11 - i}"
                  ${form.rating == (11 - i).toString() ? 'checked' : ''}>
                <label for="s${i}" title="${11 - i}">★</label>
              </c:forEach>
            </div>
            <c:if test="${not empty errors.rating}"><div class="text-danger small mt-1">${errors.rating}</div></c:if>
          </div>

          <div class="mb-4">
            <label for="note" class="form-label">Заметка:</label>
            <input type="text" class="form-control" name="note" id="note" value="<c:out value='${form.note}'/>">
            <c:if test="${not empty errors.note}"><div class="text-danger small mt-1">${errors.note}</div></c:if>
          </div>

          <button class="btn btn-danger w-100 py-2" type="submit">Добавить</button>
        </form>
      </div>

      <h2 class="mt-5 mb-3">Моя библиотека</h2>

      <c:choose>
        <c:when test="${empty libraryItems}">
          <p class="text-muted">Пока здесь пусто. Добавьте первое произведение.</p>
        </c:when>
        <c:otherwise>
          <div class="row">
            <c:forEach var="item" items="${libraryItems}">
              <div class="col-md-6 mb-3">
                <a href="${pageContext.request.contextPath}/myLibrary/card?id=${item.id}" class="card p-3 flex-row text-decoration-none text-reset h-100">

                  <c:if test="${not empty item.coverUrl}">
                    <img src="${pageContext.request.contextPath}/${item.coverUrl}" class="me-3 rounded" width="80" height="120" style="object-fit: cover;" alt="Обложка">
                  </c:if>

                  <div>
                    <h5 class="mb-1"><c:out value="${item.title}"/></h5>
                    <div class="text-muted small">
                      <c:out value="${item.typeName}"/>
                      <c:if test="${not empty item.releaseYear}">, <c:out value="${item.releaseYear}"/></c:if>
                    </div>
                    <div class="small mb-2"><c:out value="${item.genres}"/></div>

                    <span class="badge bg-secondary">
                  <c:choose>
                    <c:when test="${item.status == 'planned'}">Запланировано</c:when>
                    <c:when test="${item.status == 'in_progress'}">В процессе</c:when>
                    <c:when test="${item.status == 'completed'}">Завершено</c:when>
                    <c:when test="${item.status == 'dropped'}">Заброшено</c:when>
                  </c:choose>
                </span>
                  </div>
                </a>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>

    </div>
  </div>
</div>

<script>
  function updateFields() {
    document.querySelectorAll('.conditional-field').forEach(el => {
      el.style.display = 'none';
      el.querySelectorAll('input').forEach(i => i.disabled = true);
    });

    const typeSelect = document.getElementById('type');
    const typeName = typeSelect.options[typeSelect.selectedIndex].getAttribute('data-name');

    if (typeName === 'Фильм') {
      show('field-runtime');
    } else if (typeName === 'Сериал') {
      show('field-seasons');
      show('field-episodes');
      show('field-seasons-watched');
      show('field-episodes-watched');
    } else if (typeName === 'Книга') {
      show('field-pages');
      show('field-pages-read');
    } else if (typeName === 'Игра') {
      show('field-chapters');
      show('field-runtime');
    }
  }

  function show(id) {
    const el = document.getElementById(id);
    el.style.display = 'block';
    el.querySelectorAll('input').forEach(i => i.disabled = false);
  }
  // Вызов при загрузке страницы (чтобы поля появились сразу)
  document.addEventListener('DOMContentLoaded', updateFields);
</script>
</body>
</html>