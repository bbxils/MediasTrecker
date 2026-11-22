<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:choose>
  <c:when test="${sessionScope.role == 'admin'}">
    <t:admin_layout title="Каталог">
      <t:catalog_table />
    </t:admin_layout>
  </c:when>

  <c:otherwise>
    <t:header />
    <t:catalog_table />
  </c:otherwise>
</c:choose>