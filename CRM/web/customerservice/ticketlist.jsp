<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
            body {
                margin: 0;
                font-family: "Segoe UI", sans-serif;
                background: linear-gradient(135deg, #3a7bd5, #3a6073);
                color: #333;
            }

            .main-content {
                margin-left: 270px;
                padding: 30px;
                min-height: 100vh;
            }

            .header {
                background: rgba(255,255,255,0.95);
                backdrop-filter: blur(20px);
                padding: 25px 30px;
                border-radius: 20px;
                box-shadow: 0 10px 25px rgba(0,0,0,0.15);
                margin-bottom: 30px;
            }

            .header h2 {
                font-weight:700;
                font-size:26px;
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

            table {
                width:100%;
                border-collapse:collapse;
            }

            th, td {
                padding:12px;
                border-bottom:1px solid rgba(0,0,0,0.1);
            }

            th {
                background: rgba(102,126,234,0.1);
            }

            .badge {
                border-radius:15px;
                padding:5px 10px;
                font-size:12px;
                color:white;
            }

            .low {
                background:#17a2b8;
            }
            .medium {
                background:#ffc107;
                color:black;
            }
            .high {
                background:#fd7e14;
            }
            .urgent {
                background:#dc3545;
            }

            .status-open {
                background:#dc3545;
            }
            .status-progress {
                background:#ffc107;
                color:black;
            }
            .status-resolved {
                background:#28a745;
            }

            .action-btns {
                display:flex;
                gap:8px;
            }

            .view-btn {
                background:#4facfe;
                color:white;
                padding:6px 10px;
                border-radius:6px;
                text-decoration:none;
            }

            .empty-msg {
                text-align:center;
                padding:20px;
                color:#777;
            }

            .btn-purple {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 8px 18px;
                border-radius: 10px;
                font-weight: 600;
                text-decoration: none;
                transition: 0.3s;
                display: inline-flex;
                align-items: center;
                gap: 8px;
            }

            .btn-purple:hover {
                opacity: 0.85;
                transform: translateY(-2px);
            }
        </style>
    </head>

    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <div class="header">
                <h2>
                    <i class="fas fa-ticket-alt"></i>
                    Quản Lý Ticket
                </h2>
            </div>
            <div class="card-header">
                <c:if test="${sessionScope.userSession.staff}">
                    <a href="${pageContext.request.contextPath}/customerservice/addticket"
                       class="btn-purple">
                        <i class="fas fa-plus"></i> Add Ticket
                    </a>
                </c:if>
            </div>
            <!-- ===== TABLE 1: ALL TICKETS ===== -->
            <div class="section-title">
                📋 Tất Cả Ticket
            </div>

            <div class="card">
                <div style="padding:20px">

                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Khách hàng</th>
                                <th>Ưu tiên</th>
                                <th>Trạng thái</th>
                                <th>Phụ trách</th>
                                <th>Ngày tạo</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="t" items="${ticketList}">
                                <tr>
                                    <td>${t.id}</td>
                                    <td>${t.title}</td>
                                    <td>${t.customerName}</td>

                                    <td>
                                        <span class="badge ${t.priority.toLowerCase()}">
                                            ${t.priority}
                                        </span>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${t.status == 'Open'}">
                                                <span class="badge status-open">Open</span>
                                            </c:when>
                                            <c:when test="${t.status == 'In Progress'}">
                                                <span class="badge status-progress">In Progress</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge status-resolved">Resolved</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>${t.assignedName}</td>
                                    <td>${t.createdAt}</td>

                                    <td>
                                        <div class="action-btns">
                                            <a href="${pageContext.request.contextPath}/customerservice/ticketdetail?id=${t.id}"
                                               class="view-btn">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <c:if test="${empty ticketList}">
                        <div class="empty-msg">
                            Không có ticket nào.
                        </div>
                    </c:if>

                </div>
            </div>

            <!-- ===== TABLE 2: MY TICKETS ===== -->
            <div class="section-title">
                👤 Ticket Của Tôi
            </div>

            <div class="card">
                <div style="padding:20px">

                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Ưu tiên</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="t" items="${myTickets}">
                                <tr>
                                    <td>${t.id}</td>
                                    <td>${t.title}</td>

                                    <td>
                                        <span class="badge ${t.priority.toLowerCase()}">
                                            ${t.priority}
                                        </span>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${t.status == 'Open'}">
                                                <span class="badge status-open">Open</span>
                                            </c:when>
                                            <c:when test="${t.status == 'In Progress'}">
                                                <span class="badge status-progress">In Progress</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge status-resolved">Resolved</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>${t.createdAt}</td>

                                    <td>
                                        <div class="action-btns">
                                            <a href="${pageContext.request.contextPath}/customerservice/ticketdetail?id=${t.id}"
                                               class="view-btn">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <c:if test="${empty myTickets}">
                        <div class="empty-msg">
                            Bạn chưa được assign ticket nào.
                        </div>
                    </c:if>

                </div>
            </div>

        </div>

    </body>
</html>
