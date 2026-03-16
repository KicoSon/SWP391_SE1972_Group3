<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Phản Hồi</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="icon" type="image/png"
              href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
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
                flex-wrap:wrap;
                gap:15px;
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

            /* KPI */
            .stats-grid {
                display:grid;
                grid-template-columns:repeat(auto-fit,minmax(200px,1fr));
                gap:20px;
                margin-bottom:30px;
            }
            .stat-card {
                background:white;
                border-radius:20px;
                box-shadow:0 10px 30px rgba(0,0,0,0.1);
                padding:24px;
                display:flex;
                align-items:center;
                gap:18px;
            }
            .stat-icon {
                width:56px;
                height:56px;
                border-radius:14px;
                display:flex;
                align-items:center;
                justify-content:center;
                font-size:24px;
                color:white;
                flex-shrink:0;
            }
            .bg-purple {
                background:linear-gradient(135deg,#667eea,#764ba2);
            }
            .bg-gold   {
                background:linear-gradient(135deg,#f5a623,#f0932b);
            }
            .bg-green  {
                background:linear-gradient(135deg,#28a745,#20c997);
            }
            .stat-info p  {
                margin:0;
                font-size:13px;
                color:#888;
            }
            .stat-info h3 {
                margin:4px 0 0;
                font-size:28px;
                font-weight:700;
                color:#333;
            }

            /* Distribution bar */
            .dist-grid {
                display:flex;
                flex-direction:column;
                gap:12px;
                padding:20px;
            }
            .dist-row  {
                display:flex;
                align-items:center;
                gap:10px;
            }
            .dist-label {
                font-size:15px;
                width:82px;
                flex-shrink:0;
                color:#f5a623;
                font-weight:600;
            }
            .dist-bar-wrap {
                flex:1;
                background:#f0f0f0;
                border-radius:20px;
                height:18px;
                overflow:hidden;
            }
            .dist-bar {
                height:100%;
                border-radius:20px;
                transition:width 0.9s ease;
            }
            .dist-bar.r5 {
                background:linear-gradient(90deg,#28a745,#20c997);
            }
            .dist-bar.r4 {
                background:linear-gradient(90deg,#5cb85c,#52d68a);
            }
            .dist-bar.r3 {
                background:linear-gradient(90deg,#ffc107,#fd7e14);
            }
            .dist-bar.r2 {
                background:linear-gradient(90deg,#fd7e14,#e74c3c);
            }
            .dist-bar.r1 {
                background:linear-gradient(90deg,#dc3545,#c0392b);
            }
            .dist-count {
                font-size:12px;
                color:#888;
                width:110px;
                text-align:right;
                flex-shrink:0;
            }

            /* Filter */
            .filter-bar {
                display:flex;
                align-items:center;
                gap:10px;
                padding:14px 18px;
                background:#f8f9fc;
                border-bottom:1px solid rgba(0,0,0,0.07);
                flex-wrap:wrap;
            }
            .filter-label {
                font-weight:600;
                font-size:14px;
                color:#555;
            }
            .filter-btn {
                padding:6px 16px;
                border-radius:20px;
                border:2px solid #e0e0e0;
                background:white;
                font-size:13px;
                font-weight:600;
                cursor:pointer;
                text-decoration:none;
                color:#555;
                transition:all 0.2s;
            }
            .filter-btn:hover {
                border-color:#667eea;
                color:#667eea;
            }
            .filter-btn.active {
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
                border-color:transparent;
            }
            .filter-btn.star-5.active {
                background:linear-gradient(135deg,#28a745,#20c997);
            }
            .filter-btn.star-4.active {
                background:linear-gradient(135deg,#5cb85c,#52d68a);
            }
            .filter-btn.star-3.active {
                background:linear-gradient(135deg,#ffc107,#fd7e14);
                color:#333;
            }
            .filter-btn.star-2.active {
                background:linear-gradient(135deg,#fd7e14,#e74c3c);
            }
            .filter-btn.star-1.active {
                background:linear-gradient(135deg,#dc3545,#c0392b);
            }

            /* Table */
            table {
                width:100%;
                border-collapse:collapse;
            }
            th, td {
                padding:12px;
                border-bottom:1px solid rgba(0,0,0,0.07);
            }
            th {
                background:rgba(102,126,234,0.1);
                font-weight:600;
                text-align:left;
            }

            .badge {
                border-radius:15px;
                padding:5px 10px;
                font-size:12px;
                color:white;
                display:inline-block;
            }
            .low    {
                background:#17a2b8;
            }
            .medium {
                background:#ffc107;
                color:#333;
            }
            .high   {
                background:#fd7e14;
            }
            .urgent {
                background:#dc3545;
            }

            .rating-badge {
                display:inline-flex;
                align-items:center;
                gap:4px;
                padding:4px 12px;
                border-radius:12px;
                font-size:12px;
                font-weight:700;
            }
            .rating-5 {
                background:#28a745;
                color:white;
            }
            .rating-4 {
                background:#5cb85c;
                color:white;
            }
            .rating-3 {
                background:#ffc107;
                color:#333;
            }
            .rating-2 {
                background:#fd7e14;
                color:white;
            }
            .rating-1 {
                background:#dc3545;
                color:white;
            }

            .stars-cell   {
                color:#f5a623;
                font-size:15px;
                letter-spacing:1px;
            }
            .comment-cell {
                max-width:200px;
                overflow:hidden;
                text-overflow:ellipsis;
                white-space:nowrap;
                font-size:13px;
                color:#666;
                font-style:italic;
            }
            .ticket-cell  {
                max-width:160px;
                overflow:hidden;
                text-overflow:ellipsis;
                white-space:nowrap;
                font-size:13px;
                color:#444;
            }

            .btn-export {
                background:linear-gradient(135deg,#28a745,#20c997);
                color:white;
                padding:9px 20px;
                border-radius:10px;
                font-weight:600;
                text-decoration:none;
                display:inline-flex;
                align-items:center;
                gap:8px;
                transition:0.3s;
                font-size:14px;
            }
            .btn-export:hover {
                opacity:0.85;
                transform:translateY(-2px);
            }

            .result-count {
                font-size:13px;
                color:#888;
                padding:10px 18px;
                border-bottom:1px solid rgba(0,0,0,0.07);
            }
            .empty-msg {
                text-align:center;
                padding:40px;
                color:#aaa;
                font-size:15px;
            }

            .view-btn {
                background:#4facfe;
                color:white;
                padding:4px 10px;
                border-radius:6px;
                text-decoration:none;
                font-size:12px;
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

            <!-- HEADER -->
            <div class="header">
                <h2>
                    <i class="fas fa-star" style="color:#f5a623"></i>
                    Quản Lý Phản Hồi Khách Hàng
                </h2>
                <a href="${pageContext.request.contextPath}/customerservice/exportfeedback${ratingFilter > 0 ? '?rating='.concat(ratingFilter.toString()) : ''}"
                   class="btn-export">
                    <i class="fas fa-file-excel"></i> Xuất Excel
                </a>
            </div>

            <!-- KPI CARDS -->
            <div class="section-title">📊 Tổng Quan</div>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon bg-purple"><i class="fas fa-comments"></i></div>
                    <div class="stat-info"><p>Tổng phản hồi</p><h3>${stats['total']}</h3></div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon bg-gold"><i class="fas fa-star"></i></div>
                    <div class="stat-info">
                        <p>Rating trung bình</p>
                        <h3>${stats['avgRating']} <span style="font-size:16px;color:#aaa">/ 5</span></h3>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon bg-green"><i class="fas fa-smile"></i></div>
                    <div class="stat-info">
                        <p>Hài lòng (4–5 ★)</p>
                        <h3>${stats['count4'] + stats['count5']}</h3>
                    </div>
                </div>
            </div>

            <!-- DISTRIBUTION -->
            <div class="section-title">📈 Phân Bổ Đánh Giá</div>
            <div class="card">
                <div class="dist-grid">
                    <div class="dist-row">
                        <div class="dist-label">★★★★★</div>
                        <div class="dist-bar-wrap">
                            <div class="dist-bar r5" style="width:${stats['pct5']}%"></div>
                        </div>
                        <div class="dist-count">${stats['count5']} (${stats['pct5']}%)</div>
                    </div>
                    <div class="dist-row">
                        <div class="dist-label">★★★★☆</div>
                        <div class="dist-bar-wrap">
                            <div class="dist-bar r4" style="width:${stats['pct4']}%"></div>
                        </div>
                        <div class="dist-count">${stats['count4']} (${stats['pct4']}%)</div>
                    </div>
                    <div class="dist-row">
                        <div class="dist-label">★★★☆☆</div>
                        <div class="dist-bar-wrap">
                            <div class="dist-bar r3" style="width:${stats['pct3']}%"></div>
                        </div>
                        <div class="dist-count">${stats['count3']} (${stats['pct3']}%)</div>
                    </div>
                    <div class="dist-row">
                        <div class="dist-label">★★☆☆☆</div>
                        <div class="dist-bar-wrap">
                            <div class="dist-bar r2" style="width:${stats['pct2']}%"></div>
                        </div>
                        <div class="dist-count">${stats['count2']} (${stats['pct2']}%)</div>
                    </div>
                    <div class="dist-row">
                        <div class="dist-label">★☆☆☆☆</div>
                        <div class="dist-bar-wrap">
                            <div class="dist-bar r1" style="width:${stats['pct1']}%"></div>
                        </div>
                        <div class="dist-count">${stats['count1']} (${stats['pct1']}%)</div>
                    </div>
                </div>
            </div>

            <!-- FEEDBACK TABLE -->
            <div class="section-title">💬 Danh Sách Phản Hồi</div>
            <div class="card">

                <!-- Filter bar -->
                <div class="filter-bar">
                    <span class="filter-label"><i class="fas fa-filter"></i> Lọc:</span>
                    <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement"
                       class="filter-btn ${ratingFilter == 0 ? 'active' : ''}">Tất cả</a>
                    <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?rating=5"
                       class="filter-btn star-5 ${ratingFilter == 5 ? 'active' : ''}">★★★★★</a>
                    <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?rating=4"
                       class="filter-btn star-4 ${ratingFilter == 4 ? 'active' : ''}">★★★★☆</a>
                    <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?rating=3"
                       class="filter-btn star-3 ${ratingFilter == 3 ? 'active' : ''}">★★★☆☆</a>
                    <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?rating=2"
                       class="filter-btn star-2 ${ratingFilter == 2 ? 'active' : ''}">★★☆☆☆</a>
                    <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?rating=1"
                       class="filter-btn star-1 ${ratingFilter == 1 ? 'active' : ''}">★☆☆☆☆</a>
                </div>

                <div class="result-count">
                    Hiển thị <strong>${feedbackList.size()}</strong> phản hồi
                    <c:if test="${ratingFilter > 0}">
                        với rating <strong>${ratingFilter} sao</strong>
                    </c:if>
                </div>

                <div class="card-inner">
                    <table>
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Ticket</th>
                                <th>Khách hàng</th>
                                <th>Ưu tiên</th>
                                <th>Rating</th>
                                <th>Sao</th>
                                <th>Nhận xét</th>
                                <th>Ngày đánh giá</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="fb" items="${feedbackList}" varStatus="loop">
                                <tr>
                                    <td style="text-align:center;color:#aaa">${loop.index + 1}</td>
                                    <td class="ticket-cell" title="${fb.ticketTitle}">
                                        <strong style="color:#667eea">#${fb.ticketId}</strong>
                                        ${fb.ticketTitle}
                                    </td>
                                    <td>
                                        <i class="fas fa-user-circle" style="color:#667eea;margin-right:5px"></i>
                                        ${fb.customerName}
                                    </td>
                                    <td>
                                        <c:if test="${not empty fb.ticketPriority}">
                                            <span class="badge ${fb.ticketPriority.toLowerCase()}">${fb.ticketPriority}</span>
                                        </c:if>
                                    </td>
                                    <td style="text-align:center">
                                        <span class="rating-badge rating-${fb.rating}">
                                            ${fb.rating} <i class="fas fa-star" style="font-size:9px"></i>
                                        </span>
                                    </td>
                                    <td class="stars-cell">${fb.starDisplay}</td>
                                    <td class="comment-cell" title="${fb.comments}">
                                        <c:choose>
                                            <c:when test="${not empty fb.comments}">${fb.comments}</c:when>
                                            <c:otherwise><span style="color:#ccc">—</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="font-size:12px;color:#888">${fb.createdAt}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/customerservice/ticketdetail?id=${fb.ticketId}"
                                           class="view-btn">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <c:if test="${empty feedbackList}">
                        <div class="empty-msg">
                            <i class="fas fa-comment-slash" style="font-size:30px;display:block;margin-bottom:12px"></i>
                            <c:choose>
                                <c:when test="${ratingFilter > 0}">Không có phản hồi ${ratingFilter} sao.</c:when>
                                <c:otherwise>Chưa có phản hồi nào.</c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>

        </div>
    </body>
</html>
