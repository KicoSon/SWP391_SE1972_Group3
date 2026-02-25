<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách Opportunity – Sales CRM</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sales-common.css">
    <style>
        body{margin:0;font-family:"Segoe UI";background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:30px;min-height:100vh;}
        .header{background:rgba(255,255,255,0.95);padding:22px 28px;border-radius:20px;box-shadow:0 10px 25px rgba(0,0,0,0.15);margin-bottom:25px;display:flex;justify-content:space-between;align-items:center;}
        .header h2{font-weight:700;font-size:24px;margin:0;color:#333;}
        .btn{border:none;padding:10px 16px;border-radius:8px;font-size:14px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white;}
        .btn-success{background:linear-gradient(135deg,#56ab2f,#a8e063);color:white;}
        .btn-danger{background:linear-gradient(135deg,#f093fb,#f5576c);color:white;}
        .btn-warning{background:linear-gradient(135deg,#f7971e,#ffd200);color:#333;}
        .btn-info{background:linear-gradient(135deg,#0dcaf0,#0d6efd);color:white;}
        .filter-bar{background:rgba(255,255,255,0.95);padding:20px 25px;border-radius:16px;box-shadow:0 8px 20px rgba(0,0,0,0.12);margin-bottom:20px;display:flex;gap:12px;flex-wrap:wrap;align-items:center;}
        .filter-bar input,.filter-bar select{padding:9px 12px;border-radius:8px;border:1px solid #ddd;font-size:14px;min-width:160px;}
        .card{background:rgba(255,255,255,0.95);border-radius:16px;box-shadow:0 8px 20px rgba(0,0,0,0.12);overflow:hidden;}
        table{width:100%;border-collapse:collapse;}
        th{background:linear-gradient(135deg,#667eea,#764ba2);color:white;padding:13px 15px;font-size:13px;text-align:left;}
        td{padding:12px 15px;border-bottom:1px solid #f0f0f0;font-size:14px;vertical-align:middle;}
        tr:hover td{background:#f8f9ff;}
        .badge{padding:4px 10px;border-radius:20px;font-size:12px;font-weight:600;color:white;}
        .badge-Qualification{background:#6c757d;}
        .badge-need-analysis,.badge-Need\.Analysis{background:#0dcaf0;}
        .badge-Product\.Proposal{background:#0d6efd;}
        .badge-Quotation{background:#ffc107;color:#000;}
        .badge-Negotiation{background:#fd7e14;}
        .badge-Closed\.Won,.badge-Won{background:#198754;}
        .badge-Closed\.Lost,.badge-Lost{background:#dc3545;}
        .stage-badge{padding:3px 9px;border-radius:12px;font-size:11px;font-weight:700;}
        .status-Open{background:#e3f2fd;color:#1565c0;}
        .status-Won{background:#e8f5e9;color:#2e7d32;}
        .status-Lost{background:#fce4ec;color:#c62828;}
        .pagination{display:flex;gap:8px;justify-content:center;padding:20px;}
        .btn-group{display:flex;gap:5px;}
        .empty-state{text-align:center;padding:50px;color:#888;}
        .val-currency{font-weight:700;color:#2e7d32;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div class="header">
        <div>
            <h2><i class="fas fa-handshake" style="color:#667eea"></i> Danh sách Opportunity</h2>
            <p style="margin:0;color:#888;font-size:14px;">Quản lý các cơ hội bán hàng</p>
        </div>
        <div style="display:flex;gap:10px;">
            <a href="${pageContext.request.contextPath}/sales/pipeline-board" class="btn btn-info"><i class="fas fa-columns"></i> Kanban</a>
            <c:if test="${isManager}">
                <a href="${pageContext.request.contextPath}/sales/opportunity-export" class="btn btn-success"><i class="fas fa-file-csv"></i> Export CSV</a>
            </c:if>
            <a href="${pageContext.request.contextPath}/sales/opportunity-create" class="btn btn-primary"><i class="fas fa-plus"></i> Tạo mới</a>
        </div>
    </div>

    <!-- Filters -->
    <form method="get" action="${pageContext.request.contextPath}/sales/opportunities">
        <div class="filter-bar">
            <input type="text" name="search" placeholder="🔍 Tìm kiếm..." value="${searchVal}">
            <select name="stage">
                <option value="">-- Tất cả Stage --</option>
                <option value="Qualification"    <c:if test="${stageVal == 'Qualification'}">selected</c:if>>Qualification</option>
                <option value="Need Analysis"    <c:if test="${stageVal == 'Need Analysis'}">selected</c:if>>Need Analysis</option>
                <option value="Product Proposal" <c:if test="${stageVal == 'Product Proposal'}">selected</c:if>>Product Proposal</option>
                <option value="Quotation"        <c:if test="${stageVal == 'Quotation'}">selected</c:if>>Quotation</option>
                <option value="Negotiation"      <c:if test="${stageVal == 'Negotiation'}">selected</c:if>>Negotiation</option>
                <option value="Closed Won"       <c:if test="${stageVal == 'Closed Won'}">selected</c:if>>Closed Won</option>
                <option value="Closed Lost"      <c:if test="${stageVal == 'Closed Lost'}">selected</c:if>>Closed Lost</option>
            </select>

            <select name="status">
                <option value="">-- Tất cả Status --</option>
                <option value="Open"  <c:if test="${statusVal == 'Open'}">selected</c:if>>Open</option>
                <option value="Won"   <c:if test="${statusVal == 'Won'}">selected</c:if>>Won</option>
                <option value="Lost"  <c:if test="${statusVal == 'Lost'}">selected</c:if>>Lost</option>
            </select>
            <button type="submit" class="btn btn-primary"><i class="fas fa-search"></i> Lọc</button>
            <a href="${pageContext.request.contextPath}/sales/opportunities" class="btn btn-warning"><i class="fas fa-redo"></i> Reset</a>
        </div>
    </form>

    <!-- Table -->
    <div class="card">
        <table>
            <thead>
                <tr>
                    <th>#</th>
                    <th>Tiêu đề</th>
                    <th>Khách hàng</th>
                    <th>Sales phụ trách</th>
                    <th>Stage</th>
                    <th>Status</th>
                    <th>Giá trị dự kiến</th>
                    <th>Ngày đóng</th>
                    <th>Thao tác</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty opportunityList}">
                        <tr><td colspan="9" class="empty-state"><i class="fas fa-inbox fa-2x"></i><br>Không có opportunity nào</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="o" items="${opportunityList}" varStatus="i">
                            <tr>
                                <td>${i.count}</td>
                                <td><strong><a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${o.id}" style="text-decoration:none;color:#667eea">${o.title}</a></strong></td>
                                <td>${not empty o.customerName ? o.customerName : '<em style="color:#aaa">Chưa có</em>'}</td>
                                <td>${o.assignedSalesName}</td>
                                <td><span class="badge badge-stage">${o.stage}</span></td>

                                <td><span class="badge status-${o.status}">${o.status}</span></td>
                                <td class="val-currency">
                                    <c:if test="${not empty o.expectedValue}">
                                        <fmt:formatNumber value="${o.expectedValue}" type="number" groupingUsed="true"/> đ
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${not empty o.expectedCloseDate}">
                                        <fmt:formatDate value="${o.expectedCloseDate}" pattern="dd/MM/yyyy"/>
                                    </c:if>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${o.id}" class="btn btn-info" title="Chi tiết"><i class="fas fa-eye"></i></a>
                                        <a href="${pageContext.request.contextPath}/sales/opportunity-update?id=${o.id}" class="btn btn-warning" title="Sửa"><i class="fas fa-edit"></i></a>
                                        <c:if test="${isManager}">
                                            <form method="post" action="${pageContext.request.contextPath}/sales/opportunities" style="display:inline" onsubmit="return confirm('Xóa opportunity này?')">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${o.id}">
                                                <button type="submit" class="btn btn-danger" title="Xóa"><i class="fas fa-trash"></i></button>
                                            </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
        <div style="padding:12px 15px;background:#f8f9ff;border-top:1px solid #eee;font-size:13px;color:#666;">
            Tổng: <strong>${not empty opportunityList ? opportunityList.size() : 0}</strong> cơ hội
        </div>
    </div>
</div>
</body>
</html>
