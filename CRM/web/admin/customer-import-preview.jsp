<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>

        <meta charset="UTF-8">

        <title>Preview Import Customers</title>

        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/admin/assets/css/customer-management.css">

        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

    </head>

    <body>

        <jsp:include page="sidebar.jsp"/>

        <div class="customer-management-content">

            <div class="customer-management-container">

                <!-- Header -->
                <div class="customer-page-header">

                    <div>
                        <h1>
                            <i class="fas fa-file-import"></i>
                            Preview Import Customers
                        </h1>
                        <p class="customer-text-muted">
                            Kiểm tra dữ liệu trước khi import
                        </p>
                    </div>

                </div>

                <!-- Table -->

                <div class="table-card">

                    <div class="table-header">
                        <h3>
                            <i class="fas fa-list"></i>
                            Preview List
                        </h3>
                    </div>

                    <c:choose>

                        <c:when test="${empty previewCustomers}">

                            <div class="empty-state">

                                <i class="fas fa-exclamation-circle"></i>

                                <h3>No data found</h3>

                                <p>File Excel không có dữ liệu hợp lệ</p>

                            </div>

                        </c:when>

                        <c:otherwise>

                            <div class="table-responsive">

                                <table class="data-table">

                                    <thead>

                                        <tr>
                                            <th>ID</th>
                                            <th>Email</th>
                                            <th>Họ tên</th>
                                            <th>Số điện thoại</th>
                                            <th>Địa chỉ</th>
                                            <th>Tier</th>
                                            <th>Status</th>
                                            <th>Owner</th>
                                        </tr>

                                    </thead>

                                    <tbody>

                                        <c:forEach var="c" items="${previewCustomers}">

                                            <tr>

                                                <td>#${c.id}</td>

                                                <td>
                                                    <div class="customer-email">
                                                        <i class="fas fa-envelope"></i>
                                                        ${c.email}
                                                    </div>
                                                </td>

                                                <td>
                                                    <div class="customer-name">
                                                        <i class="fas fa-user"></i>
                                                        ${c.fullName}
                                                    </div>
                                                </td>

                                                <td>
                                                    <div class="customer-phone">
                                                        <i class="fas fa-phone"></i>
                                                        ${c.phone}
                                                    </div>
                                                </td>

                                                <td>
                                                    <span class="badge badge-location">
                                                        <i class="fas fa-map-marker-alt"></i>
                                                        ${c.address}
                                                    </span>
                                                </td>

                                                <td>
                                                    <span class="badge badge-${c.tierName.toLowerCase()}">
                                                        ${c.tierName}
                                                    </span>
                                                </td>

                                                <td>

                                                    <c:choose>

                                                        <c:when test="${c.status == 'Active'}">

                                                            <span class="badge badge-active">
                                                                <i class="fas fa-check-circle"></i>
                                                                Active
                                                            </span>

                                                        </c:when>

                                                        <c:otherwise>

                                                            <span class="badge badge-inactive">
                                                                <i class="fas fa-ban"></i>
                                                                Inactive
                                                            </span>

                                                        </c:otherwise>

                                                    </c:choose>

                                                </td>

                                                <td>${c.ownerName}</td>

                                            </tr>

                                        </c:forEach>

                                    </tbody>

                                </table>

                            </div>

                            <!-- Buttons -->

                            <div style="padding:20px; display:flex; justify-content:flex-end; gap:10px;">

                                <form action="${pageContext.request.contextPath}/admin/customer-import-confirm"
                                      method="post">

                                    <c:forEach var="c" items="${previewCustomers}" varStatus="s">

                                        <input type="hidden" name="name${s.index}" value="${c.fullName}">
                                        <input type="hidden" name="email${s.index}" value="${c.email}">
                                        <input type="hidden" name="phone${s.index}" value="${c.phone}">
                                        <input type="hidden" name="address${s.index}" value="${c.address}">
                                        <input type="hidden" name="tier${s.index}" value="${c.tierId}">
                                        <input type="hidden" name="owner${s.index}" value="${c.ownerId}">
                                        <input type="hidden" name="status${s.index}" value="${c.status}">

                                    </c:forEach>

                                    <input type="hidden" name="size" value="${previewCustomers.size()}">

                                    <button class="btn btn-primary">
                                        <i class="fas fa-check"></i>
                                        Import
                                    </button>

                                </form>

                                <a href="${pageContext.request.contextPath}/managecustomer"
                                   class="btn btn-clear">

                                    <i class="fas fa-times"></i>
                                    Cancel

                                </a>

                            </div>

                        </c:otherwise>

                    </c:choose>

                </div>

            </div>

        </div>

    </body>
</html>