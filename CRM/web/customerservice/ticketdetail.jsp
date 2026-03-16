<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Ticket Detail</title>
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
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .header h2 {
                font-weight: 700;
                font-size: 26px;
                margin: 0;
            }

            .section-title {
                font-size: 20px;
                font-weight: 600;
                margin: 25px 0 15px;
                color: white;
            }
            .card {
                background: white;
                border-radius: 20px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                overflow: hidden;
                margin-bottom: 30px;
            }
            .card-header-strip {
                padding: 16px 22px;
                font-weight: 700;
                font-size: 15px;
                display: flex;
                align-items: center;
                gap: 10px;
                color: white;
            }
            .strip-purple {
                background: linear-gradient(135deg, #667eea, #764ba2);
            }
            .strip-orange {
                background: linear-gradient(135deg, #fd7e14, #e74c3c);
            }
            .card-inner {
                padding: 22px;
            }

            /* ── Info table ─────────────────────────────────────── */
            table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                padding: 13px 15px;
                border-bottom: 1px solid rgba(0,0,0,0.07);
                font-size: 14px;
            }
            th {
                background: rgba(102,126,234,0.07);
                text-align: left;
                width: 170px;
                font-weight: 600;
                color: #555;
            }
            tr:last-child th, tr:last-child td {
                border-bottom: none;
            }

            /* ── Badges ─────────────────────────────────────────── */
            .badge {
                border-radius: 12px;
                padding: 5px 12px;
                font-size: 12px;
                font-weight: 600;
                color: white;
                display: inline-block;
            }
            .low     {
                background: #17a2b8;
            }
            .medium  {
                background: #ffc107;
                color: #333;
            }
            .high    {
                background: #fd7e14;
            }
            .urgent  {
                background: #dc3545;
            }
            .status-open     {
                background: #dc3545;
            }
            .status-progress {
                background: #ffc107;
                color: #333;
            }
            .status-resolved {
                background: #28a745;
            }

            /* ── Form elements ──────────────────────────────────── */
            .form-group {
                margin-bottom: 18px;
            }
            .form-group label {
                display: block;
                font-weight: 600;
                color: #555;
                font-size: 14px;
                margin-bottom: 7px;
            }
            .form-select {
                padding: 10px 14px;
                border-radius: 10px;
                border: 2px solid #e0e0e0;
                font-size: 14px;
                min-width: 220px;
                outline: none;
                transition: border-color 0.3s;
            }
            .form-select:focus {
                border-color: #667eea;
            }

            /* ── Buttons ────────────────────────────────────────── */
            .btn-purple {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                border: none;
                padding: 9px 22px;
                border-radius: 10px;
                font-weight: 600;
                font-size: 14px;
                cursor: pointer;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                transition: 0.3s;
                text-decoration: none;
            }
            .btn-purple:hover {
                opacity: 0.85;
                transform: translateY(-2px);
            }

            .btn-orange {
                background: linear-gradient(135deg, #fd7e14, #e74c3c);
                color: white;
                border: none;
                padding: 9px 22px;
                border-radius: 10px;
                font-weight: 600;
                font-size: 14px;
                cursor: pointer;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                transition: 0.3s;
            }
            .btn-orange:hover {
                opacity: 0.85;
                transform: translateY(-2px);
            }

            .btn-back {
                background: #f0f0f0;
                color: #555;
                padding: 9px 18px;
                border-radius: 10px;
                font-weight: 600;
                font-size: 14px;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                transition: 0.3s;
            }
            .btn-back:hover {
                background: #e0e0e0;
            }

            .action-row {
                display: flex;
                gap: 12px;
                align-items: center;
                margin-top: 6px;
            }

            .readonly-notice {
                background: #f0f4ff;
                border-left: 4px solid #667eea;
                border-radius: 0 10px 10px 0;
                padding: 14px 18px;
                font-size: 14px;
                color: #555;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            /* ── Current assignee highlight ─────────────────────── */
            .current-assignee {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                background: #f0f4ff;
                border-radius: 10px;
                padding: 8px 14px;
                font-size: 14px;
                margin-bottom: 14px;
            }
        </style>
    </head>
    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <!-- ===== HEADER ===== -->
            <div class="header">
                <h2>
                    <i class="fas fa-ticket-alt" style="color:#667eea"></i>
                    Ticket #${ticket.id}
                </h2>
                <a href="${pageContext.request.contextPath}/customerservice/ticketlist"
                   class="btn-back">
                    <i class="fas fa-arrow-left"></i> Quay lại
                </a>
            </div>

            <!-- ===== THÔNG TIN TICKET ===== -->
            <div class="section-title">📋 Thông Tin Ticket</div>
            <div class="card">
                <div class="card-inner">
                    <table>
                        <tr>
                            <th><i class="fas fa-hashtag" style="color:#667eea"></i> ID</th>
                            <td style="font-weight:600">#${ticket.id}</td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-heading" style="color:#667eea"></i> Tiêu đề</th>
                            <td style="font-weight:600">${ticket.title}</td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-align-left" style="color:#667eea"></i> Mô tả</th>
                            <td style="line-height:1.7">${ticket.description}</td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-user" style="color:#667eea"></i> Khách hàng</th>
                            <td>${ticket.customerName}</td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-user-tie" style="color:#667eea"></i> Phụ trách</th>
                            <td>
                                <i class="fas fa-circle" style="color:#28a745;font-size:9px;margin-right:6px"></i>
                                ${ticket.assignedName}
                            </td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-flag" style="color:#667eea"></i> Ưu tiên</th>
                            <td>
                                <span class="badge ${ticket.priority.toLowerCase()}">${ticket.priority}</span>
                            </td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-circle-dot" style="color:#667eea"></i> Trạng thái</th>
                            <td>
                                <c:choose>
                                    <c:when test="${ticket.status == 'Open'}">
                                        <span class="badge status-open">Open</span>
                                    </c:when>
                                    <c:when test="${ticket.status == 'In Progress'}">
                                        <span class="badge status-progress">In Progress</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge status-resolved">Resolved</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-calendar-plus" style="color:#667eea"></i> Ngày tạo</th>
                            <td>${ticket.createdAt}</td>
                        </tr>
                        <c:if test="${ticket.updateAt != null}">
                            <tr>
                                <th><i class="fas fa-calendar-check" style="color:#667eea"></i> Cập nhật lần cuối</th>
                                <td>${ticket.updateAt}</td>
                            </tr>
                        </c:if>
                    </table>
                </div>
            </div>

            <%-- ===== CHỈ STAFF thấy 2 form bên dưới ===== --%>
            <c:if test="${userSession.isStaff()}">

                <!-- ===== CẬP NHẬT TRẠNG THÁI ===== -->
                <div class="section-title">🔧 Cập Nhật Trạng Thái</div>
                <div class="card">
                    <div class="card-header-strip strip-purple">
                        <i class="fas fa-edit"></i> Thay Đổi Trạng Thái
                    </div>
                    <div class="card-inner">
                        <form action="${pageContext.request.contextPath}/customerservice/updateticket"
                              method="post">
                            <input type="hidden" name="ticketId" value="${ticket.id}"/>
                            <div class="form-group">
                                <label><i class="fas fa-circle-dot"></i> Trạng thái mới</label>
                                <select name="status" class="form-select">
                                    <option value="Open"
                                            <c:if test="${ticket.status == 'Open'}">selected</c:if>>Open</option>
                                            <option value="In Progress"
                                            <c:if test="${ticket.status == 'In Progress'}">selected</c:if>>In Progress</option>
                                            <option value="Resolved"
                                            <c:if test="${ticket.status == 'Resolved'}">selected</c:if>>Resolved</option>
                                    </select>
                                </div>
                                <div class="action-row">
                                    <button type="submit" class="btn-purple">
                                        <i class="fas fa-save"></i> Lưu thay đổi
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- ===== RE-ASSIGN TICKET (BỀN 2) ===== -->
                    <div class="section-title">🔄 Chuyển Người Xử Lý</div>
                    <div class="card">
                        <div class="card-header-strip strip-orange">
                            <i class="fas fa-user-tag"></i> Re-assign Ticket
                        </div>
                        <div class="card-inner">
                            <div class="current-assignee">
                                <i class="fas fa-user-tie" style="color:#667eea"></i>
                                Đang xử lý: <strong>${ticket.assignedName}</strong>
                        </div>

                        <form action="${pageContext.request.contextPath}/customerservice/reassignticket"
                              method="post">
                            <input type="hidden" name="ticketId" value="${ticket.id}"/>
                            <div class="form-group">
                                <label><i class="fas fa-user-tag"></i> Chuyển sang nhân viên</label>
                                <select name="assignedTo" class="form-select">
                                    <option value="">-- Chọn nhân viên --</option>
                                    <c:forEach var="s" items="${staffList}">
                                        <option value="${s[0]}"
                                                <c:if test="${s[0] == ticket.assignedTo.toString()}">selected</c:if>>
                                            ${s[1]}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="action-row">
                                <button type="submit" class="btn-orange">
                                    <i class="fas fa-exchange-alt"></i> Chuyển người xử lý
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

            </c:if>

            <%-- Customer thấy thông báo readonly --%>
            <c:if test="${userSession.isCustomer()}">
                <div class="section-title">🔧 Trạng Thái Xử Lý</div>
                <div class="card">
                    <div class="card-inner">
                        <div class="readonly-notice">
                            <i class="fas fa-info-circle" style="color:#667eea;font-size:18px"></i>
                            Ticket đang được xử lý bởi đội ngũ hỗ trợ. Chúng tôi sẽ cập nhật trạng thái sớm nhất có thể.
                        </div>
                    </div>
                </div>
            </c:if>

            <%-- ===== FEEDBACK SECTION — nhúng cho cả staff lẫn customer ===== --%>
            <%@ include file="feedbackSection.jsp" %>

        </div>
    </body>
</html>
