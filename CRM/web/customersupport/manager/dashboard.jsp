<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Support Manager - SWP</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/support/manager/assets/css/dashboard.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <jsp:include page="/components/sidebar.jsp" />

    <div class="main-content">
        <div class="page-header">
            <h1><i class="fas fa-chart-line"></i> Dashboard Support Manager</h1>
            <p>Tổng quan và báo cáo hệ thống hỗ trợ khách hàng</p>
        </div>

        <!-- Statistics Cards -->
        <div class="stats-grid">
            <div class="stat-card total">
                <div class="icon">
                    <i class="fas fa-ticket-alt"></i>
                </div>
                <h3>${totalTickets}</h3>
                <p>Tổng Tickets</p>
            </div>

            <div class="stat-card open">
                <div class="icon">
                    <i class="fas fa-folder-open"></i>
                </div>
                <h3>${openTickets}</h3>
                <p>Đang Mở</p>
            </div>

            <div class="stat-card progress">
                <div class="icon">
                    <i class="fas fa-spinner"></i>
                </div>
                <h3>${inProgressTickets}</h3>
                <p>Đang Xử Lý</p>
            </div>

            <div class="stat-card resolved">
                <div class="icon">
                    <i class="fas fa-check-circle"></i>
                </div>
                <h3>${resolvedTickets}</h3>
                <p>Đã Giải Quyết</p>
            </div>

            <div class="stat-card closed">
                <div class="icon">
                    <i class="fas fa-times-circle"></i>
                </div>
                <h3>${closedTickets}</h3>
                <p>Đã Đóng</p>
            </div>
        </div>

        <!-- Charts -->
        <div class="charts-grid">
            <div class="chart-card">
                <h3><i class="fas fa-chart-pie"></i> Phân Bổ Theo Trạng Thái</h3>
                <canvas id="statusChart"></canvas>
            </div>

            <div class="chart-card">
                <h3><i class="fas fa-chart-bar"></i> Phân Bổ Theo Độ Ưu Tiên</h3>
                <canvas id="priorityChart"></canvas>
            </div>
        </div>

        <!-- Recent Tickets -->
        <div class="table-card">
            <h3><i class="fas fa-clock"></i> Tickets Gần Đây</h3>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tiêu Đề</th>
                        <th>Khách Hàng</th>
                        <th>Agent</th>
                        <th>Trạng Thái</th>
                        <th>Độ Ưu Tiên</th>
                        <th>Thời Gian</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ticket" items="${recentTickets}">
                        <tr>
                            <td>#${ticket.id}</td>
                            <td>${ticket.subject}</td>
                            <td>${ticket.customerName}</td>
                            <td>${ticket.staffName != null ? ticket.staffName : '<em>Chưa gán</em>'}</td>
                            <td>
                                <span class="status-badge ${ticket.status.toLowerCase().replace('_', '-')}">
                                    ${ticket.status}
                                </span>
                            </td>
                            <td>
                                <span class="priority-badge ${ticket.priority.toLowerCase()}">
                                    ${ticket.priority}
                                </span>
                            </td>
                            <td>
                                <fmt:formatDate value="${ticket.createdAtDate}" pattern="dd/MM/yyyy HH:mm"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- Agent Performance -->
        <div class="table-card">
            <h3><i class="fas fa-users"></i> Hiệu Suất Agents</h3>
            <table>
                <thead>
                    <tr>
                        <th>Agent</th>
                        <th>Tổng Tickets</th>
                        <th>Đã Giải Quyết</th>
                        <th>Đã Đóng</th>
                        <th>Tỷ Lệ Hoàn Thành</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="agent" items="${agentStats}">
                        <tr>
                            <td>${agent.agentName}</td>
                            <td>${agent.totalTickets}</td>
                            <td>${agent.resolvedTickets}</td>
                            <td>${agent.closedTickets}</td>
                            <td>
                                <c:set var="completionRate" value="${agent.totalTickets > 0 ? ((agent.resolvedTickets + agent.closedTickets) * 100.0 / agent.totalTickets) : 0}"/>
                                <fmt:formatNumber value="${completionRate}" maxFractionDigits="1"/>%
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <script>
        // Status Chart
        const statusCtx = document.getElementById('statusChart').getContext('2d');
        new Chart(statusCtx, {
            type: 'doughnut',
            data: {
                labels: ['Đang Mở', 'Đang Xử Lý', 'Đã Giải Quyết', 'Đã Đóng'],
                datasets: [{
                    data: [
                        ${statusStats['OPEN'] != null ? statusStats['OPEN'] : 0},
                        ${statusStats['IN_PROGRESS'] != null ? statusStats['IN_PROGRESS'] : 0},
                        ${statusStats['RESOLVED'] != null ? statusStats['RESOLVED'] : 0},
                        ${statusStats['CLOSED'] != null ? statusStats['CLOSED'] : 0}
                    ],
                    backgroundColor: [
                        'rgba(245, 87, 108, 0.8)',
                        'rgba(0, 181, 216, 0.8)',
                        'rgba(56, 161, 105, 0.8)',
                        'rgba(128, 90, 213, 0.8)'
                    ],
                    borderWidth: 0
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });

        // Priority Chart
        const priorityCtx = document.getElementById('priorityChart').getContext('2d');
        new Chart(priorityCtx, {
            type: 'bar',
            data: {
                labels: ['Thấp', 'Trung Bình', 'Cao', 'Khẩn Cấp'],
                datasets: [{
                    label: 'Số Lượng Tickets',
                    data: [
                        ${priorityStats['LOW'] != null ? priorityStats['LOW'] : 0},
                        ${priorityStats['MEDIUM'] != null ? priorityStats['MEDIUM'] : 0},
                        ${priorityStats['HIGH'] != null ? priorityStats['HIGH'] : 0},
                        ${priorityStats['CRITICAL'] != null ? priorityStats['CRITICAL'] : 0}
                    ],
                    backgroundColor: [
                        'rgba(0, 181, 216, 0.8)',
                        'rgba(214, 158, 46, 0.8)',
                        'rgba(229, 62, 62, 0.8)',
                        'rgba(128, 90, 213, 0.8)'
                    ],
                    borderWidth: 0,
                    borderRadius: 8
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
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
