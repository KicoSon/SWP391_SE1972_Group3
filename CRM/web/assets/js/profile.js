// Profile Form Validation and Handling

document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form:not(#changePasswordForm)');
    const phoneInput = document.querySelector('input[name="phone"]');
    
    // Phone validation
    if (phoneInput) {
        phoneInput.addEventListener('input', function(e) {
            // Remove non-numeric characters
            this.value = this.value.replace(/[^0-9]/g, '');
            
            // Limit to 10 digits
            if (this.value.length > 10) {
                this.value = this.value.slice(0, 10);
            }
        });
    }
    
    // Form submission validation
    if (form) {
        form.addEventListener('submit', function(e) {
            const fullName = document.querySelector('input[name="fullName"]');
            const phone = document.querySelector('input[name="phone"]');
            
            // Validate full name
            if (fullName && fullName.value.trim().length < 2) {
                e.preventDefault();
                showError('Họ tên phải có ít nhất 2 ký tự');
                fullName.focus();
                return false;
            }
            
            // Validate phone (if required)
            if (phone && phone.hasAttribute('required')) {
                if (phone.value.length !== 10) {
                    e.preventDefault();
                    showError('Số điện thoại phải có đúng 10 chữ số');
                    phone.focus();
                    return false;
                }
            }
            
            // Show loading state
            const submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang cập nhật...';
            }
        });
    }
    
    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        setTimeout(function() {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s';
            setTimeout(function() {
                alert.remove();
            }, 500);
        }, 5000);
    });
    
    // Change Password Form Handling
    const changePasswordForm = document.getElementById('changePasswordForm');
    if (changePasswordForm) {
        changePasswordForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            console.log('Form submitted');
            
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            console.log('Passwords:', {
                current: currentPassword ? '***' : 'empty',
                new: newPassword ? '***' : 'empty',
                confirm: confirmPassword ? '***' : 'empty'
            });
            
            // Validate new password length
            if (newPassword.length < 6) {
                showPasswordError('Mật khẩu mới phải có ít nhất 6 ký tự');
                return false;
            }
            
            // Validate password match
            if (newPassword !== confirmPassword) {
                showPasswordError('Mật khẩu xác nhận không khớp');
                return false;
            }
            
            // Validate not same as current
            if (currentPassword === newPassword) {
                showPasswordError('Mật khẩu mới phải khác mật khẩu hiện tại');
                return false;
            }
            
            console.log('Validation passed, submitting to:', changePasswordForm.action);
            
            // Show loading state
            const submitBtn = changePasswordForm.querySelector('button[type="submit"]');
            submitBtn.disabled = true;
            submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Đang xử lý...';
            
            // Prepare form data as URL-encoded string
            const formData = new URLSearchParams();
            formData.append('currentPassword', currentPassword);
            formData.append('newPassword', newPassword);
            formData.append('confirmPassword', confirmPassword);
            
            // Debug: Log form data
            console.log('Form data to send:');
            for (let pair of formData.entries()) {
                console.log(pair[0] + ': ' + (pair[1] ? '***' : 'empty'));
            }
            
            fetch(changePasswordForm.action, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: formData.toString()
            })
            .then(response => {
                console.log('Response status:', response.status);
                console.log('Response headers:', response.headers.get('content-type'));
                
                // Check if response is JSON
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    return response.json();
                } else {
                    // Not JSON, might be error page
                    return response.text().then(text => {
                        console.error('Non-JSON response:', text);
                        throw new Error('Server không trả về dữ liệu đúng định dạng');
                    });
                }
            })
            .then(data => {
                console.log('Response data:', data);
                if (data.success) {
                    showPasswordSuccess(data.message || 'Đổi mật khẩu thành công!');
                    setTimeout(function() {
                        closeChangePasswordModal();
                        changePasswordForm.reset();
                    }, 2000);
                } else {
                    showPasswordError(data.message || 'Mật khẩu hiện tại không đúng');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showPasswordError('Có lỗi xảy ra: ' + error.message);
            })
            .finally(() => {
                submitBtn.disabled = false;
                submitBtn.innerHTML = '<i class="fas fa-check"></i> Đổi Mật Khẩu';
            });
        });
    }
});

// Modal Functions
function openChangePasswordModal() {
    const modal = document.getElementById('changePasswordModal');
    if (modal) {
        modal.classList.add('show');
        document.body.style.overflow = 'hidden';
        
        // Clear form
        const form = document.getElementById('changePasswordForm');
        if (form) form.reset();
        
        // Hide alerts
        document.getElementById('passwordError').style.display = 'none';
        document.getElementById('passwordSuccess').style.display = 'none';
        
        // Focus first input
        setTimeout(() => {
            document.getElementById('currentPassword').focus();
        }, 300);
    }
}

function closeChangePasswordModal() {
    const modal = document.getElementById('changePasswordModal');
    if (modal) {
        modal.classList.remove('show');
        document.body.style.overflow = '';
    }
}

// Close modal when clicking outside
window.addEventListener('click', function(event) {
    const modal = document.getElementById('changePasswordModal');
    if (event.target === modal) {
        closeChangePasswordModal();
    }
});

// Close modal with ESC key
window.addEventListener('keydown', function(event) {
    if (event.key === 'Escape') {
        closeChangePasswordModal();
    }
});

// Toggle Password Visibility
function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const button = input.parentElement.querySelector('.toggle-password');
    const icon = button.querySelector('i');
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

// Show error message in modal
function showPasswordError(message) {
    console.log('showPasswordError called with:', message);
    
    const modal = document.getElementById('changePasswordModal');
    console.log('Modal found:', modal);
    
    if (modal) {
        const errorDiv = modal.querySelector('#passwordError');
        const errorMsg = modal.querySelector('#passwordErrorMsg');
        const successDiv = modal.querySelector('#passwordSuccess');
        
        console.log('errorDiv:', errorDiv);
        console.log('errorMsg:', errorMsg);
        
        if (errorDiv && errorMsg) {
            errorMsg.textContent = message;
            errorDiv.style.display = 'flex';
            if (successDiv) successDiv.style.display = 'none';
            
            // Scroll to top of modal
            const modalContent = errorDiv.closest('.modal-content');
            if (modalContent) {
                modalContent.scrollTop = 0;
            }
            
            // Auto-hide after 5 seconds
            setTimeout(() => {
                errorDiv.style.display = 'none';
            }, 5000);
        } else {
            console.error('Error elements not found in modal!');
            alert(message);
        }
    } else {
        console.error('Modal not found!');
        alert(message);
    }
}

// Show success message in modal
function showPasswordSuccess(message) {
    console.log('showPasswordSuccess called with:', message);
    
    const modal = document.getElementById('changePasswordModal');
    console.log('Modal found:', modal);
    
    if (modal) {
        const successDiv = modal.querySelector('#passwordSuccess');
        const successMsg = modal.querySelector('#passwordSuccessMsg');
        const errorDiv = modal.querySelector('#passwordError');
        
        console.log('successDiv:', successDiv);
        console.log('successMsg:', successMsg);
        
        if (successDiv && successMsg) {
            successMsg.textContent = message;
            successDiv.style.display = 'flex';
            if (errorDiv) errorDiv.style.display = 'none';
            
            // Scroll to top of modal
            const modalContent = successDiv.closest('.modal-content');
            if (modalContent) {
                modalContent.scrollTop = 0;
            }
        } else {
            console.error('Success elements not found in modal!');
            alert(message);
        }
    } else {
        console.error('Modal not found!');
        alert(message);
    }
}

// Show error message
function showError(message) {
    // Remove existing error alerts
    const existingErrors = document.querySelectorAll('.alert-error');
    existingErrors.forEach(function(alert) {
        alert.remove();
    });
    
    // Create new error alert
    const alert = document.createElement('div');
    alert.className = 'alert alert-error';
    alert.innerHTML = '<i class="fas fa-exclamation-circle"></i> ' + message;
    
    // Insert at the top of profile container
    const container = document.querySelector('.profile-container');
    if (container) {
        container.insertBefore(alert, container.firstChild);
        
        // Scroll to alert
        alert.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        
        // Auto-hide after 5 seconds
        setTimeout(function() {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s';
            setTimeout(function() {
                alert.remove();
            }, 500);
        }, 5000);
    }
}
