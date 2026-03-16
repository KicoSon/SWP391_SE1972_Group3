<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Khách hàng - Admin</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <!--<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">-->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/customer-management.css">
    </head>
    <body>
        <!-- Include Sidebar -->
        <jsp:include page="sidebar.jsp" />

        <div class="customer-management-content">
            <div class="customer-management-container">
                <!-- Header -->
                <div class="customer-page-header">
                    <div>
                        <h1>
                            <i class="fas fa-users"></i>
                            Quản lý Khách hàng
                        </h1>
                        <p class="customer-text-muted">Quản lý toàn bộ khách hàng trong hệ thống</p>
                    </div>
                    <div>
                        <c:if test="${canCreate}">
                            <a href="${pageContext.request.contextPath}/managecustomer?action=add" class="btn btn-primary">
                                <i class="fas fa-plus"></i> Thêm khách hàng
                            </a>
                        </c:if>
                        <form action="${pageContext.request.contextPath}/admin/customer-import-preview"
                              method="post"
                              enctype="multipart/form-data"
                              style="display:inline">

                            <label class="btn btn-primary">
                                <i class="fas fa-file-import"></i> Import List
                                <input type="file"
                                       name="excelFile"
                                       accept=".xlsx,.xls"
                                       onchange="this.form.submit()"
                                       style="display:none">
                            </label>

                        </form>
                    </div>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        ${sessionScope.successMessage}
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-circle"></i>
                        ${sessionScope.errorMessage}
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <!-- Statistics Cards -->
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-icon stat-icon-total">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stat-details">
                            <div class="stat-value">${totalCustomers}</div>
                            <div class="stat-label">Tổng khách hàng</div>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon stat-icon-active">
                            <i class="fas fa-user-check"></i>
                        </div>
                        <div class="stat-details">
                            <div class="stat-value">
                                ${activeCount}
                            </div>
                            <div class="stat-label">Đang hoạt động</div>
                        </div>
                    </div>

                    <div class="stat-card">
                        <div class="stat-icon stat-icon-inactive">
                            <i class="fas fa-user-slash"></i>
                        </div>
                        <div class="stat-details">
                            <div class="stat-value">${inactiveCount}</div>
                            <div class="stat-label">Đã bị khóa</div>
                        </div>
                    </div>
                </div>

                <!-- Search & Filter -->
                <div class="search-box">
                    <form action="${pageContext.request.contextPath}/managecustomer" method="GET">
                        <input type = "hidden" name="pageSize" value = "${pageSize}">
                        <div class="search-input-group">
                            <i class="fas fa-search"></i>
                            <input type="text" 
                                   name="search" 
                                   placeholder="Tìm kiếm theo tên, email, số điện thoại, địa chỉ..." 
                                   value="${param.search}">
                            <button type="submit" class="btn btn-search">
                                <i class="fas fa-search"></i> Tìm kiếm
                            </button>
                            <c:if test="${not empty param.search}">
                                <a href="${pageContext.request.contextPath}/managecustomer" class="btn btn-clear">
                                    <i class="fas fa-times"></i> Xóa bộ lọc
                                </a>
                            </c:if>
                        </div>
                    </form>
                </div>

                <!-- Filter Box -->
                <div class="search-box" style="margin-top:15px;">

                    <form action="${pageContext.request.contextPath}/managecustomer" method="GET">
                        <input type = "hidden" name="pageSize" value = "${pageSize}">
                        <!-- Giữ search -->
                        <input type="hidden" name="search" value="${param.search}"/>

                        <div class="filter-main">

                            <!-- Rank -->
                            <div class="filter-col">

                                <div class="filter-inner">

                                    <div class="filter-title">Hạng</div>

                                    <div class="filter-options">

                                        <label>
                                            <input type="checkbox" name="rank" value="Bronze" onchange="this.form.submit()"
                                                   <c:if test="${fn:contains(fn:join(paramValues.rank, ','), 'Bronze')}">checked</c:if>>
                                                   Bronze
                                            </label>

                                            <label>
                                                <input type="checkbox" name="rank" value="Silver" onchange="this.form.submit()"
                                                <c:if test="${fn:contains(fn:join(paramValues.rank, ','), 'Silver')}">checked</c:if>>
                                                Silver
                                            </label>

                                            <label>
                                                <input type="checkbox" name="rank" value="Gold" onchange="this.form.submit()"
                                                <c:if test="${fn:contains(fn:join(paramValues.rank, ','), 'Gold')}">checked</c:if>>
                                                Gold
                                            </label>

                                            <label>
                                                <input type="checkbox" name="rank" value="Platinum" onchange="this.form.submit()"
                                                <c:if test="${fn:contains(fn:join(paramValues.rank, ','), 'Platinum')}">checked</c:if>>
                                                Platinum
                                            </label>

                                        </div>
                                    </div>
                                </div>


                                <!-- Status -->
                                <div class="filter-col">

                                    <div class="filter-inner">

                                        <div class="filter-title">Trạng thái</div>

                                        <div class="filter-options">

                                            <label>
                                                <input type="checkbox" name="status" value="Active" onchange="this.form.submit()"
                                                <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'Active')}">checked</c:if>>
                                                Hoạt động
                                            </label>

                                            <label>
                                                <input type="checkbox" name="status" value="Inactive" onchange="this.form.submit()"
                                                <c:if test="${fn:contains(fn:join(paramValues.status, ','), 'Inactive')}">checked</c:if>>
                                                Đã khóa
                                            </label>

                                        </div>
                                    </div>
                                </div>


                            </div>
                            <!-- Clear -->
                            <div class="filter-col filter-clear">

                            <c:if test="${not empty param.rank or not empty param.status}">
                                <a href="${pageContext.request.contextPath}/managecustomer"
                                   class="btn btn-clear">
                                    <i class="fas fa-times"></i> Xóa lọc
                                </a>
                            </c:if>
                        </div>
                    </form>

                </div>

                <!-- Customers Table -->
                <div class="table-card">
                    <div class="table-header" style="display:flex; justify-content:space-between; align-items:center;">
                        <h3>
                            <i class="fas fa-list"></i> 
                            Danh sách khách hàng
                            <c:if test="${not empty param.search}">
                                <span class="badge badge-info">Kết quả tìm kiếm: "${param.search}"</span>
                            </c:if>
                        </h3>
                        <form method="get" action="${pageContext.request.contextPath}/managecustomer"
                              class="page-size-box">

                            <c:if test="${not empty param.search}">
                                <input type="hidden" name="search" value="${param.search}">
                            </c:if>

                            <label>Show</label>

                            <select name="pageSize"
                                    class="page-size-select"
                                    onchange="this.form.submit()">

                                <option value="8" ${pageSize==8?'selected':''}>8</option>
                                <option value="10" ${pageSize==10?'selected':''}>10</option>
                                <option value="15" ${pageSize==15?'selected':''}>15</option>
                                <option value="20" ${pageSize==20?'selected':''}>20</option>

                            </select>

                            <label>rows</label>

                        </form>
                    </div>

                    <c:choose>
                        <c:when test="${empty customers}">
                            <div class="empty-state">
                                <i class="fas fa-users"></i>
                                <h3>Không tìm thấy khách hàng</h3>
                                <p>
                                    <c:choose>
                                        <c:when test="${not empty param.search}">
                                            Không có kết quả nào phù hợp với từ khóa "${param.search}"
                                        </c:when>
                                        <c:otherwise>
                                            Chưa có khách hàng nào trong hệ thống
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-responsive">
                                <table class="data-table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Email</th>
                                            <th>Họ tên</th>
                                            <th>Số điện thoại</th>
                                            <th>Địa chỉ</th>
                                            <th>Hạng</th>
                                            <th>Trạng thái</th>
                                            <th>Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${customers}" var="customer">
                                            <tr>
                                                <td>#${customer.id}</td>
                                                <td>
                                                    <div class="customer-email">
                                                        <i class="fas fa-envelope"></i>
                                                        ${customer.email}
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="customer-name">
                                                        <i class="fas fa-user"></i>
                                                        ${customer.fullName}
                                                    </div>
                                                </td>
                                                <td>
                                                    <div class="customer-phone">
                                                        <i class="fas fa-phone"></i>
                                                        ${customer.phone}
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="badge badge-location">
                                                        <i class="fas fa-map-marker-alt"></i>
                                                        ${customer.address}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span class="badge badge-${customer.tierName.toLowerCase()}">
                                                        ${customer.tierName}
                                                    </span>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${customer.status == 'Active'}">
                                                            <span class="badge badge-active">
                                                                <i class="fas fa-check-circle"></i>
                                                                Hoạt động
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge badge-inactive">
                                                                <i class="fas fa-ban"></i>
                                                                Đã khóa
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <div class="action-buttons">
                                                        <a href="${pageContext.request.contextPath}/managecustomer?action=view&id=${customer.id}" 
                                                           class="btn btn-sm btn-view"
                                                           title="Xem chi tiết">
                                                            <i class="fas fa-eye"></i>
                                                        </a>
                                                        <c:if test="${canEdit}">
                                                            <a href="${pageContext.request.contextPath}/managecustomer?action=edit&id=${customer.id}" 
                                                               class="btn btn-sm btn-edit"
                                                               title="Chỉnh sửa">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                        </c:if>
                                                        <c:if test="${canDelete}">
                                                            <c:choose>
                                                                <c:when test="${customer.status == 'Active'}">
                                                                    <button onclick="confirmBan(${customer.id}, '${customer.fullName}')" 
                                                                            class="btn btn-sm btn-ban"
                                                                            title="Khóa tài khoản">
                                                                        <i class="fas fa-lock"></i>
                                                                    </button>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <button onclick="confirmUnban(${customer.id}, '${customer.fullName}')" 
                                                                            class="btn btn-sm btn-unban"
                                                                            title="Mở khóa tài khoản">
                                                                        <i class="fas fa-unlock"></i>
                                                                    </button>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:if>
                                                        <button onclick="confirmProvide(${customer.id}, '${customer.fullName}')" 
                                                                class="btn btn-sm btn-provide"
                                                                title="Cung cấp tài khoản">
                                                            <i class="fas fa-key"></i>
                                                        </button>   
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Pagination -->
                            <c:if test="${totalPages > 1}">
                                <div class="pagination">
                                    <c:if test="${currentPage > 1}">
                                        <a href="${pageContext.request.contextPath}/managecustomer?page=${currentPage - 1}<c:if test='${not empty param.search}'>&search=${param.search}</c:if>" 
                                           class="page-link">
                                                <i class="fas fa-chevron-left"></i>
                                            </a>
                                    </c:if>

                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <c:choose>
                                            <c:when test="${i == currentPage}">
                                                <span class="page-link active">${i}</span>
                                            </c:when>
                                            <c:when test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                                <a href="${pageContext.request.contextPath}/managecustomer?page=${i}<c:if test='${not empty param.search}'>&search=${param.search}</c:if>" 
                                                   class="page-link">${i}</a>
                                            </c:when>
                                            <c:when test="${i == currentPage - 3 || i == currentPage + 3}">
                                                <span class="page-link">...</span>
                                            </c:when>
                                        </c:choose>
                                    </c:forEach>

                                    <c:if test="${currentPage < totalPages}">
                                        <a href="${pageContext.request.contextPath}/managecustomer?page=${currentPage + 1}<c:if test='${not empty param.search}'>&search=${param.search}</c:if>" 
                                           class="page-link">
                                                <i class="fas fa-chevron-right"></i>
                                            </a>
                                    </c:if>
                                </div>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Ban/Unban Form (hidden) -->
        <form id="statusForm" method="POST" action="${pageContext.request.contextPath}/managecustomer" style="display: none;">
            <input type="hidden" name="action" id="statusAction">
            <input type="hidden" name="customerId" id="customerId">
        </form>
    </body>
</html>
<script>
    function confirmBan(customerId, customerName) {
        if (confirm('Bạn có chắc muốn khóa tài khoản của "' + customerName + '"?\n\nKhách hàng sẽ không thể đăng nhập sau khi bị khóa.')) {
            document.getElementById('statusAction').value = 'ban';
            document.getElementById('customerId').value = customerId;
            document.getElementById('statusForm').submit();
        }
    }

    function confirmUnban(customerId, customerName) {
        if (confirm('Bạn có chắc muốn mở khóa tài khoản của "' + customerName + '"?')) {
            document.getElementById('statusAction').value = 'unban';
            document.getElementById('customerId').value = customerId;
            document.getElementById('statusForm').submit();
        }
    }
    function confirmProvide(customerId, customerName) {
        if (confirm('Bạn có muốn cung cấp tài khoản cho "' + customerName + '" ?\n\nHệ thống sẽ gửi email chứa tài khoản và mật khẩu cho khách hàng.')) {
            document.getElementById('statusAction').value = 'provideAccount';
            document.getElementById('customerId').value = customerId;
            document.getElementById('statusForm').submit();
        }
    }

    // Auto-hide alerts after 5 seconds
    setTimeout(function () {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function (alert) {
            alert.style.opacity = '0';
            setTimeout(function () {
                alert.remove();
            }, 300);
        });
    }, 5000);
    function openFilePicker() {
        document.getElementById("excelFile").click();
    }

    function autoPreview(input) {

        if (input.files.length > 0) {
            document.getElementById("importForm").submit();
        }

    }
    window.onload = function () {

        const hasPreview = "${not empty previewCustomers}";

        if (hasPreview === "true") {
            var modal = new bootstrap.Modal(document.getElementById("previewModal"));
            modal.show();
        }

    }
</script>