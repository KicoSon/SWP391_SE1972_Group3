<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Cấu hình Pipeline</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body{margin:0;font-family:"Segoe UI";background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:30px;min-height:100vh;}
        .card{background:rgba(255,255,255,0.95);border-radius:18px;box-shadow:0 10px 25px rgba(0,0,0,0.15);padding:25px;margin-bottom:20px;}
        .section-title{font-size:18px;font-weight:700;margin-bottom:20px;display:flex;align-items:center;gap:8px;}
        .btn{border:none;padding:9px 14px;border-radius:8px;font-size:13px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white;}
        .btn-danger{background:linear-gradient(135deg,#f093fb,#f5576c);color:white;}
        .btn-success{background:linear-gradient(135deg,#56ab2f,#a8e063);color:white;}
        .btn-warning{background:linear-gradient(135deg,#f7971e,#ffd200);color:#333;}
        .btn-sm{padding:5px 10px;font-size:12px;}
        table{width:100%;border-collapse:collapse;}
        th{background:#f3f4f7;color:#555;padding:10px 14px;font-size:12px;text-align:left;font-weight:700;}
        td{padding:10px 14px;border-bottom:1px solid #f0f0f0;font-size:14px;vertical-align:middle;}
        .form-inline{display:flex;gap:10px;align-items:center;flex-wrap:wrap;}
        .form-inline input,.form-inline select{padding:8px 11px;border:1.5px solid #ddd;border-radius:8px;font-size:13px;}
        .color-dot{width:20px;height:20px;border-radius:50%;display:inline-block;border:2px solid rgba(0,0,0,0.1);}
        .drag-handle{cursor:grab;color:#aaa;} .drag-handle:hover{color:#667eea;}
        .badge-active{background:#e8f5e9;color:#2e7d32;padding:3px 9px;border-radius:12px;font-size:11px;font-weight:700;}
        .badge-inactive{background:#fce4ec;color:#c62828;padding:3px 9px;border-radius:12px;font-size:11px;font-weight:700;}
        .alert{padding:12px;border-radius:8px;margin-bottom:15px;}
        .alert-success{background:#e8f5e9;color:#2e7d32;border:1px solid #a5d6a7;}
        .alert-error{background:#fce4ec;color:#c62828;border:1px solid #f8bbd9;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div style="background:rgba(255,255,255,0.95);padding:18px 25px;border-radius:16px;margin-bottom:20px;">
        <h2 style="margin:0;font-size:22px;font-weight:700;"><i class="fas fa-sliders-h" style="color:#667eea"></i> Cấu hình Pipeline</h2>
        <p style="margin:4px 0 0;color:#888;font-size:13px;">Quản lý stages và lý do thất bại – Chỉ Sales Manager</p>
    </div>

    <c:if test="${not empty successMsg}"><div class="alert alert-success"><i class="fas fa-check-circle"></i> ${successMsg}</div></c:if>
    <c:if test="${not empty errorMsg}"><div class="alert alert-error"><i class="fas fa-exclamation-circle"></i> ${errorMsg}</div></c:if>

    <!-- Stages -->
    <div class="card">
        <div class="section-title"><i class="fas fa-layer-group" style="color:#667eea"></i> Pipeline Stages</div>

        <table id="stageTable">
            <thead><tr><th></th><th>Tên Stage</th><th>Màu</th><th>Thứ tự</th><th>Won/Lost</th><th>Thao tác</th></tr></thead>
            <tbody>
                <c:forEach var="s" items="${stages}" varStatus="i">
                    <tr class="stage-row" data-id="${s.id}">
                        <td><span class="drag-handle"><i class="fas fa-grip-vertical"></i></span></td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/sales/pipeline-config" style="display:flex;gap:8px;align-items:center">
                                <input type="hidden" name="action" value="updateStage">
                                <input type="hidden" name="stageId" value="${s.id}">
                                <input type="hidden" name="orderIndex" value="${s.orderIndex}">
                                <input type="text" name="stageName" value="${s.stageName}" style="border:1.5px solid #ddd;border-radius:6px;padding:6px 10px;font-size:13px;width:160px;">
                                <input type="color" name="color" value="${s.color}" style="padding:0;border:none;width:36px;height:34px;cursor:pointer;border-radius:6px;">
                                <button type="submit" class="btn btn-primary btn-sm"><i class="fas fa-save"></i></button>
                            </form>
                        </td>
                        <td><span class="color-dot" style="background:${s.color}"></span> ${s.color}</td>
                        <td>${s.orderIndex}</td>
                        <td>
                            <c:if test="${s.won}"><span class="badge-active">Won</span></c:if>
                            <c:if test="${s.lost}"><span class="badge-inactive">Lost</span></c:if>
                        </td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/sales/pipeline-config" onsubmit="return confirm('Xóa stage này?')">
                                <input type="hidden" name="action" value="deleteStage">
                                <input type="hidden" name="stageId" value="${s.id}">
                                <button type="submit" class="btn btn-danger btn-sm"><i class="fas fa-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div style="margin-top:20px;padding:16px;background:#f8f9ff;border-radius:12px;">
            <strong style="font-size:14px;">Thêm Stage mới</strong>
            <form method="post" action="${pageContext.request.contextPath}/sales/pipeline-config" style="margin-top:12px;">
                <input type="hidden" name="action" value="addStage">
                <input type="hidden" name="pipelineId" value="${pipeline.id}">
                <div class="form-inline">
                    <input type="text" name="stageName" placeholder="Tên stage..." required style="flex:1;min-width:160px;">
                    <input type="color" name="color" value="#667eea" style="width:48px;height:38px;border:none;cursor:pointer;border-radius:8px;">
                    <select name="isWon">
                        <option value="0">Thường</option>
                        <option value="1">Won stage</option>
                    </select>
                    <button type="submit" class="btn btn-success"><i class="fas fa-plus"></i> Thêm Stage</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Lost Reasons -->
    <div class="card">
        <div class="section-title"><i class="fas fa-times-circle" style="color:#dc3545"></i> Lý do thất bại</div>
        <table>
            <thead><tr><th>Lý do</th><th>Trạng thái</th><th>Thao tác</th></tr></thead>
            <tbody>
                <c:forEach var="lr" items="${lostReasons}">
                    <tr>
                        <td>${lr.reason}</td>
                        <td>
                            <c:choose>
                                <c:when test="${lr.active}"><span class="badge-active">Đang dùng</span></c:when>
                                <c:otherwise><span class="badge-inactive">Tắt</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/sales/pipeline-config">
                                <input type="hidden" name="action" value="toggleLostReason">
                                <input type="hidden" name="lostReasonId" value="${lr.id}">
                                <button type="submit" class="btn ${lr.active ? 'btn-warning' : 'btn-success'} btn-sm">
                                    <c:choose>
                                        <c:when test="${lr.active}"><i class="fas fa-ban"></i> Tắt</c:when>
                                        <c:otherwise><i class="fas fa-check"></i> Kích hoạt</c:otherwise>
                                    </c:choose>
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div style="margin-top:20px;padding:16px;background:#f8f9ff;border-radius:12px;">
            <strong style="font-size:14px;">Thêm lý do mới</strong>
            <form method="post" action="${pageContext.request.contextPath}/sales/pipeline-config" style="margin-top:12px;">
                <input type="hidden" name="action" value="addLostReason">
                <div class="form-inline">
                    <input type="text" name="reason" placeholder="Nhập lý do thất bại..." required style="flex:1;min-width:250px;">
                    <button type="submit" class="btn btn-danger"><i class="fas fa-plus"></i> Thêm</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
