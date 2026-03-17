<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Xác thực OTP - SWP Support</title>

        <!-- CSS -->
        <link rel="stylesheet" href="assets/css/verify-otp.css">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <div class="verify-otp-container">
            <div class="verify-otp-box">
                <!-- Header -->
                <div class="header">
                    <div class="logo">
                        <div class="logo-icon">
                            <i class="fas fa-headset"></i>
                        </div>
                        <span>SWP Support</span>
                    </div>

                    <div class="icon-wrapper">
                        <i class="fas fa-envelope-open-text"></i>
                    </div>

                    <h2 class="title">Xác thực mã OTP</h2>
                    <p class="subtitle">
                        Mã OTP đã được gửi đến email:<br>
                        <strong>${email}</strong>
                    </p>
                </div>

                <!-- Success Message -->
                <c:if test="${not empty success}">
                    <div class="message success-message">
                        <i class="fas fa-check-circle"></i>
                        <span>${success}</span>
                    </div>
                </c:if>

                <!-- Error Message -->
                <c:if test="${not empty error}">
                    <div class="message error-message">
                        <i class="fas fa-exclamation-circle"></i>
                        <span>${error}</span>
                    </div>
                </c:if>

                <!-- Timer -->
                <div class="timer-section">
                    <div class="timer">
                        <i class="fas fa-clock"></i>
                        <span>Mã OTP có hiệu lực trong: </span>
                        <span id="countdown" class="countdown">5:00</span>
                    </div>
                </div>

                <!-- OTP Form -->
                <form id="verifyOTPForm" action="verify-otp" method="post">
                    <div class="otp-input-group">
                        <input type="text" class="otp-input" maxlength="1" data-index="0" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="1" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="2" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="3" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="4" autocomplete="off">
                        <input type="text" class="otp-input" maxlength="1" data-index="5" autocomplete="off">
                    </div>

                    <input type="hidden" id="otp" name="otp">

                    <button type="submit" class="submit-btn">
                        <i class="fas fa-check"></i>
                        Xác thực
                    </button>
                </form>

                <!-- Resend OTP -->
                <div class="resend-section">
                    <p>Không nhận được mã?</p>
                    <form id="resendForm" action="verify-otp" method="post">
                        <input type="hidden" name="action" value="resend">
                        <button type="submit" class="resend-btn" id="resendBtn">
                            <i class="fas fa-redo"></i>
                            Gửi lại mã OTP
                        </button>
                    </form>
                </div>

                <!-- Back -->
                <div class="back-link">
                    <a href="forgot-password">
                        <i class="fas fa-arrow-left"></i>
                        Thay đổi email
                    </a>
                </div>
            </div>
        </div>
        <%
        Long otpTime = (Long) session.getAttribute("otpTime");
        long now = System.currentTimeMillis();

        int remainingSeconds = 300;

        if (otpTime != null) {
            long elapsed = (now - otpTime) / 1000;
            remainingSeconds = (int)(300 - elapsed);
            if (remainingSeconds < 0) remainingSeconds = 0;
        }

        request.setAttribute("remainingSeconds", remainingSeconds);
        %>
        <script>
            // Pass remaining seconds to JavaScript
            const remainingSeconds = ${remainingSeconds != null ? remainingSeconds : 300};
        </script>
        <script src="assets/js/verify-otp.js"></script>
    </body>
</html>
