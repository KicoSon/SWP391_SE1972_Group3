<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title><c:choose><c:when test="${mode == 'create'}">Tạo Opportunity</c:when><c:when test="${mode == 'convert'}">Convert Lead → Opportunity</c:when><c:otherwise>Sửa Opportunity</c:otherwise></c:choose></title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body{margin:0;font-family:"Segoe UI";background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:30px;min-height:100vh;}
        .card{background:rgba(255,255,255,0.95);border-radius:18px;box-shadow:0 10px 25px rgba(0,0,0,0.15);padding:30px;max-width:800px;margin:0 auto;}
        .card-title{font-size:22px;font-weight:700;color:#333;margin-bottom:25px;display:flex;align-items:center;gap:10px;}
        .form-grid{display:grid;grid-template-columns:1fr 1fr;gap:20px;}
        .form-group{display:flex;flex-direction:column;gap:5px;}
        .form-group.full{grid-column:1/-1;}
        .form-group label{font-size:13px;font-weight:600;color:#555;}
        .form-group input,.form-group select,.form-group textarea{padding:10px 12px;border:1.5px solid #ddd;border-radius:8px;font-size:14px;transition:border-color 0.2s;}
        .form-group input:focus,.form-group select:focus,.form-group textarea:focus{border-color:#667eea;outline:none;}
        .btn{border:none;padding:11px 20px;border-radius:8px;font-size:14px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white;}
        .btn-warning{background:linear-gradient(135deg,#f7971e,#ffd200);color:#333;}
        .actions{display:flex;gap:12px;justify-content:flex-end;margin-top:25px;}
        .alert-error{background:#fce4ec;border:1px solid #f8bbd9;border-radius:8px;padding:12px 16px;margin-bottom:20px;color:#c62828;}
        .convert-info{background:#e8f5e9;border:1px solid #a5d6a7;border-radius:10px;padding:14px;margin-bottom:20px;}
        .convert-info strong{color:#2e7d32;}
        /* Range input styling */
        input[type=range]{width:100%;accent-color:#667eea;}
        .prob-display{font-weight:700;color:#667eea;font-size:18px;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">
    <div style="margin-bottom:20px;">
        <a href="${pageContext.request.contextPath}/sales/opportunities" style="color:rgba(255,255,255,0.8);text-decoration:none;font-size:13px;">
            <i class="fas fa-arrow-left"></i> Quay lại danh sách
        </a>
    </div>

    <div class="card">
        <div class="card-title">
            <i class="fas fa-handshake" style="color:#667eea"></i>
            <c:choose>
                <c:when test="${mode == 'create'}">Tạo Opportunity mới</c:when>
                <c:when test="${mode == 'convert'}">Convert Lead → Opportunity</c:when>
                <c:otherwise>Chỉnh sửa Opportunity</c:otherwise>
            </c:choose>
        </div>

        <c:if test="${not empty error}">
            <div class="alert-error"><i class="fas fa-exclamation-circle"></i> ${error}</div>
        </c:if>

        <c:if test="${not empty lead}">
            <div class="convert-info">
                <strong><i class="fas fa-user-plus"></i> Lead nguồn:</strong> ${lead.fullName}
                (${lead.phone} – ${lead.email}) | Quan tâm: ${lead.productInterest}
            </div>
            <input type="hidden" name="leadId" value="${lead.id}">
        </c:if>

        <c:set var="action" value="${mode == 'edit' ? pageContext.request.contextPath.concat('/sales/opportunity-update') : pageContext.request.contextPath.concat('/sales/opportunity-create')}"/>
        <c:if test="${mode == 'convert'}"><c:set var="action" value="${pageContext.request.contextPath}/sales/convert-lead"/></c:if>

        <form method="post" action="${action}">
            <c:if test="${mode == 'edit'}"><input type="hidden" name="id" value="${opportunity.id}"></c:if>
            <c:if test="${not empty lead}"><input type="hidden" name="leadId" value="${lead.id}"></c:if>

            <div class="form-grid">
                <div class="form-group full">
                    <label>Tiêu đề Opportunity *</label>
                    <input type="text" name="title" required
                        value="${not empty opportunity ? opportunity.title : (not empty lead ? 'Cơ hội từ '.concat(lead.fullName) : '')}"
                        placeholder="Nhập tiêu đề cơ hội bán hàng...">
                </div>

                <div class="form-group">
                    <label>Khách hàng</label>
                    <select name="customerId">
                        <option value="">-- Chưa liên kết --</option>
                        <c:forEach var="cust" items="${customers}">
                            <option value="${cust.id}" ${not empty opportunity && opportunity.customerId == cust.id ? 'selected' : ''}>${cust.fullName} – ${cust.phone}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Sales phụ trách *</label>
                    <select name="assignedSalesId">
                        <c:forEach var="s" items="${staffList}">
                            <option value="${s.id}" ${not empty opportunity && opportunity.assignedSalesId == s.id ? 'selected' : ''}>${s.fullName}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group">
                    <label>Stage</label>
                    <select name="stage">
                        <option value="Qualification"    ${not empty opportunity && opportunity.stage == 'Qualification'    ? 'selected' : ''}>Qualification</option>
                        <option value="Need Analysis"    ${not empty opportunity && opportunity.stage == 'Need Analysis'    ? 'selected' : ''}>Need Analysis</option>
                        <option value="Product Proposal" ${not empty opportunity && opportunity.stage == 'Product Proposal' ? 'selected' : ''}>Product Proposal</option>
                        <option value="Quotation"        ${not empty opportunity && opportunity.stage == 'Quotation'        ? 'selected' : ''}>Quotation</option>
                        <option value="Negotiation"      ${not empty opportunity && opportunity.stage == 'Negotiation'      ? 'selected' : ''}>Negotiation</option>
                        <option value="Closed Won"       ${not empty opportunity && opportunity.stage == 'Closed Won'       ? 'selected' : ''}>Closed Won</option>
                        <option value="Closed Lost"      ${not empty opportunity && opportunity.stage == 'Closed Lost'      ? 'selected' : ''}>Closed Lost</option>
                    </select>
                </div>


                <div class="form-group">
                    <label>Nguồn</label>
                    <select name="source">
                        <option value="Manual" ${not empty opportunity && opportunity.source == 'Manual' ? 'selected' : ''}>Manual</option>
                        <option value="Lead" ${not empty opportunity && opportunity.source == 'Lead' ? 'selected' : (mode == 'convert' ? 'selected' : '')}>Lead</option>
                        <option value="Campaign" ${not empty opportunity && opportunity.source == 'Campaign' ? 'selected' : ''}>Campaign</option>
                    </select>
                </div>

                <div class="form-group">
                    <label>Giá trị dự kiến (VNĐ)</label>
                    <input type="number" name="expectedValue" min="0" step="1000"
                        value="${not empty opportunity ? opportunity.expectedValue : ''}">
                </div>

                <div class="form-group">
                    <label>Xác suất đóng: <span id="probVal" class="prob-display">${not empty opportunity ? opportunity.closeProbability : '30'}%</span></label>
                    <input type="range" name="closeProbability" min="0" max="100" step="5"
                        value="${not empty opportunity ? opportunity.closeProbability : '30'}"
                        oninput="document.getElementById('probVal').textContent=this.value+'%'">
                </div>

                <div class="form-group">
                    <label>Ngày dự kiến đóng</label>
                    <input type="date" name="expectedCloseDate"
                        value="<fmt:formatDate value='${opportunity.expectedCloseDate}' pattern='yyyy-MM-dd'/>">
                </div>

                <div class="form-group">
                    <label>Pipeline</label>
                    <select name="pipelineId">
                        <c:forEach var="pl" items="${pipelines}">
                            <option value="${pl.id}" ${not empty opportunity && opportunity.pipelineId == pl.id ? 'selected' : (pl.isDefault() ? 'selected' : '')}>${pl.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="form-group full">
                    <label>Ghi chú</label>
                    <textarea name="notes" rows="4" placeholder="Thêm ghi chú về cơ hội này...">${not empty opportunity ? opportunity.notes : ''}</textarea>
                </div>
            </div>

            <div class="actions">
                <a href="${pageContext.request.contextPath}/sales/opportunities" class="btn btn-warning"><i class="fas fa-times"></i> Hủy</a>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i>
                    <c:choose>
                        <c:when test="${mode == 'edit'}">Lưu thay đổi</c:when>
                        <c:when test="${mode == 'convert'}">Convert Lead</c:when>
                        <c:otherwise>Tạo Opportunity</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
