<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Khách hàng - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/customer-management.css">
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/components/sidebar.jsp" />

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
                <c:if test="${canCreate}">
                    <a href="${pageContext.request.contextPath}/managecustomer?action=add" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Thêm khách hàng
                    </a>
                </c:if>
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
                            <c:set var="activeCount" value="0"/>
                            <c:forEach items="${customers}" var="c">
                                <c:if test="${c.active}">
                                    <c:set var="activeCount" value="${activeCount + 1}"/>
                                </c:if>
                            </c:forEach>
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
                        <div class="stat-value">${totalCustomers - activeCount}</div>
                        <div class="stat-label">Đã bị khóa</div>
                    </div>
                </div>
            </div>

            <!-- Search & Filter -->
            <div class="search-box">
                <form action="${pageContext.request.contextPath}/managecustomer" method="GET">
                    <div class="search-input-group">
                        <i class="fas fa-search"></i>
                        <input type="text" 
                               name="search" 
                               placeholder="Tìm kiếm theo tên, email, số điện thoại, thành phố..." 
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

            <!-- Customers Table -->
            <div class="table-card">
                <div class="table-header">
                    <h3>
                        <i class="fas fa-list"></i> 
                        Danh sách khách hàng
                        <c:if test="${not empty param.search}">
                            <span class="badge badge-info">Kết quả tìm kiếm: "${param.search}"</span>
                        </c:if>
                    </h3>
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
                                        <th>Thành phố</th>
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
                                            <td>${customer.address}</td>
                                            <td>
                                                <span class="badge badge-location">
                                                    <i class="fas fa-map-marker-alt"></i>
                                                    ${customer.city}
                                                </span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${customer.active}">
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
                                                            <c:when test="${customer.active}">
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

        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                alert.style.opacity = '0';
                setTimeout(function() {
                    alert.remove();
                }, 300);
            });
        }, 5000);
    </script>
</body>
</html>
