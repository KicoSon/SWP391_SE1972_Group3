<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>
        <c:choose>
            <c:when test="${mode == 'edit'}">Sửa Báo giá</c:when>
            <c:otherwise>Tạo Báo giá mới</c:otherwise>
        </c:choose>
    </title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        body { margin:0; font-family:"Segoe UI",sans-serif; background:linear-gradient(135deg,#3a7bd5,#3a6073); color:#333; }
        .main-content { margin-left:270px; padding:30px; min-height:100vh; }
        .card { background:rgba(255,255,255,0.97); border-radius:18px; box-shadow:0 10px 25px rgba(0,0,0,0.15); padding:28px; margin-bottom:20px; }
        .card-title { font-size:20px; font-weight:700; margin-bottom:22px; display:flex; align-items:center; gap:10px; }
        .form-grid { display:grid; grid-template-columns:1fr 1fr; gap:18px; }
        .form-group { display:flex; flex-direction:column; gap:5px; }
        .form-group.full { grid-column:1/-1; }
        .form-group label { font-size:13px; font-weight:600; color:#555; }
        .form-group input, .form-group select, .form-group textarea {
            padding:9px 12px; border:1.5px solid #ddd; border-radius:8px; font-size:14px;
        }
        .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
            border-color:#667eea; outline:none;
        }
        .btn { border:none; padding:9px 15px; border-radius:8px; font-size:13px; font-weight:600;
               cursor:pointer; text-decoration:none; display:inline-flex; align-items:center; gap:6px; }
        .btn-primary { background:linear-gradient(135deg,#667eea,#764ba2); color:white; }
        .btn-danger  { background:linear-gradient(135deg,#f093fb,#f5576c); color:white; }
        .btn-success { background:linear-gradient(135deg,#56ab2f,#a8e063); color:white; }
        .btn-warning { background:linear-gradient(135deg,#f7971e,#ffd200); color:#333; }
        .btn-sm { padding:5px 10px; font-size:12px; }
        /* Line items table */
        #itemsTable { width:100%; border-collapse:collapse; margin-bottom:12px; }
        #itemsTable th { background:#f3f4f7; padding:9px 12px; font-size:12px; font-weight:700; color:#555; text-align:left; }
        #itemsTable td { padding:8px 10px; border-bottom:1px solid #f0f0f0; vertical-align:middle; }
        #itemsTable input, #itemsTable select {
            width:100%; padding:7px; border:1.5px solid #ddd; border-radius:6px; font-size:13px; box-sizing:border-box;
        }
        .totals-section { text-align:right; padding:14px 0 4px; }
        .totals-section .line { display:flex; justify-content:flex-end; gap:16px; font-size:14px; margin-bottom:5px; }
        .grand-line { font-size:18px; font-weight:800; color:#2e7d32; border-top:2px solid #ddd; padding-top:10px; margin-top:5px; }
        .alert-error { background:#fce4ec; border:1px solid #f8bbd9; border-radius:8px; padding:12px; margin-bottom:15px; color:#c62828; }
        /* Product Search Modal */
        .prod-modal-overlay { display:none; position:fixed; inset:0; background:rgba(0,0,0,0.55); z-index:3000;
            align-items:center; justify-content:center; }
        .prod-modal-overlay.open { display:flex; }
        .prod-modal-box { background:#fff; border-radius:20px; padding:28px; width:660px; max-width:95vw;
            box-shadow:0 20px 50px rgba(0,0,0,0.3); max-height:80vh; display:flex; flex-direction:column; }
        .prod-modal-box h4 { font-size:18px; font-weight:700; margin-bottom:16px; display:flex; align-items:center; gap:10px; }
        .prod-search-bar { display:flex; gap:10px; margin-bottom:14px; }
        .prod-search-bar input { flex:1; padding:9px 14px; border:1.5px solid #ddd; border-radius:8px; font-size:14px; }
        .prod-search-bar input:focus { border-color:#667eea; outline:none; }
        .prod-list { overflow-y:auto; flex:1; border:1px solid #f0f0f0; border-radius:10px; }
        .prod-list-item { display:flex; align-items:center; justify-content:space-between;
            padding:12px 16px; border-bottom:1px solid #f5f5f5; cursor:pointer; transition:background .15s; }
        .prod-list-item:hover { background:#f0f0ff; }
        .prod-list-item:last-child { border-bottom:none; }
        .prod-item-info strong { font-size:14px; display:block; }
        .prod-item-info small { color:#888; font-size:12px; }
        .prod-item-price { font-weight:700; color:#2e7d32; font-size:15px; }
        .prod-cat-badge { background:#ede7f6; color:#5e35b1; padding:2px 8px; border-radius:10px; font-size:10px; font-weight:700; margin-left:6px; }
        .prod-empty { text-align:center; padding:30px; color:#aaa; }
        .modal-footer-btns { display:flex; justify-content:flex-end; margin-top:16px; }
    </style>
</head>
<body>
<jsp:include page="/sales/sidebar.jsp"/>
<div class="main-content">

    <div style="margin-bottom:16px;">
        <a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${opportunityId}"
           style="color:rgba(255,255,255,0.85);text-decoration:none;font-size:13px;">
            <i class="fas fa-arrow-left"></i> Quay lại Opportunity
        </a>
    </div>

    <div class="card">
        <div class="card-title">
            <i class="fas fa-file-invoice-dollar" style="color:#667eea"></i>
            <c:choose>
                <c:when test="${mode == 'edit'}">Sửa Báo giá</c:when>
                <c:otherwise>Tạo Báo giá mới</c:otherwise>
            </c:choose>
            <c:if test="${not empty quotation}">
                <span style="font-size:14px;color:#888;font-weight:400;">– ${quotation.quotationCode}</span>
            </c:if>
        </div>

        <c:if test="${not empty error}">
            <div class="alert-error"><i class="fas fa-exclamation-circle"></i> ${error}</div>
        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/sales/${mode == 'edit' ? 'quotation-edit' : 'quotation-create'}"
              id="quoteForm">
            <input type="hidden" name="opportunityId" value="${opportunityId}">
            <c:if test="${mode == 'edit'}">
                <input type="hidden" name="id" value="${quotation.id}">
            </c:if>
            <input type="hidden" name="totalAmount" id="totalAmountHidden" value="0">

            <div class="form-grid">
                <div class="form-group">
                    <label>Hạn sử dụng báo giá *</label>
                    <input type="date" name="validUntil" required
                           value="<fmt:formatDate value='${quotation.validUntil}' pattern='yyyy-MM-dd'/>">
                </div>
                <div class="form-group">
                    <label>Phương thức thanh toán</label>
                    <select name="paymentMethod">
                        <option value="Chuyển khoản" ${quotation.paymentMethod == 'Chuyển khoản' ? 'selected' : ''}>Chuyển khoản</option>
                        <option value="Tiền mặt"     ${quotation.paymentMethod == 'Tiền mặt'     ? 'selected' : ''}>Tiền mặt</option>
                        <option value="Thẻ tín dụng" ${quotation.paymentMethod == 'Thẻ tín dụng' ? 'selected' : ''}>Thẻ tín dụng</option>
                    </select>
                </div>
                <div class="form-group full">
                    <label>Ghi chú</label>
                    <textarea name="notes" rows="2" placeholder="Điều khoản, ghi chú thêm...">${quotation.notes}</textarea>
                </div>
            </div>

            <!-- ===== Line Items ===== -->
            <div style="display:flex;justify-content:space-between;align-items:center;margin:22px 0 10px;padding-top:18px;border-top:1px solid #eee;">
                <span style="font-size:16px;font-weight:700;"><i class="fas fa-list"></i> Danh sách sản phẩm</span>
                <div style="display:flex;gap:8px;">
                    <button type="button" class="btn btn-primary btn-sm" onclick="openProductModal()">
                        <i class="fas fa-search"></i> Chọn từ danh sách
                    </button>
                    <button type="button" class="btn btn-success btn-sm" onclick="addRow()">
                        <i class="fas fa-plus"></i> Thêm dòng trống
                    </button>
                </div>
            </div>

            <table id="itemsTable">
                <thead>
                    <tr>
                        <th style="width:30%">Sản phẩm</th>
                        <th style="width:10%">Số lượng</th>
                        <th style="width:16%">Đơn giá (đ)</th>
                        <th style="width:10%">Giảm giá%</th>
                        <th style="width:9%">Thuế%</th>
                        <th style="width:16%">Thành tiền</th>
                        <th style="width:4%"></th>
                    </tr>
                </thead>
                <tbody id="itemsBody">
                    <c:choose>
                        <c:when test="${not empty quotationItems}">
                            <c:forEach var="item" items="${quotationItems}">
                                <tr class="item-row">
                                    <td>
                                        <select name="productId[]" onchange="loadPrice(this)">
                                            <option value="">-- Chọn SP --</option>
                                            <c:forEach var="p" items="${products}">
                                                <option value="${p.id}" data-price="${p.basePrice}"
                                                    ${item.productId == p.id ? 'selected' : ''}>${p.name}</option>
                                            </c:forEach>
                                        </select>
                                    </td>
                                    <td><input type="number" name="quantity[]" min="1" value="${item.quantity}" class="qty-input" oninput="calcRow(this)"></td>
                                    <td><input type="number" name="unitPrice[]" min="0" step="1000" value="${item.unitPrice}" class="price-input" oninput="calcRow(this)"></td>
                                    <td><input type="number" name="discount[]" min="0" max="100" step="0.5" value="${item.discount}" class="disc-input" oninput="calcRow(this)"></td>
                                    <td><input type="number" name="taxRate[]" min="0" max="30" step="0.5" value="${item.taxRate}" class="tax-input" oninput="calcRow(this)"></td>
                                    <td><input type="text" class="line-total" readonly style="background:#f8fff8;font-weight:700;color:#2e7d32;"></td>
                                    <td><button type="button" class="btn btn-danger btn-sm" onclick="removeRow(this)"><i class="fas fa-trash"></i></button></td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <%-- Empty tbody — JS will add one row on load --%>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <!-- Totals -->
            <div class="totals-section">
                <div class="line"><span>Tạm tính:</span><span id="subtotalDisplay">0 đ</span></div>
                <div class="line"><span>Thuế:</span><span id="taxDisplay">0 đ</span></div>
                <div class="line grand-line"><span>Tổng cộng:</span><span id="grandDisplay">0 đ</span></div>
            </div>

            <div style="display:flex;gap:12px;justify-content:flex-end;margin-top:22px;">
                <a href="${pageContext.request.contextPath}/sales/opportunity-detail?id=${opportunityId}"
                   class="btn btn-warning"><i class="fas fa-times"></i> Hủy</a>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save"></i>
                    <c:choose>
                        <c:when test="${mode == 'edit'}">Lưu thay đổi</c:when>
                        <c:otherwise>Tạo Báo giá</c:otherwise>
                    </c:choose>
                </button>
            </div>
        </form>
    </div><!-- /card -->
</div><!-- /main-content -->

<!-- ===== Modal: Tìm kiếm & Chọn Sản phẩm ===== -->
<div id="productModal" class="prod-modal-overlay" onclick="closeProductModal(event)">
    <div class="prod-modal-box" onclick="event.stopPropagation()">
        <h4>
            <i class="fas fa-boxes" style="color:#667eea"></i>
            Chọn Sản phẩm
        </h4>
        <div class="prod-search-bar">
            <input type="text" id="prodSearchInput" placeholder="🔍 Tìm theo tên hoặc SKU..."
                   oninput="filterProductModal()">
            <select id="prodCatFilter" onchange="filterProductModal()"
                    style="padding:9px 12px;border:1.5px solid #ddd;border-radius:8px;font-size:14px;">
                <option value="">-- Tất cả danh mục --</option>
            </select>
        </div>
        <div class="prod-list" id="prodModalList">
            <!-- Filled by JS from PRICES_DATA -->
        </div>
        <div class="modal-footer-btns">
            <button type="button" class="btn btn-warning" onclick="closeProductModal()">
                <i class="fas fa-times"></i> Đóng
            </button>
        </div>
    </div>
</div>

<!-- ===== Product price lookup ===== -->
<script>
const PRICES = {};
<c:forEach var="p" items="${products}">
PRICES[${p.id}] = ${p.basePrice};
</c:forEach>

function loadPrice(sel) {
    const price = PRICES[sel.value] || 0;
    const row = sel.closest('tr');
    row.querySelector('.price-input').value = price;
    calcRow(sel);
}

function calcRow(el) {
    const row   = el.closest('tr');
    const qty   = parseFloat(row.querySelector('.qty-input').value)   || 0;
    const price = parseFloat(row.querySelector('.price-input').value) || 0;
    const disc  = parseFloat(row.querySelector('.disc-input').value)  || 0;
    const tax   = parseFloat(row.querySelector('.tax-input').value)   || 0;
    const afterDisc = price * qty * (1 - disc / 100);
    const total     = afterDisc * (1 + tax / 100);
    row.querySelector('.line-total').value = Math.round(total).toLocaleString('vi-VN');
    recalcTotals();
}

function recalcTotals() {
    let sub = 0, taxAmt = 0, grand = 0;
    document.querySelectorAll('#itemsBody .item-row').forEach(row => {
        const qty   = parseFloat(row.querySelector('.qty-input').value)   || 0;
        const price = parseFloat(row.querySelector('.price-input').value) || 0;
        const disc  = parseFloat(row.querySelector('.disc-input').value)  || 0;
        const tax   = parseFloat(row.querySelector('.tax-input').value)   || 0;
        const s = price * qty * (1 - disc / 100);
        const t = s * (tax / 100);
        sub   += s;
        taxAmt += t;
        grand  += s + t;
    });
    document.getElementById('subtotalDisplay').textContent = Math.round(sub).toLocaleString('vi-VN') + ' đ';
    document.getElementById('taxDisplay').textContent      = Math.round(taxAmt).toLocaleString('vi-VN') + ' đ';
    document.getElementById('grandDisplay').textContent    = Math.round(grand).toLocaleString('vi-VN') + ' đ';
    document.getElementById('totalAmountHidden').value     = Math.round(grand);
}

function buildProductOptions() {
    let html = '<option value="">-- Chọn SP --</option>';
    <c:forEach var="p" items="${products}">
    html += '<option value="${p.id}" data-price="${p.basePrice}">${p.name}</option>';
    </c:forEach>
    return html;
}

function addRow() {
    const tr = document.createElement('tr');
    tr.className = 'item-row';
    tr.innerHTML = `
        <td><select name="productId[]" onchange="loadPrice(this)">${buildProductOptions()}</select></td>
        <td><input type="number" name="quantity[]" min="1" value="1" class="qty-input" oninput="calcRow(this)"></td>
        <td><input type="number" name="unitPrice[]" min="0" step="1000" value="0" class="price-input" oninput="calcRow(this)"></td>
        <td><input type="number" name="discount[]" min="0" max="100" step="0.5" value="0" class="disc-input" oninput="calcRow(this)"></td>
        <td><input type="number" name="taxRate[]" min="0" max="30" step="0.5" value="10" class="tax-input" oninput="calcRow(this)"></td>
        <td><input type="text" class="line-total" readonly style="background:#f8fff8;font-weight:700;color:#2e7d32;" value="0"></td>
        <td><button type="button" class="btn btn-danger btn-sm" onclick="removeRow(this)"><i class="fas fa-trash"></i></button></td>`;
    document.getElementById('itemsBody').appendChild(tr);
    recalcTotals();
}

function removeRow(btn) {
    if (document.querySelectorAll('#itemsBody .item-row').length <= 1) {
        alert('Cần ít nhất 1 sản phẩm trong báo giá!');
        return;
    }
    btn.closest('tr').remove();
    recalcTotals();
}

// On submit: sync totalAmount hidden field
document.getElementById('quoteForm').addEventListener('submit', function() {
    recalcTotals();
});

// Init: add empty row if none exist, then calc totals
if (document.querySelectorAll('#itemsBody .item-row').length === 0) {
    addRow();
} else {
    // Re-calc line totals for existing items (edit mode)
    document.querySelectorAll('#itemsBody .item-row').forEach(row => {
        const el = row.querySelector('.qty-input');
        if (el) calcRow(el);
    });
}

// =========================================================
// PRODUCT SEARCH MODAL
// =========================================================
const PRODUCTS_DATA = [
<c:forEach var="p" items="${products}" varStatus="s">
    { id: ${p.id}, name: "<c:out value='${p.name}' escapeXml='false'/>".replace(/"/g,'\\"'), sku: "<c:out value='${p.sku}' escapeXml='false'/>",
      price: ${p.basePrice}, category: "<c:out value='${p.category}' escapeXml='false'/>" }${!s.last ? ',' : ''}

</c:forEach>
];

function openProductModal() {
    document.getElementById('productModal').classList.add('open');
    document.getElementById('prodSearchInput').value = '';
    buildModalList(PRODUCTS_DATA);
    buildCatSelect();
    setTimeout(() => document.getElementById('prodSearchInput').focus(), 80);
}

function closeProductModal(e) {
    if (!e || e.target === document.getElementById('productModal')) {
        document.getElementById('productModal').classList.remove('open');
    }
}

function buildCatSelect() {
    const sel = document.getElementById('prodCatFilter');
    const cats = [...new Set(PRODUCTS_DATA.map(p => p.category))].sort();
    sel.innerHTML = '<option value="">-- Tất cả danh mục --</option>';
    cats.forEach(c => { const o = document.createElement('option'); o.value = c; o.textContent = c; sel.appendChild(o); });
}

function filterProductModal() {
    const q   = document.getElementById('prodSearchInput').value.toLowerCase();
    const cat = document.getElementById('prodCatFilter').value;
    const filtered = PRODUCTS_DATA.filter(p =>
        (!q || p.name.toLowerCase().includes(q) || p.sku.toLowerCase().includes(q)) &&
        (!cat || p.category === cat)
    );
    buildModalList(filtered);
}

function buildModalList(list) {
    var container = document.getElementById('prodModalList');
    if (!list.length) {
        container.innerHTML = '<div class="prod-empty"><i class="fas fa-box-open fa-2x" style="display:block;margin-bottom:10px;color:#eee"></i>Kh&#244;ng t&#236;m th&#7845;y s&#7843;n ph&#7849;m</div>';
        return;
    }
    var html = '';
    list.forEach(function(p) {
        var safeName = p.name.replace(/'/g, "\\'");
        html += '<div class="prod-list-item" onclick="selectProduct(' + p.id + ', \'' + safeName + '\', ' + p.price + ')">';
        html += '<div class="prod-item-info">';
        html += '<strong>' + escHtml(p.name) + ' <span class="prod-cat-badge">' + escHtml(p.category) + '</span></strong>';
        html += '<small>SKU: ' + escHtml(p.sku) + ' &nbsp;|&nbsp; <i class="fas fa-tag"></i> &#272;&#417;n gi&aacute; g&#7889;c</small>';
        html += '</div>';
        html += '<div class="prod-item-price">' + formatVND(p.price) + ' &#273;</div>';
        html += '</div>';
    });
    container.innerHTML = html;
}

function escHtml(s) { return s ? s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;') : ''; }
function formatVND(n) { return Math.round(n).toLocaleString('vi-VN'); }

function selectProduct(id, name, price) {
    var tbody = document.getElementById('itemsBody');
    var tr = document.createElement('tr');
    tr.className = 'item-row';
    var opts = buildProductOptions();
    opts = opts.replace('value="' + id + '"', 'value="' + id + '" selected');
    tr.innerHTML =
        '<td><select name="productId[]" onchange="loadPrice(this)">' + opts + '</select></td>' +
        '<td><input type="number" name="quantity[]" min="1" value="1" class="qty-input" oninput="calcRow(this)"></td>' +
        '<td><input type="number" name="unitPrice[]" min="0" step="1000" value="' + price + '" class="price-input" oninput="calcRow(this)"></td>' +
        '<td><input type="number" name="discount[]" min="0" max="100" step="0.5" value="0" class="disc-input" oninput="calcRow(this)"></td>' +
        '<td><input type="number" name="taxRate[]" min="0" max="30" step="0.5" value="10" class="tax-input" oninput="calcRow(this)"></td>' +
        '<td><input type="text" class="line-total" readonly style="background:#f8fff8;font-weight:700;color:#2e7d32;"></td>' +
        '<td><button type="button" class="btn btn-danger btn-sm" onclick="removeRow(this)"><i class="fas fa-trash"></i></button></td>';
    tbody.appendChild(tr);
    calcRow(tr.querySelector('.qty-input'));
    closeProductModal();
}
</script>
</body>
</html>
