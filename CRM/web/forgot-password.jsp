<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên mật khẩu - SWP Support</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="assets/css/forgot-password.css">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="forgot-password-container">
        <div class="forgot-password-box">
            <!-- Header -->
            <div class="header">
                <div class="logo">
                    <div class="logo-icon">
                        <i class="fas fa-headset"></i>
                    </div>
                    <span>SWP Support</span>
                </div>
                
                <h2 class="title">Quên mật khẩu?</h2>
                <p class="subtitle">Nhập email của bạn để nhận mã xác thực</p>
            </div>
            
            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="message error-message">
                    <i class="fas fa-exclamation-circle"></i>
                    <span>${error}</span>
                </div>
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
            
            <!-- Form -->
            <form id="forgotPasswordForm" action="forgot-password" method="post">
                <input type="hidden" id="userType" name="userType" value="${userType != null ? userType : 'customer'}">
                
                <div class="form-group">
                    <label for="email">
                        <i class="fas fa-envelope"></i>
                        Địa chỉ Email
                    </label>
                    <input type="email" 
                           id="email" 
                           name="email" 
                           class="form-input" 
                           placeholder="Nhập email của bạn"
                           value="${email != null ? email : ''}"
                           required>
                    <span class="input-hint">Chúng tôi sẽ gửi mã OTP đến email này</span>
                </div>
                
                <button type="submit" class="submit-btn">
                    <i class="fas fa-paper-plane"></i>
                    Gửi mã OTP
                </button>
            </form>
            
            <!-- Back to Login -->
            <div class="back-to-login">
                <a href="login">
                    <i class="fas fa-arrow-left"></i>
                    Quay lại đăng nhập
                </a>
            </div>
        </div>
    </div>
    
    <script src="assets/js/forgot-password.js"></script>
</body>
</html>
