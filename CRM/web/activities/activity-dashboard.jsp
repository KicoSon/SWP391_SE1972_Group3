
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Sale Staff</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-dashboard.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        
        <style>
            .admin-header {
                background: rgba(255,255,255,0.95);
                backdrop-filter: blur(20px);
                padding: 20px 30px;
                margin-left: 270px;
                border-radius: 0 0 20px 20px;
                box-shadow: 0 5px 20px rgba(0,0,0,0.1);
                display: flex;
                justify-content: flex-end;
                align-items: center;
                position: sticky;
                top: 0;
                z-index: 1000;
            }

            .admin-info {
                display: flex;
                align-items: center;
                gap: 20px;
            }

            .admin-welcome {
                font-size: 16px;
                font-weight: 500;
                color: #333;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .admin-welcome i {
                color: #667eea;
                font-size: 20px;
            }

            .btn-logout {
                background: linear-gradient(135deg, #f093fb, #f5576c);
                color: white;
                padding: 8px 16px;
                border-radius: 8px;
                font-weight: 600;
                text-decoration: none;
                transition: 0.3s;
            }

            .btn-logout:hover {
                opacity: 0.85;
            }

            .main-content {
                margin-left: 270px;
                padding: 0;
            }

            .fade-in {
                animation: fadeIn 0.7s ease-in;
            }

            @keyframes fadeIn {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }
        </style>
    </head>
    <body>

        <%@ include file="sidebar.jsp" %>
        <!-- Header riêng cho Admin -->
        <div class="admin-header fade-in">
            <div class="admin-info">
                <div class="admin-welcome">
                    <div>

                        <i class="fas fa-user"></i>

                        Xin chào,

                        <b>${sessionScope.userSession.displayName}</b>

                    </div>


                </div>
                <a href="<%= request.getContextPath() %>/logout" class="btn-logout">
                    <i class="fas fa-right-from-bracket"></i> Đăng xuất
                </a>
            </div>
        </div>

        <!-- Main Page Content -->
        <div class="main-content">
        <div class="page-wrapper">
            <h1 class="page-title">Activity Dashboard - ${sessionScope.userSession.displayName} </h1>

            <!-- Filter Bar -->
            <div class="filter-bar">
                <input type="text" class="search-input" placeholder="Search">

                <div class="filter-group">
                    <span class="filter-label">Filter Type:</span>
                    <select>
                        <option>All</option>
                        <option>Call</option>
                        <option>Task</option>
                        <option>Email</option>
                        <option>Note</option>
                        <option>Meeting</option>
                    </select>
                </div>

                <div class="filter-group">
                    <span class="filter-label">From</span>
                    <input type="date" value="2026-01-01">
                </div>

                <div class="filter-group">
                    <span class="filter-label">To</span>
                    <input type="date" value="2026-02-03">
                </div>

                <button class="btn btn-primary">Filter</button>
            </div>

            <!-- Action Bar -->
            <div class="action-bar">
                <div class="action-buttons">
                    <a href="${pageContext.request.contextPath}/activities/create" class="btn btn-primary" style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center;">
                        + Add activity
                    </a>
                    <button class="btn btn-success">
                        Export Excel
                        <span class="badge-note">Manager only*</span>
                    </button>
                </div>
                <div class="overdue-indicator">overdue tasks: 2</div>
            </div>

            <!-- Activity Table -->
            <table class="activity-table">
                <thead>
                    <tr>
                        <th>Time</th>
                        <th>Type</th>
                        <th>Title</th>
                        <th>Description</th>
                        <th>Customer</th>
                        <th>Due Date</th>
                        <th>Status</th>
                        <th>Assigned To</th>
                        <th>Creator</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Activities sẽ được load từ controller/AJAX -->
                </tbody>
            </table>

            <!-- Pagination -->
            <div class="pagination">
                <div class="pagination-controls">
                    <button class="page-btn" disabled>&lt;</button>
                    <span class="page-info">Page 1 / 3</span>
                    <button class="page-btn">&gt;</button>
                </div>
                <div class="total-info">Total: 25 activities</div>
            </div>

            <!-- Summary Section -->
            <div class="summary-section">
                <h2 class="summary-title">Summary</h2>

                <div class="summary-stats">
                    <div class="stat-item">
                        <span class="stat-label">Tasks: 10</span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Overdue: <span class="stat-value stat-overdue">2</span></span>
                    </div>
                    <div class="stat-item">
                        <span class="stat-label">Completed: <span class="stat-value stat-completed">8</span></span>
                    </div>
                </div>

                <div class="chart-container">
                    <div class="chart-bar chart-bar-blue"></div>
                    <div class="chart-bar chart-bar-yellow"></div>
                    <div class="chart-bar chart-bar-green"></div>
                    <div class="chart-bar chart-bar-purple"></div>
                </div>
            </div>
        </div>
        </div>

        <script src="${pageContext.request.contextPath}/assets/js/activity-dashboard.js"></script>

    </body>
</html>