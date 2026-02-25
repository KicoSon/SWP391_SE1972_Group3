<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <title>Sale Staff</title>
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
                        rel="stylesheet">
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-dashboard.css">
                    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
                </head>

                <body>

                    <c:if test="${not empty sessionScope.success}">
                        <div id="toastSuccess" class="toast-success">
                            <i class="fas fa-check-circle"></i>
                            ${sessionScope.success}
                        </div>
                        <c:remove var="success" scope="session" />
                    </c:if>

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

                            <!-- Page Header Card -->
                            <div class="page-header-card fade-in">
                                <h2>
                                    <i class="fas fa-chart-line"></i>
                                    Activity Dashboard
                                </h2>
                                <a href="${pageContext.request.contextPath}/activities/create" class="btn btn-primary">
                                    <i class="fas fa-plus"></i> Add Activity
                                </a>
                            </div>

                            <div class="page-wrapper">

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
                                        <button class="btn btn-success">
                                            <i class="fas fa-file-excel"></i>
                                            Export Excel
                                            <span class="badge-note">Manager only*</span>
                                        </button>
                                    </div>
                                    <div class="overdue-indicator"><i class="fas fa-exclamation-triangle"></i> overdue
                                        tasks: 2</div>
                                </div>

                                <!-- Activity Table -->
                                <div class="card">
                                    <div class="card-body">
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
                                                <c:forEach items="${activities}" var="act">
                                                    <tr>
                                                        <td>
                                                            <fmt:formatDate value="${act.createdAt}"
                                                                pattern="dd/MM/yyyy HH:mm" />
                                                        </td>

                                                        <td>
                                                            <span class="type-badge type-${fn:toLowerCase(act.type)}">
                                                                <c:choose>
                                                                    <c:when test="${act.type == 'Call'}"><i
                                                                            class="fas fa-phone-alt"></i></c:when>
                                                                    <c:when test="${act.type == 'Email'}"><i
                                                                            class="fas fa-envelope"></i></c:when>
                                                                    <c:when test="${act.type == 'Meeting'}"><i
                                                                            class="fas fa-users"></i></c:when>
                                                                    <c:when test="${act.type == 'Task'}"><i
                                                                            class="fas fa-tasks"></i></c:when>
                                                                    <c:when test="${act.type == 'Note'}"><i
                                                                            class="fas fa-sticky-note"></i></c:when>
                                                                    <c:otherwise><i class="fas fa-briefcase"></i>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                ${act.type}
                                                            </span>
                                                        </td>

                                                        <td style="font-weight: 500;">${act.title}</td>

                                                        <td style="max-width: 150px;" title="${act.description}">
                                                            <div class="text-truncate">
                                                                ${empty act.description ? '---' : act.description}
                                                            </div>
                                                        </td>

                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${not empty act.customerName}">
                                                                    <span style="color: #2e5bff; font-weight: 500;"><i
                                                                            class="fas fa-building"></i>
                                                                        ${act.customerName}</span>
                                                                </c:when>
                                                                <c:when test="${not empty act.leadName}">
                                                                    <span style="color: #00b8d9; font-weight: 500;"><i
                                                                            class="fas fa-bullseye"></i>
                                                                        ${act.leadName}</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span style="color: #999;"><i>---</i></span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>

                                                        <td>
                                                            <c:if test="${not empty act.dueDate}">
                                                                <span class="due-date">
                                                                    <i class="far fa-calendar-alt"></i>
                                                                    <fmt:formatDate value="${act.dueDate}"
                                                                        pattern="dd/MM/yyyy HH:mm" />
                                                                </span>
                                                            </c:if>
                                                        </td>

                                                        <td>
                                                            <span
                                                                class="status-badge status-${fn:toLowerCase(fn:replace(act.status, ' ', '-'))}">
                                                                ${act.status}
                                                            </span>
                                                        </td>

                                                        <td>
                                                            <span class="badge badge-light">
                                                                <i class="fas fa-user-circle"></i> ${not empty
                                                                act.assigneeName ? act.assigneeName : 'Chưa phân công'}
                                                            </span>
                                                        </td>

                                                        <td>
                                                            <span style="color: #666; font-size: 0.9em;">
                                                                ${not empty act.creatorName ? act.creatorName : 'Hệ
                                                                thống'}
                                                            </span>
                                                        </td>

                                                        <td class="action-cell">
                                                            <a href="javascript:void(0)"
                                                                onclick="openDetailModal(${act.id})"
                                                                class="action-btn view-btn" title="Xem chi tiết"><i
                                                                    class="fas fa-eye"></i></a>
                                                    </tr>
                                                </c:forEach>

                                                <c:if test="${empty activities}">
                                                    <tr>
                                                        <td colspan="10"
                                                            style="text-align: center; padding: 20px; color: #666;">
                                                            Không tìm thấy hoạt động nào.
                                                        </td>
                                                    </tr>
                                                </c:if>
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
                                    </div><!-- /.card-body -->
                                </div><!-- /.card -->

                                <!-- Summary Section -->
                                <div class="summary-section">
                                    <h2 class="summary-title">Summary</h2>

                                    <div class="summary-stats">
                                        <div class="stat-item">
                                            <span class="stat-label">Tasks: 10</span>
                                        </div>
                                        <div class="stat-item">
                                            <span class="stat-label">Overdue: <span
                                                    class="stat-value stat-overdue">2</span></span>
                                        </div>
                                        <div class="stat-item">
                                            <span class="stat-label">Completed: <span
                                                    class="stat-value stat-completed">8</span></span>
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
                <div id="detailModalOverlay" class="detail-modal-overlay">
                    <div class="detail-modal-content">
                        <button class="detail-modal-close" onclick="closeDetailModal()"><i
                                class="fas fa-times"></i></button>
                        <iframe id="detailIframe" class="detail-modal-iframe" src=""></iframe>
                    </div>
                </div>

                </html>
