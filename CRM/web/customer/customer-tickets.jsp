<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Phiếu Hỗ Trợ Của Tôi - FPT Supporter</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <style>
            /* ── Layout (khớp dashboard.jsp) ──────────────────────── */
            .page-header {
                margin-bottom: 24px;
            }
            .page-header h1 {
                font-size: 24px;
                font-weight: 600;
                margin: 0 0 4px;
            }
            .page-header .subtitle {
                color: #6b7280;
                font-size: 14px;
                margin: 0;
            }

            /* ── KPI cards ─────────────────────────────────────────── */
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
                gap: 16px;
                margin-bottom: 24px;
            }
            .stat-card {
                background: white;
                border-radius: 12px;
                padding: 20px;
                display: flex;
                align-items: center;
                gap: 14px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.08);
                cursor: pointer;
                transition: box-shadow 0.2s, transform 0.15s;
                border: 2px solid transparent;
                text-decoration: none;
                color: inherit;
            }
            .stat-card:hover {
                box-shadow: 0 4px 12px rgba(0,0,0,0.12);
                transform: translateY(-2px);
            }
            .stat-card.active {
                border-color: currentColor;
            }
            .stat-icon {
                width: 48px;
                height: 48px;
                border-radius: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 20px;
                flex-shrink: 0;
            }
            .stat-primary .stat-icon {
                background: #eff6ff;
                color: #3b82f6;
            }
            .stat-warning .stat-icon {
                background: #fffbeb;
                color: #f59e0b;
            }
            .stat-info    .stat-icon {
                background: #f0f9ff;
                color: #0ea5e9;
            }
            .stat-success .stat-icon {
                background: #f0fdf4;
                color: #22c55e;
            }
            .stat-content h3 {
                font-size: 26px;
                font-weight: 700;
                margin: 0 0 2px;
            }
            .stat-content p  {
                font-size: 12px;
                color: #6b7280;
                margin: 0;
            }

            /* ── Filter bar ─────────────────────────────────────────── */
            .filter-bar {
                background: white;
                border-radius: 12px;
                padding: 16px 20px;
                margin-bottom: 20px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.08);
                display: flex;
                align-items: center;
                gap: 12px;
                flex-wrap: wrap;
            }
            .filter-label {
                font-size: 13px;
                font-weight: 500;
                color: #374151;
                white-space: nowrap;
            }
            .filter-tabs  {
                display: flex;
                gap: 8px;
                flex-wrap: wrap;
            }
            .filter-tab {
                padding: 6px 16px;
                border-radius: 20px;
                font-size: 13px;
                font-weight: 500;
                border: 1.5px solid #e5e7eb;
                background: white;
                color: #6b7280;
                cursor: pointer;
                text-decoration: none;
                transition: all 0.2s;
            }
            .filter-tab:hover {
                border-color: #6366f1;
                color: #6366f1;
            }
            .filter-tab.active {
                background: #6366f1;
                color: white;
                border-color: #6366f1;
            }
            .result-count {
                font-size: 12px;
                color: #9ca3af;
                margin-left: auto;
            }

            /* ── Ticket table card ──────────────────────────────────── */
            .dashboard-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 1px 3px rgba(0,0,0,0.08);
                overflow: hidden;
            }
            .card-header {
                padding: 16px 20px;
                border-bottom: 1px solid #f3f4f6;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .card-header h3 {
                font-size: 15px;
                font-weight: 600;
                margin: 0;
                display: flex;
                align-items: center;
                gap: 8px;
            }
            .card-content {
                padding: 0;
            }

            .data-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 14px;
            }
            .data-table th {
                background: #f9fafb;
                padding: 11px 16px;
                text-align: left;
                font-size: 12px;
                font-weight: 600;
                color: #6b7280;
                border-bottom: 1px solid #f3f4f6;
            }
            .data-table td {
                padding: 14px 16px;
                border-bottom: 1px solid #f9fafb;
                vertical-align: middle;
            }
            .data-table tr:last-child td {
                border-bottom: none;
            }
            .data-table tr:hover td {
                background: #fafafa;
            }

            /* ── Status & priority badges ───────────────────────────── */
            .ticket-status, .ticket-priority {
                display: inline-block;
                padding: 3px 10px;
                border-radius: 12px;
                font-size: 11px;
                font-weight: 600;
            }
            .status-open       {
                background: #fef2f2;
                color: #dc2626;
            }
            .status-inprogress {
                background: #fffbeb;
                color: #d97706;
            }
            .status-resolved   {
                background: #f0fdf4;
                color: #16a34a;
            }
            .priority-low      {
                background: #f0f9ff;
                color: #0369a1;
            }
            .priority-medium   {
                background: #fffbeb;
                color: #b45309;
            }
            .priority-high     {
                background: #fef2f2;
                color: #dc2626;
            }
            .priority-urgent   {
                background: #fdf4ff;
                color: #9333ea;
            }

            /* ── Feedback stars (display) ───────────────────────────── */
            .stars-display {
                color: #f59e0b;
                font-size: 14px;
                letter-spacing: 1px;
            }
            .stars-empty   {
                color: #d1d5db;
                font-size: 14px;
            }

            /* ── Feedback button ────────────────────────────────────── */
            .btn-feedback {
                display: inline-flex;
                align-items: center;
                gap: 5px;
                padding: 5px 12px;
                border-radius: 8px;
                background: #eff6ff;
                color: #3b82f6;
                border: 1.5px solid #bfdbfe;
                font-size: 12px;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.2s;
                white-space: nowrap;
            }
            .btn-feedback:hover {
                background: #3b82f6;
                color: white;
                border-color: #3b82f6;
            }

            /* ── Empty state ────────────────────────────────────────── */
            .empty-state {
                text-align: center;
                padding: 48px 20px;
                color: #9ca3af;
            }
            .empty-state i   {
                font-size: 40px;
                margin-bottom: 12px;
                display: block;
            }
            .empty-state p   {
                font-size: 15px;
                font-weight: 500;
                margin: 0 0 4px;
            }
            .empty-state small {
                font-size: 13px;
            }

            /* ── Alert ──────────────────────────────────────────────── */
            .alert {
                padding: 12px 16px;
                border-radius: 8px;
                margin-bottom: 16px;
                font-size: 14px;
                display: flex;
                align-items: center;
                gap: 8px;
            }
            .alert-success {
                background: #f0fdf4;
                color: #166534;
                border: 1px solid #bbf7d0;
            }
            .alert-danger  {
                background: #fef2f2;
                color: #991b1b;
                border: 1px solid #fecaca;
            }

            /* ════════════════════════════════════════════════════════
               FEEDBACK MODAL
               ════════════════════════════════════════════════════════ */
            .modal-overlay {
                display: none;
                position: fixed;
                inset: 0;
                background: rgba(0,0,0,0.45);
                z-index: 1000;
                align-items: center;
                justify-content: center;
            }
            .modal-overlay.open {
                display: flex;
            }

            .modal-box {
                background: white;
                border-radius: 16px;
                width: 100%;
                max-width: 460px;
                margin: 16px;
                overflow: hidden;
                box-shadow: 0 20px 60px rgba(0,0,0,0.2);
            }
            .modal-header {
                background: linear-gradient(135deg, #6366f1, #8b5cf6);
                color: white;
                padding: 20px 24px;
                display: flex;
                align-items: center;
                gap: 10px;
            }
            .modal-header h4 {
                margin: 0;
                font-size: 16px;
                font-weight: 600;
                flex: 1;
            }
            .modal-close {
                background: rgba(255,255,255,0.2);
                border: none;
                color: white;
                width: 28px;
                height: 28px;
                border-radius: 50%;
                cursor: pointer;
                font-size: 14px;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            .modal-close:hover {
                background: rgba(255,255,255,0.35);
            }
            .modal-body   {
                padding: 24px;
            }
            .modal-footer {
                padding: 16px 24px;
                border-top: 1px solid #f3f4f6;
                display: flex;
                gap: 10px;
                justify-content: flex-end;
            }

            /* Ticket info trong modal */
            .modal-ticket-info {
                background: #f8fafc;
                border-left: 3px solid #6366f1;
                border-radius: 0 8px 8px 0;
                padding: 12px 14px;
                margin-bottom: 20px;
                font-size: 13px;
            }
            .modal-ticket-info .ticket-id {
                color: #6366f1;
                font-weight: 600;
            }
            .modal-ticket-info .ticket-title {
                color: #374151;
                font-weight: 500;
                margin-top: 2px;
            }

            /* Star rating input */
            .star-input-label {
                font-size: 13px;
                font-weight: 600;
                color: #374151;
                margin-bottom: 10px;
                display: block;
            }
            .star-rating-input {
                display: flex;
                flex-direction: row-reverse;
                justify-content: flex-end;
                gap: 4px;
                margin-bottom: 6px;
            }
            .star-rating-input input[type="radio"] {
                display: none;
            }
            .star-rating-input label {
                font-size: 36px;
                color: #d1d5db;
                cursor: pointer;
                transition: color 0.15s, transform 0.1s;
                user-select: none;
            }
            .star-rating-input label:hover,
            .star-rating-input label:hover ~ label,
            .star-rating-input input:checked ~ label {
                color: #f59e0b;
            }
            .star-rating-input label:hover {
                transform: scale(1.1);
            }

            .rating-hint {
                font-size: 13px;
                color: #6366f1;
                font-weight: 500;
                min-height: 18px;
                margin-bottom: 16px;
            }

            /* Comment textarea */
            .modal-textarea {
                width: 100%;
                box-sizing: border-box;
                border: 1.5px solid #e5e7eb;
                border-radius: 8px;
                padding: 10px 12px;
                font-size: 13px;
                font-family: inherit;
                resize: vertical;
                min-height: 88px;
                outline: none;
                transition: border-color 0.2s;
            }
            .modal-textarea:focus {
                border-color: #6366f1;
            }
            .char-count {
                text-align: right;
                font-size: 11px;
                color: #9ca3af;
                margin-top: 4px;
            }

            /* Modal buttons */
            .btn-primary {
                background: #6366f1;
                color: white;
                border: none;
                padding: 9px 22px;
                border-radius: 8px;
                font-size: 13px;
                font-weight: 600;
                cursor: pointer;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                transition: background 0.2s;
            }
            .btn-primary:hover {
                background: #4f46e5;
            }
            .btn-primary:disabled {
                opacity: 0.5;
                cursor: not-allowed;
            }
            .btn-ghost {
                background: white;
                color: #6b7280;
                border: 1.5px solid #e5e7eb;
                padding: 9px 18px;
                border-radius: 8px;
                font-size: 13px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.2s;
            }
            .btn-ghost:hover {
                border-color: #6366f1;
                color: #6366f1;
            }

            /* ── Ticket title cell ──────────────────────────────────── */
            .ticket-title-cell {
                max-width: 220px;
            }
            .ticket-title-text {
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                font-weight: 500;
                display: block;
            }
            .ticket-desc {
                font-size: 12px;
                color: #9ca3af;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                margin-top: 2px;
            }
        </style>
    </head>

    <body>
        <div class="dashboard-layout">

            <jsp:include page="sidebar.jsp" />

            <main class="main-content">
                <div class="content-wrapper">

                    <!-- Page Header -->
                    <div class="page-header">
                        <h1><i class="fas fa-ticket-alt"></i> Phiếu Hỗ Trợ Của Tôi</h1>
                        <p class="subtitle">Theo dõi và quản lý các yêu cầu hỗ trợ của bạn</p>
                    </div>

                    <!-- Flash messages -->
                    <c:if test="${not empty sessionScope.successMessage}">
                        <div class="alert alert-success">
                            <i class="fas fa-check-circle"></i> ${sessionScope.successMessage}
                        </div>
                        <c:remove var="successMessage" scope="session"/>
                    </c:if>
                    <c:if test="${not empty sessionScope.errorMessage}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle"></i> ${sessionScope.errorMessage}
                        </div>
                        <c:remove var="errorMessage" scope="session"/>
                    </c:if>
                    <c:if test="${not empty sessionScope.feedbackSuccess}">
                        <div class="alert alert-success">
                            <i class="fas fa-star"></i> ${sessionScope.feedbackSuccess}
                        </div>
                        <c:remove var="feedbackSuccess" scope="session"/>
                    </c:if>
                    <c:if test="${not empty sessionScope.feedbackError}">
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle"></i> ${sessionScope.feedbackError}
                        </div>
                        <c:remove var="feedbackError" scope="session"/>
                    </c:if>

                    <!-- KPI cards (click để filter) -->
                    <div class="stats-grid">
                        <a href="?statusFilter=" class="stat-card stat-primary ${statusFilter == '' ? 'active' : ''}">
                            <div class="stat-icon"><i class="fas fa-ticket-alt"></i></div>
                            <div class="stat-content">
                                <h3>${totalTickets}</h3>
                                <p>Tổng phiếu</p>
                            </div>
                        </a>
                        <a href="?statusFilter=Open" class="stat-card stat-warning ${statusFilter == 'Open' ? 'active' : ''}">
                            <div class="stat-icon"><i class="fas fa-door-open"></i></div>
                            <div class="stat-content">
                                <h3>${countOpen}</h3>
                                <p>Đang mở</p>
                            </div>
                        </a>
                        <a href="?statusFilter=In+Progress" class="stat-card stat-info ${statusFilter == 'In Progress' ? 'active' : ''}">
                            <div class="stat-icon"><i class="fas fa-spinner"></i></div>
                            <div class="stat-content">
                                <h3>${countProgress}</h3>
                                <p>Đang xử lý</p>
                            </div>
                        </a>
                        <a href="?statusFilter=Resolved" class="stat-card stat-success ${statusFilter == 'Resolved' ? 'active' : ''}">
                            <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
                            <div class="stat-content">
                                <h3>${countResolved}</h3>
                                <p>Đã giải quyết</p>
                            </div>
                        </a>
                    </div>

                    <!-- Filter bar -->
                    <div class="filter-bar">
                        <span class="filter-label"><i class="fas fa-filter"></i> Lọc:</span>
                        <div class="filter-tabs">
                            <a href="?statusFilter="            class="filter-tab ${statusFilter == '' ? 'active' : ''}">Tất cả</a>
                            <a href="?statusFilter=Open"        class="filter-tab ${statusFilter == 'Open' ? 'active' : ''}">Open</a>
                            <a href="?statusFilter=In+Progress" class="filter-tab ${statusFilter == 'In Progress' ? 'active' : ''}">In Progress</a>
                            <a href="?statusFilter=Resolved"    class="filter-tab ${statusFilter == 'Resolved' ? 'active' : ''}">Resolved</a>
                        </div>
                        <span class="result-count">${ticketList.size()} phiếu</span>
                    </div>

                    <!-- Ticket table -->
                    <div class="dashboard-card">
                        <div class="card-header">
                            <h3>
                                <i class="fas fa-list" style="color:#6366f1"></i>
                                Danh Sách Phiếu Hỗ Trợ
                            </h3>
                        </div>
                        <div class="card-content">
                            <c:choose>
                                <c:when test="${empty ticketList}">
                                    <div class="empty-state">
                                        <i class="fas fa-ticket-alt"></i>
                                        <p>Không có phiếu hỗ trợ nào</p>
                                        <small>
                                            <c:choose>
                                                <c:when test="${statusFilter != ''}">
                                                    Không có phiếu nào ở trạng thái "${statusFilter}"
                                                </c:when>
                                                <c:otherwise>
                                                    Các phiếu hỗ trợ của bạn sẽ hiển thị ở đây
                                                </c:otherwise>
                                            </c:choose>
                                        </small>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <table class="data-table">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Vấn đề</th>
                                                <th>Ưu tiên</th>
                                                <th>Trạng thái</th>
                                                <th>Ngày tạo</th>
                                                <th>Đánh giá</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${ticketList}" var="ticket">
                                                <tr>
                                                    <td style="font-weight:600;color:#6366f1">#${ticket.id}</td>

                                                    <td class="ticket-title-cell">
                                                        <span class="ticket-title-text" title="${ticket.title}">
                                                            ${ticket.title}
                                                        </span>
                                                        <c:if test="${not empty ticket.description}">
                                                            <span class="ticket-desc" title="${ticket.description}">
                                                                ${ticket.description}
                                                            </span>
                                                        </c:if>
                                                    </td>

                                                    <td>
                                                        <span class="ticket-priority priority-${ticket.priority.toLowerCase()}">
                                                            ${ticket.priority}
                                                        </span>
                                                    </td>

                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${ticket.status == 'Open'}">
                                                                <span class="ticket-status status-open">Open</span>
                                                            </c:when>
                                                            <c:when test="${ticket.status == 'In Progress'}">
                                                                <span class="ticket-status status-inprogress">In Progress</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="ticket-status status-resolved">Resolved</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>

                                                    <td style="font-size:12px;color:#6b7280">
                                                        <fmt:formatDate value="${ticket.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                    </td>

                                                    <%-- ===== FEEDBACK COLUMN ===== --%>
                                                    <td>
                                                        <c:choose>
                                                            <%-- Ticket Resolved + đã có feedback → hiện sao --%>
                                                            <c:when test="${ticket.status == 'Resolved' and feedbackMap[ticket.id] != null}">
                                                                <span title="${feedbackMap[ticket.id].ratingLabel}">
                                                                    <c:forEach begin="1" end="${feedbackMap[ticket.id].rating}" var="s">
                                                                        <span class="stars-display">★</span>
                                                                    </c:forEach>
                                                                    <c:forEach begin="${feedbackMap[ticket.id].rating + 1}" end="5" var="s">
                                                                        <span class="stars-empty">★</span>
                                                                    </c:forEach>
                                                                </span>
                                                            </c:when>

                                                            <%-- Ticket Resolved + chưa có feedback → nút đánh giá --%>
                                                            <c:when test="${ticket.status == 'Resolved' and feedbackMap[ticket.id] == null}">
                                                                <button class="btn-feedback"
                                                                        onclick="openFeedbackModal(${ticket.id}, '${ticket.title}')">
                                                                    <i class="fas fa-star"></i>
                                                                    Đánh giá
                                                                </button>
                                                            </c:when>

                                                            <%-- Ticket chưa Resolved --%>
                                                            <c:otherwise>
                                                                <span style="font-size:12px;color:#d1d5db">—</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                </div>
            </main>
        </div>

        <!-- ════════════════════════════════════════════════════
             FEEDBACK MODAL
             ════════════════════════════════════════════════════ -->
        <div class="modal-overlay" id="feedbackModal">
            <div class="modal-box">
                <div class="modal-header">
                    <i class="fas fa-star"></i>
                    <h4>Đánh Giá Chất Lượng Hỗ Trợ</h4>
                    <button class="modal-close" onclick="closeFeedbackModal()">
                        <i class="fas fa-times"></i>
                    </button>
                </div>

                <div class="modal-body">
                    <div class="modal-ticket-info">
                        <div class="ticket-id" id="modalTicketId"></div>
                        <div class="ticket-title" id="modalTicketTitle"></div>
                    </div>

                    <form id="feedbackForm"
                          action="${pageContext.request.contextPath}/customerservice/submitfeedback"
                          method="post">

                        <input type="hidden" name="ticketId" id="feedbackTicketId">
                        <%-- Redirect về trang này sau khi submit --%>
                        <input type="hidden" name="redirectUrl" value="${pageContext.request.contextPath}/customer/tickets">

                        <label class="star-input-label">
                            Mức độ hài lòng <span style="color:#ef4444">*</span>
                        </label>

                        <div class="star-rating-input">
                            <input type="radio" name="rating" id="r5" value="5" required>
                            <label for="r5" title="5 sao — Rất hài lòng">★</label>
                            <input type="radio" name="rating" id="r4" value="4">
                            <label for="r4" title="4 sao — Hài lòng">★</label>
                            <input type="radio" name="rating" id="r3" value="3">
                            <label for="r3" title="3 sao — Bình thường">★</label>
                            <input type="radio" name="rating" id="r2" value="2">
                            <label for="r2" title="2 sao — Chưa hài lòng">★</label>
                            <input type="radio" name="rating" id="r1" value="1">
                            <label for="r1" title="1 sao — Rất không hài lòng">★</label>
                        </div>

                        <div class="rating-hint" id="ratingHint">Chọn số sao để đánh giá</div>

                        <label class="star-input-label" for="fbComment">
                            Nhận xét thêm <span style="color:#9ca3af;font-weight:400">(không bắt buộc)</span>
                        </label>
                        <textarea id="fbComment" name="comments" class="modal-textarea"
                                  maxlength="1000"
                                  placeholder="Chia sẻ trải nghiệm của bạn về quá trình hỗ trợ..."></textarea>
                        <div class="char-count"><span id="charCount">0</span>/1000</div>

                        <div style="font-size:12px;color:#9ca3af;margin-top:8px">
                            <i class="fas fa-info-circle"></i>
                            Bạn chỉ có thể đánh giá 1 lần cho mỗi phiếu hỗ trợ.
                        </div>
                    </form>
                </div>

                <div class="modal-footer">
                    <button class="btn-ghost" onclick="closeFeedbackModal()">Hủy</button>
                    <button class="btn-primary" id="submitFeedbackBtn" onclick="submitFeedback()" disabled>
                        <i class="fas fa-paper-plane"></i> Gửi Đánh Giá
                    </button>
                </div>
            </div>
        </div>

        <!-- ── Script ──────────────────────────────────────────────── -->
        <script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
        <script>
                        // ── Feedback modal ──────────────────────────────────────
                        var hints = {
                            1: '😞 Rất không hài lòng',
                            2: '😕 Chưa hài lòng',
                            3: '😐 Bình thường',
                            4: '😊 Hài lòng',
                            5: '😄 Rất hài lòng!'
                        };

                        function openFeedbackModal(ticketId, ticketTitle) {
                            document.getElementById('modalTicketId').textContent = 'Phiếu #' + ticketId;
                            document.getElementById('modalTicketTitle').textContent = ticketTitle;
                            document.getElementById('feedbackTicketId').value = ticketId;

                            // Reset form
                            document.getElementById('feedbackForm').reset();
                            document.getElementById('ratingHint').textContent = 'Chọn số sao để đánh giá';
                            document.getElementById('ratingHint').style.color = '#6b7280';
                            document.getElementById('submitFeedbackBtn').disabled = true;
                            document.getElementById('charCount').textContent = '0';

                            document.getElementById('feedbackModal').classList.add('open');
                            document.body.style.overflow = 'hidden';
                        }

                        function closeFeedbackModal() {
                            document.getElementById('feedbackModal').classList.remove('open');
                            document.body.style.overflow = '';
                        }

                        // Click outside modal để đóng
                        document.getElementById('feedbackModal').addEventListener('click', function (e) {
                            if (e.target === this)
                                closeFeedbackModal();
                        });

                        // Star rating interaction
                        document.querySelectorAll('input[name="rating"]').forEach(function (radio) {
                            radio.addEventListener('change', function () {
                                var hint = document.getElementById('ratingHint');
                                hint.textContent = hints[this.value];
                                hint.style.color = '#f59e0b';
                                document.getElementById('submitFeedbackBtn').disabled = false;
                            });
                        });

                        // Char counter
                        document.getElementById('fbComment').addEventListener('input', function () {
                            document.getElementById('charCount').textContent = this.value.length;
                        });

                        // Submit
                        function submitFeedback() {
                            var rating = document.querySelector('input[name="rating"]:checked');
                            if (!rating) {
                                document.getElementById('ratingHint').textContent = '⚠ Vui lòng chọn số sao';
                                document.getElementById('ratingHint').style.color = '#ef4444';
                                return;
                            }
                            document.getElementById('feedbackForm').submit();
                        }

                        // Esc để đóng modal
                        document.addEventListener('keydown', function (e) {
                            if (e.key === 'Escape')
                                closeFeedbackModal();
                        });
        </script>
    </body>
</html>
