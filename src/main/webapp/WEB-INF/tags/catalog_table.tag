<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="container mt-4">
  <h2 class="mb-4">Каталог</h2>

  <form action="${pageContext.request.contextPath}/catalog" method="get" class="mb-4 d-flex gap-2 align-items-center">
    <input type="text" class="form-control" name="q" placeholder="Название" style="max-width: 250px;" value="<c:out value='${q}'/>">
    <select class="form-select" name="type" style="max-width: 200px;">
      <option value="">Все типы</option>
      <c:forEach var="t" items="${types}">
        <option value="${t.id}" ${t.id == typeId ? 'selected' : ''}><c:out value="${t.name}"/></option>
      </c:forEach>
    </select>
    <button type="submit" class="btn btn-primary">Найти</button>

    <c:if test="${sessionScope.role == 'admin'}">
      <a href="${pageContext.request.contextPath}/admin/media/form" class="btn btn-success ms-auto">Добавить карточку</a>
    </c:if>
  </form>

  <div class="row">
    <c:forEach var="item" items="${items}">
      <div class="col-md-6 mb-3">
        <c:choose>
          <c:when test="${sessionScope.role == 'admin'}">
            <c:set var="linkUrl" value="${pageContext.request.contextPath}/admin/media/form?id=${item.id}" />
          </c:when>
          <c:otherwise>
            <c:set var="linkUrl" value="${pageContext.request.contextPath}/catalog/card?id=${item.id}" />
          </c:otherwise>
        </c:choose>

        <a href="${linkUrl}" class="card p-3 flex-row text-decoration-none text-reset h-100 shadow-sm">
          <!-- Обложка -->
          <c:if test="${not empty item.coverUrl}">
            <img src="${pageContext.request.contextPath}/${item.coverUrl}" class="me-3 rounded" width="80" height="120" style="object-fit: cover;" alt="Обложка">
          </c:if>

          <div class="d-flex flex-column w-100">
            <h5 class="mb-1"><c:out value="${item.title}"/></h5>
            <div class="text-muted small">
              <c:out value="${item.typeName}"/>
              <c:if test="${not empty item.releaseYear}">, <c:out value="${item.releaseYear}"/></c:if>
            </div>
            <div class="small mb-2 text-info"><c:out value="${item.genres}"/></div>

            <div class="mt-auto d-flex justify-content-between align-items-end">
              <c:if test="${sessionScope.role == 'admin'}">
                <span class="badge bg-secondary">В библиотеках: ${item.usersCount}</span>
              </c:if>

              <c:if test="${sessionScope.role != 'admin'}">
                <span class="btn btn-sm btn-success">Подробнее</span>
              </c:if>
            </div>
          </div>
        </a>
      </div>
    </c:forEach>
  </div>
</div>