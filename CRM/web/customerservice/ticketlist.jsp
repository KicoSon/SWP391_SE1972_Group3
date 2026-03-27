<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Ticket</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="icon" type="image/png"
              href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <style>
            /* ── Base ────────────────────────────────────────────── */
            body {
                margin:0;
                font-family:"Segoe UI",sans-serif;
                background:linear-gradient(135deg,#3a7bd5,#3a6073);
                color:#333;
            }
            .main-content {
                margin-left:270px;
                padding:30px;
                min-height:100vh;
            }
            .header {
                background:rgba(255,255,255,0.95);
                backdrop-filter:blur(20px);
                padding:25px 30px;
                border-radius:20px;
                box-shadow:0 10px 25px rgba(0,0,0,0.15);
                margin-bottom:30px;
                display:flex;
                align-items:center;
                justify-content:space-between;
            }
            .header h2 {
                font-weight:700;
                font-size:26px;
                margin:0;
            }
            .section-title {
                font-size:20px;
                font-weight:600;
                margin:25px 0 15px;
                color:white;
            }
            .card {
                background:white;
                border-radius:20px;
                box-shadow:0 10px 30px rgba(0,0,0,0.1);
                overflow:hidden;
                margin-bottom:30px;
            }
            .card-inner {
                padding:20px;
            }
            table {
                width:100%;
                border-collapse:collapse;
            }
            th, td {
                padding:12px;
                border-bottom:1px solid rgba(0,0,0,0.08);
                font-size:14px;
            }
            th {
                background:rgba(102,126,234,0.1);
                font-weight:600;
                text-align:left;
            }
            tr:hover td {
                background:#fafafa;
            }

            /* ── Priority & Status badges ────────────────────────── */
            .badge {
                border-radius:12px;
                padding:4px 10px;
                font-size:12px;
                font-weight:600;
                color:white;
                display:inline-block;
            }
            .low     {
                background:#17a2b8;
            }
            .medium  {
                background:#ffc107;
                color:#333;
            }
            .high    {
                background:#fd7e14;
            }
            .urgent  {
                background:#dc3545;
            }
            .status-open     {
                background:#dc3545;
            }
            .status-progress {
                background:#ffc107;
                color:#333;
            }
            .status-resolved {
                background:#28a745;
            }

            /* ── SLA badge ───────────────────────────────────────── */
            .sla-badge {
                display: inline-flex;
                align-items: center;
                gap: 4px;
                padding: 3px 8px;
                border-radius: 10px;
                font-size: 11px;
                font-weight: 600;
                margin-left: 6px;
            }
            .sla-overdue {
                background: #FCEBEB;
                color: #A32D2D;
                border: 1px solid #F09595;
                animation: pulse 1.5s infinite;
            }
            .sla-warning {
                background: #FAEEDA;
                color: #633806;
                border: 1px solid #FAC775;
            }
            .sla-ok {
                background: #EAF3DE;
                color: #27500A;
                border: 1px solid #C0DD97;
            }
            @keyframes pulse {
                0%, 100% {
                    opacity: 1;
                }
                50%       {
                    opacity: 0.6;
                }
            }

            /* ── Filter bar ──────────────────────────────────────── */
            .filter-bar {
                display:flex;
                gap:12px;
                margin-bottom:25px;
                flex-wrap:wrap;
            }
            .filter-input {
                padding:10px 14px;
                border-radius:10px;
                border:2px solid rgba(255,255,255,0.4);
                background:rgba(255,255,255,0.9);
                font-size:14px;
                outline:none;
                min-width:240px;
            }
            .filter-select {
                padding:10px 14px;
                border-radius:10px;
                border:2px solid rgba(255,255,255,0.4);
                background:rgba(255,255,255,0.9);
                font-size:14px;
            }
            .btn-search {
                background:rgba(255,255,255,0.2);
                color:white;
                border:2px solid rgba(255,255,255,0.5);
                padding:10px 20px;
                border-radius:10px;
                font-size:14px;
                font-weight:600;
                cursor:pointer;
                display:inline-flex;
                align-items:center;
                gap:7px;
                transition:0.3s;
            }
            .btn-search:hover {
                background:rgba(255,255,255,0.35);
            }

            /* ── SLA summary bar (trên bảng All Tickets) ─────────── */
            .sla-summary {
                display: flex;
                gap: 12px;
                padding: 12px 20px;
                background: #fff9f9;
                border-bottom: 1px solid rgba(0,0,0,0.07);
                align-items: center;
                flex-wrap: wrap;
            }
            .sla-summary-label {
                font-size:12px;
                font-weight:600;
                color:#888;
            }
            .sla-chip {
                font-size:12px;
                font-weight:600;
                padding:4px 12px;
                border-radius:12px;
                display:flex;
                align-items:center;
                gap:5px;
            }
            .sla-chip-red    {
                background:#FCEBEB;
                color:#A32D2D;
            }
            .sla-chip-orange {
                background:#FAEEDA;
                color:#633806;
            }
            .sla-chip-green  {
                background:#EAF3DE;
                color:#27500A;
            }

            /* ── Action buttons ──────────────────────────────────── */
            .view-btn {
                background:#4facfe;
                color:white;
                padding:6px 10px;
                border-radius:6px;
                text-decoration:none;
                font-size:13px;
            }
            .btn-purple {
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
                padding:8px 18px;
                border-radius:10px;
                font-weight:600;
                text-decoration:none;
                display:inline-flex;
                align-items:center;
                gap:8px;
                transition:0.3s;
            }
            .btn-purple:hover {
                opacity:0.85;
                transform:translateY(-2px);
            }

            .empty-msg {
                text-align:center;
                padding:30px;
                color:#aaa;
                font-size:14px;
            }

            /* ── Flash message ───────────────────────────────────── */
            .alert-success {
                background:#d4edda;
                color:#155724;
                border:1px solid #c3e6cb;
                padding:12px 18px;
                border-radius:10px;
                margin-bottom:20px;
                display:flex;
                align-items:center;
                gap:10px;
                font-size:14px;
            }

            @media(max-width:992px) {
                .main-content{
                    margin-left:0;
                    padding:20px;
                }
            }
        </style>
    </head>
    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <!-- ===== HEADER ===== -->
            <div class="header">
                <h2><i class="fas fa-ticket-alt" style="color:#667eea"></i> Quản Lý Ticket</h2>
                <c:if test="${userSession.isStaff()}">
                    <a href="${pageContext.request.contextPath}/customerservice/addticket"
                       class="btn-purple">
                        <i class="fas fa-plus"></i> Tạo Ticket
                    </a>
                </c:if>
            </div>

            <!-- ===== FILTER (chỉ staff) ===== -->
            <c:if test="${userSession.isStaff()}">
                <form action="${pageContext.request.contextPath}/customerservice/ticketlist"
                      method="get" class="filter-bar">
                    <input type="text" name="search" class="filter-input"
                           placeholder="Tìm theo tiêu đề..." value="${search}">
                    <select name="statusFilter" class="filter-select">
                        <option value="">-- Tất cả trạng thái --</option>
                        <option value="Open"        ${statusFilter=='Open'        ? 'selected':''}>Open</option>
                        <option value="In Progress" ${statusFilter=='In Progress' ? 'selected':''}>In Progress</option>
                        <option value="Resolved"    ${statusFilter=='Resolved'    ? 'selected':''}>Resolved</option>
                    </select>
                    <button type="submit" class="btn-search">
                        <i class="fas fa-search"></i> Tìm kiếm
                    </button>
                </form>
            </c:if>

            <!-- ===== FLASH MESSAGE ===== -->
            <c:if test="${not empty sessionScope.statusUpdateSuccess}">
                <div class="alert-success">
                    <i class="fas fa-check-circle"></i>
                    ${sessionScope.statusUpdateSuccess}
                </div>
                <c:remove var="statusUpdateSuccess" scope="session"/>
            </c:if>

            <!-- ===== ALL TICKETS (staff) ===== -->
            <c:if test="${userSession.isStaff()}">
                <div class="section-title">📋 Tất Cả Ticket</div>
                <div class="card">

                    <%-- SLA Summary bar --%>
                    <c:set var="overdueCount" value="0"/>
                    <c:forEach var="t" items="${ticketList}">
                        <c:if test="${t.overdue}">
                            <c:set var="overdueCount" value="${overdueCount + 1}"/>
                        </c:if>
                    </c:forEach>

                    <div class="sla-summary">
                        <span class="sla-summary-label"><i class="fas fa-clock"></i> SLA Status:</span>
                        <c:choose>
                            <c:when test="${overdueCount > 0}">
                                <span class="sla-chip sla-chip-red">
                                    <i class="fas fa-exclamation-circle" style="font-size:11px"></i>
                                    ${overdueCount} ticket quá hạn
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="sla-chip sla-chip-green">
                                    <i class="fas fa-check-circle" style="font-size:11px"></i>
                                    Tất cả trong hạn
                                </span>
                            </c:otherwise>
                        </c:choose>
                        <span style="font-size:12px;color:#bbb;margin-left:auto">
                            Urgent: 4h | High: 24h | Medium: 72h | Low: 7 ngày
                        </span>
                    </div>

                    <div class="card-inner">
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Tiêu đề</th>
                                    <th>Khách hàng</th>
                                    <th>Ưu tiên</th>
                                    <th>Trạng thái</th>
                                    <th>SLA</th>
                                    <th>Phụ trách</th>
                                    <th>Ngày tạo</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="t" items="${ticketList}">
                                    <tr>
                                        <td style="color:#aaa;font-size:12px">#${t.id}</td>
                                        <td style="font-weight:500">${t.title}</td>
                                        <td>${t.customerName}</td>
                                        <td>
                                            <span class="badge ${t.priority.toLowerCase()}">
                                                ${t.priority}
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${t.status=='Open'}">
                                                    <span class="badge status-open">Open</span>
                                                </c:when>
                                                <c:when test="${t.status=='In Progress'}">
                                                    <span class="badge status-progress">In Progress</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge status-resolved">Resolved</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <%-- ===== SLA BADGE ===== --%>
                                        <td>
                                            <c:choose>
                                                <c:when test="${t.status == 'Resolved'}">
                                                    <span class="sla-chip sla-chip-green"
                                                          style="font-size:11px">
                                                        <i class="fas fa-check" style="font-size:9px"></i>
                                                        Done
                                                    </span>
                                                </c:when>
                                                <c:when test="${t.overdue}">
                                                    <span class="sla-badge sla-overdue"
                                                          title="${t.slaStatusText}">
                                                        <i class="fas fa-fire" style="font-size:9px"></i>
                                                        ${t.slaStatusText}
                                                    </span>
                                                </c:when>
                                                <c:when test="${t.hoursElapsed * 100 / t.slaHoursLimit >= 75}">
                                                    <span class="sla-badge sla-warning"
                                                          title="${t.slaStatusText}">
                                                        <i class="fas fa-exclamation" style="font-size:9px"></i>
                                                        ${t.slaStatusText}
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="sla-badge sla-ok"
                                                          title="${t.slaStatusText}">
                                                        <i class="fas fa-circle-check" style="font-size:9px"></i>
                                                        ${t.slaStatusText}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td>${t.assignedName}</td>
                                        <td style="font-size:12px;color:#888">${t.createdAt}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/customerservice/ticketdetail?id=${t.id}"
                                               class="view-btn">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <c:if test="${empty ticketList}">
                            <div class="empty-msg">
                                <i class="fas fa-ticket-alt" style="font-size:28px;display:block;margin-bottom:10px"></i>
                                Không có ticket nào.
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:if>

            <!-- ===== MY TICKETS ===== -->
            <div class="section-title">
                <c:choose>
                    <c:when test="${userSession.isStaff()}">👤 Ticket Của Tôi</c:when>
                    <c:otherwise>🎫 Ticket Của Bạn</c:otherwise>
                </c:choose>
            </div>
            <div class="card">
                <div class="card-inner">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Ưu tiên</th>
                                <th>Trạng thái</th>
                                <c:if test="${userSession.isStaff()}"><th>SLA</th></c:if>
                                    <th>Ngày tạo</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="t" items="${myTickets}">
                                <tr>
                                    <td style="color:#aaa;font-size:12px">#${t.id}</td>
                                    <td style="font-weight:500">${t.title}</td>
                                    <td>
                                        <span class="badge ${t.priority.toLowerCase()}">${t.priority}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t.status=='Open'}">
                                                <span class="badge status-open">Open</span>
                                            </c:when>
                                            <c:when test="${t.status=='In Progress'}">
                                                <span class="badge status-progress">In Progress</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge status-resolved">Resolved</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <c:if test="${userSession.isStaff()}">
                                        <td>
                                            <c:choose>
                                                <c:when test="${t.status == 'Resolved'}">
                                                    <span class="sla-chip sla-chip-green" style="font-size:11px">
                                                        <i class="fas fa-check" style="font-size:9px"></i> Done
                                                    </span>
                                                </c:when>
                                                <c:when test="${t.overdue}">
                                                    <span class="sla-badge sla-overdue" title="${t.slaStatusText}">
                                                        <i class="fas fa-fire" style="font-size:9px"></i>
                                                        ${t.slaStatusText}
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="sla-badge sla-ok" title="${t.slaStatusText}">
                                                        <i class="fas fa-circle-check" style="font-size:9px"></i>
                                                        ${t.slaStatusText}
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </c:if>
                                    <td style="font-size:12px;color:#888">${t.createdAt}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/customerservice/ticketdetail?id=${t.id}"
                                           class="view-btn"><i class="fas fa-eye"></i></a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <c:if test="${empty myTickets}">
                        <div class="empty-msg">
                            <c:choose>
                                <c:when test="${userSession.isStaff()}">Bạn chưa được assign ticket nào.</c:when>
                                <c:otherwise>Bạn chưa có ticket nào.</c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>

        </div>
    </body>
</html>