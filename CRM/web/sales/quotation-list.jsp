<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Báo giá – ${opportunity.title}</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body{margin:0;font-family:"Segoe UI",sans-serif;background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:30px;min-height:100vh;}
        .page-header{background:rgba(255,255,255,0.95);padding:22px 28px;border-radius:20px;box-shadow:0 10px 25px rgba(0,0,0,0.15);margin-bottom:25px;display:flex;justify-content:space-between;align-items:center;}
        .page-header h2{font-weight:700;font-size:24px;margin:0;color:#333;}
        .card{background:rgba(255,255,255,0.95);border-radius:16px;box-shadow:0 8px 20px rgba(0,0,0,0.12);overflow:hidden;}
        .btn{border:none;padding:10px 16px;border-radius:8px;font-size:14px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white;}
        .btn-info{background:linear-gradient(135deg,#0dcaf0,#0d6efd);color:white;}
        .btn-warning{background:linear-gradient(135deg,#f7971e,#ffd200);color:#333;}
        .btn-sm{padding:5px 10px;font-size:12px;}
        table{width:100%;border-collapse:collapse;}
        th{background:linear-gradient(135deg,#667eea,#764ba2);color:white;padding:13px 15px;font-size:13px;text-align:left;}
        td{padding:12px 15px;border-bottom:1px solid #f0f0f0;font-size:14px;vertical-align:middle;}
        tr:hover td{background:#f8f9ff;}
        .badge{padding:4px 10px;border-radius:20px;font-size:12px;font-weight:600;}
        .badge-Draft{background:#e3f2fd;color:#1565c0;}
        .badge-Approved{background:#e8f5e9;color:#2e7d32;}
        .badge-Rejected{background:#fce4ec;color:#c62828;}
        .badge-Sent{background:#f3e5f5;color:#6a1b9a;}
        .badge-Converted{background:#e0f2f1;color:#00695c;}
        .val-currency{font-weight:700;color:#2e7d32;}
        .version-badge{background:#ede7f6;color:#5e35b1;padding:3px 9px;border-radius:10px;font-size:11px;font-weight:700;}
        .empty-state{text-align:center;padding:50px;color:#aaa;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div class="page-header">
        <div>
            <a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${opportunity.id}"
               style="font-size:13px;color:#667eea;text-decoration:none;">
               <i class="fas fa-arrow-left"></i> Quay lại Opportunity
            </a>
            <h2 style="margin:4px 0 0;">
                <i class="fas fa-file-invoice-dollar" style="color:#667eea"></i>
                Báo giá – ${opportunity.title}
            </h2>
        </div>
        <a href="${pageContext.request.contextPath}/sales/quotation-create?opportunityId=${opportunity.id}" class="btn btn-primary">
            <i class="fas fa-plus"></i> Tạo báo giá mới
        </a>
    </div>

    <div class="card">
        <c:choose>
            <c:when test="${empty quotations}">
                <div class="empty-state">
                    <i class="fas fa-file-invoice fa-3x" style="margin-bottom:12px;display:block;color:#ddd"></i>
                    Chưa có báo giá nào cho opportunity này.<br>
                    <a href="${pageContext.request.contextPath}/sales/quotation-create?opportunityId=${opportunity.id}"
                       class="btn btn-primary" style="margin-top:14px;">
                        <i class="fas fa-plus"></i> Tạo báo giá đầu tiên
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>Mã báo giá</th>
                            <th>Phiên bản</th>
                            <th>Status</th>
                            <th>Người tạo</th>
                            <th>Tổng tiền</th>
                            <th>Hiệu lực đến</th>
                            <th>Ngày tạo</th>
                            <th>Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="q" items="${quotations}">
                            <tr>
                                <td><strong>${q.quotationCode}</strong></td>
                                <td><span class="version-badge">v${q.version}</span></td>
                                <td>
                                    <span class="badge badge-${q.status}">${q.status}</span>
                                </td>
                                <td>${q.createdByName}</td>
                                <td class="val-currency">
                                    <fmt:formatNumber value="${q.totalAmount}" type="number" groupingUsed="true"/> đ
                                </td>
                                <td><fmt:formatDate value="${q.validUntil}" pattern="dd/MM/yyyy"/></td>
                                <td><fmt:formatDate value="${q.createdAt}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <div style="display:flex;gap:5px;">
                                        <a href="${pageContext.request.contextPath}/sales/quotation-detail?id=${q.id}"
                                           class="btn btn-info btn-sm"><i class="fas fa-eye"></i></a>
                                        <c:if test="${q.status == 'Draft'}">
                                            <a href="${pageContext.request.contextPath}/sales/quotation-edit?id=${q.id}&opportunityId=${opportunity.id}"
                                               class="btn btn-warning btn-sm"><i class="fas fa-edit"></i></a>
                                        </c:if>
                                        <form method="post" action="${pageContext.request.contextPath}/sales/quotation-version" style="display:inline">
                                            <input type="hidden" name="id" value="${q.id}">
                                            <button type="submit" class="btn btn-primary btn-sm" title="Tạo phiên bản mới"><i class="fas fa-copy"></i></button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
