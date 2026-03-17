<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${order.orderCode} – Chi tiết Đơn hàng</title>
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
        .badge-New,.badge-Pending{background:#e3f2fd;color:#1565c0;}
        .badge-Processing{background:#fff8e1;color:#f57f17;}
        .badge-Delivered{background:#e8f5e9;color:#2e7d32;}
        .badge-Cancelled{background:#fce4ec;color:#c62828;}
        .badge-Paid{background:#e8f5e9;color:#2e7d32;}
        .badge-Unpaid{background:#fce4ec;color:#c62828;}
        .badge-PartiallyPaid{background:#fff8e1;color:#f57f17;}
        .info-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:16px;margin-bottom:20px;}
        .info-item label{font-size:11px;color:#888;text-transform:uppercase;font-weight:700;margin-bottom:4px;display:block;}
        .info-item span{font-size:15px;font-weight:600;}
        table{width:100%;border-collapse:collapse;}
        th{background:#f3f4f7;color:#555;padding:10px 14px;font-size:12px;text-align:left;font-weight:700;}
        td{padding:10px 14px;border-bottom:1px solid #f0f0f0;font-size:14px;}
        .total-box{background:linear-gradient(135deg,#e8f5e9,#f1f8e9);border-radius:12px;padding:20px;text-align:right;margin-top:15px;}
        .total-box .amount{font-size:24px;font-weight:800;color:#2e7d32;}
        .status-form{display:flex;gap:10px;align-items:center;flex-wrap:wrap;}
        .status-form select{padding:9px 12px;border:1.5px solid #ddd;border-radius:8px;font-size:14px;}
        .alert{padding:12px 16px;border-radius:8px;margin-bottom:15px;font-size:14px;}
        .alert-success{background:#e8f5e9;color:#2e7d32;} .alert-error{background:#fce4ec;color:#c62828;}
        .timeline-item{display:flex;gap:14px;padding:10px 0;border-bottom:1px solid #f5f5f5;}
        .timeline-dot{width:12px;height:12px;border-radius:50%;background:#667eea;flex-shrink:0;margin-top:5px;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div class="header-row">
        <div>
            <a href="${pageContext.request.contextPath}/sales/opportunities" style="color:rgba(255,255,255,0.8);text-decoration:none;font-size:13px;"><i class="fas fa-arrow-left"></i> Quay lại</a>
            <h2 style="color:white;margin:6px 0 0;font-size:22px;"><i class="fas fa-shopping-cart"></i> ${order.orderCode}</h2>
        </div>
        <div style="display:flex;gap:8px;flex-wrap:wrap;">
            <form method="post" action="${pageContext.request.contextPath}/sales/order-sync" title="Đồng bộ lên hệ thống Khách hàng" style="display:inline">
                <input type="hidden" name="id" value="${order.id}">
                <button type="submit" class="btn btn-info"><i class="fas fa-sync"></i> Sync</button>
            </form>
        </div>
    </div>

    <c:if test="${not empty successMsg}"><div class="alert alert-success">${successMsg}</div></c:if>
    <c:if test="${not empty errorMsg}"><div class="alert alert-error">${errorMsg}</div></c:if>

    <!-- Order Info -->
    <div class="card">
        <h3 style="margin:0 0 16px;font-size:17px;font-weight:700;"><i class="fas fa-info-circle" style="color:#667eea"></i> Thông tin đơn hàng</h3>
        <div class="info-grid">
            <div class="info-item"><label>Mã đơn</label><span>${order.orderCode}</span></div>
            <div class="info-item"><label>Khách hàng</label><span>${order.customerName}</span></div>
            <div class="info-item"><label>Trạng thái</label><span class="badge badge-${order.status}">${order.status}</span></div>
            <div class="info-item"><label>Thanh toán</label><span class="badge badge-${order.paymentStatus}">${order.paymentStatus}</span></div>
            <div class="info-item"><label>Ngày đặt</label><span><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy"/></span></div>
            <div class="info-item"><label>Giao hàng</label><span><fmt:formatDate value="${order.deliveryDate}" pattern="dd/MM/yyyy"/></span></div>
            <div class="info-item"><label>Hình thức TT</label><span>${order.paymentMethod}</span></div>
            <div class="info-item"><label>Địa chỉ giao</label><span>${order.shippingAddress}</span></div>
        </div>

        <!-- Update Status -->
        <div style="background:#f8f9ff;border-radius:10px;padding:16px;margin-top:10px;">
            <strong>Cập nhật trạng thái:</strong>
            <form method="post" action="${pageContext.request.contextPath}/sales/order-detail" style="margin-top:10px;" class="status-form">
                <input type="hidden" name="id" value="${order.id}">
                <select name="status">
                    <option value="Pending"   ${order.status == 'Pending' ? 'selected' : ''}>Pending</option>
                    <option value="Paid"      ${order.status == 'Paid' ? 'selected' : ''}>Paid</option>
                    <option value="Shipped"   ${order.status == 'Shipped' ? 'selected' : ''}>Shipped</option>
                    <option value="Delivered" ${order.status == 'Delivered' ? 'selected' : ''}>Delivered</option>
                </select>
                <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Cập nhật</button>
            </form>
        </div>
    </div>

    <!-- Order Items -->
    <div class="card">
        <h3 style="margin:0 0 16px;font-size:17px;font-weight:700;"><i class="fas fa-boxes" style="color:#667eea"></i> Sản phẩm</h3>
        <c:choose>
            <c:when test="${empty orderItems}">
                <p style="color:#aaa;text-align:center;">Chưa có sản phẩm</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead><tr><th>#</th><th>Sản phẩm</th><th>SL</th><th>Đơn giá</th><th>Thành tiền</th></tr></thead>
                    <tbody>
                        <c:forEach var="item" items="${orderItems}" varStatus="i">
                            <tr>
                                <td>${i.count}</td>
                                <td><strong>${item.productName}</strong></td>
                                <td>${item.quantity}</td>
                                <td><fmt:formatNumber value="${item.unitPrice}" type="number" groupingUsed="true"/> đ</td>
                                <td><strong><fmt:formatNumber value="${item.totalPrice}" type="number" groupingUsed="true"/> đ</strong></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
        <div class="total-box">
            <div style="font-size:13px;color:#555;">Tổng cộng</div>
            <div class="amount"><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true"/> đ</div>
        </div>
    </div>

    <!-- Linked Quotation -->
    <c:if test="${not empty quotation}">
    <div class="card">
        <h3 style="margin:0 0 14px;font-size:16px;font-weight:700;"><i class="fas fa-file-invoice-dollar" style="color:#667eea"></i> Báo giá nguồn</h3>
        <div style="display:flex;align-items:center;gap:15px;">
            <span style="font-size:16px;font-weight:700;">${quotation.quotationCode}</span>
            <span style="font-size:13px;color:#888">v${quotation.version}</span>
            <a href="${pageContext.request.contextPath}/sales/quotation-detail?id=${quotation.id}" class="btn btn-info" style="padding:6px 12px;font-size:12px"><i class="fas fa-eye"></i> Xem báo giá</a>
        </div>
    </div>
    </c:if>
</div>
</body>
</html>
