// Login Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    
    // Elements
    const toggleBtns = document.querySelectorAll('.toggle-btn');
    const userTypeInput = document.getElementById('userType');
    const loginForm = document.getElementById('loginForm');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const loginBtn = document.getElementById('loginBtn');
    const errorMessage = document.querySelector('.error-message');
    
    // User type toggle functionality
    toggleBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            // Remove active class from all buttons
            toggleBtns.forEach(b => b.classList.remove('active'));
            
            // Add active class to clicked button
            this.classList.add('active');
            
            // Update hidden input value
            const userType = this.getAttribute('data-type');
            userTypeInput.value = userType;
            
            // Update placeholder text based on user type
            updatePlaceholders(userType);
            
            // Clear form and errors
            clearForm();
        });
    });
    
    // Update placeholder text based on user type
    function updatePlaceholders(userType) {
        if (userType === 'customer') {
            emailInput.placeholder = 'Email khách hàng';
            document.querySelector('.login-subtitle').textContent = 'Đăng nhập để theo dõi đơn hàng và tạo phiếu hỗ trợ';
        } else {
            emailInput.placeholder = 'Email nhân viên';
            document.querySelector('.login-subtitle').textContent = 'Đăng nhập vào hệ thống quản lý';
        }
    }
    
    // Form validation
    function validateForm() {
        let isValid = true;
        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();
        
        // Clear previous error states
        clearErrors();
        
        // Email validation
        if (!email) {
            showFieldError(emailInput, 'Vui lòng nhập email');
            isValid = false;
        } else if (!isValidEmail(email)) {
            showFieldError(emailInput, 'Email không hợp lệ');
            isValid = false;
        }
        
        // Password validation
        if (!password) {
            showFieldError(passwordInput, 'Vui lòng nhập mật khẩu');
            isValid = false;
        } else if (password.length < 6) {
            showFieldError(passwordInput, 'Mật khẩu phải có ít nhất 6 ký tự');
            isValid = false;
        }
        
        return isValid;
    }
    
    // Email validation helper
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    
    // Show field error
    function showFieldError(field, message) {
        field.style.borderColor = '#e53e3e';
        field.style.background = '#fed7d7';
        
        // Create error message element if not exists
        let errorElement = field.parentNode.querySelector('.field-error');
        if (!errorElement) {
            errorElement = document.createElement('div');
            errorElement.className = 'field-error';
            errorElement.style.cssText = `
                color: #e53e3e;
                font-size: 0.8rem;
                margin-top: 5px;
                display: flex;
                align-items: center;
            `;
            field.parentNode.appendChild(errorElement);
        }
        errorElement.textContent = message;
    }
    
    // Clear field errors
    function clearErrors() {
        const inputs = [emailInput, passwordInput];
        inputs.forEach(input => {
            input.style.borderColor = '#e1e5e9';
            input.style.background = '#fafbfc';
            
            const errorElement = input.parentNode.querySelector('.field-error');
            if (errorElement) {
                errorElement.remove();
            }
        });
    }
    
    // Clear form
    function clearForm() {
        emailInput.value = '';
        passwordInput.value = '';
        clearErrors();
        hideErrorMessage();
    }
    
    // Hide error message
    function hideErrorMessage() {
        if (errorMessage) {
            errorMessage.style.display = 'none';
        }
    }
    
    // Show loading state
    function showLoading() {
        loginBtn.disabled = true;
        loginBtn.innerHTML = `
            <span class="loading">Đang đăng nhập...</span>
        `;
        loginBtn.style.opacity = '0.8';
    }
    
    // Hide loading state
    function hideLoading() {
        loginBtn.disabled = false;
        loginBtn.innerHTML = 'Đăng nhập';
        loginBtn.style.opacity = '1';
    }
    
    // Form submission
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        if (validateForm()) {
            showLoading();
            
            // Simulate network delay for better UX
            setTimeout(() => {
                // Submit form normally
                this.submit();
            }, 500);
        }
    });
    
    // Input focus effects
    const inputs = [emailInput, passwordInput];
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentNode.classList.add('focused');
            clearErrors();
        });
        
        input.addEventListener('blur', function() {
            this.parentNode.classList.remove('focused');
        });
        
        input.addEventListener('input', function() {
            clearErrors();
            hideErrorMessage();
        });
    });
    
    // Enter key handling
    inputs.forEach(input => {
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                loginForm.dispatchEvent(new Event('submit'));
            }
        });
    });
    
    // Auto-hide error message after 5 seconds
    if (errorMessage && errorMessage.style.display !== 'none') {
        setTimeout(() => {
            errorMessage.style.opacity = '0';
            setTimeout(() => {
                errorMessage.style.display = 'none';
            }, 300);
        }, 5000);
    }
    
    // Initialize form state
    const initialUserType = userTypeInput.value || 'customer';
    document.querySelector(`[data-type="${initialUserType}"]`).classList.add('active');
    updatePlaceholders(initialUserType);
    
    // Add floating label effect
    inputs.forEach(input => {
        if (input.value) {
            input.parentNode.classList.add('has-value');
        }
        
        input.addEventListener('input', function() {
            if (this.value) {
                this.parentNode.classList.add('has-value');
            } else {
                this.parentNode.classList.remove('has-value');
            }
        });
    });
    
    // Remember last selected user type
    const savedUserType = localStorage.getItem('lastUserType');
    if (savedUserType) {
        const savedBtn = document.querySelector(`[data-type="${savedUserType}"]`);
        if (savedBtn) {
            toggleBtns.forEach(b => b.classList.remove('active'));
            savedBtn.classList.add('active');
            userTypeInput.value = savedUserType;
            updatePlaceholders(savedUserType);
        }
    }
    
    // Save user type on toggle
    toggleBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            localStorage.setItem('lastUserType', this.getAttribute('data-type'));
        });
    });
    
    // Add smooth transitions
    const style = document.createElement('style');
    style.textContent = `
        .form-group.focused .form-input {
            border-color: #4facfe !important;
            background: white !important;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(79, 172, 254, 0.15);
        }
        
        .form-group.has-value .form-input {
            background: white;
        }
        
        .field-error {
            animation: slideDown 0.3s ease;
        }
        
        @keyframes slideDown {
            from {
                opacity: 0;
                transform: translateY(-10px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }
    `;
    document.head.appendChild(style);
});