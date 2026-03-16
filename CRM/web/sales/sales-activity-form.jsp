<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo Activity – Opportunity</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body{margin:0;font-family:"Segoe UI";background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:30px;min-height:100vh;}
        .card{background:rgba(255,255,255,0.95);border-radius:18px;box-shadow:0 10px 25px rgba(0,0,0,0.15);padding:28px;max-width:700px;margin:0 auto 20px;}
        .card-title{font-size:20px;font-weight:700;margin-bottom:20px;display:flex;align-items:center;gap:10px;}
        .form-group{display:flex;flex-direction:column;gap:6px;margin-bottom:18px;}
        .form-group label{font-size:13px;font-weight:600;color:#555;}
        .form-group input,.form-group select,.form-group textarea{padding:10px 12px;border:1.5px solid #ddd;border-radius:8px;font-size:14px;}
        .form-group input:focus,.form-group select:focus{border-color:#667eea;outline:none;}
        .form-row{display:grid;grid-template-columns:1fr 1fr;gap:18px;}
        .btn{border:none;padding:11px 18px;border-radius:8px;font-size:14px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white;}
        .btn-warning{background:linear-gradient(135deg,#f7971e,#ffd200);color:#333;}
        .actions{display:flex;gap:12px;justify-content:flex-end;margin-top:22px;}
        .alert-error{background:#fce4ec;border:1px solid #f8bbd9;border-radius:8px;padding:12px;margin-bottom:15px;color:#c62828;}
        .type-icons{display:flex;gap:10px;flex-wrap:wrap;}
        .type-btn{border:2px solid #ddd;background:white;border-radius:10px;padding:10px 16px;cursor:pointer;display:flex;align-items:center;gap:6px;font-size:13px;font-weight:600;transition:all 0.2s;}
        .type-btn:hover,.type-btn.selected{border-color:#667eea;background:#f0f2ff;color:#667eea;}
        .type-btn input[type=radio]{display:none;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">
    <div style="margin-bottom:16px;">
        <a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${opportunity.id}" style="color:rgba(255,255,255,0.8);text-decoration:none;font-size:13px;">
            <i class="fas fa-arrow-left"></i> Quay lại Opportunity
        </a>
    </div>

    <div class="card">
        <div class="card-title">
            <i class="fas fa-tasks" style="color:#667eea"></i> Tạo Hoạt động mới
            <c:if test="${not empty opportunity}">
                <span style="font-size:14px;color:#888;font-weight:400;">cho ${opportunity.title}</span>
            </c:if>
        </div>

        <c:if test="${not empty error}"><div class="alert-error"><i class="fas fa-exclamation-circle"></i> ${error}</div></c:if>

        <form method="post" action="${pageContext.request.contextPath}/sales/activity-create">
            <input type="hidden" name="opportunityId" value="${opportunity.id}">

            <!-- Activity Type -->
            <div class="form-group">
                <label>Loại hoạt động *</label>
                <div class="type-icons" id="typeGroup">
                    <label class="type-btn selected">
                        <input type="radio" name="type" value="Call" checked> <i class="fas fa-phone" style="color:#0dcaf0"></i> Gọi điện
                    </label>
                    <label class="type-btn">
                        <input type="radio" name="type" value="Meeting"> <i class="fas fa-users" style="color:#667eea"></i> Gặp mặt
                    </label>
                    <label class="type-btn">
                        <input type="radio" name="type" value="Task"> <i class="fas fa-check-square" style="color:#fd7e14"></i> Tác vụ
                    </label>
                    <label class="type-btn">
                        <input type="radio" name="type" value="Email"> <i class="fas fa-envelope" style="color:#56ab2f"></i> Email
                    </label>
                    <label class="type-btn">
                        <input type="radio" name="type" value="Note"> <i class="fas fa-sticky-note" style="color:#6c757d"></i> Ghi chú
                    </label>
                </div>
            </div>

            <div class="form-group">
                <label>Tiêu đề *</label>
                <input type="text" name="title" required placeholder="Nhập tiêu đề hoạt động...">
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label>Hạn hoàn thành</label>
                    <input type="datetime-local" name="dueDate">
                </div>
                <div class="form-group">
                    <label>Độ ưu tiên</label>
                    <select name="priority">
                        <option value="Low">Thấp</option>
                        <option value="Medium" selected>Trung bình</option>
                        <option value="High">Cao</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label>Nội dung / Ghi chú</label>
                <textarea name="description" rows="4" placeholder="Mô tả nội dung hoạt động..."></textarea>
            </div>

            <div class="actions">
                <a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${opportunityId}" class="btn btn-warning"><i class="fas fa-times"></i> Hủy</a>
                <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu hoạt động</button>
            </div>
        </form>
    </div>

    <!-- Existing Activities Timeline -->
    <c:if test="${not empty activities}">
    <div class="card" style="max-width:700px;margin:0 auto;">
        <div style="font-size:16px;font-weight:700;margin-bottom:16px;"><i class="fas fa-history" style="color:#667eea"></i> Lịch sử hoạt động</div>
        <c:forEach var="act" items="${activities}">
            <div style="display:flex;gap:14px;padding:10px 0;border-bottom:1px solid #f5f5f5;">
                <div style="width:38px;height:38px;border-radius:50%;display:flex;align-items:center;justify-content:center;color:white;font-size:14px;flex-shrink:0;
                    background:<c:choose><c:when test="${act.type=='Call'}">#0dcaf0</c:when><c:when test="${act.type=='Meeting'}">#667eea</c:when><c:when test="${act.type=='Task'}">#fd7e14</c:when><c:when test="${act.type=='Email'}">#56ab2f</c:when><c:otherwise>#6c757d</c:otherwise></c:choose>;">
                    <c:choose>
                        <c:when test="${act.type=='Call'}"><i class="fas fa-phone"></i></c:when>
                        <c:when test="${act.type=='Meeting'}"><i class="fas fa-users"></i></c:when>
                        <c:when test="${act.type=='Task'}"><i class="fas fa-check-square"></i></c:when>
                        <c:when test="${act.type=='Email'}"><i class="fas fa-envelope"></i></c:when>
                        <c:otherwise><i class="fas fa-sticky-note"></i></c:otherwise>
                    </c:choose>
                </div>
                <div style="flex:1">
                    <strong>${act.title}</strong>
                    <span style="background:#f0f0f0;padding:2px 7px;border-radius:10px;font-size:11px;margin-left:6px;">${act.type}</span>
                    <span style="background:#e8f5e9;color:#2e7d32;padding:2px 7px;border-radius:10px;font-size:11px;margin-left:4px;">${act.priority}</span>
                    <p style="margin:4px 0 2px;font-size:13px;color:#555;">${act.description}</p>
                    <small style="color:#aaa"><fmt:formatDate value="${act.createdAt}" pattern="dd/MM/yyyy HH:mm"/></small>
                </div>
            </div>
        </c:forEach>
    </div>
    </c:if>
</div>

<script>
// Highlight selected type button
document.querySelectorAll('.type-btn input[type=radio]').forEach(radio => {
    radio.addEventListener('change', function() {
        document.querySelectorAll('.type-btn').forEach(b => b.classList.remove('selected'));
        this.closest('.type-btn').classList.add('selected');
    });
});
</script>
</body>
</html>
