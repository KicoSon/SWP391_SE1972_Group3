<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Support Agent Dashboard</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    
    <!-- Favicon -->
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico">
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/components/sidebar.jsp" />
    
    <!-- Main Content -->
    <div class="main-content">
        <!-- Header -->
        <header class="dashboard-header">
            <div class="header-left">
                <button class="mobile-menu-btn" id="mobileMenuBtn">
                    <i class="fas fa-bars"></i>
                </button>
                <h1 class="page-title">Dashboard</h1>
            </div>
            
            <div class="header-right">
                <div class="header-stats">
                    <div class="header-stat">
                        <span class="stat-label">Today's Tickets</span>
                        <span class="stat-value">${todayTickets}</span>
                    </div>
                </div>
                
                <div class="header-actions">
                    <button class="header-btn" id="refreshBtn" title="Refresh">
                        <i class="fas fa-sync-alt"></i>
                    </button>
                    
                    <div class="user-menu">
                        <button class="user-menu-btn">
                            <div class="user-avatar">
                                <i class="fas fa-user"></i>
                            </div>
                            <span class="user-name">
                                <c:choose>
                                    <c:when test="${not empty userSession.staffInfo}">
                                        ${userSession.staffInfo.fullName}
                                    </c:when>
                                    <c:otherwise>
                                        Support Agent
                                    </c:otherwise>
                                </c:choose>
                            </span>
                            <i class="fas fa-chevron-down"></i>
                        </button>
                        
                        <div class="user-menu-dropdown">
                            <a href="${pageContext.request.contextPath}/support/agent/profile" class="dropdown-item">
                                <i class="fas fa-user"></i>
                                Profile
                            </a>
                            <a href="${pageContext.request.contextPath}/support/agent/settings" class="dropdown-item">
                                <i class="fas fa-cog"></i>
                                Settings
                            </a>
                            <div class="dropdown-divider"></div>
                            <a href="${pageContext.request.contextPath}/logout" class="dropdown-item text-danger">
                                <i class="fas fa-sign-out-alt"></i>
                                Logout
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </header>
        
        <!-- Dashboard Content -->
        <div class="dashboard-content">
            <!-- Stats Cards -->
            <div class="stats-grid">
                <div class="stat-card stat-card-primary">
                    <div class="stat-icon">
                        <i class="fas fa-ticket-alt"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number">${assignedTrend.current}</div>
                        <div class="stat-label">Phiếu được giao (7 ngày qua)</div>
                        <div class="stat-change ${assignedTrend.changeClass}">
                            <i class="fas ${assignedTrend.changeIcon}"></i>
                            ${assignedTrend.change > 0 ? '+' : ''}${assignedTrend.change} phiếu •
                            <fmt:formatNumber value="${assignedTrend.changePercentage}" pattern="#0.#"/>% so với tuần trước
                        </div>
                    </div>
                </div>
                
                <div class="stat-card stat-card-warning">
                    <div class="stat-icon">
                        <i class="fas fa-clock"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number">${pendingTrend.current}</div>
                        <div class="stat-label">Phiếu đang chờ (7 ngày qua)</div>
                        <div class="stat-change ${pendingTrend.changeClass}">
                            <i class="fas ${pendingTrend.changeIcon}"></i>
                            ${pendingTrend.change > 0 ? '+' : ''}${pendingTrend.change} lần cập nhật •
                            <fmt:formatNumber value="${pendingTrend.changePercentage}" pattern="#0.#"/>% so với tuần trước
                        </div>
                    </div>
                </div>
                
                <div class="stat-card stat-card-success">
                    <div class="stat-icon">
                        <i class="fas fa-check-circle"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number">${resolvedTrend.current}</div>
                        <div class="stat-label">Phiếu đã xử lý (7 ngày qua)</div>
                        <div class="stat-change ${resolvedTrend.changeClass}">
                            <i class="fas ${resolvedTrend.changeIcon}"></i>
                            ${resolvedTrend.change > 0 ? '+' : ''}${resolvedTrend.change} phiếu •
                            <fmt:formatNumber value="${resolvedTrend.changePercentage}" pattern="#0.#"/>% so với tuần trước
                        </div>
                    </div>
                </div>
                
                <div class="stat-card stat-card-info">
                    <div class="stat-icon">
                        <i class="fas fa-calendar-day"></i>
                    </div>
                    <div class="stat-content">
                        <div class="stat-number">${todayTrend.current}</div>
                        <div class="stat-label">Phiếu trong ngày</div>
                        <div class="stat-change ${todayTrend.changeClass}">
                            <i class="fas ${todayTrend.changeIcon}"></i>
                            ${todayTrend.change > 0 ? '+' : ''}${todayTrend.change} so với hôm qua
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Main Dashboard Grid -->
            <div class="dashboard-grid">
                <!-- Recent Tickets -->
                <div class="dashboard-card">
                    <div class="card-header">
                        <h3 class="card-title">
                            <i class="fas fa-clock"></i>
                            Recent Tickets
                        </h3>
                        <div class="card-actions">
                            <a href="${pageContext.request.contextPath}/support/agent/tickets" class="btn btn-outline">
                                View All
                                <i class="fas fa-arrow-right"></i>
                            </a>
                        </div>
                    </div>
                    
                    <div class="card-content">
                        <c:choose>
                            <c:when test="${not empty recentTickets}">
                                <div class="ticket-list">
                                    <c:forEach var="ticket" items="${recentTickets}" end="4">
                                        <div class="ticket-item">
                                            <div class="ticket-info">
                                                <div class="ticket-title">${ticket.subject}</div>
                                                <div class="ticket-meta">
                                                    <span class="ticket-customer">
                                                        <i class="fas fa-user"></i>
                                                        ${ticket.customerName}
                                                    </span>
                                                    <span class="ticket-time">
                                                        <i class="fas fa-clock"></i>
                                                        ${ticket.createdAtFormatted}
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="ticket-badges">
                                                <span class="badge badge-${ticket.priorityClass}">${ticket.priority}</span>
                                                <span class="badge badge-${ticket.statusClass}">${ticket.status}</span>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-ticket-alt"></i>
                                    <h4>No recent tickets</h4>
                                    <p>You haven't been assigned any tickets recently.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Pending Tickets -->
                <div class="dashboard-card">
                    <div class="card-header">
                        <h3 class="card-title">
                            <i class="fas fa-exclamation-triangle"></i>
                            Pending Actions
                        </h3>
                        <div class="card-actions">
                            <a href="${pageContext.request.contextPath}/support/agent/tickets?status=pending" class="btn btn-outline">
                                View All
                                <i class="fas fa-arrow-right"></i>
                            </a>
                        </div>
                    </div>
                    
                    <div class="card-content">
                        <c:choose>
                            <c:when test="${not empty pendingTicketsList}">
                                <div class="ticket-list">
                                    <c:forEach var="ticket" items="${pendingTicketsList}">
                                        <div class="ticket-item urgent">
                                            <div class="ticket-info">
                                                <div class="ticket-title">${ticket.subject}</div>
                                                <div class="ticket-meta">
                                                    <span class="ticket-customer">
                                                        <i class="fas fa-user"></i>
                                                        ${ticket.customerName}
                                                    </span>
                                                    <span class="ticket-time">
                                                        <i class="fas fa-clock"></i>
                                                        ${ticket.createdAtFormatted}
                                                    </span>
                                                </div>
                                            </div>
                                            <div class="ticket-actions">
                                                <a href="${pageContext.request.contextPath}/support/agent/tickets/${ticket.id}" 
                                                   class="btn btn-sm btn-primary">
                                                    Resolve
                                                </a>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-check-circle"></i>
                                    <h4>All caught up!</h4>
                                    <p>No pending tickets requiring your attention.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Performance Chart -->
                <div class="dashboard-card chart-card">
                    <div class="card-header">
                        <h3 class="card-title">
                            <i class="fas fa-chart-line"></i>
                            Performance Overview
                        </h3>
                        <div class="card-actions">
                            <span class="chart-period-label">7 ngày gần nhất (${dashboardRangeLabel})</span>
                        </div>
                    </div>
                    
                    <div class="card-content">
                        <canvas id="performanceChart" width="400" height="200"></canvas>
                    </div>
                </div>
                
                <!-- Priority Distribution -->
                <div class="dashboard-card">
                    <div class="card-header">
                        <h3 class="card-title">
                            <i class="fas fa-chart-pie"></i>
                            Priority Distribution
                        </h3>
                    </div>
                    
                    <div class="card-content">
                        <div class="priority-stats">
                            <div class="priority-item">
                                <div class="priority-color priority-high"></div>
                                <div class="priority-info">
                                    <div class="priority-label">High Priority</div>
                                    <div class="priority-count">${agentStats.highPriorityTickets}</div>
                                </div>
                                <div class="priority-percentage">
                                    <c:set var="total" value="${agentStats.highPriorityTickets + agentStats.mediumPriorityTickets + agentStats.lowPriorityTickets}" />
                                    <c:choose>
                                        <c:when test="${total > 0}">
                                            <fmt:formatNumber value="${(agentStats.highPriorityTickets / total) * 100}" pattern="#.#"/>%
                                        </c:when>
                                        <c:otherwise>0%</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            
                            <div class="priority-item">
                                <div class="priority-color priority-medium"></div>
                                <div class="priority-info">
                                    <div class="priority-label">Medium Priority</div>
                                    <div class="priority-count">${agentStats.mediumPriorityTickets}</div>
                                </div>
                                <div class="priority-percentage">
                                    <c:choose>
                                        <c:when test="${total > 0}">
                                            <fmt:formatNumber value="${(agentStats.mediumPriorityTickets / total) * 100}" pattern="#.#"/>%
                                        </c:when>
                                        <c:otherwise>0%</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            
                            <div class="priority-item">
                                <div class="priority-color priority-low"></div>
                                <div class="priority-info">
                                    <div class="priority-label">Low Priority</div>
                                    <div class="priority-count">${agentStats.lowPriorityTickets}</div>
                                </div>
                                <div class="priority-percentage">
                                    <c:choose>
                                        <c:when test="${total > 0}">
                                            <fmt:formatNumber value="${(agentStats.lowPriorityTickets / total) * 100}" pattern="#.#"/>%
                                        </c:when>
                                        <c:otherwise>0%</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                        
                        <div class="response-time">
                            <div class="response-time-label">Average Response Time</div>
                            <div class="response-time-value">
                                <fmt:formatNumber value="${agentStats.avgResponseTime}" pattern="#.#"/> hours
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- JavaScript -->
    <script>
        window.dashboardData = window.dashboardData || {};
        window.dashboardData.performanceChart = {
            rangeLabel: '${dashboardRangeLabel}',
            labels: [
                <c:forEach var="stat" items="${dailyStats}" varStatus="loop">
                    '${stat.label}'${loop.last ? '' : ','}
                </c:forEach>
            ],
            newTickets: [
                <c:forEach var="stat" items="${dailyStats}" varStatus="loop">
                    ${stat.newTickets}${loop.last ? '' : ','}
                </c:forEach>
            ],
            resolvedTickets: [
                <c:forEach var="stat" items="${dailyStats}" varStatus="loop">
                    ${stat.resolvedTickets}${loop.last ? '' : ','}
                </c:forEach>
            ],
            pendingTickets: [
                <c:forEach var="stat" items="${dailyStats}" varStatus="loop">
                    ${stat.pendingTickets}${loop.last ? '' : ','}
                </c:forEach>
            ]
        };
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/dashboard.js"></script>
    <script>
        // Set active navigation for dashboard
        document.addEventListener('DOMContentLoaded', function() {
            const dashboardLink = document.querySelector('a[href*="/dashboard"]');
            if (dashboardLink) {
                dashboardLink.closest('.nav-item').classList.add('active');
            }
        });
    </script>
</body>
</html>