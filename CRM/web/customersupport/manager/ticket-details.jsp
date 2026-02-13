<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="model.TicketDTO, model.TicketHistory, model.UserSession, util.PermissionChecker, java.util.List" %>
<%
    // Get user session
    UserSession userSession = (UserSession) session.getAttribute("userSession");
    if (userSession == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    // Permission check
    if (!PermissionChecker.canView(userSession, "/support/manager/tickets/details")) {
        response.sendRedirect(request.getContextPath() + "/403.jsp");
        return;
    }
    
    // Get ticket from request
    TicketDTO ticket = (TicketDTO) request.getAttribute("ticket");
    if (ticket == null) {
        response.sendRedirect(request.getContextPath() + "/support/manager/tickets");
        return;
    }
    
    // Get ticket history
    @SuppressWarnings("unchecked")
    List<TicketHistory> history = (List<TicketHistory>) request.getAttribute("history");
    
    boolean canManageAssign = PermissionChecker.canManage(userSession, "/support/tickets/assign");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiết Ticket #<%= ticket.getId() %> - Hỗ Trợ Khách Hàng</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/sidebar.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/support/manager/assets/css/dashboard.css">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .details-container {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        
        .details-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e5e7eb;
        }
        
        .details-title {
            flex: 1;
        }
        
        .details-title h2 {
            font-size: 24px;
            color: #1f2937;
            margin: 0 0 10px 0;
        }
        
        .details-meta {
            display: flex;
            gap: 20px;
            font-size: 14px;
            color: #6b7280;
        }
        
        .details-actions {
            display: flex;
            gap: 10px;
        }
        
        .btn {
            padding: 10px 20px;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            border: none;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 5px;
            transition: all 0.2s;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background: #f3f4f6;
            color: #374151;
        }
        
        .btn-secondary:hover {
            background: #e5e7eb;
        }
        
        .details-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
            margin-bottom: 30px;
        }
        
        .details-section {
            background: #f9fafb;
            border-radius: 8px;
            padding: 20px;
        }
        
        .section-title {
            font-size: 16px;
            font-weight: 600;
            color: #1f2937;
            margin: 0 0 15px 0;
            padding-bottom: 10px;
            border-bottom: 1px solid #e5e7eb;
        }
        
        .detail-row {
            display: flex;
            padding: 10px 0;
            border-bottom: 1px solid #e5e7eb;
        }
        
        .detail-row:last-child {
            border-bottom: none;
        }
        
        .detail-label {
            font-weight: 500;
            color: #6b7280;
            width: 140px;
            flex-shrink: 0;
        }
        
        .detail-value {
            color: #1f2937;
            flex: 1;
        }
        
        .badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .badge-open { background: #dbeafe; color: #1e40af; }
        .badge-in_progress { background: #fef3c7; color: #92400e; }
        .badge-resolved { background: #d1fae5; color: #065f46; }
        .badge-closed { background: #e5e7eb; color: #374151; }
        
        .badge-high { background: #fee2e2; color: #991b1b; }
        .badge-medium { background: #fef3c7; color: #92400e; }
        .badge-low { background: #dbeafe; color: #1e40af; }
        
        .full-width-section {
            background: #f9fafb;
            border-radius: 8px;
            padding: 20px;
            margin-top: 30px;
        }
        
        .history-section {
            background: #f9fafb;
            border-radius: 8px;
            padding: 20px;
            margin-top: 30px;
        }
        
        .history-item {
            background: white;
            border-left: 4px solid #667eea;
            padding: 15px;
            margin-bottom: 15px;
            border-radius: 4px;
        }
        
        .history-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            font-size: 14px;
        }
        
        .history-user {
            font-weight: 600;
            color: #1f2937;
        }
        
        .history-time {
            color: #6b7280;
            font-size: 12px;
        }
        
        .history-action {
            color: #4b5563;
            font-size: 14px;
        }
        
        .history-details {
            color: #6b7280;
            font-size: 13px;
            margin-top: 8px;
            padding: 10px;
            background: #f3f4f6;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <%@ include file="/components/sidebar.jsp" %>
    
    <div class="main-content">
        <div class="page-header">
            <div>
                <h1>Chi Tiết Ticket</h1>
                <p>Xem thông tin chi tiết và quản lý ticket</p>
            </div>
        </div>

        <div class="details-container">
            <div class="details-header">
                <div class="details-title">
                    <h2>#<%= ticket.getId() %> - <%= ticket.getSubject() %></h2>
                    <div class="details-meta">
                        <span>Tạo: <%= ticket.getFormattedCreatedAt() %></span>
                        <% if (ticket.getUpdatedAt() != null) { %>
                        <span>Cập nhật: <%= ticket.getUpdatedAtFormatted() %></span>
                        <% } %>
                    </div>
                </div>
                <div class="details-actions">
                    <a href="<%= request.getContextPath() %>/support/manager/tickets" class="btn btn-secondary">
                        ← Quay lại
                    </a>
                    <% if (canManageAssign && ticket.getStaffId() == null) { %>
                    <a href="<%= request.getContextPath() %>/support/manager/tickets#assign-<%= ticket.getId() %>" class="btn btn-primary">
                        Phân Công
                    </a>
                    <% } %>
                </div>
            </div>
            
            <div class="details-grid">
                <!-- Ticket Information -->
                <div class="details-section">
                    <h3 class="section-title">Thông Tin Ticket</h3>
                    <div class="detail-row">
                        <span class="detail-label">Mã Ticket:</span>
                        <span class="detail-value">#<%= ticket.getId() %></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Trạng Thái:</span>
                        <span class="detail-value">
                            <span class="badge badge-<%= ticket.getStatus().toLowerCase() %>">
                                <%= ticket.getStatus() %>
                            </span>
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Độ Ưu Tiên:</span>
                        <span class="detail-value">
                            <span class="badge badge-<%= ticket.getPriority().toLowerCase() %>">
                                <%= ticket.getPriority() %>
                            </span>
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Nhân Viên:</span>
                        <span class="detail-value">
                            <%= ticket.getStaffName() != null ? ticket.getStaffName() : "<em>Chưa phân công</em>" %>
                        </span>
                    </div>
                    <% if (ticket.getOrderId() != null) { %>
                    <div class="detail-row">
                        <span class="detail-label">Đơn Hàng:</span>
                        <span class="detail-value">#<%= ticket.getOrderId() %></span>
                    </div>
                    <% } %>
                </div>
                
                <!-- Customer Information -->
                <div class="details-section">
                    <h3 class="section-title">Thông Tin Khách Hàng</h3>
                    <div class="detail-row">
                        <span class="detail-label">Họ Tên:</span>
                        <span class="detail-value"><%= ticket.getCustomerName() %></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Email:</span>
                        <span class="detail-value"><%= ticket.getCustomerEmail() %></span>
                    </div>
                    <% if (ticket.getCustomerPhone() != null) { %>
                    <div class="detail-row">
                        <span class="detail-label">Điện Thoại:</span>
                        <span class="detail-value"><%= ticket.getCustomerPhone() %></span>
                    </div>
                    <% } %>
                    <% if (ticket.getCustomerCity() != null) { %>
                    <div class="detail-row">
                        <span class="detail-label">Thành Phố:</span>
                        <span class="detail-value"><%= ticket.getCustomerCity() %></span>
                    </div>
                    <% } %>
                    <% if (ticket.getCustomerProvince() != null) { %>
                    <div class="detail-row">
                        <span class="detail-label">Tỉnh/Thành:</span>
                        <span class="detail-value"><%= ticket.getCustomerProvince() %></span>
                    </div>
                    <% } %>
                </div>
            </div>
            
            <div class="full-width-section">
                <h3 class="section-title">Nội Dung Ticket</h3>
                <div class="detail-row">
                    <span class="detail-label">Tiêu Đề:</span>
                    <span class="detail-value"><%= ticket.getSubject() %></span>
                </div>
            </div>
            
            <% if (history != null && !history.isEmpty()) { %>
            <div class="history-section">
                <h3 class="section-title">Lịch Sử Xử Lý</h3>
                <% for (TicketHistory h : history) { %>
                <div class="history-item">
                    <div class="history-header">
                        <span class="history-user">
                            <i class="fas fa-user-circle"></i> 
                            <%= h.getStaffName() != null ? h.getStaffName() : "Hệ thống" %>
                        </span>
                        <span class="history-time">
                            <i class="fas fa-clock"></i> 
                            <%= h.getFormattedCreatedAt() %>
                        </span>
                    </div>
                    <div class="history-action">
                        <strong><%= h.getAction() %></strong>
                    </div>
                    <% if (h.getDetails() != null && !h.getDetails().trim().isEmpty()) { %>
                    <div class="history-details">
                        <%= h.getDetails() %>
                    </div>
                    <% } %>
                </div>
                <% } %>
            </div>
            <% } else { %>
            <div class="history-section">
                <h3 class="section-title">Lịch Sử Xử Lý</h3>
                <p style="text-align: center; color: #6b7280; padding: 20px;">Chưa có lịch sử xử lý</p>
            </div>
            <% } %>
        </div>
    </div>
</body>
</html>
