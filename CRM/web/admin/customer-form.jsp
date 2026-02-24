<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${customer != null ? 'Chỉnh sửa' : 'Thêm'} Khách hàng - Admin</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/admin/assets/css/customer-form.css">
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/components/sidebar.jsp" />

    <div class="main-content">
        <div class="container">
            <!-- Breadcrumb -->
            <div class="breadcrumb">
                <a href="${pageContext.request.contextPath}/admin/dashboard">
                    <i class="fas fa-home"></i> Dashboard
                </a>
                <i class="fas fa-chevron-right"></i>
                <a href="${pageContext.request.contextPath}/managecustomer">
                    <i class="fas fa-users"></i> Quản lý Khách hàng
                </a>
                <i class="fas fa-chevron-right"></i>
                <span>${customer != null ? 'Chỉnh sửa' : 'Thêm mới'}</span>
            </div>

            <!-- Page Header -->
            <div class="page-header">
                <h1>
                    <i class="fas ${customer != null ? 'fa-edit' : 'fa-plus-circle'}"></i>
                    ${customer != null ? 'Chỉnh sửa' : 'Thêm mới'} Khách hàng
                </h1>
            </div>

            <!-- Error Messages -->
            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i>
                    ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session" />
            </c:if>

            <!-- Form Card -->
            <div class="form-card">
                <form action="${pageContext.request.contextPath}/managecustomer" method="POST" id="customerForm">
                    <input type="hidden" name="action" value="${customer != null ? 'edit' : 'add'}">
                    <c:if test="${customer != null}">
                        <input type="hidden" name="id" value="${customer.id}">
                    </c:if>

                    <!-- Personal Information Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <i class="fas fa-user"></i>
                            <h2>Thông tin cá nhân</h2>
                        </div>

                        <div class="form-grid">
                            <div class="form-group">
                                <label for="fullName">
                                    Họ và tên <span class="required">*</span>
                                </label>
                                <div class="input-with-icon">
                                    <i class="fas fa-user"></i>
                                    <input type="text" 
                                           id="fullName" 
                                           name="fullName" 
                                           value="${customer != null ? customer.fullName : ''}" 
                                           required
                                           placeholder="Nhập họ và tên">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="email">
                                    Email <span class="required">*</span>
                                </label>
                                <div class="input-with-icon">
                                    <i class="fas fa-envelope"></i>
                                    <input type="email" 
                                           id="email" 
                                           name="email" 
                                           value="${customer != null ? customer.email : ''}" 
                                           required
                                           placeholder="Nhập email">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="phone">
                                    Số điện thoại <span class="required">*</span>
                                </label>
                                <div class="input-with-icon">
                                    <i class="fas fa-phone"></i>
                                    <input type="tel" 
                                           id="phone" 
                                           name="phone" 
                                           value="${customer != null ? customer.phone : ''}" 
                                           required
                                           placeholder="Nhập số điện thoại"
                                           pattern="[0-9]{10,11}">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="password">
                                    Mật khẩu ${customer != null ? '(để trống nếu không đổi)' : ''} <c:if test="${customer == null}"><span class="required">*</span></c:if>
                                </label>
                                <div class="input-with-icon">
                                    <i class="fas fa-lock"></i>
                                    <input type="password" 
                                           id="password" 
                                           name="password" 
                                           ${customer == null ? 'required' : ''}
                                           placeholder="Nhập mật khẩu"
                                           minlength="6">
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Address Information Section -->
                    <div class="form-section">
                        <div class="section-header">
                            <i class="fas fa-map-marker-alt"></i>
                            <h2>Thông tin địa chỉ</h2>
                        </div>

                        <div class="form-grid">
                            <div class="form-group full-width">
                                <label for="address">
                                    Địa chỉ <span class="required">*</span>
                                </label>
                                <div class="input-with-icon">
                                    <i class="fas fa-home"></i>
                                    <input type="text" 
                                           id="address" 
                                           name="address" 
                                           value="${customer != null ? customer.address : ''}" 
                                           required
                                           placeholder="Nhập địa chỉ chi tiết">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="city">
                                    Thành phố <span class="required">*</span>
                                </label>
                                <div class="input-with-icon">
                                    <i class="fas fa-city"></i>
                                    <input type="text" 
                                           id="city" 
                                           name="city" 
                                           value="${customer != null ? customer.city : ''}" 
                                           required
                                           placeholder="Nhập thành phố">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="province">
                                    Tỉnh/Thành phố <span class="required">*</span>
                                </label>
                                <div class="input-with-icon">
                                    <i class="fas fa-map"></i>
                                    <input type="text" 
                                           id="province" 
                                           name="province" 
                                           value="${customer != null ? customer.province : ''}" 
                                           required
                                           placeholder="Nhập tỉnh/thành phố">
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Account Status Section (Edit only) -->
                    <c:if test="${customer != null}">
                        <div class="form-section">
                            <div class="section-header">
                                <i class="fas fa-toggle-on"></i>
                                <h2>Trạng thái tài khoản</h2>
                            </div>

                            <div class="form-group">
                                <label class="checkbox-label">
                                    <input type="checkbox" 
                                           name="isActive" 
                                           value="true" 
                                           ${customer.active ? 'checked' : ''}>
                                    <span>Tài khoản đang hoạt động</span>
                                </label>
                            </div>
                        </div>
                    </c:if>

                    <!-- Form Actions -->
                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/managecustomer" class="btn btn-cancel">
                            <i class="fas fa-times"></i>
                            Hủy bỏ
                        </a>
                        <button type="submit" class="btn btn-submit">
                            <i class="fas fa-save"></i>
                            ${customer != null ? 'Cập nhật' : 'Thêm mới'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Form validation
        document.getElementById('customerForm').addEventListener('submit', function(e) {
            const phone = document.getElementById('phone').value;
            const phonePattern = /^[0-9]{10,11}$/;
            
            if (!phonePattern.test(phone)) {
                e.preventDefault();
                alert('Số điện thoại phải có 10-11 chữ số');
                return false;
            }

            const password = document.getElementById('password').value;
            if (password && password.length < 6) {
                e.preventDefault();
                alert('Mật khẩu phải có ít nhất 6 ký tự');
                return false;
            }
        });
    </script>
</body>
</html>
