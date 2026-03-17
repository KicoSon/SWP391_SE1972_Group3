<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Nhân Viên</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-management.css">
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="sidebar.jsp" />
    
    <div class="staff-management-content">
            <div class="staff-page-header">
                <div>
                    <h1>Quản Lý Nhân Viên</h1>
                    <p>Quản lý thông tin và tài khoản nhân viên hệ thống</p>
                </div>
                <c:if test="${canCreate}">
                    <a href="${pageContext.request.contextPath}/admin/staff?action=add" class="btn-add-staff">
                        <i class="fas fa-plus"></i> Thêm Nhân Viên
                    </a>
                </c:if>
            </div>

            <!-- Statistics Cards -->
            <div class="stats-grid">
                <div class="stat-card total">
                    <div class="stat-icon">
                        <i class="fas fa-users"></i>
                    </div>
                    <div class="stat-info">
                        <h3>${totalStaff}</h3>
                        <p>Tổng Nhân Viên</p>
                    </div>
                </div>
                <div class="stat-card active">
                    <div class="stat-icon">
                        <i class="fas fa-user-check"></i>
                    </div>
                    <div class="stat-info">
                        <h3>${activeStaff}</h3>
                        <p>Đang Hoạt Động</p>
                    </div>
                </div>
                <div class="stat-card inactive">
                    <div class="stat-icon">
                        <i class="fas fa-user-times"></i>
                    </div>
                    <div class="stat-info">
                        <h3>${inactiveStaff}</h3>
                        <p>Ngưng Hoạt Động</p>
                    </div>
                </div>
            </div>

            <!-- Messages -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> ${successMessage}
                </div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>
            
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>

            <!-- Search and Filter -->
            <div class="search-section">
                <form action="${pageContext.request.contextPath}/admin/staff" method="get" class="search-form">
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" 
                               name="search" 
                               value="${param.search}"
                               placeholder="Tìm theo tên, email, số điện thoại, phòng ban...">
                        <button type="submit" class="btn-search">Tìm Kiếm</button>
                        <c:if test="${not empty param.search}">
                            <a href="${pageContext.request.contextPath}/admin/staff" class="btn-clear">
                                <i class="fas fa-times"></i>
                            </a>
                        </c:if>
                    </div>
                </form>
            </div>

            <!-- Staff Table -->
            <div class="table-container">
                <table class="staff-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Họ Tên</th>
                            <th>Email</th>
                            <th>Phòng Ban</th>
                            <th>Trạng Thái</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty staffList}">
                                <tr>
                                    <td colspan="7" class="no-data">
                                        <i class="fas fa-inbox"></i>
                                        <p>Không tìm thấy nhân viên nào</p>
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="staff" items="${staffList}">
                                    <tr>
                                        <td>${staff.id}</td>
                                        <td>
                                            <div class="staff-name">
                                                <i class="fas fa-user-circle"></i>
                                                ${staff.fullName}
                                            </div>
                                        </td>
                                        <td>${staff.email}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty staff.department}">
                                                    ${staff.department}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">Chưa xác định</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${staff.active}">
                                                    <span class="badge badge-active">
                                                        <i class="fas fa-check-circle"></i> Hoạt động
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-inactive">
                                                        <i class="fas fa-times-circle"></i> Ngưng hoạt động
                                                    </span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <div class="action-buttons">
                                                <!-- View button always available -->
                                                <a href="${pageContext.request.contextPath}/admin/staff?action=view&id=${staff.id}" 
                                                   class="btn-action btn-view" title="Xem chi tiết">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                
                                                <!-- Edit/Activate/Deactivate ONLY if NOT ADMIN -->
                                                <c:choose>
                                                    <c:when test="${staff.admin}">
                                                        <!-- ADMIN role - show lock icon instead -->
                                                        <span class="btn-action btn-locked" title="Không thể chỉnh sửa ADMIN">
                                                            <i class="fas fa-lock"></i>
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <!-- Regular staff - show edit and activate/deactivate -->
                                                        <c:if test="${canEdit}">
                                                            <a href="${pageContext.request.contextPath}/admin/staff?action=edit&id=${staff.id}" 
                                                               class="btn-action btn-edit" title="Chỉnh sửa">
                                                                <i class="fas fa-edit"></i>
                                                            </a>
                                                        </c:if>
                                                        <c:if test="${canDelete}">
                                                            <c:choose>
                                                                <c:when test="${staff.active}">
                                                                    <form action="${pageContext.request.contextPath}/admin/staff" 
                                                                          method="post" style="display:inline;"
                                                                          onsubmit="return confirm('Bạn có chắc muốn vô hiệu hóa nhân viên này?');">
                                                                        <input type="hidden" name="action" value="deactivate">
                                                                        <input type="hidden" name="id" value="${staff.id}">
                                                                        <button type="submit" class="btn-action btn-deactivate" title="Vô hiệu hóa">
                                                                            <i class="fas fa-ban"></i>
                                                                        </button>
                                                                    </form>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <form action="${pageContext.request.contextPath}/admin/staff" 
                                                                          method="post" style="display:inline;"
                                                                          onsubmit="return confirm('Bạn có chắc muốn kích hoạt lại nhân viên này?');">
                                                                        <input type="hidden" name="action" value="activate">
                                                                        <input type="hidden" name="id" value="${staff.id}">
                                                                        <button type="submit" class="btn-action btn-activate" title="Kích hoạt">
                                                                            <i class="fas fa-check"></i>
                                                                        </button>
                                                                    </form>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </c:if>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/admin/staff?page=${currentPage - 1}<c:if test='${not empty param.search}'>&search=${param.search}</c:if>" 
                           class="page-link">
                            <i class="fas fa-chevron-left"></i> Trước
                        </a>
                    </c:if>
                    
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <span class="page-link active">${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/admin/staff?page=${i}<c:if test='${not empty param.search}'>&search=${param.search}</c:if>" 
                                   class="page-link">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/admin/staff?page=${currentPage + 1}<c:if test='${not empty param.search}'>&search=${param.search}</c:if>" 
                           class="page-link">
                            Sau <i class="fas fa-chevron-right"></i>
                        </a>
                    </c:if>
                </div>
            </c:if>
        </div>
</body>
</html>
