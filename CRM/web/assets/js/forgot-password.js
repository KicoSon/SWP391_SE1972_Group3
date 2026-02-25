// Forgot Password JavaScript
document.addEventListener('DOMContentLoaded', function() {
    
    // Elements
    const toggleBtns = document.querySelectorAll('.toggle-btn');
    const userTypeInput = document.getElementById('userType');
    const forgotPasswordForm = document.getElementById('forgotPasswordForm');
    const emailInput = document.getElementById('email');
    
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
            
            // Clear error message
            const errorMessage = document.querySelector('.error-message');
            if (errorMessage) {
                errorMessage.style.display = 'none';
            }
        });
    });
    
    // Form validation
    forgotPasswordForm.addEventListener('submit', function(e) {
        const email = emailInput.value.trim();
        
        if (!email) {
            e.preventDefault();
            showError('Vui lòng nhập địa chỉ email');
            return false;
        }
        
        if (!isValidEmail(email)) {
            e.preventDefault();
            showError('Địa chỉ email không hợp lệ');
            return false;
        }
    });
    
    // Email validation
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
    
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
            errorDiv.style.display = 'none';
        }, 5000);
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
    
    // Focus email input on load
    emailInput.focus();
});
