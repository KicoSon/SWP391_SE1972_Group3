<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng nhập - Hệ thống hỗ trợ khách hàng</title>

        <!-- CSS -->
        <link rel="stylesheet" href="assets/css/login.css">

        <!-- Favicon -->
        <link rel="icon" type="image/x-icon" href="assets/images/favicon.ico">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <div class="login-container">
            <!-- Phần giới thiệu bên trái -->
            <div class="intro-section">
                <div class="intro-content">
                    <div class="logo">
                        <div class="logo-icon">
                            <i class="fas fa-headset"></i>
                        </div>
                        <span>SWP Support</span>
                    </div>

                    <h1 class="intro-title">Hệ thống hỗ trợ khách hàng chuyên nghiệp</h1>

                    <p class="intro-subtitle">
                        Nền tảng quản lý hỗ trợ khách hàng toàn diện với công nghệ hiện đại, 
                        giúp doanh nghiệp nâng cao chất lượng dịch vụ và tăng sự hài lòng của khách hàng.
                    </p>

                    <ul class="features">
                        <li>Quản lý phiếu hỗ trợ thông minh</li>
                        <li>Theo dõi đơn hàng và bảo hành</li>
                        <li>Hệ thống phân quyền linh hoạt</li>
                        <li>Báo cáo và thống kê chi tiết</li>
                        <li>Giao diện thân thiện và dễ sử dụng</li>
                    </ul>
                </div>
            </div>

            <!-- Phần form login bên phải -->
            <div class="login-section">
                <div class="login-header">
                    <h2 class="login-title">Chào mừng bạn trở lại</h2>
                    <p class="login-subtitle">Đăng nhập để tiếp tục sử dụng hệ thống</p>
                </div>

                <!-- Error Message -->
                <c:if test="${not empty error}">
                    <div class="error-message">
                        <i class="fas fa-exclamation-circle"></i>
                        ${error}
                    </div>
                </c:if>

                <!-- Success Message (for reset password success) -->
                <c:if test="${not empty sessionScope.resetSuccess}">
                    <div class="success-message">
                        <i class="fas fa-check-circle"></i>
                        ${sessionScope.resetSuccess}
                    </div>
                    <c:remove var="resetSuccess" scope="session" />
                </c:if>


                <!-- User Type Toggle -->
                <div class="user-type-toggle">
                    <button type="button" class="toggle-btn ${userType == 'customer' || empty userType ? 'active' : ''}" 
                            data-type="customer">
                        <i class="fas fa-user"></i>
                        Khách hàng
                    </button>
                    <button type="button" class="toggle-btn ${userType == 'staff' ? 'active' : ''}" 
                            data-type="staff">
                        <i class="fas fa-user-tie"></i>
                        Nhân viên
                    </button>
                </div>

                <!-- Login Form -->
                <form id="loginForm" action="login" method="post" class="login-form">
                    <input type="hidden" id="userType" name="userType" value="${userType != null ? userType : 'customer'}">

                    <div class="form-group">
                        <input type="email" 
                               id="email" 
                               name="email" 
                               class="form-input" 
                               placeholder="Email khách hàng"
                               value="${email != null ? email : ''}"
                               required>
                    </div>

                    <div class="form-group">
                        <input type="password" 
                               id="password" 
                               name="password" 
                               class="form-input" 
                               placeholder="Mật khẩu"
                               required>
                    </div>

                    <div class="form-group remember-me">
                        <label class="remember-checkbox">
                            <input type="checkbox" 
                                   id="rememberMe" 
                                   name="rememberMe" 
                                   value="true"
                                   ${rememberMe ? 'checked' : ''}>
                            <span class="checkmark"></span>
                            <span class="remember-text">Ghi nhớ đăng nhập</span>
                        </label>
                    </div>

                    <button type="submit" id="loginBtn" class="login-btn">
                        <i class="fas fa-sign-in-alt"></i>
                        Đăng nhập
                    </button>
                </form>

                <div class="forgot-password">
                    <a href="forgot-password">
                        <i class="fas fa-key"></i>
                        Quên mật khẩu?
                    </a>
                </div>
            </div>
        </div>

        <script src="assets/js/login.js"></script>

    </body>
</html>

