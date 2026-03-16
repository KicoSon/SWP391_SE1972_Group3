<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Dashboard</title>
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
            }
            .header h2 {
                font-weight: 700;
                font-size: 26px;
                margin: 0;
            }
            .header p  {
                margin: 6px 0 0;
                color: #888;
                font-size: 14px;
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
            .card-header-strip.purple {
                background: linear-gradient(135deg, #667eea, #764ba2);
            }
            .card-header-strip.teal   {
                background: linear-gradient(135deg, #20c997, #0fb8ad);
            }
            .card-inner {
                padding: 20px;
            }

            /* ── KPI grid ────────────────────────────────────────── */
            .kpi-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(175px, 1fr));
                gap: 18px;
                margin-bottom: 28px;
            }
            .kpi-card {
                background: white;
                border-radius: 18px;
                padding: 22px 20px;
                box-shadow: 0 8px 24px rgba(0,0,0,0.09);
                display: flex;
                align-items: center;
                gap: 16px;
                transition: transform 0.2s;
            }
            .kpi-card:hover {
                transform: translateY(-3px);
            }

            .kpi-icon {
                width: 52px;
                height: 52px;
                border-radius: 13px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 22px;
                color: white;
                flex-shrink: 0;
            }
            .bg-blue   {
                background: linear-gradient(135deg, #4facfe, #00f2fe);
            }
            .bg-red    {
                background: linear-gradient(135deg, #dc3545, #e74c3c);
            }
            .bg-yellow {
                background: linear-gradient(135deg, #ffc107, #fd7e14);
            }
            .bg-green  {
                background: linear-gradient(135deg, #28a745, #20c997);
            }
            .bg-purple {
                background: linear-gradient(135deg, #667eea, #764ba2);
            }
            .bg-gold   {
                background: linear-gradient(135deg, #f5a623, #f0932b);
            }
            .bg-teal   {
                background: linear-gradient(135deg, #20c997, #0fb8ad);
            }
            .bg-pink   {
                background: linear-gradient(135deg, #fd79a8, #e84393);
            }

            .kpi-info p  {
                margin: 0;
                font-size: 12px;
                color: #999;
                font-weight: 500;
            }
            .kpi-info h3 {
                margin: 4px 0 0;
                font-size: 30px;
                font-weight: 800;
                color: #222;
                line-height: 1;
            }
            .kpi-info small {
                font-size: 13px;
                color: #aaa;
            }

            /* ── 2-col layout ────────────────────────────────────── */
            .two-col {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 24px;
                margin-bottom: 28px;
            }

            /* ── Donut chart (CSS) ───────────────────────────────── */
            .donut-wrap {
                display: flex;
                align-items: center;
                gap: 28px;
                padding: 20px;
            }
            .donut {
                position: relative;
                width: 130px;
                height: 130px;
                flex-shrink: 0;
            }
            .donut svg {
                transform: rotate(-90deg);
            }
            .donut-center {
                position: absolute;
                top: 50%;
                left: 50%;
                transform: translate(-50%, -50%);
                text-align: center;
            }
            .donut-center span {
                display: block;
            }
            .donut-center .donut-num  {
                font-size: 26px;
                font-weight: 800;
                color: #333;
            }
            .donut-center .donut-lbl  {
                font-size: 11px;
                color: #999;
                margin-top: 2px;
            }

            .donut-legend {
                flex: 1;
            }
            .legend-item {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 10px;
                font-size: 13px;
            }
            .legend-dot {
                width: 12px;
                height: 12px;
                border-radius: 50%;
                flex-shrink: 0;
            }
            .legend-label {
                flex: 1;
                color: #555;
            }
            .legend-val   {
                font-weight: 700;
                color: #333;
            }
            .legend-pct   {
                font-size: 11px;
                color: #aaa;
                margin-left: 4px;
            }

            /* ── Rating distribution bar ─────────────────────────── */
            .dist-grid  {
                display: flex;
                flex-direction: column;
                gap: 10px;
                padding: 20px;
            }
            .dist-row   {
                display: flex;
                align-items: center;
                gap: 10px;
            }
            .dist-label {
                font-size: 15px;
                width: 82px;
                flex-shrink: 0;
                color: #f5a623;
                font-weight: 600;
            }
            .dist-bar-wrap {
                flex: 1;
                background: #f0f0f0;
                border-radius: 20px;
                height: 18px;
                overflow: hidden;
            }
            .dist-bar {
                height: 100%;
                border-radius: 20px;
                transition: width 1s ease;
            }
            .dist-bar.r5 {
                background: linear-gradient(90deg,#28a745,#20c997);
            }
            .dist-bar.r4 {
                background: linear-gradient(90deg,#5cb85c,#52d68a);
            }
            .dist-bar.r3 {
                background: linear-gradient(90deg,#ffc107,#fd7e14);
            }
            .dist-bar.r2 {
                background: linear-gradient(90deg,#fd7e14,#e74c3c);
            }
            .dist-bar.r1 {
                background: linear-gradient(90deg,#dc3545,#c0392b);
            }
            .dist-count {
                font-size: 12px;
                color: #888;
                width: 80px;
                text-align: right;
                flex-shrink: 0;
            }

            /* ── Recent tables ───────────────────────────────────── */
            table {
                width: 100%;
                border-collapse: collapse;
            }
            th, td {
                padding: 11px 12px;
                border-bottom: 1px solid rgba(0,0,0,0.07);
                font-size: 13px;
            }
            th {
                background: rgba(102,126,234,0.08);
                font-weight: 600;
                text-align: left;
            }
            tr:last-child td {
                border-bottom: none;
            }
            tr:hover td {
                background: #fafafa;
            }

            .badge {
                border-radius: 12px;
                padding: 4px 10px;
                font-size: 11px;
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

            .rating-badge {
                display: inline-flex;
                align-items: center;
                gap: 3px;
                padding: 4px 10px;
                border-radius: 12px;
                font-size: 11px;
                font-weight: 700;
            }
            .rating-5 {
                background: #28a745;
                color: white;
            }
            .rating-4 {
                background: #5cb85c;
                color: white;
            }
            .rating-3 {
                background: #ffc107;
                color: #333;
            }
            .rating-2 {
                background: #fd7e14;
                color: white;
            }
            .rating-1 {
                background: #dc3545;
                color: white;
            }

            .stars-sm {
                color: #f5a623;
                font-size: 13px;
                letter-spacing: 1px;
            }

            .view-btn {
                background: #4facfe;
                color: white;
                padding: 5px 10px;
                border-radius: 6px;
                text-decoration: none;
                font-size: 12px;
            }

            .empty-msg {
                text-align: center;
                padding: 30px;
                color: #aaa;
                font-size: 14px;
            }

            /* ── Shortcut links ──────────────────────────────────── */
            .shortcut-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
                gap: 16px;
                margin-bottom: 30px;
            }
            .shortcut-card {
                background: white;
                border-radius: 16px;
                padding: 20px;
                box-shadow: 0 6px 18px rgba(0,0,0,0.08);
                text-decoration: none;
                display: flex;
                align-items: center;
                gap: 14px;
                transition: all 0.25s;
                color: #333;
            }
            .shortcut-card:hover {
                transform: translateY(-3px);
                box-shadow: 0 12px 28px rgba(0,0,0,0.14);
            }
            .shortcut-icon {
                width: 44px;
                height: 44px;
                border-radius: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 20px;
                color: white;
                flex-shrink: 0;
            }
            .shortcut-card span {
                font-weight: 600;
                font-size: 14px;
            }

            @media (max-width: 992px) {
                .main-content {
                    margin-left: 0;
                    padding: 20px;
                }
                .two-col {
                    grid-template-columns: 1fr;
                }
                .kpi-grid {
                    grid-template-columns: 1fr 1fr;
                }
            }
        </style>
    </head>
    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <!-- ===== HEADER ===== -->
            <div class="header">
                <h2><i class="fas fa-chart-line" style="color:#667eea"></i> Dashboard</h2>
                <p>Tổng quan hệ thống hỗ trợ khách hàng</p>
            </div>

            <!-- ===== KPI — TICKET ===== -->
            <div class="section-title">🎫 Thống Kê Ticket</div>
            <div class="kpi-grid">
                <div class="kpi-card">
                    <div class="kpi-icon bg-blue"><i class="fas fa-ticket-alt"></i></div>
                    <div class="kpi-info">
                        <p>Tổng Ticket</p>
                        <h3>${ticketStats['total']}</h3>
                    </div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon bg-red"><i class="fas fa-door-open"></i></div>
                    <div class="kpi-info">
                        <p>Đang Mở</p>
                        <h3>${ticketStats['open']}</h3>
                        <small>Open</small>
                    </div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon bg-yellow"><i class="fas fa-spinner"></i></div>
                    <div class="kpi-info">
                        <p>Đang Xử Lý</p>
                        <h3>${ticketStats['inProgress']}</h3>
                        <small>In Progress</small>
                    </div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon bg-green"><i class="fas fa-check-circle"></i></div>
                    <div class="kpi-info">
                        <p>Đã Giải Quyết</p>
                        <h3>${ticketStats['resolved']}</h3>
                        <small>Resolved</small>
                    </div>
                </div>
            </div>

            <!-- ===== KPI — FEEDBACK ===== -->
            <div class="section-title">⭐ Thống Kê Phản Hồi</div>
            <div class="kpi-grid">
                <div class="kpi-card">
                    <div class="kpi-icon bg-purple"><i class="fas fa-comments"></i></div>
                    <div class="kpi-info">
                        <p>Tổng Phản Hồi</p>
                        <h3>${feedbackStats['total']}</h3>
                    </div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon bg-gold"><i class="fas fa-star"></i></div>
                    <div class="kpi-info">
                        <p>Rating Trung Bình</p>
                        <h3>${feedbackStats['avgRating']}<small style="font-size:15px;color:#bbb"> /5</small></h3>
                    </div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon bg-teal"><i class="fas fa-smile"></i></div>
                    <div class="kpi-info">
                        <p>Hài Lòng (4–5★)</p>
                        <h3>${feedbackStats['count4'] + feedbackStats['count5']}</h3>
                    </div>
                </div>
                <div class="kpi-card">
                    <div class="kpi-icon bg-pink"><i class="fas fa-frown"></i></div>
                    <div class="kpi-info">
                        <p>Chưa Hài Lòng (1–2★)</p>
                        <h3>${feedbackStats['count1'] + feedbackStats['count2']}</h3>
                    </div>
                </div>
            </div>

            <!-- ===== CHARTS ROW ===== -->
            <div class="two-col">

                <%-- Donut: Ticket theo Status --%>
                <div class="card">
                    <div class="card-header-strip purple">
                        <i class="fas fa-circle-half-stroke"></i> Ticket Theo Trạng Thái
                    </div>
                    <div class="donut-wrap">
                        <%-- Donut SVG render bằng JS bên dưới --%>
                        <div class="donut">
                            <svg width="130" height="130" viewBox="0 0 130 130">
                            <circle cx="65" cy="65" r="50"
                                    fill="none" stroke="#f0f0f0" stroke-width="18"/>
                            <%-- Segments được tính bằng JS --%>
                            <circle id="seg-open"     cx="65" cy="65" r="50"
                                    fill="none" stroke="#dc3545" stroke-width="18"
                                    stroke-dasharray="0 314" stroke-linecap="round"/>
                            <circle id="seg-progress" cx="65" cy="65" r="50"
                                    fill="none" stroke="#ffc107" stroke-width="18"
                                    stroke-dasharray="0 314" stroke-linecap="round"/>
                            <circle id="seg-resolved" cx="65" cy="65" r="50"
                                    fill="none" stroke="#28a745" stroke-width="18"
                                    stroke-dasharray="0 314" stroke-linecap="round"/>
                            </svg>
                            <div class="donut-center">
                                <span class="donut-num">${ticketStats['total']}</span>
                                <span class="donut-lbl">Tickets</span>
                            </div>
                        </div>
                        <div class="donut-legend">
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#dc3545"></div>
                                <span class="legend-label">Open</span>
                                <span class="legend-val">${ticketStats['open']}</span>
                                <span class="legend-pct" id="pct-open"></span>
                            </div>
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#ffc107"></div>
                                <span class="legend-label">In Progress</span>
                                <span class="legend-val">${ticketStats['inProgress']}</span>
                                <span class="legend-pct" id="pct-progress"></span>
                            </div>
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#28a745"></div>
                                <span class="legend-label">Resolved</span>
                                <span class="legend-val">${ticketStats['resolved']}</span>
                                <span class="legend-pct" id="pct-resolved"></span>
                            </div>
                        </div>
                    </div>
                </div>

                <%-- Donut: Ticket theo Priority --%>
                <div class="card">
                    <div class="card-header-strip purple">
                        <i class="fas fa-circle-half-stroke"></i> Ticket Theo Mức Ưu Tiên
                    </div>
                    <div class="donut-wrap">
                        <div class="donut">
                            <svg width="130" height="130" viewBox="0 0 130 130">
                            <circle cx="65" cy="65" r="50"
                                    fill="none" stroke="#f0f0f0" stroke-width="18"/>
                            <circle id="seg-urgent" cx="65" cy="65" r="50"
                                    fill="none" stroke="#dc3545" stroke-width="18"
                                    stroke-dasharray="0 314" stroke-linecap="round"/>
                            <circle id="seg-high"   cx="65" cy="65" r="50"
                                    fill="none" stroke="#fd7e14" stroke-width="18"
                                    stroke-dasharray="0 314" stroke-linecap="round"/>
                            <circle id="seg-medium" cx="65" cy="65" r="50"
                                    fill="none" stroke="#ffc107" stroke-width="18"
                                    stroke-dasharray="0 314" stroke-linecap="round"/>
                            <circle id="seg-low"    cx="65" cy="65" r="50"
                                    fill="none" stroke="#17a2b8" stroke-width="18"
                                    stroke-dasharray="0 314" stroke-linecap="round"/>
                            </svg>
                            <div class="donut-center">
                                <span class="donut-num">${ticketStats['total']}</span>
                                <span class="donut-lbl">Tickets</span>
                            </div>
                        </div>
                        <div class="donut-legend">
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#dc3545"></div>
                                <span class="legend-label">Urgent</span>
                                <span class="legend-val">${ticketStats['urgent']}</span>
                                <span class="legend-pct" id="pct-urgent"></span>
                            </div>
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#fd7e14"></div>
                                <span class="legend-label">High</span>
                                <span class="legend-val">${ticketStats['high']}</span>
                                <span class="legend-pct" id="pct-high"></span>
                            </div>
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#ffc107"></div>
                                <span class="legend-label">Medium</span>
                                <span class="legend-val">${ticketStats['medium']}</span>
                                <span class="legend-pct" id="pct-medium"></span>
                            </div>
                            <div class="legend-item">
                                <div class="legend-dot" style="background:#17a2b8"></div>
                                <span class="legend-label">Low</span>
                                <span class="legend-val">${ticketStats['low']}</span>
                                <span class="legend-pct" id="pct-low"></span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ===== FEEDBACK DISTRIBUTION ===== -->
            <div class="two-col">
                <div class="card">
                    <div class="card-header-strip teal">
                        <i class="fas fa-chart-bar"></i> Phân Bổ Rating Feedback
                    </div>
                    <div class="dist-grid">
                        <div class="dist-row">
                            <div class="dist-label">★★★★★</div>
                            <div class="dist-bar-wrap">
                                <div class="dist-bar r5" style="width:${feedbackStats['pct5']}%"></div>
                            </div>
                            <div class="dist-count">${feedbackStats['count5']} (${feedbackStats['pct5']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★★★☆</div>
                            <div class="dist-bar-wrap">
                                <div class="dist-bar r4" style="width:${feedbackStats['pct4']}%"></div>
                            </div>
                            <div class="dist-count">${feedbackStats['count4']} (${feedbackStats['pct4']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★★☆☆</div>
                            <div class="dist-bar-wrap">
                                <div class="dist-bar r3" style="width:${feedbackStats['pct3']}%"></div>
                            </div>
                            <div class="dist-count">${feedbackStats['count3']} (${feedbackStats['pct3']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★★☆☆☆</div>
                            <div class="dist-bar-wrap">
                                <div class="dist-bar r2" style="width:${feedbackStats['pct2']}%"></div>
                            </div>
                            <div class="dist-count">${feedbackStats['count2']} (${feedbackStats['pct2']}%)</div>
                        </div>
                        <div class="dist-row">
                            <div class="dist-label">★☆☆☆☆</div>
                            <div class="dist-bar-wrap">
                                <div class="dist-bar r1" style="width:${feedbackStats['pct1']}%"></div>
                            </div>
                            <div class="dist-count">${feedbackStats['count1']} (${feedbackStats['pct1']}%)</div>
                        </div>
                    </div>
                </div>

                <%-- Recent feedbacks --%>
                <div class="card">
                    <div class="card-header-strip teal">
                        <i class="fas fa-clock"></i> Phản Hồi Gần Đây
                    </div>
                    <div class="card-inner" style="padding:0">
                        <table>
                            <thead>
                                <tr>
                                    <th>Khách hàng</th>
                                    <th>Rating</th>
                                    <th>Nhận xét</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="fb" items="${feedbackStats['recentFeedbacks']}">
                                    <tr>
                                        <td>
                                            <i class="fas fa-user-circle" style="color:#667eea;margin-right:5px"></i>
                                            ${fb['customerName']}
                                        </td>
                                        <td>
                                            <span class="rating-badge rating-${fb['rating']}">
                                                ${fb['rating']}
                                                <i class="fas fa-star" style="font-size:9px"></i>
                                            </span>
                                        </td>
                                        <td style="max-width:160px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;font-style:italic;color:#666;font-size:12px;">
                                            <c:choose>
                                                <c:when test="${not empty fb['comments']}">${fb['comments']}</c:when>
                                                <c:otherwise><span style="color:#ccc">—</span></c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                        <c:if test="${empty feedbackStats['recentFeedbacks']}">
                            <div class="empty-msg">Chưa có phản hồi nào.</div>
                        </c:if>
                    </div>
                </div>
            </div>

            <!-- ===== RECENT TICKETS ===== -->
            <div class="section-title">🕐 Ticket Mới Nhất</div>
            <div class="card">
                <div class="card-inner" style="padding:0">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu đề</th>
                                <th>Khách hàng</th>
                                <th>Ưu tiên</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="t" items="${ticketStats['recentTickets']}">
                                <tr>
                                    <td style="color:#aaa;font-size:12px">#${t['id']}</td>
                                    <td style="font-weight:500;max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap">${t['title']}</td>
                                    <td>
                                        <i class="fas fa-user-circle" style="color:#667eea;margin-right:5px"></i>
                                        ${t['customerName']}
                                    </td>
                                    <td>
                                        <span class="badge ${t['priority'].toLowerCase()}">${t['priority']}</span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${t['status'] == 'Open'}">
                                                <span class="badge status-open">Open</span>
                                            </c:when>
                                            <c:when test="${t['status'] == 'In Progress'}">
                                                <span class="badge status-progress">In Progress</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge status-resolved">Resolved</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="font-size:12px;color:#888">${t['createdAt']}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/customerservice/ticketdetail?id=${t['id']}"
                                           class="view-btn">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <c:if test="${empty ticketStats['recentTickets']}">
                        <div class="empty-msg">Chưa có ticket nào.</div>
                    </c:if>
                </div>
                <div style="padding:14px 20px;border-top:1px solid rgba(0,0,0,0.07);text-align:right">
                    <a href="${pageContext.request.contextPath}/customerservice/ticketlist"
                       style="font-size:13px;color:#667eea;font-weight:600;text-decoration:none">
                        Xem tất cả ticket <i class="fas fa-arrow-right"></i>
                    </a>
                </div>
            </div>

            <!-- ===== SHORTCUT LINKS ===== -->
            <div class="section-title">🔗 Truy Cập Nhanh</div>
            <div class="shortcut-grid">
                <a href="${pageContext.request.contextPath}/customerservice/ticketlist"
                   class="shortcut-card">
                    <div class="shortcut-icon bg-blue"><i class="fas fa-ticket-alt"></i></div>
                    <span>Quản lý Ticket</span>
                </a>
                <a href="${pageContext.request.contextPath}/customerservice/feedbackmanagement"
                   class="shortcut-card">
                    <div class="shortcut-icon bg-gold"><i class="fas fa-star"></i></div>
                    <span>Quản lý Phản Hồi</span>
                </a>
                <a href="${pageContext.request.contextPath}/customerservice/customerlist"
                   class="shortcut-card">
                    <div class="shortcut-icon bg-purple"><i class="fas fa-users"></i></div>
                    <span>Quản lý Khách Hàng</span>
                </a>
                <a href="${pageContext.request.contextPath}/customerservice/exportfeedback"
                   class="shortcut-card">
                    <div class="shortcut-icon bg-green"><i class="fas fa-file-excel"></i></div>
                    <span>Xuất Excel Feedback</span>
                </a>
            </div>

        </div>

        <!-- ===== DONUT CHART SCRIPT ===== -->
        <script>
            // Hàm vẽ donut dùng stroke-dasharray/dashoffset
            function drawDonut(segments, ids) {
                const circumference = 2 * Math.PI * 50; // r=50 → ~314
                const total = segments.reduce((s, x) => s + x.val, 0);
                if (total === 0)
                    return;

                let offset = 0;
                segments.forEach(seg => {
                    const el = document.getElementById(seg.id);
                    if (!el)
                        return;
                    const pct = seg.val / total;
                    const dash = pct * circumference;
                    const gap = circumference - dash;
                    el.setAttribute("stroke-dasharray", dash + " " + gap);
                    el.setAttribute("stroke-dashoffset", -offset);
                    offset += dash;

                    // Ghi % vào legend
                    if (seg.pctId) {
                        const p = document.getElementById(seg.pctId);
                        if (p)
                            p.textContent = "(" + Math.round(pct * 100) + "%)";
                    }
                });
            }

            document.addEventListener("DOMContentLoaded", function () {
                // Donut 1: Status
                drawDonut([
                    {id: "seg-open", val: ${ticketStats['open']}, pctId: "pct-open"},
                    {id: "seg-progress", val: ${ticketStats['inProgress']}, pctId: "pct-progress"},
                    {id: "seg-resolved", val: ${ticketStats['resolved']}, pctId: "pct-resolved"}
                ]);

                // Donut 2: Priority
                drawDonut([
                    {id: "seg-urgent", val: ${ticketStats['urgent']}, pctId: "pct-urgent"},
                    {id: "seg-high", val: ${ticketStats['high']}, pctId: "pct-high"},
                    {id: "seg-medium", val: ${ticketStats['medium']}, pctId: "pct-medium"},
                    {id: "seg-low", val: ${ticketStats['low']}, pctId: "pct-low"}
                ]);
            });
        </script>

    </body>
</html>