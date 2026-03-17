<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${quotation.quotationCode} – Chi tiết Báo giá</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body{margin:0;font-family:"Segoe UI";background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:30px;min-height:100vh;}
        .card{background:rgba(255,255,255,0.95);border-radius:18px;box-shadow:0 10px 25px rgba(0,0,0,0.15);padding:25px;margin-bottom:20px;}
        .header-row{display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:15px;margin-bottom:22px;}
        .btn{border:none;padding:9px 14px;border-radius:8px;font-size:13px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white;}
        .btn-success{background:linear-gradient(135deg,#56ab2f,#a8e063);color:white;}
        .btn-danger{background:linear-gradient(135deg,#f093fb,#f5576c);color:white;}
        .btn-warning{background:linear-gradient(135deg,#f7971e,#ffd200);color:#333;}
        .btn-info{background:linear-gradient(135deg,#0dcaf0,#0d6efd);color:white;}
        .badge{padding:5px 12px;border-radius:20px;font-weight:700;font-size:13px;}
        .badge-Draft{background:#e3f2fd;color:#1565c0;}
        .badge-Approved{background:#e8f5e9;color:#2e7d32;}
        .badge-Rejected{background:#fce4ec;color:#c62828;}
        .badge-Sent{background:#f3e5f5;color:#6a1b9a;}
        .badge-Converted{background:#e0f2f1;color:#00695c;}
        .info-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:16px;margin-bottom:20px;}
        .info-item label{font-size:11px;color:#888;text-transform:uppercase;font-weight:700;margin-bottom:4px;display:block;}
        .info-item span{font-size:15px;font-weight:600;}
        table{width:100%;border-collapse:collapse;}
        th{background:#f3f4f7;color:#555;padding:10px 14px;font-size:12px;text-align:left;font-weight:700;}
        td{padding:10px 14px;border-bottom:1px solid #f0f0f0;font-size:14px;vertical-align:middle;}
        .totals-box{background:#f8f9ff;border-radius:10px;padding:16px;text-align:right;}
        .totals-box .row-line{display:flex;justify-content:flex-end;gap:30px;padding:4px 0;font-size:14px;}
        .grand-total{font-size:18px;font-weight:800;color:#2e7d32;border-top:2px solid #ddd;padding-top:8px;margin-top:8px;}
        .version-badge{background:#ede7f6;color:#5e35b1;padding:3px 9px;border-radius:10px;font-size:12px;font-weight:700;}
        .alert{padding:12px;border-radius:8px;margin-bottom:15px;font-size:14px;}
        .alert-success{background:#e8f5e9;color:#2e7d32;} .alert-error{background:#fce4ec;color:#c62828;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div class="header-row">
        <div>
            <a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${quotation.opportunityId}"
               style="color:rgba(255,255,255,0.8);text-decoration:none;font-size:13px;">
               <i class="fas fa-arrow-left"></i> Quay lại Opportunity
            </a>
            <h2 style="color:white;margin:6px 0 0;font-size:22px;">
                ${quotation.quotationCode} <span class="version-badge">v${quotation.version}</span>
            </h2>
        </div>
        <div style="display:flex;gap:8px;flex-wrap:wrap;">
            <c:if test="${quotation.status == 'Draft'}">
                <a href="${pageContext.request.contextPath}/sales/quotation-edit?id=${quotation.id}&opportunityId=${quotation.opportunityId}" class="btn btn-warning"><i class="fas fa-edit"></i> Sửa</a>
                
                <form method="post" action="${pageContext.request.contextPath}/sales/quotation-approve" style="display:inline" onsubmit="return confirm('Are you sure you want to approve this quotation?');">
                    <input type="hidden" name="id" value="${quotation.id}">
                    <input type="hidden" name="action" value="approve">
                    <button type="submit" class="btn btn-success"><i class="fas fa-check"></i> Approve</button>
                </form>

                <form method="post" action="${pageContext.request.contextPath}/sales/quotation-approve" style="display:inline" onsubmit="return confirm('Are you sure you want to reject this quotation?');">
                    <input type="hidden" name="id" value="${quotation.id}">
                    <input type="hidden" name="action" value="reject">
                    <button type="submit" class="btn btn-danger"><i class="fas fa-times"></i> Reject</button>
                </form>
            </c:if>
            
            <c:if test="${quotation.status == 'Approved'}">
                <form method="post" action="${pageContext.request.contextPath}/sales/convert-to-order" style="display:inline" onsubmit="return confirm('Convert this quotation to an order?');">
                    <input type="hidden" name="quotationId" value="${quotation.id}">
                    <button type="submit" class="btn btn-primary"><i class="fas fa-exchange-alt"></i> Convert to Order</button>
                </form>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/sales/quotation-version" style="display:inline">
                <input type="hidden" name="id" value="${quotation.id}">
                <button type="submit" class="btn btn-primary"><i class="fas fa-copy"></i> Phiên bản mới</button>
            </form>
        </div>
    </div>

    <c:if test="${not empty successMsg}"><div class="alert alert-success">${successMsg}</div></c:if>
    <c:if test="${not empty errorMsg}"><div class="alert alert-error">${errorMsg}</div></c:if>

    <!-- Header Info -->
    <div class="card">
        <div class="info-grid">
            <div class="info-item"><label>Opportunity</label><span>${quotation.opportunityTitle}</span></div>
            <div class="info-item"><label>Khách hàng</label><span>${quotation.customerName}</span></div>
            <div class="info-item"><label>Status</label><span class="badge badge-${quotation.status}">${quotation.status}</span></div>
            <div class="info-item"><label>Tạo bởi</label><span>${quotation.createdByName}</span></div>
            <div class="info-item"><label>Hiệu lực đến</label><span><fmt:formatDate value="${quotation.validUntil}" pattern="dd/MM/yyyy"/></span></div>
            <div class="info-item"><label>Ngày tạo</label><span><fmt:formatDate value="${quotation.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span></div>
        </div>
        <c:if test="${not empty quotation.notes}">
            <div style="background:#f8f9ff;padding:12px;border-radius:10px;margin-top:10px;">
                <label style="font-size:11px;color:#888;font-weight:700;text-transform:uppercase;">Ghi chú</label>
                <p style="margin:6px 0 0">${quotation.notes}</p>
            </div>
        </c:if>
    </div>

    <!-- Items -->
    <div class="card">
        <h3 style="margin:0 0 16px;font-size:17px;font-weight:700;"><i class="fas fa-list" style="color:#667eea"></i> Chi tiết sản phẩm</h3>
        <table>
            <thead>
                <tr>
                    <th>#</th><th>Tên sản phẩm</th><th>SL</th><th>Đơn giá</th><th>Giảm giá</th><th>Thuế</th><th>Thành tiền</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${items}" varStatus="i">
                    <tr>
                        <td>${i.count}</td>
                        <td><strong>${item.productName}</strong></td>
                        <td>${item.quantity}</td>
                        <td><fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/> đ</td>
                        <td>${item.discount}%</td>
                        <td>${item.taxRate}%</td>
                        <td><strong><fmt:formatNumber value="${item.lineTotal}" type="number" groupingUsed="true"/> đ</strong></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <div class="totals-box" style="margin-top:15px;">
            <div class="row-line"><span>Tổng cộng:</span><strong style="font-size:20px;color:#2e7d32"><fmt:formatNumber value="${quotation.totalAmount}" type="number" groupingUsed="true"/> đ</strong></div>
        </div>
    </div>

    <!-- All Versions -->
    <c:if test="${not empty allVersions}">
    <div class="card">
        <h3 style="margin:0 0 15px;font-size:16px;font-weight:700;"><i class="fas fa-history" style="color:#667eea"></i> Lịch sử phiên bản</h3>
        <table>
            <thead><tr><th>Mã</th><th>Phiên bản</th><th>Status</th><th>Tổng tiền</th><th>Ngày tạo</th><th></th></tr></thead>
            <tbody>
                <c:forEach var="v" items="${allVersions}">
                    <tr style="${v.id == quotation.id ? 'background:#fffde7' : ''}">
                        <td><strong>${v.quotationCode}</strong></td>
                        <td><span class="version-badge">v${v.version}</span></td>
                        <td><span class="badge badge-${v.status}">${v.status}</span></td>
                        <td><fmt:formatNumber value="${v.totalAmount}" type="number" groupingUsed="true"/> đ</td>
                        <td><fmt:formatDate value="${v.createdAt}" pattern="dd/MM/yyyy"/></td>
                        <td>
                            <c:if test="${v.id != quotation.id}">
                                <a href="${pageContext.request.contextPath}/sales/quotation-detail?id=${v.id}" class="btn btn-info" style="padding:5px 10px;font-size:11px"><i class="fas fa-eye"></i></a>
                            </c:if>
                            <c:if test="${v.id == quotation.id}"><span style="color:#f57f17;font-size:12px;font-weight:700;">← Đang xem</span></c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
    </c:if>
</div>
</body>
</html>
