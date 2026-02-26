<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Sales Dashboard – Tổng quan</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        body{margin:0;font-family:"Segoe UI";background:linear-gradient(135deg,#3a7bd5,#3a6073);color:#333;}
        .main-content{margin-left:270px;padding:28px;min-height:100vh;}
        .page-title{color:white;font-size:26px;font-weight:700;margin-bottom:6px;}
        .page-sub{color:rgba(255,255,255,0.75);font-size:14px;margin-bottom:25px;}
        /* KPI Cards */
        .kpi-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(195px,1fr));gap:18px;margin-bottom:28px;}
        .kpi-card{background:rgba(255,255,255,0.95);border-radius:16px;padding:20px;box-shadow:0 8px 20px rgba(0,0,0,0.12);text-align:center;position:relative;overflow:hidden;}
        .kpi-card::before{content:'';position:absolute;top:0;left:0;right:0;height:4px;}
        .kpi-card.blue::before{background:linear-gradient(135deg,#667eea,#764ba2);}
        .kpi-card.green::before{background:linear-gradient(135deg,#56ab2f,#a8e063);}
        .kpi-card.orange::before{background:linear-gradient(135deg,#f7971e,#ffd200);}
        .kpi-card.red::before{background:linear-gradient(135deg,#f093fb,#f5576c);}
        .kpi-card.teal::before{background:linear-gradient(135deg,#0dcaf0,#0d6efd);}
        .kpi-icon{width:50px;height:50px;border-radius:14px;display:flex;align-items:center;justify-content:center;font-size:20px;margin:0 auto 10px;}
        .kpi-icon.blue{background:#f0f2ff;color:#667eea;}
        .kpi-icon.green{background:#f1f8e9;color:#56ab2f;}
        .kpi-icon.orange{background:#fff8e1;color:#f57f17;}
        .kpi-icon.red{background:#fce4ec;color:#c62828;}
        .kpi-icon.teal{background:#e0f7fa;color:#00838f;}
        .kpi-value{font-size:26px;font-weight:800;margin-bottom:4px;}
        .kpi-label{font-size:12px;color:#888;font-weight:600;text-transform:uppercase;}
        /* Chart section */
        .charts-grid{display:grid;grid-template-columns:1fr 1fr;gap:20px;margin-bottom:25px;}
        .chart-card{background:rgba(255,255,255,0.95);border-radius:16px;padding:22px;box-shadow:0 8px 20px rgba(0,0,0,0.12);}
        .chart-card h4{margin:0 0 16px;font-size:16px;font-weight:700;display:flex;align-items:center;gap:8px;}
        /* Top opps */
        .top-card{background:rgba(255,255,255,0.95);border-radius:16px;padding:22px;box-shadow:0 8px 20px rgba(0,0,0,0.12);margin-bottom:22px;}
        .top-card h4{margin:0 0 16px;font-size:16px;font-weight:700;}
        table{width:100%;border-collapse:collapse;}
        th{background:#f3f4f7;color:#555;padding:10px 14px;font-size:12px;text-align:left;font-weight:700;}
        td{padding:10px 14px;border-bottom:1px solid #f0f0f0;font-size:14px;}
        .badge-Won{background:#e8f5e9;color:#2e7d32;}
        .badge-Open{background:#e3f2fd;color:#1565c0;}
        .badge-Lost{background:#fce4ec;color:#c62828;}
        .badge{padding:3px 9px;border-radius:12px;font-size:11px;font-weight:700;}
        .btn{border:none;padding:8px 14px;border-radius:8px;font-size:13px;font-weight:600;cursor:pointer;text-decoration:none;display:inline-flex;align-items:center;gap:6px;}
        .btn-info{background:linear-gradient(135deg,#0dcaf0,#0d6efd);color:white;}
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div class="page-title"><i class="fas fa-tachometer-alt"></i> Sales Dashboard</div>
    <div class="page-sub">Tổng quan hiệu quả bán hàng – Cập nhật real-time</div>

    <!-- KPI Cards -->
    <div class="kpi-grid">
        <div class="kpi-card blue">
            <div class="kpi-icon blue"><i class="fas fa-handshake"></i></div>
            <div class="kpi-value" style="color:#667eea">${kpiTotalOpps}</div>
            <div class="kpi-label">Tổng Opportunity</div>
        </div>
        <div class="kpi-card green">
            <div class="kpi-icon green"><i class="fas fa-trophy"></i></div>
            <div class="kpi-value" style="color:#56ab2f">${kpiWon}</div>
            <div class="kpi-label">Đã Win</div>
        </div>
        <div class="kpi-card teal">
            <div class="kpi-icon teal"><i class="fas fa-chart-line"></i></div>
            <div class="kpi-value" style="color:#00838f">
                <fmt:formatNumber value="${kpiValueWon}" type="number" groupingUsed="true"/>
            </div>
            <div class="kpi-label">Doanh thu Win (đ)</div>
        </div>
        <div class="kpi-card orange">
            <div class="kpi-icon orange"><i class="fas fa-file-invoice-dollar"></i></div>
            <div class="kpi-value" style="color:#f57f17">${kpiQuotations}</div>
            <div class="kpi-label">Báo giá phát sinh</div>
        </div>
        <div class="kpi-card red">
            <div class="kpi-icon red"><i class="fas fa-times-circle"></i></div>
            <div class="kpi-value" style="color:#c62828">${kpiLost}</div>
            <div class="kpi-label">Đã mất</div>
        </div>
    </div>

    <!-- Charts -->
    <div class="charts-grid">
        <div class="chart-card">
            <h4><i class="fas fa-chart-bar" style="color:#667eea"></i> Opportunity theo Stage</h4>
            <canvas id="stageChart" height="250"></canvas>
        </div>
        <div class="chart-card">
            <h4><i class="fas fa-chart-line" style="color:#56ab2f"></i> Doanh thu dự báo theo tháng</h4>
            <canvas id="revChart" height="250"></canvas>
        </div>
    </div>

    <!-- Top Opportunities -->
    <div class="top-card">
        <h4><i class="fas fa-star" style="color:#f57f17"></i> Top 5 Cơ hội giá trị cao</h4>
        <c:choose>
            <c:when test="${empty topOpps}"><p style="color:#aaa;text-align:center">Không có dữ liệu</p></c:when>
            <c:otherwise>
                <table>
                    <thead><tr><th>#</th><th>Tiêu đề</th><th>Sales phụ trách</th><th>Stage</th><th>Giá trị (đ)</th><th>Status</th><th></th></tr></thead>
                    <tbody>
                        <c:forEach var="o" items="${topOpps}" varStatus="i">
                            <tr>
                                <td>${i.count}</td>
                                <td><strong>${o.title}</strong></td>
                                <td>${o.assignedSalesName}</td>
                                <td>${o.stage}</td>
                                <td><strong style="color:#2e7d32"><fmt:formatNumber value="${o.expectedValue}" type="number" groupingUsed="true"/></strong></td>
                                <td><span class="badge badge-${o.status}">${o.status}</span></td>
                                <td><a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${o.id}" class="btn btn-info" style="padding:5px 10px;font-size:11px"><i class="fas fa-eye"></i></a></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Lost Reason Analysis -->
    <c:if test="${not empty lostReasonData}">
    <div style="display:grid;grid-template-columns:1fr 1fr;gap:20px;">
        <div class="chart-card">
            <h4><i class="fas fa-pie-chart" style="color:#dc3545"></i> Phân tích lý do thất bại</h4>
            <canvas id="lostChart" height="250"></canvas>
        </div>
        <div class="top-card" style="margin:0">
            <h4><i class="fas fa-exclamation-triangle" style="color:#dc3545"></i> Chi tiết lý do thất bại</h4>
            <table>
                <thead><tr><th>Lý do</th><th>Số lần</th><th>Tỷ lệ</th></tr></thead>
                <tbody>
                    <c:set var="totalLost" value="0"/>
                    <c:forEach var="e" items="${lostReasonData}"><c:set var="totalLost" value="${totalLost + e.value}"/></c:forEach>
                    <c:forEach var="e" items="${lostReasonData}">
                        <tr>
                            <td>${e.key}</td>
                            <td>${e.value}</td>
                            <td><fmt:formatNumber value="${e.value * 100.0 / (totalLost > 0 ? totalLost : 1)}" maxFractionDigits="1"/>%</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    </c:if>

</div>

<script>
// Stage Chart
const stageLabels = [<c:forEach var="e" items="${stageCount}" varStatus="x">'${e.key}'<c:if test="${!x.last}">,</c:if></c:forEach>];
const stageValues = [<c:forEach var="e" items="${stageCount}" varStatus="x">${e.value}<c:if test="${!x.last}">,</c:if></c:forEach>];
new Chart(document.getElementById('stageChart'), {
    type: 'bar',
    data: {
        labels: stageLabels,
        datasets: [{
            label: 'Số OPP',
            data: stageValues,
            backgroundColor: ['#6c757d','#0dcaf0','#0d6efd','#ffc107','#fd7e14','#198754','#dc3545'],
            borderRadius: 8
        }]
    },
    options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
});

// Revenue Chart
const revLabels = [<c:forEach var="e" items="${monthlyRevenue}" varStatus="x">'${e.key}'<c:if test="${!x.last}">,</c:if></c:forEach>];
const revValues = [<c:forEach var="e" items="${monthlyRevenue}" varStatus="x">${e.value}<c:if test="${!x.last}">,</c:if></c:forEach>];
new Chart(document.getElementById('revChart'), {
    type: 'line',
    data: {
        labels: revLabels,
        datasets: [{
            label: 'Doanh thu (đ)',
            data: revValues,
            borderColor: '#56ab2f',
            backgroundColor: 'rgba(86,171,47,0.15)',
            tension: 0.4,
            fill: true,
            pointRadius: 5,
            pointBackgroundColor: '#56ab2f'
        }]
    },
    options: { responsive: true, scales: { y: { beginAtZero: true } } }
});

// Lost Reason Pie
<c:if test="${not empty lostReasonData}">
const lostLabels = [<c:forEach var="e" items="${lostReasonData}" varStatus="x">'${e.key}'<c:if test="${!x.last}">,</c:if></c:forEach>];
const lostValues = [<c:forEach var="e" items="${lostReasonData}" varStatus="x">${e.value}<c:if test="${!x.last}">,</c:if></c:forEach>];
new Chart(document.getElementById('lostChart'), {
    type: 'doughnut',
    data: {
        labels: lostLabels,
        datasets: [{ data: lostValues,
            backgroundColor: ['#dc3545','#fd7e14','#ffc107','#6c757d','#0dcaf0','#0d6efd'],
            borderWidth: 2
        }]
    },
    options: { responsive: true, plugins: { legend: { position: 'bottom', labels: { boxWidth: 12 } } } }
});
</c:if>
</script>
</body>
</html>
