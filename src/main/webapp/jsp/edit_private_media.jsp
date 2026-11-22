<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="ru" data-bs-theme="dark">
<head>
    <title>Редактирование: <c:out value="${media.title}"/></title>
    <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<t:header/>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-10 col-lg-8">
            <h2 class="mb-4">Редактирование карточки</h2>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="card p-4 shadow-sm">
                <form action="${pageContext.request.contextPath}/myLibrary/card/edit-media" method="post" enctype="multipart/form-data">
                    <!-- СКРЫТЫЙ ID МЕДИА -->
                    <input type="hidden" name="id" value="${media.id}">

                    <div class="mb-3">
                        <label for="title" class="form-label">Название:</label>
                        <input type="text" class="form-control" name="title" id="title" required value="<c:out value='${media.title}'/>">
                    </div>

                    <div class="mb-3">
                        <label for="cover" class="form-label">Обложка (оставьте пустым, чтобы не менять):</label>
                        <input type="file" class="form-control" name="cover" id="cover" accept="image/*">
                        <c:if test="${not empty media.coverUrl}">
                            <small class="text-muted">Текущая: ${media.coverUrl}</small>
                        </c:if>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="type" class="form-label">Тип:</label>
                            <select class="form-select" name="type" id="type" required>
                                <c:forEach var="item" items="${types}">
                                    <option value="${item.id}" ${media.typeId == item.id ? 'selected' : ''}>
                                        <c:out value="${item.name}"/>
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label for="release_year" class="form-label">Год выпуска:</label>
                            <input type="number" class="form-control" name="release_year" id="release_year"
                                   min="1455" max="2026" value="${media.releaseYear}">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label d-block">Жанры:</label>
                        <c:forEach var="item" items="${genres}">
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" name="genre" id="genre_${item.id}" value="${item.id}"
                                       <c:if test="${currentGenreIds.contains(item.id)}">checked</c:if>>
                                <label class="form-check-label" for="genre_${item.id}"><c:out value="${item.name}"/></label>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">Описание:</label>
                        <textarea class="form-control" name="description" id="description" rows="4"><c:out value="${media.description}"/></textarea>
                    </div>

                    <!-- Блок характеристик (показываем все, вдруг пользователь захочет заполнить) -->
                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label for="page_count" class="form-label">Страниц:</label>
                            <input type="number" class="form-control" name="page_count" id="page_count" min="0" value="${media.pageCount}">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="seasons_count" class="form-label">Сезонов:</label>
                            <input type="number" class="form-control" name="seasons_count" id="seasons_count" min="0" value="${media.seasonsCount}">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="episodes_count" class="form-label">Эпизодов:</label>
                            <input type="number" class="form-control" name="episodes_count" id="episodes_count" min="0" value="${media.episodesCount}">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label for="runtime" class="form-label">Длительность (мин):</label>
                            <input type="number" class="form-control" name="runtime" id="runtime" min="0" value="${media.runtime}">
                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-4">
                        <button type="submit" class="btn btn-primary w-50">Сохранить изменения</button>
                        <a href="${pageContext.request.contextPath}/myLibrary" class="btn btn-secondary w-50">Отмена</a>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>