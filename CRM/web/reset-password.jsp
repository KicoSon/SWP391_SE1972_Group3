<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt lại mật khẩu - SWP Support</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="assets/css/reset-password.css">
    
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="reset-password-container">
        <div class="reset-password-box">
            <!-- Header -->
            <div class="header">
                <div class="logo">
                    <div class="logo-icon">
                        <i class="fas fa-headset"></i>
                    </div>
                    <span>SWP Support</span>
                </div>
                
                <div class="icon-wrapper">
                    <i class="fas fa-key"></i>
                </div>
                
                <h2 class="title">Đặt lại mật khẩu</h2>
                <p class="subtitle">Nhập mật khẩu mới cho tài khoản của bạn</p>
            </div>
            
            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="message error-message">
                    <i class="fas fa-exclamation-circle"></i>
                    <span>${error}</span>
                </div>
            </c:if>
            
            <!-- Form -->
            <form id="resetPasswordForm" action="reset-password" method="post">
                <div class="form-group">
                    <label for="newPassword">
                        <i class="fas fa-lock"></i>
                        Mật khẩu mới
                    </label>
                    <div class="password-input-wrapper">
                        <input type="password" 
                               id="newPassword" 
                               name="newPassword" 
                               class="form-input" 
                               placeholder="Nhập mật khẩu mới"
                               minlength="6"
                               required>
                        <button type="button" class="toggle-password" data-target="newPassword">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                    <div class="password-strength" id="passwordStrength">
                        <div class="strength-bar">
                            <div class="strength-fill"></div>
                        </div>
                        <span class="strength-text"></span>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="confirmPassword">
                        <i class="fas fa-lock"></i>
                        Xác nhận mật khẩu
                    </label>
                    <div class="password-input-wrapper">
                        <input type="password" 
                               id="confirmPassword" 
                               name="confirmPassword" 
                               class="form-input" 
                               placeholder="Nhập lại mật khẩu mới"
                               minlength="6"
                               required>
                        <button type="button" class="toggle-password" data-target="confirmPassword">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                    <span class="match-indicator" id="matchIndicator"></span>
                </div>
                
                <!-- Password Requirements -->
                <div class="password-requirements">
                    <p class="requirements-title">
                        <i class="fas fa-info-circle"></i>
                        Yêu cầu mật khẩu:
                    </p>
                    <ul>
                        <li id="req-length">
                            <i class="fas fa-circle"></i>
                            Tối thiểu 6 ký tự
                        </li>
                        <li id="req-match">
                            <i class="fas fa-circle"></i>
                            Mật khẩu xác nhận khớp
                        </li>
                    </ul>
                </div>
                
                <button type="submit" class="submit-btn" id="submitBtn">
                    <i class="fas fa-check"></i>
                    Đặt lại mật khẩu
                </button>
            </form>
            
            <!-- Security Note -->
            <div class="security-note">
                <i class="fas fa-shield-alt"></i>
                <p>Mật khẩu của bạn sẽ được mã hóa và bảo mật</p>
            </div>
        </div>
    </div>
    
    <script src="assets/js/reset-password.js"></script>
</body>
</html>
