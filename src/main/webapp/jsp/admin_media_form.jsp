<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<t:admin_layout title="Карточка">
  <div class="container mt-4">
    <div class="row justify-content-center">
      <div class="col-md-8">
        <h2 class="mb-4">${empty form.id ? 'Новая карточка' : 'Редактирование карточки'}</h2>

        <c:if test="${not empty errors.form}">
          <div class="alert alert-danger">${errors.form}</div>
        </c:if>

        <div class="card p-4 shadow-sm">
          <form action="${pageContext.request.contextPath}/admin/media/form" method="post" enctype="multipart/form-data">
            <input type="hidden" name="id" value="<c:out value='${form.id}'/>">

            <div class="mb-3">
              <label for="title" class="form-label">Название: *</label>
              <input type="text" class="form-control" name="title" id="title" maxlength="100" required value="<c:out value='${form.title}'/>">
              <c:if test="${not empty errors.title}"><div class="text-danger small mt-1">${errors.title}</div></c:if>
            </div>

            <div class="mb-3">
              <label for="cover" class="form-label">Обложка (пусто: не менять):</label>
              <input type="file" class="form-control" name="cover" id="cover" accept="image/*">
            </div>

            <div class="mb-3">
              <label for="type" class="form-label">Тип: *</label>
              <select class="form-select" name="type" id="type" onchange="updateFields()">
                <c:forEach var="item" items="${types}">
                  <option value="${item.id}" data-name="<c:out value='${item.name}'/>"
                    ${item.id.toString() == form.typeId ? 'selected' : ''}><c:out value="${item.name}"/></option>
                </c:forEach>
              </select>
              <c:if test="${not empty errors.type}"><div class="text-danger small mt-1">${errors.type}</div></c:if>
            </div>

            <div class="mb-3">
              <label class="form-label">Жанры:</label><br>
              <c:forEach var="g" items="${genres}">
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="checkbox" name="genre" id="genre_${g.id}" value="${g.id}"
                    ${form.genreIds.contains(g.id.toString()) ? 'checked' : ''}>
                  <label class="form-check-label" for="genre_${g.id}"><c:out value="${g.name}"/></label>
                </div>
              </c:forEach>
              <c:if test="${not empty errors.genre}"><div class="text-danger small mt-1">${errors.genre}</div></c:if>
            </div>

            <div class="mb-3">
              <label for="release" class="form-label">Год выпуска: *</label>
              <input type="number" class="form-control" name="release_year" id="release" min="1455" required value="<c:out value='${form.releaseYear}'/>">
              <c:if test="${not empty errors.releaseYear}"><div class="text-danger small mt-1">${errors.releaseYear}</div></c:if>
            </div>

            <div class="mb-4">
              <label for="description" class="form-label">Описание:</label>
              <textarea class="form-control" name="description" id="description" rows="4" maxlength="2000"><c:out value="${form.description}"/></textarea>
              <c:if test="${not empty errors.description}"><div class="text-danger small mt-1">${errors.description}</div></c:if>
            </div>

            <div class="conditional-field mb-3" id="f-pages">
              <label for="page_count" class="form-label">Количество страниц:</label>
              <input type="number" class="form-control" name="page_count" id="page_count" min="1" value="<c:out value='${form.pageCount}'/>">
              <c:if test="${not empty errors.pageCount}"><div class="text-danger small mt-1">${errors.pageCount}</div></c:if>
            </div>

            <div class="conditional-field mb-3" id="f-seasons">
              <label for="seasons_count" class="form-label">Количество сезонов:</label>
              <input type="number" class="form-control" name="seasons_count" id="seasons_count" min="1" value="<c:out value='${form.seasonsCount}'/>">
              <c:if test="${not empty errors.seasonsCount}"><div class="text-danger small mt-1">${errors.seasonsCount}</div></c:if>
            </div>

            <div class="conditional-field mb-3" id="f-episodes">
              <label for="episodes_count" class="form-label">Количество эпизодов:</label>
              <input type="number" class="form-control" name="episodes_count" id="episodes_count" min="1" value="<c:out value='${form.episodesCount}'/>">
              <c:if test="${not empty errors.episodesCount}"><div class="text-danger small mt-1">${errors.episodesCount}</div></c:if>
            </div>

            <div class="conditional-field mb-4" id="f-runtime">
              <label for="runtime" class="form-label">Длительность (в минутах):</label>
              <input type="number" class="form-control" name="runtime" id="runtime" min="1" value="<c:out value='${form.runtime}'/>">
              <c:if test="${not empty errors.runtime}"><div class="text-danger small mt-1">${errors.runtime}</div></c:if>
            </div>

            <div class="d-flex gap-2">
              <button type="submit" class="btn btn-primary">Сохранить</button>
              <a href="${pageContext.request.contextPath}/admin/media" class="btn btn-secondary">Отмена</a>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>

  <script>
    const FIELDS = {'Фильм': ['runtime'], 'Сериал': ['seasons', 'episodes'], 'Книга': ['pages'], 'Игра': ['runtime']};
    function updateFields() {
      document.querySelectorAll('.conditional-field').forEach(el => {
        el.style.display = 'none';
        el.querySelectorAll('input').forEach(i => i.disabled = true);
      });
      const select = document.getElementById('type');
      const name = select.options[select.selectedIndex].getAttribute('data-name');
      (FIELDS[name] || []).forEach(key => {
        const el = document.getElementById('f-' + key);
        el.style.display = 'block';
        el.querySelectorAll('input').forEach(i => i.disabled = false);
      });
    }
    document.addEventListener('DOMContentLoaded', updateFields);
  </script>
</t:admin_layout>