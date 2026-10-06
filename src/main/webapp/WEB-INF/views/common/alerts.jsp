<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- Messages set by servlets (request attribute or one-time flash after a redirect) --%>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-error mb-4"><c:out value="${errorMessage}"/></div>
</c:if>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success mb-4"><c:out value="${successMessage}"/></div>
</c:if>
