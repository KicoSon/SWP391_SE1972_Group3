<%-- 
    Document   : activityDasboard
    Created on : Feb 3, 2026, 3:48:59 PM
    Author     : Viehai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Activity Dashboard - Viehai</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-dashboard.css">
    </head>
    <body>
        <div class="container">
            <div class="header">

            </div>
            <!-- Main Page -->
            <div class="page-wrapper">
                <h1 class="page-title">Activity Dashboard - Viehai (ID: 123)</h1>

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
                        <a href="activity-create.jsp" class="btn btn-primary" style="text-decoration: none; display: inline-flex; align-items: center; justify-content: center;">
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
                        <tr>
                            <td>18/01/2026<br>10:30</td>
                            <td>
                                <div class="activity-type">
                                    <span class="activity-icon">📞</span>
                                    <span>Call</span>
                                </div>
                            </td>
                            <td>Gọi tư vấn sản phẩm</td>
                            <td>Khách hỏi giá laptop, đồng ý hẹn gặp</td>
                            <td>Customer A</td>
                            <td>-</td>
                            <td><span class="status-badge status-completed">Completed</span></td>
                            <td>Viehai</td>
                            <td>Manager</td>
                            <td>
                                <div class="action-links">
                                    <a href="#" class="action-link">Edit</a>
                                    <span class="action-separator">|</span>
                                    <a href="#" class="action-link">Delete</a>
                                    <span class="action-separator">|</span>
                                    <a href="#" class="action-link">Comment</a>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>17/01/2026<br>14:00</td>
                            <td>
                                <div class="activity-type">
                                    <span class="activity-icon">✓</span>
                                    <span>Task</span>
                                </div>
                            </td>
                            <td>Follow-up gửi báo giá</td>
                            <td>Gửi catalog + khuyến mãi tháng 1</td>
                            <td>Customer D</td>
                            <td>19/01/2026</td>
                            <td><span class="status-badge status-pending">Pending</span></td>
                            <td>Nam</td>
                            <td>Viehai</td>
                            <td>
                                <div class="action-links">
                                    <a href="#" class="action-link">Edit</a>
                                    <span class="action-separator">|</span>
                                    <a href="#" class="action-link">Delete</a>
                                    <span class="action-separator">|</span>
                                    <a href="#" class="action-link">Comment</a>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>16/01/2026<br>11:00</td>
                            <td>
                                <div class="activity-type">
                                    <span class="activity-icon">✉️</span>
                                    <span>Email</span>
                                </div>
                            </td>
                            <td>Xác nhận đơn hàng</td>
                            <td>Khách reply "Cảm ơn, chờ giao hàng"</td>
                            <td>Customer C</td>
                            <td>-</td>
                            <td><span class="status-badge status-completed">Completed</span></td>
                            <td>-</td>
                            <td>Minh</td>
                            <td>
                                <div class="action-links">
                                    <a href="#" class="action-link">Edit</a>
                                    <span class="action-separator">|</span>
                                    <a href="#" class="action-link">Delete</a>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>15/01/2026<br>04:00</td>
                            <td>
                                <div class="activity-type">
                                    <span class="activity-icon">📝</span>
                                    <span>Note</span>
                                </div>
                            </td>
                            <td>Ghi chú hài bò</td>
                            <td>🤗Support Khách cần hỗ trợ lắp đặt thêm</td>
                            <td>Customer A</td>
                            <td>-</td>
                            <td>-</td>
                            <td>-</td>
                            <td>Viehai</td>
                            <td>
                                <div class="action-links">
                                    <a href="#" class="action-link">Edit</a>
                                    <span class="action-separator">|</span>
                                    <a href="#" class="action-link">Delete</a>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>14/01/2026<br>16:00</td>
                            <td>
                                <div class="activity-type">
                                    <span class="activity-icon">🎥</span>
                                    <span>Meeting</span>
                                </div>
                            </td>
                            <td>Demo sản phẩm tại nhà</td>
                            <td>Khách hỏi kỹ, upsell thêm phụ kiện</td>
                            <td>Customer B</td>
                            <td>-</td>
                            <td><span class="status-badge status-overdue">Overdue</span></td>
                            <td>Nam</td>
                            <td>Minh</td>
                            <td>
                                <div class="action-links">
                                    <a href="#" class="action-link">Edit</a>
                                    <span class="action-separator">|</span>
                                    <a href="#" class="action-link">Delete</a>
                                    <span class="action-separator">|</span>
                                    <a href="#" class="action-link">Comment</a>
                                </div>
                            </td>
                        </tr>
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