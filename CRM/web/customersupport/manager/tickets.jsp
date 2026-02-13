<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Tickets - Support Manager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/support/manager/assets/css/dashboard.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .tickets-container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .tickets-header {
            padding: 20px 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .tickets-header h1 {
            font-size: 24px;
            margin: 0 0 8px 0;
        }
        
        .tickets-header p {
            margin: 0;
            opacity: 0.9;
        }
        
        .filters-section {
            padding: 20px 30px;
            background: #f8f9fa;
            border-bottom: 1px solid #e9ecef;
            display: flex;
            gap: 15px;
            flex-wrap: wrap;
            align-items: center;
        }
        
        .filter-group {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        .filter-group label {
            font-size: 14px;
            color: #495057;
            font-weight: 500;
        }
        
        .filter-group select,
        .filter-group input {
            padding: 8px 12px;
            border: 1px solid #ced4da;
            border-radius: 6px;
            font-size: 14px;
            min-width: 150px;
        }
        
        .filter-group button {
            padding: 8px 16px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .filter-group button:hover {
            background: #5568d3;
            transform: translateY(-1px);
        }
        
        .tickets-table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .tickets-table thead {
            background: #f8f9fa;
        }
        
        .tickets-table th {
            padding: 15px 20px;
            text-align: left;
            font-size: 13px;
            font-weight: 600;
            color: #495057;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        
        .tickets-table td {
            padding: 15px 20px;
            border-top: 1px solid #e9ecef;
            font-size: 14px;
            color: #212529;
        }
        
        .tickets-table tbody tr:hover {
            background: #f8f9fa;
        }
        
        .ticket-id {
            font-weight: 600;
            color: #667eea;
        }
        
        .ticket-subject {
            max-width: 300px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .status-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            display: inline-block;
        }
        
        .status-badge.open {
            background: #e3f2fd;
            color: #1976d2;
        }
        
        .status-badge.in-progress {
            background: #fff3e0;
            color: #f57c00;
        }
        
        .status-badge.resolved {
            background: #e8f5e9;
            color: #388e3c;
        }
        
        .status-badge.closed {
            background: #f5f5f5;
            color: #616161;
        }
        
        .priority-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            display: inline-block;
        }
        
        .priority-badge.low {
            background: #e8f5e9;
            color: #388e3c;
        }
        
        .priority-badge.medium {
            background: #fff3e0;
            color: #f57c00;
        }
        
        .priority-badge.high {
            background: #ffebee;
            color: #d32f2f;
        }
        
        .priority-badge.critical {
            background: #3f51b5;
            color: white;
        }
        
        .action-buttons {
            display: flex;
            gap: 8px;
        }
        
        .btn {
            padding: 6px 12px;
            border: none;
            border-radius: 6px;
            font-size: 12px;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 4px;
        }
        
        .btn-view {
            background: #667eea;
            color: white;
        }
        
        .btn-view:hover {
            background: #5568d3;
        }
        
        .btn-assign {
            background: #28a745;
            color: white;
        }
        
        .btn-assign:hover {
            background: #218838;
        }
        
        .empty-state {
            padding: 60px 30px;
            text-align: center;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 48px;
            margin-bottom: 16px;
            opacity: 0.5;
        }
        
        .empty-state p {
            font-size: 16px;
            margin: 0;
        }
        
        .pagination {
            padding: 20px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            border-top: 1px solid #e9ecef;
        }
        
        .pagination-info {
            font-size: 14px;
            color: #6c757d;
        }
        
        .pagination-buttons {
            display: flex;
            gap: 8px;
        }
        
        .pagination-buttons button {
            padding: 8px 16px;
            border: 1px solid #ced4da;
            background: white;
            border-radius: 6px;
            font-size: 14px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        .pagination-buttons button:hover:not(:disabled) {
            background: #667eea;
            color: white;
            border-color: #667eea;
        }
        
        .pagination-buttons button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        
        /* Modal Styles */
        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 9999;
            align-items: center;
            justify-content: center;
        }
        
        .modal.active {
            display: flex;
        }
        
        .modal-content {
            background: white;
            border-radius: 12px;
            width: 90%;
            max-width: 500px;
            max-height: 80vh;
            overflow-y: auto;
        }
        
        .modal-header {
            padding: 20px 30px;
            border-bottom: 1px solid #e9ecef;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .modal-header h2 {
            margin: 0;
            font-size: 20px;
            color: #212529;
        }
        
        .modal-close {
            background: none;
            border: none;
            font-size: 24px;
            cursor: pointer;
            color: #6c757d;
            padding: 0;
            width: 30px;
            height: 30px;
        }
        
        .modal-body {
            padding: 30px;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-size: 14px;
            font-weight: 600;
            color: #495057;
        }
        
        .form-group select {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ced4da;
            border-radius: 6px;
            font-size: 14px;
        }
        
        .modal-footer {
            padding: 20px 30px;
            border-top: 1px solid #e9ecef;
            display: flex;
            justify-content: flex-end;
            gap: 10px;
        }
        
        .btn-cancel {
            background: #6c757d;
            color: white;
        }
        
        .btn-cancel:hover {
            background: #5a6268;
        }
        
        .btn-submit {
            background: #28a745;
            color: white;
        }
        
        .btn-submit:hover {
            background: #218838;
        }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/components/sidebar.jsp" />
    
    <div class="main-content">
        <div class="tickets-container">
            <div class="tickets-header">
                <h1><i class="fas fa-headset"></i> Quản Lý Tickets</h1>
                <p>Quản lý và phân công phiếu hỗ trợ khách hàng</p>
            </div>
            
            <!-- Filters -->
            <div class="filters-section">
                <div class="filter-group">
                    <label>Trạng thái:</label>
                    <select id="statusFilter" onchange="filterTickets()">
                        <option value="">Tất cả</option>
                        <option value="OPEN">Đang Mở</option>
                        <option value="IN_PROGRESS">Đang Xử Lý</option>
                        <option value="RESOLVED">Đã Giải Quyết</option>
                        <option value="CLOSED">Đã Đóng</option>
                    </select>
                </div>
                
                <div class="filter-group">
                    <label>Độ ưu tiên:</label>
                    <select id="priorityFilter" onchange="filterTickets()">
                        <option value="">Tất cả</option>
                        <option value="LOW">Thấp</option>
                        <option value="MEDIUM">Trung Bình</option>
                        <option value="HIGH">Cao</option>
                        <option value="CRITICAL">Khẩn Cấp</option>
                    </select>
                </div>
                
                <div class="filter-group">
                    <label>Tìm kiếm:</label>
                    <input type="text" id="searchInput" placeholder="ID, tiêu đề, khách hàng..." onkeyup="filterTickets()">
                </div>
                
                <div class="filter-group">
                    <button onclick="filterTickets()"><i class="fas fa-search"></i> Lọc</button>
                </div>
            </div>
            
            <!-- Tickets Table -->
            <c:choose>
                <c:when test="${empty tickets}">
                    <div class="empty-state">
                        <i class="fas fa-inbox"></i>
                        <p>Không có tickets nào</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="tickets-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tiêu Đề</th>
                                <th>Khách Hàng</th>
                                <th>Agent</th>
                                <th>Trạng Thái</th>
                                <th>Độ Ưu Tiên</th>
                                <th>Ngày Tạo</th>
                                <th>Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="ticket" items="${tickets}">
                                <tr>
                                    <td><span class="ticket-id">#${ticket.id}</span></td>
                                    <td><div class="ticket-subject">${ticket.subject}</div></td>
                                    <td>${ticket.customerName}</td>
                                    <td>${ticket.staffName != null ? ticket.staffName : '<span style="color: #dc3545;">Chưa phân công</span>'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ticket.status == 'OPEN'}">
                                                <span class="status-badge open">Đang Mở</span>
                                            </c:when>
                                            <c:when test="${ticket.status == 'IN_PROGRESS'}">
                                                <span class="status-badge in-progress">Đang Xử Lý</span>
                                            </c:when>
                                            <c:when test="${ticket.status == 'RESOLVED'}">
                                                <span class="status-badge resolved">Đã Giải Quyết</span>
                                            </c:when>
                                            <c:when test="${ticket.status == 'CLOSED'}">
                                                <span class="status-badge closed">Đã Đóng</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${ticket.priority == 'LOW'}">
                                                <span class="priority-badge low">Thấp</span>
                                            </c:when>
                                            <c:when test="${ticket.priority == 'MEDIUM'}">
                                                <span class="priority-badge medium">Trung Bình</span>
                                            </c:when>
                                            <c:when test="${ticket.priority == 'HIGH'}">
                                                <span class="priority-badge high">Cao</span>
                                            </c:when>
                                            <c:when test="${ticket.priority == 'CRITICAL'}">
                                                <span class="priority-badge critical">Khẩn Cấp</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                    <td>${ticket.getFormattedCreatedAt()}</td>
                                    <td>
                                        <div class="action-buttons">
                                            <a href="${pageContext.request.contextPath}/support/manager/tickets/details?id=${ticket.id}" class="btn btn-view">
                                                <i class="fas fa-eye"></i> Xem
                                            </a>
                                            <c:if test="${canManageAssign && ticket.status == 'OPEN' && ticket.staffId == null}">
                                                <button class="btn btn-assign" onclick="openAssignModal(${ticket.id}, '${ticket.subject}')">
                                                    <i class="fas fa-user-plus"></i> Phân Công
                                                </button>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    
                    <!-- Pagination -->
                    <div class="pagination">
                        <div class="pagination-info">
                            Hiển thị ${(currentPage - 1) * pageSize + 1} - ${currentPage * pageSize > totalTickets ? totalTickets : currentPage * pageSize} trong tổng số ${totalTickets} tickets
                        </div>
                        <div class="pagination-buttons">
                            <button onclick="changePage(${currentPage - 1})" ${currentPage == 1 ? 'disabled' : ''}>
                                <i class="fas fa-chevron-left"></i> Trước
                            </button>
                            <button>Trang ${currentPage} / ${totalPages}</button>
                            <button onclick="changePage(${currentPage + 1})" ${currentPage == totalPages ? 'disabled' : ''}>
                                Sau <i class="fas fa-chevron-right"></i>
                            </button>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <!-- Assign Modal -->
    <div id="assignModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2><i class="fas fa-user-plus"></i> Phân Công Ticket</h2>
                <button class="modal-close" onclick="closeAssignModal()">&times;</button>
            </div>
            <form action="${pageContext.request.contextPath}/support/manager/assign-ticket" method="post">
                <div class="modal-body">
                    <input type="hidden" name="ticketId" id="assignTicketId">
                    <div class="form-group">
                        <label>Ticket: <span id="assignTicketSubject" style="color: #667eea;"></span></label>
                    </div>
                    <div class="form-group">
                        <label for="agentSelect">Chọn Agent:</label>
                        <select name="agentId" id="agentSelect" required>
                            <option value="">-- Chọn Agent --</option>
                            <c:forEach var="agent" items="${agents}">
                                <option value="${agent.id}">${agent.fullName} (${agent.email})</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-cancel" onclick="closeAssignModal()">Hủy</button>
                    <button type="submit" class="btn btn-submit"><i class="fas fa-check"></i> Phân Công</button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        function filterTickets() {
            const status = document.getElementById('statusFilter').value;
            const priority = document.getElementById('priorityFilter').value;
            const search = document.getElementById('searchInput').value;
            
            const params = new URLSearchParams();
            if (status) params.append('status', status);
            if (priority) params.append('priority', priority);
            if (search) params.append('search', search);
            
            window.location.href = '${pageContext.request.contextPath}/support/manager/tickets?' + params.toString();
        }
        
        function changePage(page) {
            const url = new URL(window.location.href);
            url.searchParams.set('page', page);
            window.location.href = url.toString();
        }
        
        function openAssignModal(ticketId, subject) {
            document.getElementById('assignTicketId').value = ticketId;
            document.getElementById('assignTicketSubject').textContent = '#' + ticketId + ' - ' + subject;
            document.getElementById('assignModal').classList.add('active');
        }
        
        function closeAssignModal() {
            document.getElementById('assignModal').classList.remove('active');
        }
        
        // Close modal when clicking outside
        document.getElementById('assignModal').addEventListener('click', function(e) {
            if (e.target === this) {
                closeAssignModal();
            }
        });
    </script>
</body>
</html>
