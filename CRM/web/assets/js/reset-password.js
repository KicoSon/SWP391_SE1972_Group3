// Reset Password JavaScript
document.addEventListener('DOMContentLoaded', function() {
    
    // Elements
    const resetForm = document.getElementById('resetPasswordForm');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const submitBtn = document.getElementById('submitBtn');
    const togglePasswordBtns = document.querySelectorAll('.toggle-password');
    
    // Password strength elements
    const passwordStrength = document.getElementById('passwordStrength');
    const strengthFill = passwordStrength.querySelector('.strength-fill');
    const strengthText = passwordStrength.querySelector('.strength-text');
    
    // Requirements elements
    const reqLength = document.getElementById('req-length');
    const reqMatch = document.getElementById('req-match');
    
    // Match indicator
    const matchIndicator = document.getElementById('matchIndicator');
    
    // Toggle password visibility
    togglePasswordBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target');
            const targetInput = document.getElementById(targetId);
            const icon = this.querySelector('i');
            
            if (targetInput.type === 'password') {
                targetInput.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                targetInput.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });
    });
    
    // Check password strength
    newPasswordInput.addEventListener('input', function() {
        const password = this.value;
        const strength = calculatePasswordStrength(password);
        
        // Update strength bar
        strengthFill.className = 'strength-fill';
        strengthText.className = 'strength-text';
        
        if (password.length === 0) {
            strengthFill.style.width = '0';
            strengthText.textContent = '';
        } else if (strength <= 2) {
            strengthFill.classList.add('weak');
            strengthText.classList.add('weak');
            strengthText.textContent = 'Yếu';
        } else if (strength <= 3) {
            strengthFill.classList.add('medium');
            strengthText.classList.add('medium');
            strengthText.textContent = 'Trung bình';
        } else {
            strengthFill.classList.add('strong');
            strengthText.classList.add('strong');
            strengthText.textContent = 'Mạnh';
        }
        
        // Check length requirement
        if (password.length >= 6) {
            reqLength.classList.add('met');
            newPasswordInput.classList.remove('error');
        } else {
            reqLength.classList.remove('met');
        }
        
        // Check if passwords match (if confirm is filled)
        checkPasswordMatch();
        updateSubmitButton();
    });
    
    // Check password match
    confirmPasswordInput.addEventListener('input', function() {
        checkPasswordMatch();
        updateSubmitButton();
    });
    
    function checkPasswordMatch() {
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        
        if (confirmPassword.length === 0) {
            matchIndicator.textContent = '';
            matchIndicator.className = 'match-indicator';
            reqMatch.classList.remove('met');
            confirmPasswordInput.classList.remove('success', 'error');
            return;
        }
        
        if (newPassword === confirmPassword && newPassword.length > 0) {
            matchIndicator.textContent = '✓ Mật khẩu khớp';
            matchIndicator.classList.add('match');
            matchIndicator.classList.remove('no-match');
            reqMatch.classList.add('met');
            confirmPasswordInput.classList.add('success');
            confirmPasswordInput.classList.remove('error');
        } else {
            matchIndicator.textContent = '✗ Mật khẩu không khớp';
            matchIndicator.classList.add('no-match');
            matchIndicator.classList.remove('match');
            reqMatch.classList.remove('met');
            confirmPasswordInput.classList.add('error');
            confirmPasswordInput.classList.remove('success');
        }
    }
    
    // Calculate password strength
    function calculatePasswordStrength(password) {
        let strength = 0;
        
        if (password.length >= 6) strength++;
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
        if (/\d/.test(password)) strength++;
        if (/[^a-zA-Z0-9]/.test(password)) strength++;
        
        return strength;
    }
    
    // Update submit button state
    function updateSubmitButton() {
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        
        const isValid = newPassword.length >= 6 && 
                       newPassword === confirmPassword;
        
        submitBtn.disabled = !isValid;
    }
    
    // Form validation
    resetForm.addEventListener('submit', function(e) {
        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        
        // Clear previous errors
        clearErrors();
        
        if (newPassword.length < 6) {
            e.preventDefault();
            showError('Mật khẩu phải có ít nhất 6 ký tự');
            newPasswordInput.classList.add('error');
            newPasswordInput.focus();
            return false;
        }
        
        if (newPassword !== confirmPassword) {
            e.preventDefault();
            showError('Mật khẩu xác nhận không khớp');
            confirmPasswordInput.classList.add('error');
            confirmPasswordInput.focus();
            return false;
        }
        
        // Show loading state
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
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
    
    // Clear errors
    function clearErrors() {
        newPasswordInput.classList.remove('error');
        confirmPasswordInput.classList.remove('error');
    }
    
    // Auto-hide existing error messages
    const existingError = document.querySelector('.error-message');
    if (existingError) {
        setTimeout(() => {
            existingError.style.opacity = '0';
            existingError.style.transition = 'opacity 0.3s ease';
            setTimeout(() => {
                existingError.style.display = 'none';
            }, 300);
        }, 5000);
    }
    
    // Focus new password input on load
    newPasswordInput.focus();
    
    // Initial button state
    updateSubmitButton();
});
