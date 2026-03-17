<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Leads được giao – Sales CRM</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body{margin:0;font-family:"Segoe UI";background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:30px;min-height:100vh;}
        .header{background:rgba(255,255,255,0.95);padding:22px 28px;border-radius:20px;box-shadow:0 10px 25px rgba(0,0,0,0.15);margin-bottom:25px;display:flex;justify-content:space-between;align-items:center;}
        .header h2{font-weight:700;font-size:24px;margin:0;color:#333;}
        .btn{border:none;padding:10px 16px;border-radius:8px;font-size:14px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white;}
        .btn-success{background:linear-gradient(135deg,#56ab2f,#a8e063);color:white;}
        .card{background:rgba(255,255,255,0.95);border-radius:16px;box-shadow:0 8px 20px rgba(0,0,0,0.12);overflow:hidden;}
        table{width:100%;border-collapse:collapse;}
        th{background:linear-gradient(135deg,#667eea,#764ba2);color:white;padding:13px 15px;font-size:13px;text-align:left;}
        td{padding:12px 15px;border-bottom:1px solid #f0f0f0;font-size:14px;vertical-align:middle;}
        tr:hover td{background:#f8f9ff;}
        .badge{padding:4px 10px;border-radius:20px;font-size:12px;font-weight:600;}
        .badge-new{background:#e3f2fd;color:#1565c0;}
        .badge-qualified{background:#e8f5e9;color:#2e7d32;}
        .badge-assigned{background:#fff3e0;color:#e65100;}
        .empty-state{text-align:center;padding:50px;color:#888;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div class="header">
        <div>
            <h2><i class="fas fa-user-tag" style="color:#667eea"></i>
                <c:choose>
                    <c:when test="${isManager}">Tất cả Leads chưa chuyển đổi</c:when>
                    <c:otherwise>Leads được giao cho tôi</c:otherwise>
                </c:choose>
            </h2>
            <p style="margin:0;color:#888;font-size:14px;">Danh sách leads Marketing phân công – nhấn "Chuyển đổi" để tạo Opportunity</p>
        </div>
        <a href="${pageContext.request.contextPath}/sales/opportunities" class="btn btn-primary">
            <i class="fas fa-handshake"></i> Xem Opportunities
        </a>
    </div>

    <div class="card">
        <c:choose>
            <c:when test="${empty leads}">
                <div class="empty-state">
                    <i class="fas fa-inbox" style="font-size:48px;color:#ccc;"></i>
                    <p style="font-size:16px;margin-top:12px;">Chưa có lead nào được phân công.</p>
                </div>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Họ tên</th>
                            <th>Điện thoại</th>
                            <th>Email</th>
                            <th>Sản phẩm quan tâm</th>
                            <th>Nguồn</th>
                            <th>Trạng thái</th>
                            <c:if test="${isManager}"><th>Nhân viên phụ trách</th></c:if>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="lead" items="${leads}" varStatus="s">
                            <tr>
                                <td>${s.count}</td>
                                <td><strong>${lead.fullName}</strong></td>
                                <td>${lead.phone}</td>
                                <td>${lead.email}</td>
                                <td>${lead.productInterest}</td>
                                <td>${lead.source}</td>
                                <td>
                                    <span class="badge badge-${lead.status}">${lead.status}</span>
                                </td>
                                <c:if test="${isManager}">
                                    <td>${lead.saleName}</td>
                                </c:if>
                                <td>
                                      <c:choose>
                                          <c:when test="${lead.status == 'Converted'}">
                                              <button class="btn btn-success" style="padding:6px 12px;font-size:13px;opacity:0.5;cursor:not-allowed;" disabled>
                                                  <i class="fas fa-check"></i> Đã chuyển đổi
                                              </button>
                                          </c:when>
                                          <c:otherwise>
                                              <a href="${pageContext.request.contextPath}/sales/convert-lead?leadId=${lead.id}"
                                                 class="btn btn-success" style="padding:6px 12px;font-size:13px;">
                                                  <i class="fas fa-exchange-alt"></i> Chuyển đổi
                                              </a>
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
</body>
</html>
