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

            /* ── KPI mini ──────────────────────────────────────── */
            .kpi-row {
                display:grid;
                grid-template-columns:repeat(auto-fit,minmax(160px,1fr));
                gap:16px;
                margin-bottom:24px;
            }
            .kpi-mini {
                background:white;
                border-radius:16px;
                box-shadow:0 6px 20px rgba(0,0,0,0.09);
                padding:18px;
                display:flex;
                align-items:center;
                gap:14px;
            }
            .kpi-icon {
                width:46px;
                height:46px;
                border-radius:12px;
                display:flex;
                align-items:center;
                justify-content:center;
                font-size:20px;
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
            .bg-teal   {
                background:linear-gradient(135deg,#20c997,#0fb8ad);
            }
            .kpi-info p  {
                margin:0;
                font-size:12px;
                color:#888;
            }
            .kpi-info h3 {
                margin:3px 0 0;
                font-size:24px;
                font-weight:700;
                color:#333;
            }

            /* ── Tab switch ────────────────────────────────────── */
            .tab-switch {
                display:flex;
                gap:0;
                background:rgba(255,255,255,0.15);
                border-radius:14px;
                padding:4px;
                margin-bottom:24px;
                width:fit-content;
            }
            .tab-btn {
                padding:10px 28px;
                border-radius:10px;
                font-size:14px;
                font-weight:600;
                cursor:pointer;
                border:none;
                background:transparent;
                color:white;
                transition:all 0.25s;
                display:flex;
                align-items:center;
                gap:8px;
            }
            .tab-btn.active {
                background:white;
                color:#333;
                box-shadow:0 4px 12px rgba(0,0,0,0.12);
            }
            .tab-content {
                display:none;
            }
            .tab-content.active {
                display:block;
            }

            /* ── Distribution bar ──────────────────────────────── */
            .dist-grid {
                display:flex;
                flex-direction:column;
                gap:10px;
                padding:18px 20px;
            }
            .dist-row  {
                display:flex;
                align-items:center;
                gap:10px;
            }
            .dist-label {
                font-size:14px;
                width:78px;
                flex-shrink:0;
                color:#f5a623;
                font-weight:600;
            }
            .dist-bar-wrap {
                flex:1;
                background:#f0f0f0;
                border-radius:20px;
                height:16px;
                overflow:hidden;
            }
            .dist-bar {
                height:100%;
                border-radius:20px;
                transition:width 0.8s ease;
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
                width:100px;
                text-align:right;
                flex-shrink:0;
            }

            /* ── Filter bar ────────────────────────────────────── */
            .filter-bar {
                display:flex;
                align-items:center;
                gap:8px;
                padding:12px 18px;
                background:#f8f9fc;
                border-bottom:1px solid rgba(0,0,0,0.07);
                flex-wrap:wrap;
            }
            .filter-label {
                font-weight:600;
                font-size:13px;
                color:#555;
            }
            .filter-btn {
                padding:5px 14px;
                border-radius:20px;
                border:2px solid #e0e0e0;
                background:white;
                font-size:12px;
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
            .filter-btn.s5.active {
                background:linear-gradient(135deg,#28a745,#20c997);
            }
            .filter-btn.s4.active {
                background:linear-gradient(135deg,#5cb85c,#52d68a);
            }
            .filter-btn.s3.active {
                background:linear-gradient(135deg,#ffc107,#fd7e14);
                color:#333;
            }
            .filter-btn.s2.active {
                background:linear-gradient(135deg,#fd7e14,#e74c3c);
            }
            .filter-btn.s1.active {
                background:linear-gradient(135deg,#dc3545,#c0392b);
            }

            /* ── Table ─────────────────────────────────────────── */
            table {
                width:100%;
                border-collapse:collapse;
            }
            th, td {
                padding:11px 12px;
                border-bottom:1px solid rgba(0,0,0,0.07);
            }
            th {
                background:rgba(102,126,234,0.08);
                font-weight:600;
                text-align:left;
                font-size:13px;
            }

            .badge {
                border-radius:12px;
                padding:4px 10px;
                font-size:11px;
                font-weight:600;
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
                gap:3px;
                padding:3px 10px;
                border-radius:10px;
                font-size:11px;
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

            .stars-cell {
                color:#f5a623;
                font-size:14px;
                letter-spacing:1px;
            }
            .comment-cell {
                max-width:220px;
                overflow:hidden;
                text-overflow:ellipsis;
                white-space:nowrap;
                font-size:12px;
                color:#666;
                font-style:italic;
            }
            .ticket-cell  {
                max-width:150px;
                overflow:hidden;
                text-overflow:ellipsis;
                white-space:nowrap;
                font-size:12px;
            }

            .btn-export {
                background:linear-gradient(135deg,#28a745,#20c997);
                color:white;
                padding:9px 18px;
                border-radius:10px;
                font-weight:600;
                text-decoration:none;
                display:inline-flex;
                align-items:center;
                gap:8px;
                transition:0.3s;
                font-size:13px;
            }
            .btn-export:hover {
                opacity:0.85;
                transform:translateY(-2px);
            }

            .view-btn {
                background:#4facfe;
                color:white;
                padding:4px 9px;
                border-radius:6px;
                text-decoration:none;
                font-size:11px;
            }
            .result-count {
                font-size:12px;
                color:#888;
                padding:8px 18px;
                border-bottom:1px solid rgba(0,0,0,0.07);
            }
            .empty-msg {
                text-align:center;
                padding:36px;
                color:#aaa;
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
                <h2>
                    <i class="fas fa-star" style="color:#f5a623"></i>
                    Quản Lý Phản Hồi Khách Hàng
                </h2>
                <a href="${pageContext.request.contextPath}/customerservice/exportfeedback"
                   class="btn-export">
                    <i class="fas fa-file-excel"></i> Xuất Excel
                </a>
            </div>

            <!-- ===== KPI TỔNG QUAN CẢ 2 NGUỒN ===== -->
            <div class="section-title">📊 Tổng Quan</div>
            <div class="kpi-row">
                <div class="kpi-mini">
                    <div class="kpi-icon bg-purple"><i class="fas fa-user"></i></div>
                    <div class="kpi-info">
                        <p>Feedback khách hàng</p>
                        <h3>${customerStats['total']}</h3>
                    </div>
                </div>
                <div class="kpi-mini">
                    <div class="kpi-icon bg-teal"><i class="fas fa-ticket-alt"></i></div>
                    <div class="kpi-info">
                        <p>Feedback theo ticket</p>
                        <h3>${ticketStats['total']}</h3>
                    </div>
                </div>
                <div class="kpi-mini">
                    <div class="kpi-icon bg-gold"><i class="fas fa-star"></i></div>
                    <div class="kpi-info">
                        <p>Rating TB khách hàng</p>
                        <h3>${customerStats['avgRating']} <span style="font-size:14px;color:#bbb">/5</span></h3>
                    </div>
                </div>
                <div class="kpi-mini">
                    <div class="kpi-icon bg-green"><i class="fas fa-star-half-alt"></i></div>
                    <div class="kpi-info">
                        <p>Rating TB theo ticket</p>
                        <h3>${ticketStats['avgRating']} <span style="font-size:14px;color:#bbb">/5</span></h3>
                    </div>
                </div>
            </div>

            <!-- ===== TAB SWITCH ===== -->
            <div class="tab-switch">
                <button class="tab-btn active" id="tab-cf" onclick="switchTab('cf')">
                    <i class="fas fa-user"></i> Customer Feedback
                </button>
                <button class="tab-btn" id="tab-tf" onclick="switchTab('tf')">
                    <i class="fas fa-ticket-alt"></i> Ticket Feedback
                </button>
            </div>

            <!-- ===================================================
                 SECTION 1: CUSTOMER FEEDBACK
                 =================================================== -->
            <div id="section-cf" class="tab-content active">

                <div class="section-title">👤 Phân Bổ — Customer Feedback</div>
                <div class="card">
                    <div class="dist-grid">
                        <div class="dist-row">
                            <div class="dist-label">★★★★★</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r5" style="width:${customerStats['pct5']}%"></div></div>
                            <div class="dist-count">${customerStats['count5']} (${customerStats['pct5']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★★★☆</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r4" style="width:${customerStats['pct4']}%"></div></div>
                            <div class="dist-count">${customerStats['count4']} (${customerStats['pct4']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★★☆☆</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r3" style="width:${customerStats['pct3']}%"></div></div>
                            <div class="dist-count">${customerStats['count3']} (${customerStats['pct3']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★☆☆☆</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r2" style="width:${customerStats['pct2']}%"></div></div>
                            <div class="dist-count">${customerStats['count2']} (${customerStats['pct2']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★☆☆☆☆</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r1" style="width:${customerStats['pct1']}%"></div></div>
                            <div class="dist-count">${customerStats['count1']} (${customerStats['pct1']}%)</div>
                        </div>
                    </div>
                </div>

                <div class="section-title">💬 Danh Sách — Customer Feedback</div>
                <div class="card">
                    <%-- Filter rating --%>
                    <div class="filter-bar">
                        <span class="filter-label"><i class="fas fa-filter"></i> Lọc:</span>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingTf=${ratingTf}"
                           class="filter-btn ${ratingCf == 0 ? 'active' : ''}">Tất cả</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=5&ratingTf=${ratingTf}"
                           class="filter-btn s5 ${ratingCf == 5 ? 'active' : ''}">★★★★★</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=4&ratingTf=${ratingTf}"
                           class="filter-btn s4 ${ratingCf == 4 ? 'active' : ''}">★★★★☆</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=3&ratingTf=${ratingTf}"
                           class="filter-btn s3 ${ratingCf == 3 ? 'active' : ''}">★★★☆☆</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=2&ratingTf=${ratingTf}"
                           class="filter-btn s2 ${ratingCf == 2 ? 'active' : ''}">★★☆☆☆</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=1&ratingTf=${ratingTf}"
                           class="filter-btn s1 ${ratingCf == 1 ? 'active' : ''}">★☆☆☆☆</a>
                    </div>
                    <div class="result-count">
                        <strong>${customerFeedbackList.size()}</strong> phản hồi
                        <c:if test="${ratingCf > 0}">— rating <strong>${ratingCf} sao</strong></c:if>
                        </div>
                        <div class="card-inner">
                            <table>
                                <thead>
                                    <tr>
                                        <th>STT</th>
                                        <th>Khách hàng</th>
                                        <th>Rating</th>
                                        <th>Sao</th>
                                        <th>Nhận xét</th>
                                        <th>Ngày đánh giá</th>
                                    </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="fb" items="${customerFeedbackList}" varStatus="loop">
                                    <tr>
                                        <td style="text-align:center;color:#aaa">${loop.index + 1}</td>
                                        <td>
                                            <i class="fas fa-user-circle" style="color:#667eea;margin-right:5px"></i>
                                            ${fb.customerName}
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
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <c:if test="${empty customerFeedbackList}">
                            <div class="empty-msg">
                                <i class="fas fa-comment-slash" style="font-size:28px;display:block;margin-bottom:10px"></i>
                                <c:choose>
                                    <c:when test="${ratingCf > 0}">Không có customer feedback ${ratingCf} sao.</c:when>
                                    <c:otherwise>Chưa có customer feedback nào.</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- ===================================================
                 SECTION 2: TICKET FEEDBACK
                 =================================================== -->
            <div id="section-tf" class="tab-content">

                <div class="section-title">🎫 Phân Bổ — Ticket Feedback</div>
                <div class="card">
                    <div class="dist-grid">
                        <div class="dist-row">
                            <div class="dist-label">★★★★★</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r5" style="width:${ticketStats['pct5']}%"></div></div>
                            <div class="dist-count">${ticketStats['count5']} (${ticketStats['pct5']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★★★☆</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r4" style="width:${ticketStats['pct4']}%"></div></div>
                            <div class="dist-count">${ticketStats['count4']} (${ticketStats['pct4']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★★☆☆</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r3" style="width:${ticketStats['pct3']}%"></div></div>
                            <div class="dist-count">${ticketStats['count3']} (${ticketStats['pct3']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★☆☆☆</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r2" style="width:${ticketStats['pct2']}%"></div></div>
                            <div class="dist-count">${ticketStats['count2']} (${ticketStats['pct2']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★☆☆☆☆</div>
                            <div class="dist-bar-wrap"><div class="dist-bar r1" style="width:${ticketStats['pct1']}%"></div></div>
                            <div class="dist-count">${ticketStats['count1']} (${ticketStats['pct1']}%)</div>
                        </div>
                    </div>
                </div>

                <div class="section-title">💬 Danh Sách — Ticket Feedback</div>
                <div class="card">
                    <%-- Filter rating ticket feedback — giữ ratingCf khi filter --%>
                    <div class="filter-bar">
                        <span class="filter-label"><i class="fas fa-filter"></i> Lọc:</span>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=${ratingCf}"
                           class="filter-btn ${ratingTf == 0 ? 'active' : ''}">Tất cả</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=${ratingCf}&ratingTf=5"
                           class="filter-btn s5 ${ratingTf == 5 ? 'active' : ''}">★★★★★</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=${ratingCf}&ratingTf=4"
                           class="filter-btn s4 ${ratingTf == 4 ? 'active' : ''}">★★★★☆</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=${ratingCf}&ratingTf=3"
                           class="filter-btn s3 ${ratingTf == 3 ? 'active' : ''}">★★★☆☆</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=${ratingCf}&ratingTf=2"
                           class="filter-btn s2 ${ratingTf == 2 ? 'active' : ''}">★★☆☆☆</a>
                        <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement?ratingCf=${ratingCf}&ratingTf=1"
                           class="filter-btn s1 ${ratingTf == 1 ? 'active' : ''}">★☆☆☆☆</a>
                    </div>
                    <div class="result-count">
                        <strong>${ticketFeedbackList.size()}</strong> phản hồi
                        <c:if test="${ratingTf > 0}">— rating <strong>${ratingTf} sao</strong></c:if>
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
                                <c:forEach var="fb" items="${ticketFeedbackList}" varStatus="loop">
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
                        <c:if test="${empty ticketFeedbackList}">
                            <div class="empty-msg">
                                <i class="fas fa-comment-slash" style="font-size:28px;display:block;margin-bottom:10px"></i>
                                <c:choose>
                                    <c:when test="${ratingTf > 0}">Không có ticket feedback ${ratingTf} sao.</c:when>
                                    <c:otherwise>Chưa có ticket feedback nào.</c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>

        </div>

        <script>
            // ── Tab switch logic ──────────────────────────────────────
            // Khi filter trong section nào → giữ tab đó khi reload
            function switchTab(tab) {
                document.getElementById('section-cf').classList.toggle('active', tab === 'cf');
                document.getElementById('section-tf').classList.toggle('active', tab === 'tf');
                document.getElementById('tab-cf').classList.toggle('active', tab === 'cf');
                document.getElementById('tab-tf').classList.toggle('active', tab === 'tf');
                sessionStorage.setItem('fbTab', tab);
            }

            // Khôi phục tab đúng khi reload sau filter
            document.addEventListener('DOMContentLoaded', function () {
                // Nếu có ratingTf trong URL → active tab ticket
                var url = new URLSearchParams(window.location.search);
                if (url.get('ratingTf') !== null && url.get('ratingTf') !== '') {
                    switchTab('tf');
                } else if (url.get('ratingCf') !== null && url.get('ratingCf') !== '') {
                    switchTab('cf');
                } else {
                    // Khôi phục từ session storage
                    var saved = sessionStorage.getItem('fbTab');
                    if (saved)
                        switchTab(saved);
                }
            });
        </script>

    </body>
</html>
