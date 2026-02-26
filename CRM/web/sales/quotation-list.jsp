<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Báo giá – ${opportunity.title}</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sales-common.css">
    <style>
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
