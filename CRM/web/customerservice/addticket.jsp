<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Tạo Ticket Hỗ Trợ</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="icon" type="image/png"
              href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <style>
            body {
                margin: 0;
                font-family: 'Segoe UI', sans-serif;
                background: linear-gradient(135deg, #3a7bd5, #3a6073);
            }
            .main-content {
                margin-left: 270px;
                min-height: 100vh;
                display: flex;
                justify-content: center;
                align-items: flex-start;
                padding: 40px 30px;
                box-sizing: border-box;
            }
            .container-box {
                width: 100%;
                max-width: 780px;
            }

            /* ── Page header ─────────────────────────────────────── */
            .page-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                color: white;
                margin-bottom: 24px;
            }
            .page-header h2 {
                margin: 0;
                font-size: 26px;
                font-weight: 700;
            }

            .btn-back {
                background: rgba(255,255,255,0.2);
                color: white;
                padding: 8px 18px;
                border-radius: 10px;
                text-decoration: none;
                font-weight: 600;
                font-size: 14px;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                transition: 0.3s;
                backdrop-filter: blur(10px);
            }
            .btn-back:hover {
                background: rgba(255,255,255,0.35);
            }

            /* ── Card ────────────────────────────────────────────── */
            .card {
                background: white;
                border-radius: 20px;
                box-shadow: 0 15px 40px rgba(0,0,0,0.15);
                overflow: hidden;
            }
            .card-header-strip {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                padding: 18px 26px;
                font-weight: 700;
                font-size: 16px;
                display: flex;
                align-items: center;
                gap: 10px;
            }
            .card-body {
                padding: 28px;
            }

            /* ── Form elements ───────────────────────────────────── */
            .row {
                display: flex;
                gap: 20px;
            }
            .col-6 {
                flex: 1;
            }

            .form-group {
                margin-bottom: 20px;
            }
            .form-group label {
                font-weight: 600;
                font-size: 14px;
                color: #444;
                display: block;
                margin-bottom: 7px;
            }
            .form-group label .required {
                color: #dc3545;
                margin-left: 3px;
            }

            .form-control {
                width: 100%;
                padding: 10px 14px;
                border-radius: 10px;
                border: 2px solid #e0e0e0;
                font-size: 14px;
                font-family: 'Segoe UI', sans-serif;
                transition: border-color 0.3s;
                box-sizing: border-box;
                outline: none;
            }
            .form-control:focus {
                border-color: #667eea;
            }

            textarea.form-control {
                resize: vertical;
                min-height: 110px;
            }

            /* ── Priority badges trong select ────────────────────── */
            .priority-hint {
                display: flex;
                gap: 8px;
                margin-top: 6px;
                flex-wrap: wrap;
            }
            .p-dot {
                font-size: 11px;
                padding: 3px 9px;
                border-radius: 10px;
                color: white;
                font-weight: 600;
            }
            .p-low    {
                background: #17a2b8;
            }
            .p-medium {
                background: #ffc107;
                color: #333;
            }
            .p-high   {
                background: #fd7e14;
            }
            .p-urgent {
                background: #dc3545;
            }

            /* ── Error alert ─────────────────────────────────────── */
            .alert-error {
                background: #fff3f3;
                border-left: 4px solid #dc3545;
                border-radius: 0 10px 10px 0;
                padding: 12px 16px;
                color: #721c24;
                font-size: 14px;
                margin-bottom: 20px;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            /* ── Submit row ──────────────────────────────────────── */
            .submit-row {
                display: flex;
                justify-content: flex-end;
                gap: 12px;
                margin-top: 10px;
            }
            .btn-reset {
                padding: 10px 22px;
                border-radius: 10px;
                border: 2px solid #e0e0e0;
                background: white;
                font-size: 14px;
                font-weight: 600;
                color: #666;
                cursor: pointer;
                transition: 0.3s;
            }
            .btn-reset:hover {
                background: #f5f5f5;
            }

            .btn-submit {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                border: none;
                padding: 10px 28px;
                border-radius: 10px;
                font-size: 14px;
                font-weight: 700;
                cursor: pointer;
                transition: 0.3s;
                display: inline-flex;
                align-items: center;
                gap: 8px;
            }
            .btn-submit:hover {
                opacity: 0.88;
                transform: translateY(-2px);
            }
        </style>
    </head>
    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">
            <div class="container-box">

                <!-- ── Page header ── -->
                <div class="page-header">
                    <h2>
                        <i class="fas fa-plus-circle"></i>
                        Tạo Ticket Hỗ Trợ
                    </h2>
                    <a href="${pageContext.request.contextPath}/customerservice/ticketlist"
                       class="btn-back">
                        <i class="fas fa-arrow-left"></i> Quay lại
                    </a>
                </div>

                <!-- ── Card ── -->
                <div class="card">
                    <div class="card-header-strip">
                        <i class="fas fa-ticket-alt"></i> Thông Tin Ticket Mới
                    </div>
                    <div class="card-body">

                        <%-- Error message nếu có --%>
                        <c:if test="${not empty errorMsg}">
                            <div class="alert-error">
                                <i class="fas fa-exclamation-circle"></i>
                                ${errorMsg}
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/customerservice/addticket"
                              method="post">

                            <div class="row">
                                <!-- Customer dropdown — thay cho ô nhập ID thô -->
                                <div class="col-6">
                                    <div class="form-group">
                                        <label>
                                            <i class="fas fa-user" style="color:#667eea"></i>
                                            Khách hàng <span class="required">*</span>
                                        </label>
                                        <select name="customerId" class="form-control" required>
                                            <option value="">-- Chọn khách hàng --</option>
                                            <c:forEach var="c" items="${customerList}">
                                                <option value="${c[0]}">${c[1]}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <!-- Priority -->
                                <div class="col-6">
                                    <div class="form-group">
                                        <label>
                                            <i class="fas fa-flag" style="color:#667eea"></i>
                                            Mức ưu tiên <span class="required">*</span>
                                        </label>
                                        <select name="priority" class="form-control">
                                            <option value="Low">Low</option>
                                            <option value="Medium" selected>Medium</option>
                                            <option value="High">High</option>
                                            <option value="Urgent">Urgent</option>
                                        </select>
                                        <div class="priority-hint">
                                            <span class="p-dot p-low">Low</span>
                                            <span class="p-dot p-medium">Medium</span>
                                            <span class="p-dot p-high">High</span>
                                            <span class="p-dot p-urgent">Urgent</span>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Assigned to -->
                            <div class="form-group">
                                <label>
                                    <i class="fas fa-user-tie" style="color:#667eea"></i>
                                    Giao cho nhân viên <span class="required">*</span>
                                </label>
                                <select name="assignedTo" class="form-control" required>
                                    <option value="">-- Chọn nhân viên xử lý --</option>
                                    <c:forEach var="s" items="${staffList}">
                                        <option value="${s[0]}">${s[1]}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Title -->
                            <div class="form-group">
                                <label>
                                    <i class="fas fa-heading" style="color:#667eea"></i>
                                    Tiêu đề <span class="required">*</span>
                                </label>
                                <input type="text" name="title" class="form-control"
                                       placeholder="Mô tả ngắn gọn vấn đề..." required>
                            </div>

                            <!-- Description -->
                            <div class="form-group">
                                <label>
                                    <i class="fas fa-align-left" style="color:#667eea"></i>
                                    Mô tả chi tiết
                                </label>
                                <textarea name="description" class="form-control"
                                          placeholder="Mô tả chi tiết vấn đề của khách hàng..."></textarea>
                            </div>

                            <div class="submit-row">
                                <button type="reset" class="btn-reset">
                                    <i class="fas fa-undo"></i> Nhập lại
                                </button>
                                <button type="submit" class="btn-submit">
                                    <i class="fas fa-paper-plane"></i> Tạo Ticket
                                </button>
                            </div>

                        </form>

                    </div>
                </div>

            </div>
        </div>
    </body>
</html>
