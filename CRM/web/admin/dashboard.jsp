<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Quản trị - SWP Support</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/admin-dashboard.css">
</head>
<body>
    <!-- Include Sidebar Component -->
    <jsp:include page="../components/sidebar.jsp" />
    
    <!-- Main Content -->
    <div class="main-content">
        <!-- Dashboard Container -->
        <div class="dashboard-container">
            <!-- Header -->
            <header class="dashboard-header">
                <div class="header-content">
                    <div class="header-left">
                        <h1>Dashboard Quản trị</h1>
                        <p class="subtitle">Tổng quan hệ thống</p>
                    </div>
                </div>
            </header>
            
            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon revenue">
                        <i class="fas fa-dollar-sign"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Tổng doanh thu</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${totalRevenue}" type="currency" currencySymbol="₫" groupingUsed="true" />
                        </div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon orders">
                        <i class="fas fa-shopping-cart"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Tổng đơn hàng</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${totalOrders}" />
                        </div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon customers">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Khách hàng</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${totalCustomers}" />
                        </div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon products">
                        <i class="fas fa-box"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Sản phẩm</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${totalProducts}" />
                        </div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon staff">
                        <i class="fas fa-user-tie"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Nhân viên</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${totalStaff}" />
                        </div>
                    </div>
                </div>
                
                <div class="stat-card">
                    <div class="stat-icon tickets">
                        <i class="fas fa-ticket-alt"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-label">Tickets</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${totalTickets}" />
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Charts Grid -->
            <div class="charts-grid">
                <!-- Revenue Chart -->
                <div class="chart-card">
                    <div class="chart-header">
                        <div>
                            <h3 class="chart-title">Doanh thu theo tháng</h3>
                            <p class="chart-subtitle">12 tháng gần nhất</p>
                        </div>
                    </div>
                    <div class="chart-content">
                        <canvas id="revenueChart"></canvas>
                    </div>
                </div>
                
                <!-- Orders Status Chart -->
                <div class="chart-card">
                    <div class="chart-header">
                        <div>
                            <h3 class="chart-title">Đơn hàng theo trạng thái</h3>
                            <p class="chart-subtitle">Tổng quan hiện tại</p>
                        </div>
                    </div>
                    <div class="chart-content">
                        <canvas id="ordersStatusChart"></canvas>
                    </div>
                </div>
            </div>
            
            <!-- Second Row Charts -->
            <div class="charts-grid">
                <!-- Tickets by Status Chart -->
                <div class="chart-card">
                    <div class="chart-header">
                        <div>
                            <h3 class="chart-title">Tickets theo trạng thái</h3>
                            <p class="chart-subtitle">Phân bổ tickets hiện tại</p>
                        </div>
                    </div>
                    <div class="chart-content">
                        <canvas id="ticketsStatusChart"></canvas>
                    </div>
                </div>
                
                <!-- Tickets by Priority Chart -->
                <div class="chart-card">
                    <div class="chart-header">
                        <div>
                            <h3 class="chart-title">Tickets theo mức độ</h3>
                            <p class="chart-subtitle">Phân loại theo độ ưu tiên</p>
                        </div>
                    </div>
                    <div class="chart-content">
                        <canvas id="ticketsPriorityChart"></canvas>
                    </div>
                </div>
            </div>
            
            <!-- Products and Recent Orders -->
            <div class="products-grid">
                <!-- Recent Orders -->
                <div class="table-card">
                    <div class="table-header">
                        <h3 class="table-title">Đơn hàng gần đây</h3>
                        <a href="${pageContext.request.contextPath}/admin/orders" class="view-all-btn">
                            Xem tất cả <i class="fas fa-arrow-right"></i>
                        </a>
                    </div>
                    <div class="table-content">
                        <table class="orders-table">
                            <thead>
                                <tr>
                                    <th>Mã ĐH</th>
                                    <th>Khách hàng</th>
                                    <th>Ngày đặt</th>
                                    <th>Tổng tiền</th>
                                    <th>Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${recentOrders}">
                                    <tr>
                                        <td class="order-id">#${order.id}</td>
                                        <td>${order.customerName}</td>
                                        <td>
                                            <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy" />
                                        </td>
                                        <td>
                                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${order.status == 'PENDING'}">
                                                    <span class="order-status status-pending">Chờ xử lý</span>
                                                </c:when>
                                                <c:when test="${order.status == 'PAID'}">
                                                    <span class="order-status status-paid">Đã thanh toán</span>
                                                </c:when>
                                                <c:when test="${order.status == 'SHIPPED'}">
                                                    <span class="order-status status-shipped">Đã giao</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="order-status status-cancelled">Đã hủy</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                
                <!-- Top Products -->
                <div class="table-card">
                    <div class="table-header">
                        <h3 class="table-title">Sản phẩm bán chạy</h3>
                        <a href="${pageContext.request.contextPath}/admin/products" class="view-all-btn">
                            Xem tất cả <i class="fas fa-arrow-right"></i>
                        </a>
                    </div>
                    <div class="table-content">
                        <ul class="top-products-list">
                            <c:forEach var="product" items="${topProducts}" varStatus="status">
                                <li class="product-item">
                                    <div class="product-rank">${status.index + 1}</div>
                                    <div class="product-info">
                                        <div class="product-name">${product.name}</div>
                                        <div class="product-sales">
                                            <fmt:formatNumber value="${product.totalSold}" /> đã bán
                                        </div>
                                    </div>
                                    <div class="product-revenue">
                                        <fmt:formatNumber value="${product.revenue}" type="currency" currencySymbol="₫" />
                                    </div>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <script>
        // Revenue Chart Data
        const revenueData = {
            labels: [
                <c:forEach var="trend" items="${revenueByMonth}" varStatus="status">
                    '${trend.label}'${!status.last ? ',' : ''}
                </c:forEach>
            ],
            datasets: [{
                label: 'Doanh thu (₫)',
                data: [
                    <c:forEach var="trend" items="${revenueByMonth}" varStatus="status">
                        ${trend.value}${!status.last ? ',' : ''}
                    </c:forEach>
                ],
                borderColor: 'rgb(102, 126, 234)',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                tension: 0.4,
                fill: true
            }]
        };

        const revenueChart = new Chart(document.getElementById('revenueChart'), {
            type: 'line',
            data: revenueData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return new Intl.NumberFormat('vi-VN').format(value) + '₫';
                            }
                        }
                    }
                }
            }
        });

        // Orders Status Chart
        const ordersStatusData = {
            labels: [
                <c:forEach var="entry" items="${ordersByStatus}" varStatus="status">
                    '${entry.key}'${!status.last ? ',' : ''}
                </c:forEach>
            ],
            datasets: [{
                data: [
                    <c:forEach var="entry" items="${ordersByStatus}" varStatus="status">
                        ${entry.value}${!status.last ? ',' : ''}
                    </c:forEach>
                ],
                backgroundColor: [
                    'rgba(255, 193, 7, 0.8)',
                    'rgba(40, 167, 69, 0.8)',
                    'rgba(23, 162, 184, 0.8)',
                    'rgba(220, 53, 69, 0.8)'
                ]
            }]
        };

        const ordersStatusChart = new Chart(document.getElementById('ordersStatusChart'), {
            type: 'doughnut',
            data: ordersStatusData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
        
        // Tickets by Status Chart
        const ticketsStatusData = {
            labels: [
                <c:forEach var="entry" items="${ticketsByStatus}" varStatus="status">
                    '${entry.key}'${!status.last ? ',' : ''}
                </c:forEach>
            ],
            datasets: [{
                data: [
                    <c:forEach var="entry" items="${ticketsByStatus}" varStatus="status">
                        ${entry.value}${!status.last ? ',' : ''}
                    </c:forEach>
                ],
                backgroundColor: [
                    'rgba(255, 193, 7, 0.8)',
                    'rgba(40, 167, 69, 0.8)',
                    'rgba(23, 162, 184, 0.8)',
                    'rgba(220, 53, 69, 0.8)',
                    'rgba(108, 117, 125, 0.8)'
                ]
            }]
        };

        const ticketsStatusChart = new Chart(document.getElementById('ticketsStatusChart'), {
            type: 'doughnut',
            data: ticketsStatusData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
        
        // Tickets by Priority Chart
        const ticketsPriorityData = {
            labels: [
                <c:forEach var="entry" items="${ticketsByPriority}" varStatus="status">
                    '${entry.key}'${!status.last ? ',' : ''}
                </c:forEach>
            ],
            datasets: [{
                label: 'Số lượng tickets',
                data: [
                    <c:forEach var="entry" items="${ticketsByPriority}" varStatus="status">
                        ${entry.value}${!status.last ? ',' : ''}
                    </c:forEach>
                ],
                backgroundColor: [
                    'rgba(220, 53, 69, 0.8)',
                    'rgba(255, 193, 7, 0.8)',
                    'rgba(40, 167, 69, 0.8)'
                ],
                borderColor: [
                    'rgb(220, 53, 69)',
                    'rgb(255, 193, 7)',
                    'rgb(40, 167, 69)'
                ],
                borderWidth: 2
            }]
        };

        const ticketsPriorityChart = new Chart(document.getElementById('ticketsPriorityChart'), {
            type: 'bar',
            data: ticketsPriorityData,
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
    </script>
</body>
</html>