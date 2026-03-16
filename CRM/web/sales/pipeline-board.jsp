<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Pipeline Board – Kanban</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body{margin:0;font-family:"Segoe UI";background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:25px;min-height:100vh;}
        .page-header{background:rgba(255,255,255,0.95);padding:18px 25px;border-radius:16px;margin-bottom:20px;display:flex;justify-content:space-between;align-items:center;}
        .page-header h2{margin:0;font-size:22px;font-weight:700;}
        .btn{border:none;padding:9px 15px;border-radius:8px;font-size:13px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-primary{background:linear-gradient(135deg,#667eea,#764ba2);color:white;}
        .btn-info{background:linear-gradient(135deg,#0dcaf0,#0d6efd);color:white;}
        /* KPI Summary */
        .kpi-row{display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:15px;margin-bottom:20px;}
        .kpi-card{background:rgba(255,255,255,0.92);border-radius:14px;padding:16px;text-align:center;box-shadow:0 6px 15px rgba(0,0,0,0.1);}
        .kpi-val{font-size:24px;font-weight:800;margin-bottom:4px;}
        .kpi-label{font-size:12px;color:#888;font-weight:600;text-transform:uppercase;}
        /* Kanban board */
        .board-wrap{display:flex;gap:16px;overflow-x:auto;padding-bottom:10px;}
        .kanban-col{flex:0 0 260px;background:rgba(255,255,255,0.85);border-radius:14px;padding:14px;box-shadow:0 6px 15px rgba(0,0,0,0.1);}
        .col-header{font-weight:700;font-size:13px;color:white;padding:8px 12px;border-radius:8px;margin-bottom:12px;display:flex;justify-content:space-between;align-items:center;}
        .col-count{background:rgba(255,255,255,0.3);border-radius:10px;padding:1px 8px;font-size:12px;}
        .opp-card{background:white;border-radius:10px;padding:12px;margin-bottom:10px;box-shadow:0 3px 8px rgba(0,0,0,0.07);cursor:pointer;transition:transform 0.15s,box-shadow 0.15s;border-left:4px solid;}
        .opp-card:hover{transform:translateY(-2px);box-shadow:0 6px 15px rgba(0,0,0,0.12);}
        .opp-card h5{margin:0 0 6px;font-size:13px;font-weight:700;line-height:1.3;}
        .opp-card .meta{font-size:11px;color:#888;margin-top:4px;}
        .opp-val{font-weight:700;color:#2e7d32;font-size:13px;}
        .empty-col{text-align:center;padding:20px;color:#bbb;font-size:13px;}
        /* Stage colors */
        .bg-q{background:#6c757d;} .bg-na{background:#0dcaf0;} .bg-pp{background:#0d6efd;}
        .bg-quot{background:#ffc107;} .bg-neg{background:#fd7e14;} .bg-won{background:#198754;} .bg-lost{background:#dc3545;}
        .border-q{border-color:#6c757d;} .border-na{border-color:#0dcaf0;} .border-pp{border-color:#0d6efd;}
        .border-quot{border-color:#ffc107;} .border-neg{border-color:#fd7e14;} .border-won{border-color:#198754;} .border-lost{border-color:#dc3545;}
        .bg-q-border{border-left-color:#6c757d;} .bg-na-border{border-left-color:#0dcaf0;} .bg-pp-border{border-left-color:#0d6efd;}
        .bg-quot-border{border-left-color:#ffc107;} .bg-neg-border{border-left-color:#fd7e14;} .bg-won-border{border-left-color:#198754;} .bg-lost-border{border-left-color:#dc3545;}


        /* Chart */
        .chart-row{display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-top:20px;}
        .chart-card{background:rgba(255,255,255,0.95);border-radius:16px;padding:20px;box-shadow:0 8px 20px rgba(0,0,0,0.12);}
        .chart-card h4{margin:0 0 15px;font-size:15px;font-weight:700;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div class="page-header">
        <div>
            <h2><i class="fas fa-columns" style="color:#667eea"></i> Pipeline Kanban Board</h2>
            <p style="margin:2px 0 0;color:#888;font-size:13px;">Kéo thả để cập nhật stage (drag-and-drop enabled)</p>
        </div>
        <div style="display:flex;gap:8px;">
            <a href="${pageContext.request.contextPath}/sales/opportunities" class="btn btn-info"><i class="fas fa-list"></i> Dạng bảng</a>
            <a href="${pageContext.request.contextPath}/sales/opportunity-create" class="btn btn-primary"><i class="fas fa-plus"></i> Tạo mới</a>
        </div>
    </div>

    <!-- KPI Row -->
    <div class="kpi-row">
        <div class="kpi-card">
            <div class="kpi-val" style="color:#667eea">${kpiTotal}</div>
            <div class="kpi-label">Tổng Opportunities</div>
        </div>
        <div class="kpi-card">
            <div class="kpi-val" style="color:#198754">
                <fmt:formatNumber value="${kpiValueOpen}" type="number" groupingUsed="true"/>
            </div>
            <div class="kpi-label">Giá trị đang mở (đ)</div>
        </div>
        <div class="kpi-card">
            <div class="kpi-val" style="color:#fd7e14">${kpiCountOpen}</div>
            <div class="kpi-label">Đang mở</div>
        </div>
        <div class="kpi-card">
            <div class="kpi-val" style="color:#0d6efd">${kpiCountQuotation}</div>
            <div class="kpi-label">Đang báo giá</div>
        </div>
        <div class="kpi-card">
            <div class="kpi-val" style="color:#dc3545">${kpiCountLost}</div>
            <div class="kpi-label">Đã thất bại</div>
        </div>
    </div>

    <!-- Board -->
    <div class="board-wrap">
        <c:forEach var="entry" items="${boardData}" varStatus="idx">
            <c:set var="stageName" value="${entry.key}"/>
            <c:set var="oppList"  value="${entry.value}"/>
            <!-- choose header color class -->
            <c:set var="hdrClass" value="bg-q"/>
            <c:if test="${stageName == 'Need Analysis'}"><c:set var="hdrClass" value="bg-na"/></c:if>
            <c:if test="${stageName == 'Product Proposal'}"><c:set var="hdrClass" value="bg-pp"/></c:if>
            <c:if test="${stageName == 'Quotation'}"><c:set var="hdrClass" value="bg-quot"/></c:if>
            <c:if test="${stageName == 'Negotiation'}"><c:set var="hdrClass" value="bg-neg"/></c:if>
            <c:if test="${stageName == 'Closed Won'}"><c:set var="hdrClass" value="bg-won"/></c:if>
            <c:if test="${stageName == 'Closed Lost'}"><c:set var="hdrClass" value="bg-lost"/></c:if>

            <div class="kanban-col" data-stage="${stageName}">
                <div class="col-header ${hdrClass}">
                    <span>${stageName}</span>
                    <span class="col-count">${not empty oppList ? oppList.size() : 0}</span>
                </div>
                <div class="drop-zone" data-stage="${stageName}">
                    <c:choose>
                        <c:when test="${empty oppList}">
                            <div class="empty-col"><i class="fas fa-inbox"></i><br>Không có OPP</div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="opp" items="${oppList}">
                                <div class="opp-card ${hdrClass}-border" draggable="true" data-id="${opp.id}" data-stage="${opp.stage}"
                                     onclick="window.location='${pageContext.request.contextPath}/sales/opportunity-detail?id=${opp.id}'">

                                    <h5>${opp.title}</h5>
                                    <div class="opp-val">
                                        <c:if test="${not empty opp.expectedValue}">
                                            <fmt:formatNumber value="${opp.expectedValue}" type="number" groupingUsed="true"/>đ
                                        </c:if>
                                    </div>
                                    <div class="meta">
                                        <i class="fas fa-user"></i> ${opp.assignedSalesName}
                                        <c:if test="${not empty opp.expectedCloseDate}">
                                            &nbsp;|&nbsp;<i class="fas fa-calendar"></i> <fmt:formatDate value="${opp.expectedCloseDate}" pattern="dd/MM"/>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:forEach>
    </div>

    <!-- Charts -->
    <div class="chart-row">
        <div class="chart-card">
            <h4><i class="fas fa-chart-bar" style="color:#667eea"></i> Opportunity theo Stage</h4>
            <canvas id="stageChart" height="220"></canvas>
        </div>
        <div class="chart-card">
            <h4><i class="fas fa-chart-line" style="color:#56ab2f"></i> Doanh thu theo tháng (dự báo)</h4>
            <canvas id="revenueChart" height="220"></canvas>
        </div>
    </div>
</div>

<script>
// Drag-and-drop
let draggedId = null, draggedEl = null;

document.querySelectorAll('.opp-card').forEach(card => {
    card.addEventListener('dragstart', e => {
        draggedId = card.dataset.id;
        draggedEl = card;
        e.dataTransfer.effectAllowed = 'move';
        setTimeout(() => card.style.opacity = '0.4', 0);
    });
    card.addEventListener('dragend', () => {
        card.style.opacity = '';
    });
});

document.querySelectorAll('.drop-zone').forEach(zone => {
    zone.addEventListener('dragover', e => {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'move';
        zone.style.background = 'rgba(102,126,234,0.12)';
        zone.style.outline = '2px dashed #667eea';
        zone.style.borderRadius = '8px';
    });
    zone.addEventListener('dragleave', () => {
        zone.style.background = '';
        zone.style.outline = '';
    });
    zone.addEventListener('drop', e => {
        e.preventDefault();
        zone.style.background = '';
        zone.style.outline = '';
        if (!draggedId || !draggedEl) return;
        const newStage = zone.dataset.stage;
        const oldStage = draggedEl.dataset.stage;
        if (newStage === oldStage) return;

        // Optimistically move card
        const emptyMsg = zone.querySelector('.empty-col');
        if (emptyMsg) emptyMsg.remove();
        zone.appendChild(draggedEl);
        draggedEl.dataset.stage = newStage;

        fetch('${pageContext.request.contextPath}/sales/opportunity-stage', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: 'id=' + draggedId + '&newStage=' + encodeURIComponent(newStage)
        }).then(r => {
            if (r.ok) {
                location.reload();
            } else {
                alert('Cập nhật stage thất bại (lỗi ' + r.status + ')');
                location.reload();
            }
        }).catch(() => {
            alert('Không thể kết nối server');
            location.reload();
        });
    });
});

// Stage Chart
const stageLabels = [<c:forEach var="e" items="${stageCount}" varStatus="x">'${e.key}'<c:if test="${!x.last}">,</c:if></c:forEach>];
const stageValues = [<c:forEach var="e" items="${stageCount}" varStatus="x">${e.value}<c:if test="${!x.last}">,</c:if></c:forEach>];
new Chart(document.getElementById('stageChart'), {
    type: 'bar',
    data: { labels: stageLabels, datasets: [{ label: 'Số OPP', data: stageValues,
        backgroundColor: ['#6c757d','#0dcaf0','#0d6efd','#ffc107','#fd7e14','#198754','#dc3545'],
        borderRadius: 6 }] },
    options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
});

// Revenue Chart
const revLabels = [<c:forEach var="e" items="${monthlyRevenue}" varStatus="x">'${e.key}'<c:if test="${!x.last}">,</c:if></c:forEach>];
const revValues = [<c:forEach var="e" items="${monthlyRevenue}" varStatus="x">${e.value}<c:if test="${!x.last}">,</c:if></c:forEach>];
new Chart(document.getElementById('revenueChart'), {
    type: 'line',
    data: { labels: revLabels, datasets: [{ label: 'Doanh thu (đ)', data: revValues,
        borderColor: '#56ab2f', backgroundColor: 'rgba(86,171,47,0.15)', tension: 0.4, fill: true, pointRadius: 5 }] },
    options: { responsive: true, scales: { y: { beginAtZero: true } } }
});
</script>
</body>
</html>
