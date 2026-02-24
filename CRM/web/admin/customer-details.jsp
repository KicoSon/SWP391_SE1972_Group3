<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết Khách hàng - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/customer-details.css">
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/components/sidebar.jsp" />

    <div class="main-content">
        <div class="container">
            <!-- Breadcrumb -->
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-home"></i> Dashboard
                </a>
                <i class="fas fa-chevron-right"></i>
                <a href="${pageContext.request.contextPath}/managecustomer">
                    <i class="fas fa-users"></i> Quản lý Khách hàng
                </a>
                <i class="fas fa-chevron-right"></i>
                <span>${customer.fullName}</span>
            </div>

            <!-- Page Header -->
            <div class="page-header">
                <div class="header-left">
                    <h1>
                        <i class="fas fa-user"></i>
                        ${customer.fullName}
                    </h1>
                    <c:choose>
                        <c:when test="${customer.active}">
                            <span class="status-badge status-active">
                                <i class="fas fa-check-circle"></i> Đang hoạt động
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-badge status-inactive">
                                <i class="fas fa-ban"></i> Đã khóa
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="header-actions">
                    <a href="${pageContext.request.contextPath}/managecustomer?action=edit&id=${customer.id}" class="btn btn-edit">
                        <i class="fas fa-edit"></i> Chỉnh sửa
                    </a>
                    <c:choose>
                        <c:when test="${customer.active}">
                            <button onclick="confirmBan(${customer.id}, '${customer.fullName}')" class="btn btn-ban">
                                <i class="fas fa-lock"></i> Khóa tài khoản
                            </button>
                        </c:when>
                        <c:otherwise>
                            <button onclick="confirmUnban(${customer.id}, '${customer.fullName}')" class="btn btn-unban">
                                <i class="fas fa-unlock"></i> Mở khóa
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Customer Information -->
            <div class="info-grid">
                <div class="info-card">
                    <div class="card-header">
                        <i class="fas fa-user"></i>
                        <h3>Thông tin cá nhân</h3>
                    </div>
                    <div class="card-body">
                        <div class="info-row">
                            <span class="info-label"><i class="fas fa-id-card"></i> ID:</span>
                            <span class="info-value">#${customer.id}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label"><i class="fas fa-envelope"></i> Email:</span>
                            <span class="info-value">${customer.email}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label"><i class="fas fa-phone"></i> Số điện thoại:</span>
                            <span class="info-value">${customer.phone}</span>
                        </div>
                    </div>
                </div>

                <div class="info-card">
                    <div class="card-header">
                        <i class="fas fa-map-marker-alt"></i>
                        <h3>Địa chỉ</h3>
                    </div>
                    <div class="card-body">
                        <div class="info-row">
                            <span class="info-label"><i class="fas fa-home"></i> Địa chỉ:</span>
                            <span class="info-value">${customer.address}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label"><i class="fas fa-city"></i> Thành phố:</span>
                            <span class="info-value">${customer.city}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label"><i class="fas fa-map"></i> Tỉnh/TP:</span>
                            <span class="info-value">${customer.province}</span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Statistics -->
            <div class="stats-grid">
                <div class="stat-card stat-orders">
                    <div class="stat-icon">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="stat-details">
                        <div class="stat-value">${not empty orders ? orders.size() : 0}</div>
                        <div class="stat-label">Đơn hàng</div>
                    </div>
                </div>

                <div class="stat-card stat-tickets">
                    <div class="stat-icon">
                        <i class="fas fa-ticket-alt"></i>
                    </div>
                    <div class="stat-details">
                        <div class="stat-value">${not empty tickets ? tickets.size() : 0}</div>
                        <div class="stat-label">Phiếu hỗ trợ</div>
                    </div>
                </div>

                <div class="stat-card stat-revenue">
                    <div class="stat-icon">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div class="stat-details">
                        <div class="stat-value">
                            <c:set var="totalRevenue" value="0"/>
                            <c:forEach items="${orders}" var="order">
                                <c:if test="${order.status == 'PAID' || order.status == 'SHIPPED' || order.status == 'COMPLETED'}">
                                    <c:set var="totalRevenue" value="${totalRevenue + order.totalAmount}"/>
                                </c:if>
                            </c:forEach>
                            <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                        </div>
                        <div class="stat-label">Tổng chi tiêu</div>
                    </div>
                </div>
            </div>

            <!-- Orders Section -->
            <div class="section-card">
                <div class="section-header">
                    <h2>
                        <i class="fas fa-shopping-cart"></i>
                        Đơn hàng (${not empty orders ? orders.size() : 0})
                    </h2>
                </div>
                <div class="section-body">
                    <c:choose>
                        <c:when test="${empty orders}">
                            <div class="empty-state">
                                <i class="fas fa-shopping-cart"></i>
                                <p>Chưa có đơn hàng nào</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>Mã đơn</th>
                                            <th>Ngày đặt</th>
                                            <th>Tổng tiền</th>
                                            <th>Trạng thái</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${orders}" var="order">
                                            <tr>
                                                <td><strong>#${order.orderNo}</strong></td>
                                                <td>
                                                    <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm"/>
                                                </td>
                                                <td>
                                                    <strong class="amount">
                                                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" groupingUsed="true"/>
                                                    </strong>
                                                </td>
                                                <td>
                                                    <span class="order-status status-${order.status.toLowerCase()}">
                                                        ${order.status}
                                                    </span>
                                                </td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/customer/order-details?id=${order.id}" 
                                                       class="btn-sm btn-view" 
                                                       target="_blank">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Tickets Section -->
            <div class="section-card">
                <div class="section-header">
                    <h2>
                        <i class="fas fa-ticket-alt"></i>
                        Phiếu hỗ trợ (${not empty tickets ? tickets.size() : 0})
                    </h2>
                </div>
                <div class="section-body">
                    <c:choose>
                        <c:when test="${empty tickets}">
                            <div class="empty-state">
                                <i class="fas fa-ticket-alt"></i>
                                <p>Chưa có phiếu hỗ trợ nào</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>Mã phiếu</th>
                                            <th>Tiêu đề</th>
                                            <th>Ngày tạo</th>
                                            <th>Trạng thái</th>
                                            <th>Ưu tiên</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${tickets}" var="ticket">
                                            <tr>
                                                <td><strong>#${ticket.id}</strong></td>
                                                <td>${ticket.subject}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty ticket.createdAtDate}">
                                                            <fmt:formatDate value="${ticket.createdAtDate}" pattern="dd/MM/yyyy"/>
                                                        </c:when>
                                                        <c:otherwise>
                                                            -
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty ticket.status}">
                                                            <span class="ticket-status status-${fn:toLowerCase(ticket.status)}">
                                                                ${ticket.status}
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="ticket-status status-open">OPEN</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty ticket.priority}">
                                                            <span class="priority-badge priority-${fn:toLowerCase(ticket.priority)}">
                                                                ${ticket.priority}
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="priority-badge priority-medium">MEDIUM</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/support/agent/ticket-details?id=${ticket.id}" 
                                                       class="btn-sm btn-view"
                                                       target="_blank">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>

    <!-- Ban/Unban Form (hidden) -->
    <form id="statusForm" method="POST" action="${pageContext.request.contextPath}/managecustomer" style="display: none;">
        <input type="hidden" name="action" id="statusAction">
        <input type="hidden" name="customerId" id="customerId">
    </form>

    <script>
        function confirmBan(customerId, customerName) {
            if (confirm('Bạn có chắc muốn khóa tài khoản của "' + customerName + '"?')) {
                document.getElementById('statusAction').value = 'ban';
                document.getElementById('customerId').value = customerId;
                document.getElementById('statusForm').submit();
            }
        }

        function confirmUnban(customerId, customerName) {
            if (confirm('Bạn có chắc muốn mở khóa tài khoản của "' + customerName + '"?')) {
                document.getElementById('statusAction').value = 'unban';
                document.getElementById('customerId').value = customerId;
                document.getElementById('statusForm').submit();
            }
        }
    </script>
</body>
</html>
