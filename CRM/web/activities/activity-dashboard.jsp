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
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
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

                    <%-- Load sidebar đúng theo vai trò --%>
                        <c:choose>
                            <c:when test="${sessionScope.userSession.admin}">
                                <jsp:include page="/components/sidebar.jsp" />
                            </c:when>
                            <c:when test="${sessionScope.userSession.supportStaff}">
                                <jsp:include page="/customerservice/sidebar.jsp" />
                            </c:when>
                            <c:when test="${sessionScope.userSession.marketingStaff}">
                                <jsp:include page="/marketingg/sidebar.jsp" />
                            </c:when>
                            <c:otherwise>
                                <%-- Sale (và mặc định) --%>
                                    <jsp:include page="/sales/sidebar.jsp" />
                            </c:otherwise>
                        </c:choose>
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

                            <div class="page-header-card fade-in">
                                <h2>
                                    <i class="fas fa-chart-line"></i>
                                    Activity Dashboard
                                </h2>
                                <%-- Chỉ Manager, Sale, Support mới được tạo Activity --%>
                                    <c:if test="${!sessionScope.userSession.marketingStaff}">
                                        <a href="${pageContext.request.contextPath}/activities/create"
                                            class="btn btn-primary">
                                            <i class="fas fa-plus"></i> Add Activity
                                        </a>
                                    </c:if>
                            </div>

                            <div class="page-wrapper">
                                <div class="summary-section" style="margin-bottom: 20px;">
                                    <h2 class="summary-title">Overview</h2>

                                    <div class="summary-stats"
                                        style="display: flex; gap: 20px; align-items: center; flex-wrap: wrap;">

                                        <div class="stat-item"
                                            style="padding-right: 20px; border-right: 2px solid #eee;">
                                            <span class="stat-label">Total:
                                                <span
                                                    style="font-weight: 800; font-size: 1.2em; color: #111827;">${statTotal}</span>
                                            </span>
                                        </div>

                                        <div class="stat-item">
                                            <span class="stat-label">Planned:
                                                <span style="color: #3B82F6; font-weight: bold;">${statPlanned}</span>
                                            </span>
                                        </div>

                                        <div class="stat-item">
                                            <span class="stat-label">In Progress:
                                                <span
                                                    style="color: #F59E0B; font-weight: bold;">${statInProgress}</span>
                                            </span>
                                        </div>

                                        <div class="stat-item">
                                            <span class="stat-label">Completed:
                                                <span style="color: #10B981; font-weight: bold;">${statCompleted}</span>
                                            </span>
                                        </div>

                                        <div class="stat-item">
                                            <span class="stat-label">Overdue:
                                                <span style="color: #EF4444; font-weight: bold;">${statOverdue}</span>
                                            </span>
                                        </div>
                                    </div>

                                    <div
                                        style="display: flex; height: 50px; margin-top: 15px; width: 100%; background-color: #E5E7EB; border-radius: 6px; overflow: hidden;">

                                        <c:if test="${statPlanned > 0}">
                                            <div style="background-color: #3B82F6; flex-grow: ${statPlanned};"
                                                title="Planned: ${statPlanned}"></div>
                                        </c:if>

                                        <c:if test="${statInProgress > 0}">
                                            <div style="background-color: #F59E0B; flex-grow: ${statInProgress};"
                                                title="In Progress: ${statInProgress}"></div>
                                        </c:if>

                                        <c:if test="${statCompleted > 0}">
                                            <div style="background-color: #10B981; flex-grow: ${statCompleted};"
                                                title="Completed: ${statCompleted}"></div>
                                        </c:if>

                                        <c:if test="${statOverdue > 0}">
                                            <div style="background-color: #EF4444; flex-grow: ${statOverdue};"
                                                title="Overdue: ${statOverdue}"></div>
                                        </c:if>

                                        <c:if test="${statTotal == 0}">
                                            <div style="flex-grow: 1; background-color: #D1D5DB;"></div>
                                        </c:if>
                                    </div>
                                </div>

                                <!-- Filter Bar -->
                                <form action="${pageContext.request.contextPath}/sale/dashboard" method="GET"
                                    class="filter-bar">

                                    <input type="text" name="search" value="${param.search}" class="search-input"
                                        placeholder="Search...">

                                    <div class="filter-group">
                                        <span class="filter-label">Filter Type:</span>
                                        <select name="type">
                                            <option value="">All</option>
                                            <option value="Call" ${param.type=='Call' ? 'selected' : '' }>Call</option>
                                            <option value="Task" ${param.type=='Task' ? 'selected' : '' }>Task</option>
                                            <option value="Email" ${param.type=='Email' ? 'selected' : '' }>Email
                                            </option>
                                            <option value="Meeting" ${param.type=='Meeting' ? 'selected' : '' }>Meeting
                                            </option>
                                            <option value="Note" ${param.type=='Note' ? 'selected' : '' }>Note</option>
                                        </select>
                                    </div>

                                    <div class="filter-group">
                                        <span class="filter-label">From</span>
                                        <input type="date" name="from" value="${param.from}">
                                    </div>

                                    <div class="filter-group">
                                        <span class="filter-label">To</span>
                                        <input type="date" name="to" value="${param.to}">
                                    </div>

                                    <button class="btn btn-primary" type="submit">Filter</button>
                                </form>

                                <!-- Action Bar -->
                                <div class="action-bar">
                                    <div class="action-buttons">
                                        <c:if test="${sessionScope.userSession.admin}">
                                            <a href="${pageContext.request.contextPath}/sale/export-activities"
                                                style="text-decoration: none;">
                                                <button class="btn btn-success" type="button">
                                                    <i class="fas fa-file-excel"></i>
                                                    Export Excel
                                                </button>
                                            </a>
                                        </c:if>
                                    </div>
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
                                                    <th>People in Charge</th>
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
                                                            <%-- Nút 👁️ Xem: Tất cả đều thấy --%>
                                                                <a href="javascript:void(0)"
                                                                    onclick="openDetailModal(${act.id})"
                                                                    class="action-btn view-btn" title="Xem chi tiết"><i
                                                                        class="fas fa-eye"></i></a>



                                                                    <%-- Nút 🗑️ Xóa: Chỉ Manager --%>
                                                                        <c:if test="${sessionScope.userSession.admin}">
                                                                            <a href="#" class="action-btn delete-btn"
                                                                                title="Xóa" style="color: #EF4444;"
                                                                                onclick="return confirm('Bạn có chắc muốn xóa hoạt động này?');"><i
                                                                                    class="fas fa-trash"></i></a>
                                                                        </c:if>

                                                                        <%-- Nút ✉️ Email: Chỉ hiện với Email-type, chưa
                                                                            Completed, không phải Marketing --%>
                                                                            <c:if test="${act.type == 'Email' && act.status != 'Completed'
                                                          && !sessionScope.userSession.marketingStaff}">
                                                                                <a href="${pageContext.request.contextPath}/emails/compose?customerId=${act.customerId}&activityId=${act.id}"
                                                                                    class="action-btn email-btn"
                                                                                    title="Gửi mail thực hiện ngay">
                                                                                    <i class="fas fa-envelope"
                                                                                        style="color: #6366F1;"></i>
                                                                                </a>
                                                                            </c:if>
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
                                                <c:if test="${currentPage > 1}">
                                                    <a href="?page=${currentPage - 1}&search=${param.search}&type=${param.type}&from=${param.from}&to=${param.to}"
                                                        class="page-btn" style="text-decoration: none;">&lt;</a>
                                                </c:if>
                                                <c:if test="${currentPage <= 1}">
                                                    <button class="page-btn" disabled>&lt;</button>
                                                </c:if>

                                                <span class="page-info">Page ${currentPage} / ${totalPages}</span>

                                                <c:if test="${currentPage < totalPages}">
                                                    <a href="?page=${currentPage + 1}&search=${param.search}&type=${param.type}&from=${param.from}&to=${param.to}"
                                                        class="page-btn" style="text-decoration: none;">&gt;</a>
                                                </c:if>
                                                <c:if test="${currentPage >= totalPages}">
                                                    <button class="page-btn" disabled>&gt;</button>
                                                </c:if>
                                            </div>
                                            <div class="total-info">Total: ${totalRecords} activities</div>
                                        </div>
                                    </div><!-- /.card-body -->
                                </div><!-- /.card -->


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