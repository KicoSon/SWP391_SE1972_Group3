<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách Sản phẩm – Sales CRM</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        body { font-family: "Segoe UI", sans-serif; background: linear-gradient(135deg, #3a7bd5, #3a6073); min-height: 100vh; color: #333; }
        .main-content { margin-left: 270px; padding: 30px; min-height: 100vh; }

        /* Header */
        .page-header { background: rgba(255,255,255,0.97); padding: 22px 28px; border-radius: 20px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.15); margin-bottom: 22px;
            display: flex; justify-content: space-between; align-items: center; }
        .page-header h2 { font-size: 24px; font-weight: 700; color: #333; }
        .page-header p  { font-size: 14px; color: #888; margin-top: 4px; }

        /* Buttons */
        .btn { border: none; padding: 9px 16px; border-radius: 8px; font-size: 13px; font-weight: 600;
               cursor: pointer; text-decoration: none; display: inline-flex; align-items: center; gap: 6px; transition: opacity .2s; }
        .btn:hover { opacity: .88; }
        .btn-primary { background: linear-gradient(135deg,#667eea,#764ba2); color: white; }
        .btn-success { background: linear-gradient(135deg,#56ab2f,#a8e063); color: white; }
        .btn-danger  { background: linear-gradient(135deg,#f093fb,#f5576c); color: white; }
        .btn-warning { background: linear-gradient(135deg,#f7971e,#ffd200); color: #333; }
        .btn-info    { background: linear-gradient(135deg,#0dcaf0,#0d6efd); color: white; }
        .btn-sm      { padding: 5px 10px; font-size: 12px; }

        /* Filter bar */
        .filter-bar { background: rgba(255,255,255,0.97); padding: 18px 24px; border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.12); margin-bottom: 20px;
            display: flex; gap: 12px; flex-wrap: wrap; align-items: center; }
        .filter-bar input, .filter-bar select {
            padding: 9px 12px; border-radius: 8px; border: 1.5px solid #ddd;
            font-size: 14px; min-width: 160px; }
        .filter-bar input:focus, .filter-bar select:focus { border-color: #667eea; outline: none; }

        /* KPI row */
        .kpi-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 22px; }
        .kpi-card { background: rgba(255,255,255,0.97); border-radius: 16px; padding: 20px 24px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.12); display: flex; align-items: center; gap: 16px; }
        .kpi-icon { width: 52px; height: 52px; border-radius: 14px; display: flex; align-items: center;
            justify-content: center; font-size: 22px; color: white; }
        .kpi-icon.purple { background: linear-gradient(135deg,#667eea,#764ba2); }
        .kpi-icon.green  { background: linear-gradient(135deg,#56ab2f,#a8e063); }
        .kpi-icon.blue   { background: linear-gradient(135deg,#2196f3,#0d6efd); }
        .kpi-icon.orange { background: linear-gradient(135deg,#f7971e,#ffd200); }
        .kpi-label { font-size: 12px; color: #888; font-weight: 600; }
        .kpi-value { font-size: 24px; font-weight: 800; color: #333; }

        /* Table card */
        .card { background: rgba(255,255,255,0.97); border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.12); overflow: hidden; }
        table { width: 100%; border-collapse: collapse; }
        thead th { background: linear-gradient(135deg,#667eea,#764ba2); color: white;
            padding: 13px 15px; font-size: 13px; text-align: left; white-space: nowrap; }
        tbody td { padding: 12px 15px; border-bottom: 1px solid #f0f0f0; font-size: 14px; vertical-align: middle; }
        tbody tr:hover td { background: #f8f9ff; }
        .empty-state { text-align: center; padding: 50px; color: #aaa; }
        .val-price { font-weight: 700; color: #2e7d32; }

        /* Category badge */
        .cat-badge { padding: 3px 10px; border-radius: 20px; font-size: 11px; font-weight: 700;
            background: #ede7f6; color: #5e35b1; }

        /* Active badge */
        .badge-active   { display: inline-block; padding: 3px 10px; border-radius: 20px; font-size: 11px;
            font-weight: 700; background: #e8f5e9; color: #2e7d32; }
        .badge-inactive { display: inline-block; padding: 3px 10px; border-radius: 20px; font-size: 11px;
            font-weight: 700; background: #fce4ec; color: #c62828; }

        /* SKU */
        .sku-code { font-family: monospace; font-size: 12px; color: #667eea;
            background: #f0f0ff; padding: 2px 7px; border-radius: 5px; }

        /* Product image */
        .prod-img { width: 42px; height: 42px; border-radius: 10px; object-fit: cover;
            background: #f0f0f0; display: inline-flex; align-items: center; justify-content: center; }
        .prod-img-placeholder { width: 42px; height: 42px; border-radius: 10px; background: linear-gradient(135deg,#667eea22,#764ba222);
            display: inline-flex; align-items: center; justify-content: center; color: #667eea; font-size: 18px; }

        /* Footer info */
        .table-footer { padding: 12px 15px; background: #f8f9ff; border-top: 1px solid #eee;
            font-size: 13px; color: #666; display: flex; justify-content: space-between; align-items: center; }
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <!-- Page Header -->
    <div class="page-header">
        <div>
            <h2><i class="fas fa-boxes" style="color:#667eea"></i> Danh sách Sản phẩm</h2>
            <p>Quản lý sản phẩm & bảng giá – Sales CRM</p>
        </div>
        <div style="display:flex;gap:10px;">
            <a href="${pageContext.request.contextPath}/sales/products?format=json" class="btn btn-info" target="_blank">
                <i class="fas fa-code"></i> JSON API
            </a>
        </div>
    </div>

    <!-- KPI Cards -->
    <div class="kpi-row">
        <div class="kpi-card">
            <div class="kpi-icon purple"><i class="fas fa-boxes"></i></div>
            <div>
                <div class="kpi-label">Tổng sản phẩm</div>
                <div class="kpi-value">${not empty products ? products.size() : 0}</div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon green"><i class="fas fa-check-circle"></i></div>
            <div>
                <div class="kpi-label">Đang kinh doanh</div>
                <div class="kpi-value">
                    <c:set var="activeCount" value="0"/>
                    <c:forEach var="p" items="${products}">
                        <c:if test="${p.active}"><c:set var="activeCount" value="${activeCount + 1}"/></c:if>
                    </c:forEach>
                    ${activeCount}
                </div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon blue"><i class="fas fa-tags"></i></div>
            <div>
                <div class="kpi-label">Danh mục</div>
                <div class="kpi-value" id="catCount">–</div>
            </div>
        </div>
        <div class="kpi-card">
            <div class="kpi-icon orange"><i class="fas fa-search"></i></div>
            <div>
                <div class="kpi-label">Kết quả tìm kiếm</div>
                <div class="kpi-value" id="searchCount">${not empty products ? products.size() : 0}</div>
            </div>
        </div>
    </div>

    <!-- Filter / Search bar -->
    <form method="get" action="${pageContext.request.contextPath}/sales/products">
        <div class="filter-bar">
            <input type="text" name="q" id="searchInput" placeholder="🔍 Tên sản phẩm hoặc SKU..."
                   value="${param.q}" oninput="liveFilter()">
            <select name="category" id="catFilter" onchange="liveFilter()">
                <option value="">-- Tất cả danh mục --</option>
                <c:set var="prevCat" value=""/>
                <c:forEach var="p" items="${products}">
                    <c:if test="${p.category != prevCat}">
                        <option value="${p.category}" ${param.category == p.category ? 'selected' : ''}>${p.category}</option>
                        <c:set var="prevCat" value="${p.category}"/>
                    </c:if>
                </c:forEach>
            </select>
            <button type="submit" class="btn btn-primary"><i class="fas fa-search"></i> Lọc</button>
            <a href="${pageContext.request.contextPath}/sales/products" class="btn btn-warning"><i class="fas fa-redo"></i> Reset</a>
        </div>
    </form>

    <!-- Product Table -->
    <div class="card">
        <c:choose>
            <c:when test="${empty products}">
                <div class="empty-state">
                    <i class="fas fa-box-open fa-3x" style="display:block;margin-bottom:14px;color:#ddd"></i>
                    Không tìm thấy sản phẩm nào.<br>
                    <small style="color:#bbb">Thử thay đổi từ khóa tìm kiếm hoặc danh mục.</small>
                </div>
            </c:when>
            <c:otherwise>
                <table id="productTable">
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Ảnh</th>
                            <th>Tên sản phẩm</th>
                            <th>SKU</th>
                            <th>Danh mục</th>
                            <th>Đơn giá (đ)</th>
                            <th>Mô tả</th>
                            <th>Trạng thái</th>
                        </tr>
                    </thead>
                    <tbody id="productBody">
                        <c:forEach var="p" items="${products}" varStatus="i">
                            <tr class="prod-row"
                                data-name="${p.name}"
                                data-sku="${p.sku}"
                                data-cat="${p.category}">

                                <td>${i.count}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty p.imageUrl}">
                                            <img src="${p.imageUrl}" alt="${p.name}" class="prod-img">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="prod-img-placeholder"><i class="fas fa-box"></i></div>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><strong>${p.name}</strong></td>
                                <td><span class="sku-code">${p.sku}</span></td>
                                <td><span class="cat-badge">${p.category}</span></td>
                                <td class="val-price">
                                    <fmt:formatNumber value="${p.basePrice}" type="number" groupingUsed="true"/> đ
                                </td>
                                <td style="max-width:200px;color:#666;font-size:13px;">
                                    <c:choose>
                                        <c:when test="${not empty p.description}">
                                            <c:choose>
                                                <c:when test="${p.description.length() > 60}">${p.description}</c:when>
                                                <c:otherwise>${p.description}</c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise><em style="color:#ccc">Chưa có mô tả</em></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.active}">
                                            <span class="badge-active"><i class="fas fa-check"></i> Đang bán</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-inactive"><i class="fas fa-ban"></i> Ngừng</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="table-footer">
                    <span>Tổng: <strong id="totalCount">${products.size()}</strong> sản phẩm</span>
                    <span style="color:#667eea;font-size:12px;">
                        <i class="fas fa-info-circle"></i>
                        Sản phẩm được sử dụng trong form Tạo/Sửa báo giá
                    </span>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

</div><!-- /main-content -->

<script>
// Live client-side filter (no server round-trip)
function liveFilter() {
    const q   = document.getElementById('searchInput').value.toLowerCase();
    const cat = document.getElementById('catFilter').value;
    const rows = document.querySelectorAll('#productBody .prod-row');
    let visible = 0, cats = new Set();

    rows.forEach(row => {
        const nameMatch = row.dataset.name.includes(q) || row.dataset.sku.includes(q);
        const catMatch  = !cat || row.dataset.cat === cat;
        if (nameMatch && catMatch) {
            row.style.display = '';
            visible++;
        } else {
            row.style.display = 'none';
        }
        cats.add(row.dataset.cat);
    });

    const sc = document.getElementById('searchCount');
    const tc = document.getElementById('totalCount');
    if (sc) sc.textContent = visible;
    if (tc) tc.textContent = visible;

    const cc = document.getElementById('catCount');
    if (cc) cc.textContent = cats.size;
}

// Count categories on load
window.addEventListener('DOMContentLoaded', function() {
    const cats = new Set();
    document.querySelectorAll('.prod-row').forEach(r => cats.add(r.dataset.cat));
    const cc = document.getElementById('catCount');
    if (cc) cc.textContent = cats.size;
});
</script>
</body>
</html>
