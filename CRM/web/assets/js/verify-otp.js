// Verify OTP JavaScript
document.addEventListener('DOMContentLoaded', function() {
    
    // Elements
    const otpInputs = document.querySelectorAll('.otp-input');
    const otpHiddenInput = document.getElementById('otp');
    const verifyForm = document.getElementById('verifyOTPForm');
    const resendForm = document.getElementById('resendForm');
    const resendBtn = document.getElementById('resendBtn');
    const countdownElement = document.getElementById('countdown');
    
    // Initialize countdown
    let timeRemaining = typeof remainingSeconds !== 'undefined' ? remainingSeconds : 300; // 5 minutes default
    let countdownInterval;
    
    // Start countdown
    startCountdown();
    
    // OTP Input functionality
    otpInputs.forEach((input, index) => {
        // Handle input
        input.addEventListener('input', function(e) {
            const value = e.target.value;
            
            // Only allow numbers
            if (!/^\d*$/.test(value)) {
                e.target.value = '';
                return;
            }
            
            // Add filled class
            if (value) {
                this.classList.add('filled');
                this.classList.remove('error');
                
                // Move to next input
                if (index < otpInputs.length - 1) {
                    otpInputs[index + 1].focus();
                }
            } else {
                this.classList.remove('filled');
            }
            
            // Update hidden input
            updateHiddenOTP();
        });
        
        // Handle keydown for backspace
        input.addEventListener('keydown', function(e) {
            if (e.key === 'Backspace' && !this.value && index > 0) {
                otpInputs[index - 1].focus();
                otpInputs[index - 1].value = '';
                otpInputs[index - 1].classList.remove('filled');
                updateHiddenOTP();
            }
        });
        
        // Handle paste
        input.addEventListener('paste', function(e) {
            e.preventDefault();
            const pastedData = e.clipboardData.getData('text');
            const digits = pastedData.replace(/\D/g, '').slice(0, 6);
            
            digits.split('').forEach((digit, i) => {
                if (i < otpInputs.length) {
                    otpInputs[i].value = digit;
                    otpInputs[i].classList.add('filled');
                }
            });
            
            updateHiddenOTP();
            
            // Focus last filled input or next empty
            const lastFilledIndex = Math.min(digits.length - 1, otpInputs.length - 1);
            otpInputs[lastFilledIndex].focus();
        });
        
        // Select all on focus
        input.addEventListener('focus', function() {
            this.select();
        });
    });
    
    // Update hidden OTP input
    function updateHiddenOTP() {
        const otp = Array.from(otpInputs).map(input => input.value).join('');
        otpHiddenInput.value = otp;
    }
    
    // Form validation
    verifyForm.addEventListener('submit', function(e) {
        const otp = otpHiddenInput.value;
        
        if (otp.length !== 6) {
            e.preventDefault();
            showError('Vui lòng nhập đầy đủ 6 chữ số OTP');
            otpInputs.forEach(input => input.classList.add('error'));
            return false;
        }
    });
    
    // Countdown timer
    function startCountdown() {
        updateCountdownDisplay();
        
        countdownInterval = setInterval(() => {
            timeRemaining--;
            
            if (timeRemaining <= 0) {
                clearInterval(countdownInterval);
                handleTimeout();
            } else {
                updateCountdownDisplay();
            }
        }, 1000);
    }
    
    function updateCountdownDisplay() {
        const minutes = Math.floor(timeRemaining / 60);
        const seconds = timeRemaining % 60;
        const display = `${minutes}:${seconds.toString().padStart(2, '0')}`;
        
        countdownElement.textContent = display;
        
        // Change color based on time remaining
        if (timeRemaining <= 30) {
            countdownElement.classList.add('danger');
            countdownElement.classList.remove('warning');
        } else if (timeRemaining <= 60) {
            countdownElement.classList.add('warning');
            countdownElement.classList.remove('danger');
        } else {
            countdownElement.classList.remove('warning', 'danger');
        }
    }
    
    function handleTimeout() {
        showError('Mã OTP đã hết hạn. Vui lòng yêu cầu gửi lại');
        otpInputs.forEach(input => {
            input.disabled = true;
            input.classList.add('error');
        });
        document.querySelector('.submit-btn').disabled = true;
    }
    
    // Resend OTP
    resendForm.addEventListener('submit', function(e) {
        // Disable button temporarily
        resendBtn.disabled = true;
        resendBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang gửi...';
    });
    
    // Show error message
    function showError(message) {
        let errorDiv = document.querySelector('.error-message');
        
        if (!errorDiv) {
            errorDiv = document.createElement('div');
            errorDiv.className = 'message error-message';
            errorDiv.innerHTML = '<i class="fas fa-exclamation-circle"></i><span></span>';
            
            const header = document.querySelector('.header');
            header.insertAdjacentElement('afterend', errorDiv);
        }
        
        errorDiv.querySelector('span').textContent = message;
        errorDiv.style.display = 'flex';
        
        // Auto hide after 5 seconds
        setTimeout(() => {
            errorDiv.style.opacity = '0';
            errorDiv.style.transition = 'opacity 0.3s ease';
            setTimeout(() => {
                errorDiv.style.display = 'none';
                errorDiv.style.opacity = '1';
            }, 300);
        }, 5000);
    }
    
    // Auto-hide existing messages
    const existingMessages = document.querySelectorAll('.message');
    existingMessages.forEach(msg => {
        setTimeout(() => {
            msg.style.opacity = '0';
            msg.style.transition = 'opacity 0.3s ease';
            setTimeout(() => {
                msg.style.display = 'none';
            }, 300);
        }, 5000);
    });
    
    // Focus first input on load
    otpInputs[0].focus();
    
    // Clear error class when user starts typing
    otpInputs.forEach(input => {
        input.addEventListener('input', function() {
            document.querySelectorAll('.otp-input').forEach(inp => {
                inp.classList.remove('error');
            });
        });
    });
});
