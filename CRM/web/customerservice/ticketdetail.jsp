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
            /* Modal overlay */
            #reopenOverlay {
                display: none;
                position: fixed;
                inset: 0;
                background: rgba(0,0,0,0.45);
                z-index: 9000;
                align-items: center;
                justify-content: center;
            }
            #reopenOverlay.open {
                display: flex;
            }

            #reopenModal {
                background: white;
                border-radius: 16px;
                width: 100%;
                max-width: 440px;
                margin: 16px;
                overflow: hidden;
                box-shadow: 0 20px 60px rgba(0,0,0,0.2);
            }

            .reopen-header {
                background: linear-gradient(135deg, #f59e0b, #d97706);
                color: white;
                padding: 18px 22px;
                display: flex;
                align-items: center;
                gap: 10px;
            }
            .reopen-header h4 {
                margin: 0;
                font-size: 16px;
                font-weight: 600;
                flex: 1;
            }

            .reopen-body {
                padding: 22px 24px;
            }
            .reopen-body p {
                margin: 0 0 16px;
                font-size: 14px;
                color: #374151;
                line-height: 1.6;
            }

            .reopen-status-flow {
                display: flex;
                align-items: center;
                gap: 10px;
                background: #fffbeb;
                border-radius: 8px;
                padding: 12px 16px;
                margin-bottom: 16px;
                font-size: 13px;
                font-weight: 600;
            }
            .status-pill-resolved {
                background: #f0fdf4;
                color: #16a34a;
                padding: 4px 12px;
                border-radius: 20px;
                font-size: 12px;
            }
            .status-pill-progress {
                background: #fffbeb;
                color: #d97706;
                padding: 4px 12px;
                border-radius: 20px;
                font-size: 12px;
            }
            .reopen-arrow {
                color: #9ca3af;
                font-size: 16px;
            }

            /* Reason textarea */
            .reopen-label {
                font-size: 13px;
                font-weight: 600;
                color: #374151;
                display: block;
                margin-bottom: 8px;
            }
            .reopen-textarea {
                width: 100%;
                box-sizing: border-box;
                border: 1.5px solid #e5e7eb;
                border-radius: 8px;
                padding: 10px 12px;
                font-size: 13px;
                font-family: inherit;
                resize: vertical;
                min-height: 80px;
                outline: none;
                transition: border-color 0.2s;
            }
            .reopen-textarea:focus {
                border-color: #f59e0b;
            }

            .reopen-footer {
                padding: 14px 22px;
                border-top: 1px solid #f3f4f6;
                display: flex;
                gap: 10px;
                justify-content: flex-end;
            }

            .btn-reopen-cancel {
                background: white;
                color: #6b7280;
                border: 1.5px solid #e5e7eb;
                padding: 8px 18px;
                border-radius: 8px;
                font-size: 13px;
                font-weight: 500;
                cursor: pointer;
                transition: all 0.2s;
            }
            .btn-reopen-cancel:hover {
                border-color: #6b7280;
            }

            .btn-reopen-confirm {
                background: #f59e0b;
                color: white;
                border: none;
                padding: 8px 20px;
                border-radius: 8px;
                font-size: 13px;
                font-weight: 600;
                cursor: pointer;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                transition: background 0.2s;
            }
            .btn-reopen-confirm:hover {
                background: #d97706;
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
        <%-- Modal HTML --%>
        <div id="reopenOverlay">
            <div id="reopenModal">
                <div class="reopen-header">
                    <i class="fas fa-exclamation-triangle"></i>
                    <h4>Xác Nhận Mở Lại Ticket</h4>
                </div>
                <div class="reopen-body">
                    <p>Ticket này đã được đánh dấu <strong>Resolved</strong>. Bạn có chắc muốn mở lại để tiếp tục xử lý?</p>

                    <div class="reopen-status-flow">
                        <span class="status-pill-resolved">Resolved</span>
                        <span class="reopen-arrow"><i class="fas fa-arrow-right"></i></span>
                        <span class="status-pill-progress">In Progress</span>
                    </div>

                    <label class="reopen-label" for="reopenReason">
                        Lý do mở lại <span style="color:#ef4444">*</span>
                    </label>
                    <textarea id="reopenReason" class="reopen-textarea"
                              placeholder="Ví dụ: Vấn đề chưa được giải quyết hoàn toàn, khách hàng phản hồi lại..."
                              maxlength="500"></textarea>
                    <div id="reopenReasonError"
                         style="color:#ef4444;font-size:12px;margin-top:4px;display:none">
                        Vui lòng nhập lý do mở lại ticket.
                    </div>
                </div>
                <div class="reopen-footer">
                    <button class="btn-reopen-cancel" onclick="cancelReopen()">Hủy</button>
                    <button class="btn-reopen-confirm" onclick="confirmReopen()">
                        <i class="fas fa-undo"></i> Xác Nhận Mở Lại
                    </button>
                </div>
            </div>
        </div>
        <script>
            (function () {
                var updateForm = document.querySelector('form[action*="updateticket"]');
                var statusSelect = updateForm ? updateForm.querySelector('select[name="status"]') : null;
                var submitBtn = updateForm ? updateForm.querySelector('button[type="submit"]') : null;

                // Đổi submit button thành button type="button" để chặn submit trực tiếp
                if (submitBtn) {
                    submitBtn.type = 'button';
                    submitBtn.addEventListener('click', checkReopenConfirm);
                }

                // Thêm hidden input lưu current status nếu chưa có
                if (updateForm && !updateForm.querySelector('input[name="currentStatus"]')) {
                    var hidden = document.createElement('input');
                    hidden.type = 'hidden';
                    hidden.name = 'currentStatus';
                    // Lấy selected option hiện tại là currentStatus
                    hidden.value = statusSelect ? statusSelect.value : '';
                    hidden.id = 'currentStatusInput';
                    updateForm.appendChild(hidden);
                }

                // Lưu trạng thái hiện tại của ticket khi trang load
                var originalStatus = statusSelect ? statusSelect.value : '';

                function checkReopenConfirm() {
                    var newStatus = statusSelect ? statusSelect.value : '';

                    // Chỉ hỏi confirmation khi đổi từ Resolved → In Progress hoặc Open
                    if (originalStatus === 'Resolved' && newStatus !== 'Resolved') {
                        document.getElementById('reopenReason').value = '';
                        document.getElementById('reopenReasonError').style.display = 'none';
                        document.getElementById('reopenOverlay').classList.add('open');
                        document.body.style.overflow = 'hidden';
                    } else {
                        // Không cần confirm — submit bình thường
                        updateForm.submit();
                    }
                }

                function confirmReopen() {
                    var reason = document.getElementById('reopenReason').value.trim();
                    if (!reason) {
                        document.getElementById('reopenReasonError').style.display = 'block';
                        return;
                    }

                    // Thêm reason vào form rồi submit
                    var reasonInput = document.createElement('input');
                    reasonInput.type = 'hidden';
                    reasonInput.name = 'reopenReason';
                    reasonInput.value = reason;
                    updateForm.appendChild(reasonInput);

                    document.getElementById('reopenOverlay').classList.remove('open');
                    document.body.style.overflow = '';
                    updateForm.submit();
                }

                function cancelReopen() {
                    // Reset dropdown về Resolved
                    if (statusSelect)
                        statusSelect.value = 'Resolved';
                    document.getElementById('reopenOverlay').classList.remove('open');
                    document.body.style.overflow = '';
                }

                // Expose ra global scope để inline onclick có thể gọi
                window.checkReopenConfirm = checkReopenConfirm;
                window.confirmReopen = confirmReopen;
                window.cancelReopen = cancelReopen;

                // Click outside modal để cancel
                document.getElementById('reopenOverlay').addEventListener('click', function (e) {
                    if (e.target === this)
                        cancelReopen();
                });

                // Esc để cancel
                document.addEventListener('keydown', function (e) {
                    if (e.key === 'Escape')
                        cancelReopen();
                });
            })();
        </script>

    </body>
</html>
