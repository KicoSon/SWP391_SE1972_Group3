// Create Ticket Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('createTicketForm');
    const variantSelect = document.getElementById('variantId');
    const orderIdInput = document.getElementById('orderId');
    const subjectTextarea = document.getElementById('subject');
    const charCount = document.getElementById('charCount');
    
    // Character counter for textarea
    if (subjectTextarea && charCount) {
        subjectTextarea.addEventListener('input', function() {
            const current = this.value.length;
            const max = this.getAttribute('maxlength');
            charCount.textContent = current + '/' + max + ' ký tự';
            
            // Change color based on character count
            if (current > max * 0.9) {
                charCount.style.color = '#e74c3c';
            } else if (current > max * 0.7) {
                charCount.style.color = '#f39c12';
            } else {
                charCount.style.color = '#999';
            }
        });
    }
    
    // Update hidden orderId field when product is selected
    if (variantSelect && orderIdInput) {
        variantSelect.addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];
            const orderId = selectedOption.getAttribute('data-order-id');
            
            if (orderId && orderId !== '0') {
                orderIdInput.value = orderId;
            } else {
                orderIdInput.value = '0';
            }
        });
    }
    
    // Form validation
    if (form) {
        form.addEventListener('submit', function(event) {
            const subject = subjectTextarea.value.trim();
            
            if (subject.length === 0) {
                event.preventDefault();
                alert('Vui lòng nhập mô tả vấn đề!');
                subjectTextarea.focus();
                return false;
            }
            
            if (subject.length < 10) {
                event.preventDefault();
                alert('Mô tả vấn đề phải có ít nhất 10 ký tự!');
                subjectTextarea.focus();
                return false;
            }
            
            // Confirm before submitting
            if (!confirm('Bạn có chắc chắn muốn tạo phiếu hỗ trợ này?')) {
                event.preventDefault();
                return false;
            }
            
            return true;
        });
    }
    
    // Character counter for subject textarea
    if (subjectTextarea) {
        const maxLength = subjectTextarea.getAttribute('maxlength');
        
        if (maxLength) {
            const counter = document.createElement('small');
            counter.style.display = 'block';
            counter.style.textAlign = 'right';
            counter.style.marginTop = '0.25rem';
            counter.style.color = '#666';
            
            subjectTextarea.parentNode.appendChild(counter);
            
            const updateCounter = function() {
                const currentLength = subjectTextarea.value.length;
                counter.textContent = currentLength + '/' + maxLength + ' ký tự';
                
                if (currentLength > maxLength * 0.9) {
                    counter.style.color = '#dc3545';
                } else {
                    counter.style.color = '#666';
                }
            };
            
            subjectTextarea.addEventListener('input', updateCounter);
            updateCounter();
        }
    }
    
    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => {
                alert.remove();
            }, 500);
        }, 5000);
    });
});
