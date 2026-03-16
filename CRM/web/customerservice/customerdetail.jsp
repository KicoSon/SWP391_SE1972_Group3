<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi Tiết Khách Hàng</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="icon" type="image/png"
              href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <style>
            /* ── Base (khớp ticketdetail.jsp) ───────────────────── */
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
            .card-inner {
                padding: 24px;
            }

            /* ── Info table ──────────────────────────────────────── */
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

            /* ── Customer name banner ────────────────────────────── */
            .customer-banner {
                background: linear-gradient(135deg, #667eea, #764ba2);
                padding: 28px 30px;
                display: flex;
                align-items: center;
                gap: 20px;
            }
            .customer-avatar-placeholder {
                width: 60px;
                height: 60px;
                border-radius: 50%;
                background: rgba(255,255,255,0.25);
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 26px;
                color: white;
                flex-shrink: 0;
            }
            .customer-banner-name {
                font-size: 22px;
                font-weight: 700;
                color: white;
            }
            .customer-banner-email {
                font-size: 14px;
                color: rgba(255,255,255,0.8);
                margin-top: 4px;
            }

            /* ── Badges ──────────────────────────────────────────── */
            .badge {
                border-radius: 12px;
                padding: 5px 12px;
                font-size: 12px;
                font-weight: 600;
                color: white;
                display: inline-flex;
                align-items: center;
                gap: 5px;
            }
            .status-active   {
                background: #28a745;
            }
            .status-inactive {
                background: #e74c3c;
            }

            .tier-badge {
                display: inline-flex;
                align-items: center;
                gap: 4px;
                padding: 4px 12px;
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

            /* ── Buttons ─────────────────────────────────────────── */
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

            @media (max-width: 992px) {
                .main-content {
                    margin-left: 0;
                    padding: 20px;
                }
            }
        </style>
    </head>
    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <!-- ===== HEADER ===== -->
            <div class="header">
                <h2>
                    <i class="fas fa-user" style="color:#667eea"></i>
                    Chi Tiết Khách Hàng
                </h2>
                <a href="${pageContext.request.contextPath}/customerservice/customerlist"
                   class="btn-back">
                    <i class="fas fa-arrow-left"></i> Quay lại
                </a>
            </div>

            <!-- ===== THÔNG TIN KHÁCH HÀNG ===== -->
            <div class="section-title">👤 Thông Tin Khách Hàng</div>

            <div class="card">
                <%-- Banner tên thay cho avatar ảnh --%>
                <div class="customer-banner">
                    <div class="customer-avatar-placeholder">
                        <i class="fas fa-user"></i>
                    </div>
                    <div>
                        <div class="customer-banner-name">${customer.fullName}</div>
                        <div class="customer-banner-email">${customer.email}</div>
                    </div>
                </div>

                <div class="card-inner">
                    <table>
                        <tr>
                            <th><i class="fas fa-hashtag" style="color:#667eea"></i> ID</th>
                            <td style="font-weight:600">#${customer.id}</td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-user" style="color:#667eea"></i> Họ tên</th>
                            <td style="font-weight:600">${customer.fullName}</td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-envelope" style="color:#667eea"></i> Email</th>
                            <td>${customer.email}</td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-phone" style="color:#667eea"></i> Số điện thoại</th>
                            <td>${customer.phone}</td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-layer-group" style="color:#667eea"></i> Tier</th>
                            <td>
                                <c:choose>
                                    <c:when test="${customer.tier == 1}">
                                        <span class="tier-badge tier-1">Tier 1</span>
                                    </c:when>
                                    <c:when test="${customer.tier == 2}">
                                        <span class="tier-badge tier-2">Tier 2</span>
                                    </c:when>
                                    <c:when test="${customer.tier == 3}">
                                        <span class="tier-badge tier-3">Tier 3</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="tier-badge tier-4">Tier ${customer.tier}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                        <tr>
                            <th><i class="fas fa-circle-dot" style="color:#667eea"></i> Trạng thái</th>
                            <td>
                                <c:choose>
                                    <c:when test="${customer.status == 'active' or customer.status == 'Active'}">
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
                        </tr>
                        <tr>
                            <th><i class="fas fa-calendar-plus" style="color:#667eea"></i> Ngày tạo</th>
                            <td style="color:#888;font-size:13px">
                                <c:if test="${not empty customer.createdAt}">
                                    ${customer.createdAt.toString().substring(0,10)}
                                </c:if>
                                <c:if test="${empty customer.createdAt}">—</c:if>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>

        </div>
    </body>
</html>
