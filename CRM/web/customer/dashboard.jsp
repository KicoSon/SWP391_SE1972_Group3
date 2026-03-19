<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Khách hàng - SWP Support</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/customer/assets/css/customer-dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/customer/assets/css/dashboard-stats.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
    <div class="dashboard-layout">
        <!-- Sidebar dùng chung -->
        <jsp:include page="sidebar.jsp" />
        
        <!-- Main Content -->
        <main class="main-content">
            <div class="content-wrapper">
                <!-- Page Header -->
                <div class="page-header">
                    <h1><i class="fas fa-tachometer-alt"></i> Dashboard Khách hàng</h1>
                    <p class="subtitle">Xin chào, <strong>${userSession.displayName}</strong>!</p>
                </div>
                
                <!-- Success/Error Messages -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>
                
                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>
                
                <div class="dashboard-grid">
                <!-- Thống kê tổng quan -->
                <div class="stats-grid">
                    <div class="stat-card stat-primary">
                        <div class="stat-icon">
                            <i class="fas fa-ticket-alt"></i>
                        </div>
                        <div class="stat-content">
                            <h3>${ticketStats.TOTAL}</h3>
                            <p>Tổng phiếu hỗ trợ</p>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-success">
                        <div class="stat-icon">
                            <i class="fas fa-shopping-cart"></i>
                        </div>
                        <div class="stat-content">
                            <h3>${totalOrders}</h3>
                            <p>Tổng đơn hàng</p>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-warning">
                        <div class="stat-icon">
                            <i class="fas fa-clock"></i>
                        </div>
                        <div class="stat-content">
                            <h3>${ticketStats.IN_PROGRESS}</h3>
                            <p>Phiếu đang xử lý</p>
                        </div>
                    </div>
                    
                    <div class="stat-card stat-info">
                        <div class="stat-icon">
                            <i class="fas fa-check-circle"></i>
                        </div>
                        <div class="stat-content">
                            <h3>${ticketStats.RESOLVED}</h3>
                            <p>Phiếu đã giải quyết</p>
                        </div>
                    </div>
                </div>
                
                <!-- Biểu đồ trạng thái ticket -->
                <div class="dashboard-card">
                    <div class="card-header">
                        <h3><i class="fas fa-chart-pie"></i> Trạng thái phiếu hỗ trợ</h3>
                    </div>
                    <div class="card-content">
                        <div class="chart-container">
                            <canvas id="ticketStatusChart"
                                    data-open="${ticketStats.OPEN}"
                                    data-inprogress="${ticketStats.IN_PROGRESS}"
                                    data-resolved="${ticketStats.RESOLVED}"></canvas>
                        </div>
                    </div>
                </div>
                
                <!-- Thông tin tài khoản -->
                <div class="dashboard-card">
                    <div class="card-header">
                        <h3><i class="fas fa-user-circle"></i> Thông tin tài khoản</h3>
                    </div>
                    <div class="card-content">
                        <div class="info-row">
                            <span class="label">Họ tên:</span>
                            <span class="value">${userSession.customerInfo.fullName}</span>
                        </div>
                        <div class="info-row">
                            <span class="label">Email:</span>
                            <span class="value">${userSession.customerInfo.email}</span>
                        </div>
                        <div class="info-row">
                            <span class="label">Số điện thoại:</span>
                            <span class="value">${userSession.customerInfo.phone}</span>
                        </div>
                        <div class="info-row">
                            <span class="label">Địa chỉ:</span>
                            <span class="value">${userSession.customerInfo.address}</span>
                        </div>
                    </div>
                </div>
                
              
                <!-- Đơn hàng gần đây -->
                <div class="dashboard-card full-width">
                    <div class="card-header">
                        <h3><i class="fas fa-shopping-cart"></i> Đơn hàng gần đây</h3>
                        <!--<a href="${pageContext.request.contextPath}/customer/orders" class="view-all">Xem tất cả</a>-->
                    </div>
                    <div class="card-content">
                        <c:choose>
                            <c:when test="${empty recentOrders}">
                                <div class="empty-state">
                                    <i class="fas fa-shopping-cart"></i>
                                    <p>Chưa có đơn hàng nào</p>
                                    <small>Các đơn hàng của bạn sẽ hiển thị ở đây</small>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="data-table">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Mã đơn hàng</th>
                                                <th>Trạng thái</th>
                                                <th>Tổng tiền</th>
                                                <th>Ngày mua</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${recentOrders}" var="order">
                                                <tr>
                                                    <td><strong>#${order.id}</strong></td>
                                                    <td>
                                                        ${order.orderNumber}
                                                    </td>
                                                    <td>
                                                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" groupingUsed="true" maxFractionDigits="0"/>
                                                    </td>
                                                    <td>
                                                        <span class="order-status status-${order.status.toLowerCase()}">
                                                            ${order.status}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
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
                
                <!-- Phiếu hỗ trợ gần đây -->
                <div class="dashboard-card full-width">
                    <div class="card-header">
                        <h3><i class="fas fa-headset"></i> Phiếu hỗ trợ gần đây</h3>
                        <!--<a href="${pageContext.request.contextPath}/customer/tickets" class="view-all">Xem tất cả</a>-->
                    </div>
                    <div class="card-content">
                        <c:choose>
                            <c:when test="${empty tickets}">
                                <div class="empty-state">
                                    <i class="fas fa-ticket-alt"></i>
                                    <p>Chưa có phiếu hỗ trợ nào</p>
                                    <small>Các phiếu hỗ trợ của bạn sẽ hiển thị ở đây</small>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="data-table">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Vấn đề</th>
                                                <th>Ưu tiên</th>
                                                <th>Trạng thái</th>
                                                <th>Ngày tạo</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${tickets}" var="ticket">
                                                <tr>
                                                    <td><strong>#${ticket.id}</strong></td>
                                                    <td>
                                                        ${ticket.title}
                                                    </td>
                                                    <td>
                                                        ${ticket.priority}
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty ticket.status}">
                                                                <span class="ticket-status status-${fn:replace(fn:toLowerCase(ticket.status), ' ', '')}">
                                                                    ${ticket.status}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="ticket-status status-open">OPEN</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <fmt:formatDate value="${ticket.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
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
        </main>
    </div>
    <script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
    <script src="${pageContext.request.contextPath}/customer/assets/js/dashboard-chart.js"></script>
</body>
</html>
