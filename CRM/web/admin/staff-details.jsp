<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi Tiết Nhân Viên</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-details.css">
    </head>
    <body>
        <div class="staff-details-container">
            <jsp:include page="sidebar.jsp" />

            <div class="staff-details-content">
                <div class="page-header">
                    <div class="header-left">
                        <a href="${pageContext.request.contextPath}/admin/staff" class="btn-back">
                            <i class="fas fa-arrow-left"></i> Quay Lại
                        </a>
                        <div>
                            <h1>Chi Tiết Nhân Viên</h1>
                            <p>Thông tin đầy đủ về nhân viên</p>
                        </div>
                    </div>
                    <div class="header-actions">
                        <c:choose>
                            <c:when test="${hasAdminRole}">
                                <!-- ADMIN role - show locked message -->
                                <div class="admin-lock-notice">
                                    <i class="fas fa-shield-alt"></i>
                                    <span>Tài khoản ADMIN - Không thể chỉnh sửa</span>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- Regular staff - show edit and activate/deactivate -->
                                <a href="${pageContext.request.contextPath}/admin/staff?action=edit&id=${staff.id}" 
                                   class="btn-edit">
                                    <i class="fas fa-edit"></i> Chỉnh Sửa
                                </a>
                                <c:choose>
                                    <c:when test="${staff.active}">
                                        <form action="${pageContext.request.contextPath}/admin/staff" 
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('Bạn có chắc muốn vô hiệu hóa nhân viên này?');">
                                            <input type="hidden" name="action" value="deactivate">
                                            <input type="hidden" name="id" value="${staff.id}">
                                            <button type="submit" class="btn-deactivate">
                                                <i class="fas fa-ban"></i> Vô Hiệu Hóa
                                            </button>
                                        </form>
                                    </c:when>
                                    <c:otherwise>
                                        <form action="${pageContext.request.contextPath}/admin/staff" 
                                              method="post" style="display:inline;"
                                              onsubmit="return confirm('Bạn có chắc muốn kích hoạt lại nhân viên này?');">
                                            <input type="hidden" name="action" value="activate">
                                            <input type="hidden" name="id" value="${staff.id}">
                                            <button type="submit" class="btn-activate">
                                                <i class="fas fa-check"></i> Kích Hoạt
                                            </button>
                                        </form>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Staff Information Card -->
                <div class="info-section">
                    <div class="info-card">
                        <div class="card-header">
                            <i class="fas fa-user"></i>
                            <h2>Thông Tin Cá Nhân</h2>
                        </div>
                        <div class="card-body">
                            <div class="info-grid">
                                <div class="info-item">
                                    <label><i class="fas fa-id-badge"></i> Mã NV:</label>
                                    <span>#${staff.id}</span>
                                </div>
                                <div class="info-item">
                                    <label><i class="fas fa-user-circle"></i> Họ Tên:</label>
                                    <span>${staff.fullName}</span>
                                </div>
                                <div class="info-item">
                                    <label><i class="fas fa-envelope"></i> Email:</label>
                                    <span>${staff.email}</span>
                                </div>
                                <div class="info-item">
                                    <label><i class="fas fa-building"></i> Phòng Ban:</label>
                                    <span>
                                        <c:choose>
                                            <c:when test="${not empty staff.department}">
                                                ${staff.department}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa xác định</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                                <div class="info-item">
                                    <label><i class="fas fa-toggle-on"></i> Trạng Thái:</label>
                                    <span>
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
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
