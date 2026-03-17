<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Khách Hàng</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="icon" type="image/png"
              href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <style>
            /* ── Base (khớp ticketList.jsp) ─────────────────────── */
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

            /* ── Filter bar ──────────────────────────────────────── */
            .filter-bar {
                display: flex;
                gap: 12px;
                margin-bottom: 25px;
                flex-wrap: wrap;
                align-items: center;
            }
            .filter-input {
                padding: 10px 14px;
                border-radius: 10px;
                border: 2px solid rgba(255,255,255,0.4);
                background: rgba(255,255,255,0.9);
                font-size: 14px;
                outline: none;
                transition: border-color 0.3s;
                min-width: 240px;
            }
            .filter-input:focus {
                border-color: white;
            }

            .filter-select {
                padding: 10px 14px;
                border-radius: 10px;
                border: 2px solid rgba(255,255,255,0.4);
                background: rgba(255,255,255,0.9);
                font-size: 14px;
                outline: none;
                cursor: pointer;
            }

            .btn-search {
                background: rgba(255,255,255,0.2);
                color: white;
                border: 2px solid rgba(255,255,255,0.5);
                padding: 10px 20px;
                border-radius: 10px;
                font-size: 14px;
                font-weight: 600;
                cursor: pointer;
                display: inline-flex;
                align-items: center;
                gap: 7px;
                transition: 0.3s;
                backdrop-filter: blur(10px);
            }
            .btn-search:hover {
                background: rgba(255,255,255,0.35);
            }

            /* ── Card & table ────────────────────────────────────── */
            .card {
                background: white;
                border-radius: 20px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                overflow: hidden;
                margin-bottom: 30px;
            }
            .result-count {
                font-size: 13px;
                color: #888;
                padding: 10px 20px;
                border-bottom: 1px solid rgba(0,0,0,0.07);
            }
            .card-inner {
                padding: 20px;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                padding: 12px;
                border-bottom: 1px solid rgba(0,0,0,0.08);
                font-size: 14px;
            }
            th {
                background: rgba(102,126,234,0.1);
                font-weight: 600;
                text-align: left;
            }
            tr:hover td {
                background: #fafafa;
            }

            /* ── Tier badge ──────────────────────────────────────── */
            .tier-badge {
                display: inline-flex;
                align-items: center;
                gap: 4px;
                padding: 4px 10px;
                border-radius: 10px;
                font-size: 12px;
                font-weight: 600;
            }
            .tier-1 {
                background: #e8f4f8;
                color: #0fb8ad;
            }
            .tier-2 {
                background: #e8f0ff;
                color: #667eea;
            }
            .tier-3 {
                background: #fff5e0;
                color: #f5a623;
            }
            .tier-4 {
                background: #ffeef0;
                color: #e74c3c;
            }

            /* ── Status badge ────────────────────────────────────── */
            .badge {
                border-radius: 12px;
                padding: 4px 12px;
                font-size: 12px;
                font-weight: 600;
                color: white;
                display: inline-block;
            }
            .status-active   {
                background: #28a745;
            }
            .status-inactive {
                background: #e74c3c;
            }

            /* ── Action buttons ──────────────────────────────────── */
            .action-btns {
                display: flex;
                gap: 8px;
            }
            .view-btn {
                background: #4facfe;
                color: white;
                padding: 6px 10px;
                border-radius: 6px;
                text-decoration: none;
                font-size: 13px;
            }
            .toggle-btn {
                background: #f5576c;
                color: white;
                padding: 6px 10px;
                border-radius: 6px;
                text-decoration: none;
                font-size: 13px;
                border: none;
                cursor: pointer;
            }
            .toggle-btn.activate {
                background: #28a745;
            }

            .empty-msg {
                text-align: center;
                padding: 40px;
                color: #aaa;
                font-size: 15px;
            }

            /* ── Toast ───────────────────────────────────────────── */
            .toast-success {
                position: fixed;
                top: 20px;
                right: 30px;
                background: linear-gradient(135deg, #00c851, #007e33);
                color: white;
                padding: 14px 24px;
                border-radius: 12px;
                box-shadow: 0 8px 25px rgba(0,0,0,0.2);
                font-weight: 600;
                z-index: 9999;
                display: flex;
                align-items: center;
                gap: 10px;
                animation: slideIn 0.4s ease;
            }
            @keyframes slideIn {
                from {
                    opacity: 0;
                    transform: translateX(80px);
                }
                to   {
                    opacity: 1;
                    transform: translateX(0);
                }
            }

            @media (max-width: 992px) {
                .main-content {
                    margin-left: 0;
                    padding: 20px;
                }
            }
        </style>
    </head>

    <body>

        <%-- Toast thông báo từ session --%>
        <c:if test="${not empty sessionScope.success}">
            <div id="toast" class="toast-success">
                <i class="fas fa-check-circle"></i>
                ${sessionScope.success}
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <!-- ===== HEADER ===== -->
            <div class="header">
                <h2>
                    <i class="fas fa-users" style="color:#667eea"></i>
                    Quản Lý Khách Hàng
                </h2>
            </div>

            <!-- ===== FILTER BAR ===== -->
            <form action="${pageContext.request.contextPath}/customerservice/customerlist"
                  method="get" class="filter-bar">

                <input type="text"
                       name="search"
                       class="filter-input"
                       placeholder="&#xf002;  Tìm theo tên hoặc email..."
                       value="${search}">

                <select name="statusFilter" class="filter-select">
                    <option value="">-- Tất cả trạng thái --</option>
                    <option value="active"   ${statusFilter == 'active'   ? 'selected' : ''}>Hoạt động</option>
                    <option value="inactive" ${statusFilter == 'inactive' ? 'selected' : ''}>Bị khóa</option>
                </select>

                <button type="submit" class="btn-search">
                    <i class="fas fa-search"></i> Tìm kiếm
                </button>

                <c:if test="${not empty search or not empty statusFilter}">
                    <a href="${pageContext.request.contextPath}/customerservice/customerlist"
                       style="color:rgba(255,255,255,0.8);font-size:13px;font-weight:600;
                       text-decoration:none;align-self:center">
                        <i class="fas fa-times"></i> Xóa bộ lọc
                    </a>
                </c:if>
            </form>

            <!-- ===== TABLE ===== -->
            <div class="card">
                <div class="result-count">
                    Tìm thấy <strong>${customerList.size()}</strong> khách hàng
                    <c:if test="${not empty search}">
                        cho "<strong>${search}</strong>"
                    </c:if>
                </div>

                <div class="card-inner">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Họ tên</th>
                                <th>Email</th>
                                <th>SĐT</th>
                                <th>Tier</th>
                                <th>Ngày tạo</th>
                                <th>Trạng thái</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${customerList}">
                                <tr>
                                    <td style="color:#aaa;font-size:12px">#${c.id}</td>

                                    <td>
                                        <i class="fas fa-user-circle"
                                           style="color:#667eea;margin-right:6px;font-size:16px"></i>
                                        <strong>${c.fullName}</strong>
                                    </td>

                                    <td style="color:#555">${c.email}</td>
                                    <td>${c.phone}</td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${c.tier == 1}">
                                                <span class="tier-badge tier-1">
                                                    <i class="fas fa-circle" style="font-size:8px"></i> Tier 1
                                                </span>
                                            </c:when>
                                            <c:when test="${c.tier == 2}">
                                                <span class="tier-badge tier-2">
                                                    <i class="fas fa-circle" style="font-size:8px"></i> Tier 2
                                                </span>
                                            </c:when>
                                            <c:when test="${c.tier == 3}">
                                                <span class="tier-badge tier-3">
                                                    <i class="fas fa-circle" style="font-size:8px"></i> Tier 3
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="tier-badge tier-4">
                                                    <i class="fas fa-circle" style="font-size:8px"></i> Tier ${c.tier}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td style="font-size:13px;color:#888">
                                        <c:choose>
                                            <c:when test="${not empty c.createdAt}">
                                                ${c.createdAt.toString().substring(0, 10)}
                                            </c:when>
                                            <c:otherwise>—</c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <c:choose>
                                            <c:when test="${c.status == 'active' or c.status == 'Active'}">
                                                <span class="badge status-active">
                                                    <i class="fas fa-check-circle" style="font-size:10px"></i>
                                                    Hoạt động
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge status-inactive">
                                                    <i class="fas fa-ban" style="font-size:10px"></i>
                                                    Bị khóa
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>

                                    <td>
                                        <div class="action-btns">
                                            <a href="${pageContext.request.contextPath}/customerservice/viewCustomer?id=${c.id}"
                                               class="view-btn">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <c:if test="${empty customerList}">
                        <div class="empty-msg">
                            <i class="fas fa-users-slash" style="font-size:32px;display:block;margin-bottom:12px"></i>
                            <c:choose>
                                <c:when test="${not empty search or not empty statusFilter}">
                                    Không tìm thấy khách hàng phù hợp với bộ lọc.
                                </c:when>
                                <c:otherwise>
                                    Chưa có khách hàng nào.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

        <script>
            // Auto-dismiss toast sau 4 giây
            (function () {
                var toast = document.getElementById('toast');
                if (!toast)
                    return;
                setTimeout(function () {
                    toast.style.transition = 'opacity 0.5s, transform 0.5s';
                    toast.style.opacity = '0';
                    toast.style.transform = 'translateX(80px)';
                    setTimeout(function () {
                        toast.remove();
                    }, 500);
                }, 4000);
            })();
        </script>

    </body>
</html>
