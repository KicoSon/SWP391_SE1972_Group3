/**
 * Customer Details Management
 * Handles customer details page interactions, modals, and actions
 */

class CustomerDetailsManager {
    constructor() {
        this.customerId = this.getCustomerId();
        this.contextPath = this.getContextPath();
        this.pendingAction = null;
        this.refreshInterval = null;
        this.autoRefreshInterval = null;
        this.autoRefreshVisibilityHandler = null;
        this.init();
    }

    /**
     * Initialize the customer details manager
     */
    init() {
        this.bindEvents();
        this.initializeModals();
        this.setupRefreshHandlers();
    }

    /**
     * Get customer ID from URL
     */
    getCustomerId() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id');
    }

    /**
     * Get context path
     */
    getContextPath() {
        const path = window.location.pathname;
        const contextPath = path.substring(0, path.indexOf('/', 1));
        return contextPath || '';
    }

    /**
     * Bind event listeners
     */
    bindEvents() {
        // Email form submission
        const emailForm = document.getElementById('emailForm');
        if (emailForm) {
            emailForm.addEventListener('submit', (e) => {
                e.preventDefault();
                this.handleEmailSend(e);
            });
        }

        // Modal close events
        this.bindModalEvents();
        
        // Copy to clipboard for contact info
        this.setupCopyToClipboard();
        
        // Auto-refresh activities
        this.setupAutoRefresh();

        // Toggle status buttons
        const toggleButtons = document.querySelectorAll('.toggle-status-btn');
        toggleButtons.forEach(button => {
            button.addEventListener('click', () => {
                const customerId = button.dataset.customerId;
                const targetStatus = button.dataset.targetStatus === 'true';
                this.toggleCustomerStatus(customerId, targetStatus);
            });
        });
    }

    /**
     * Initialize modals
     */
    initializeModals() {
        const modals = document.querySelectorAll('.modal');
        modals.forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.closeModal(modal);
                }
            });
        });
    }

    /**
     * Bind modal events
     */
    bindModalEvents() {
        // ESC key to close modals
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                const openModal = document.querySelector('.modal[style*="block"]');
                if (openModal) {
                    this.closeModal(openModal);
                }
            }
        });
    }

    /**
     * Toggle customer status
     */
    async toggleCustomerStatus(customerId, newStatus) {
        const statusText = newStatus ? 'kích hoạt' : 'tạm khóa';
        const confirmMessage = `Bạn có chắc chắn muốn ${statusText} tài khoản khách hàng này?`;
        
        this.showConfirmModal(
            'Xác nhận thay đổi trạng thái',
            confirmMessage,
            () => this.performStatusToggle(customerId, newStatus)
        );
    }

    /**
     * Perform status toggle
     */
    async performStatusToggle(customerId, newStatus) {
        try {
            const response = await fetch(`${this.contextPath}/support/agent/customers/edit`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    id: customerId,
                    action: newStatus ? 'restore' : 'delete'
                })
            });

            const result = await response.json().catch(() => null);

            if (response.ok && result && result.success) {
                this.showAlert('success', result.message || `${newStatus ? 'Kích hoạt' : 'Tạm khóa'} tài khoản thành công`);
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                const errorMessage = result && result.message ? result.message : 'Đã xảy ra lỗi khi thay đổi trạng thái';
                throw new Error(errorMessage);
            }
        } catch (error) {
            console.error('Error toggling customer status:', error);
            this.showAlert('error', error.message || 'Đã xảy ra lỗi khi thay đổi trạng thái');
        }
    }

    /**
     * Show email modal
     */
    showEmailModal() {
        const modal = document.getElementById('emailModal');
        if (modal) {
            modal.style.display = 'block';
            
            // Focus subject input
            setTimeout(() => {
                const subjectInput = modal.querySelector('#emailSubject');
                if (subjectInput) {
                    subjectInput.focus();
                }
            }, 100);
        }
    }

    /**
     * Close email modal
     */
    closeEmailModal() {
        const modal = document.getElementById('emailModal');
        if (modal) {
            modal.style.display = 'none';
            
            // Reset form
            const form = modal.querySelector('form');
            if (form) {
                form.reset();
                
                // Set recipient email back
                const emailTo = modal.querySelector('#emailTo');
                const customerEmail = document.querySelector('.customer-email')?.textContent;
                if (emailTo && customerEmail) {
                    emailTo.value = customerEmail;
                }
            }
        }
    }

    /**
     * Handle email send
     */
    async handleEmailSend(event) {
        event.preventDefault();
        
        const form = event.target;
        const submitBtn = form.querySelector('button[type="submit"]');
        const subject = form.querySelector('#emailSubject').value.trim();
        const content = form.querySelector('#emailContent').value.trim();
        
        // Validate required fields
        if (!subject || !content) {
            this.showAlert('error', 'Vui lòng điền đầy đủ tiêu đề và nội dung email');
            return;
        }

        // Set loading state
        this.setButtonLoading(submitBtn, true);

        try {
            // Simulate email sending (replace with actual API call)
            await this.simulateEmailSend({
                to: form.querySelector('#emailTo').value,
                subject: subject,
                content: content
            });

            this.closeEmailModal();
            this.showAlert('success', 'Gửi email thành công!');
            
        } catch (error) {
            console.error('Error sending email:', error);
            this.showAlert('error', 'Đã xảy ra lỗi khi gửi email');
        } finally {
            this.setButtonLoading(submitBtn, false);
        }
    }

    /**
     * Simulate email sending (replace with actual implementation)
     */
    async simulateEmailSend(emailData) {
        return new Promise((resolve) => {
            setTimeout(() => {
                console.log('Email sent:', emailData);
                resolve();
            }, 2000);
        });
    }

    /**
     * Show confirmation modal
     */
    showConfirmModal(title, message, onConfirm, type = 'primary') {
        const modal = document.getElementById('confirmModal');
        const titleElement = document.getElementById('confirmTitle');
        const messageElement = document.getElementById('confirmMessage');
        const confirmButton = document.getElementById('confirmButton');

        if (modal && titleElement && messageElement && confirmButton) {
            titleElement.textContent = title;
            messageElement.textContent = message;
            
            // Set button style
            confirmButton.className = `btn btn-${type}`;
            
            // Store confirm action
            this.pendingAction = onConfirm;
            
            modal.style.display = 'block';
        }
    }

    /**
     * Close confirmation modal
     */
    closeConfirmModal() {
        const modal = document.getElementById('confirmModal');
        if (modal) {
            modal.style.display = 'none';
            this.pendingAction = null;
        }
    }

    /**
     * Confirm pending action
     */
    confirmAction() {
        if (this.pendingAction) {
            this.pendingAction();
            this.closeConfirmModal();
        }
    }

    /**
     * Close modal
     */
    closeModal(modal) {
        modal.style.display = 'none';
        
        // Reset any forms in the modal
        const forms = modal.querySelectorAll('form');
        forms.forEach(form => {
            form.reset();
        });
    }

    /**
     * Set button loading state
     */
    setButtonLoading(button, loading) {
        if (!button) return;
        
        if (loading) {
            button.classList.add('loading');
            button.disabled = true;
        } else {
            button.classList.remove('loading');
            button.disabled = false;
        }
    }

    /**
     * Setup copy to clipboard functionality
     */
    setupCopyToClipboard() {
        // Add click handlers to email and phone links
        const emailLink = document.querySelector('.email-link');
        const phoneLink = document.querySelector('.phone-link');
        
        if (emailLink) {
            emailLink.addEventListener('click', (e) => {
                e.preventDefault();
                this.copyToClipboard(emailLink.textContent, 'Email đã được sao chép');
            });
        }
        
        if (phoneLink) {
            phoneLink.addEventListener('click', (e) => {
                e.preventDefault();
                this.copyToClipboard(phoneLink.textContent, 'Số điện thoại đã được sao chép');
            });
        }
    }

    /**
     * Copy text to clipboard
     */
    async copyToClipboard(text, message) {
        try {
            await navigator.clipboard.writeText(text);
            this.showAlert('success', message);
        } catch (error) {
            console.error('Failed to copy to clipboard:', error);
            
            // Fallback method
            const textArea = document.createElement('textarea');
            textArea.value = text;
            document.body.appendChild(textArea);
            textArea.select();
            
            try {
                document.execCommand('copy');
                this.showAlert('success', message);
            } catch (fallbackError) {
                this.showAlert('error', 'Không thể sao chép');
            }
            
            document.body.removeChild(textArea);
        }
    }

    /**
     * Setup refresh handlers
     */
    setupRefreshHandlers() {
        const autoRefreshEnabled = document.body.dataset.autoRefresh === 'true';

        if (autoRefreshEnabled) {
            // Auto-refresh page data every 5 minutes when enabled
            this.refreshInterval = setInterval(() => {
                if (!document.hidden) {
                    this.refreshPageData();
                }
            }, 5 * 60 * 1000);
        }
        
        // Manual refresh button
        const refreshBtn = document.querySelector('[onclick="refreshActivities()"]');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.refreshActivities();
            });
        }
    }

    /**
     * Setup auto refresh
     */
    setupAutoRefresh() {
        const autoRefreshEnabled = document.body.dataset.autoRefresh === 'true';
        if (!autoRefreshEnabled) {
            return;
        }

        const runCheck = () => {
            if (!document.hidden) {
                this.checkForUpdates();
            }
        };

        runCheck();
        this.autoRefreshInterval = setInterval(runCheck, 60000);

        this.autoRefreshVisibilityHandler = () => {
            if (!document.hidden) {
                runCheck();
            }
        };

        document.addEventListener('visibilitychange', this.autoRefreshVisibilityHandler);
    }

    /**
     * Check for updates
     */
    async checkForUpdates() {
        try {
            const response = await fetch(`${this.contextPath}/support/agent/customers/view?id=${this.customerId}&ajax=true`);
            if (response.ok) {
                const data = await response.json();
                this.updatePageData(data);
            }
        } catch (error) {
            // Silently fail for background updates
            console.log('Background update failed:', error);
        }
    }

    /**
     * Update page data
     */
    updatePageData(data) {
        // Update status badge if changed
        const statusBadge = document.querySelector('.status-badge');
        if (statusBadge && data.customer) {
            const isActive = data.customer.active;
            statusBadge.className = `status-badge ${isActive ? 'active' : 'inactive'}`;
            statusBadge.innerHTML = `
                <i class="fas ${isActive ? 'fa-check-circle' : 'fa-times-circle'}"></i>
                ${isActive ? 'Hoạt động' : 'Tạm khóa'}
            `;
        }
        
        // Update last login if available
        if (data.customer && data.customer.lastLogin) {
            const lastLoginElement = document.querySelector('.info-row:contains("Đăng nhập cuối") .info-value');
            if (lastLoginElement) {
                lastLoginElement.textContent = this.formatDate(data.customer.lastLogin);
            }
        }
    }

    /**
     * Refresh activities
     */
    async refreshActivities() {
        const refreshBtn = document.querySelector('[onclick="refreshActivities()"]');
        this.setButtonLoading(refreshBtn, true);

        try {
            // Simulate refresh (replace with actual API call)
            await new Promise(resolve => setTimeout(resolve, 1000));
            
            this.showAlert('success', 'Làm mới dữ liệu thành công');
            
            // In a real implementation, you would fetch new activity data
            // and update the timeline
            
        } catch (error) {
            console.error('Error refreshing activities:', error);
            this.showAlert('error', 'Đã xảy ra lỗi khi làm mới dữ liệu');
        } finally {
            this.setButtonLoading(refreshBtn, false);
        }
    }

    /**
     * Refresh page data
     */
    async refreshPageData() {
        try {
            window.location.reload();
        } catch (error) {
            console.error('Error refreshing page:', error);
        }
    }

    /**
     * Format date
     */
    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('vi-VN', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit'
        });
    }

    /**
     * Show alert message
     */
    showAlert(type, message) {
        // Remove existing alerts
        const existingAlerts = document.querySelectorAll('.alert.dynamic');
        existingAlerts.forEach(alert => alert.remove());

        // Create new alert
        const alert = document.createElement('div');
        alert.className = `alert alert-${type === 'error' ? 'danger' : type} dynamic`;
        alert.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            z-index: 3000;
            min-width: 300px;
            padding: 15px 20px;
            border-radius: 12px;
            color: white;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 10px;
            animation: slideInRight 0.3s ease-out;
        `;
        
        if (type === 'success') {
            alert.style.background = 'rgba(34, 197, 94, 0.9)';
            alert.style.border = '1px solid rgba(34, 197, 94, 0.3)';
        } else if (type === 'error') {
            alert.style.background = 'rgba(239, 68, 68, 0.9)';
            alert.style.border = '1px solid rgba(239, 68, 68, 0.3)';
        }
        
        alert.innerHTML = `
            <i class="fas fa-${this.getAlertIcon(type)}"></i>
            ${message}
        `;

        document.body.appendChild(alert);

        // Auto remove after 5 seconds
        setTimeout(() => {
            alert.style.animation = 'slideOutRight 0.3s ease-in';
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.remove();
                }
            }, 300);
        }, 5000);
    }

    /**
     * Get alert icon
     */
    getAlertIcon(type) {
        const icons = {
            success: 'check-circle',
            error: 'exclamation-circle',
            warning: 'exclamation-triangle',
            info: 'info-circle'
        };
        return icons[type] || 'info-circle';
    }

    /**
     * Cleanup when leaving page
     */
    cleanup() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
        }

        if (this.autoRefreshInterval) {
            clearInterval(this.autoRefreshInterval);
        }

        if (this.autoRefreshVisibilityHandler) {
            document.removeEventListener('visibilitychange', this.autoRefreshVisibilityHandler);
            this.autoRefreshVisibilityHandler = null;
        }
    }
}

// Global functions for JSP onclick handlers
function toggleCustomerStatus(customerId, newStatus) {
    if (window.customerDetailsManager) {
        window.customerDetailsManager.toggleCustomerStatus(customerId, newStatus);
    }
}

function sendEmail() {
    if (window.customerDetailsManager) {
        window.customerDetailsManager.showEmailModal();
    }
}

function closeEmailModal() {
    if (window.customerDetailsManager) {
        window.customerDetailsManager.closeEmailModal();
    }
}

function handleEmailSend(event) {
    if (window.customerDetailsManager) {
        window.customerDetailsManager.handleEmailSend(event);
    }
}

function refreshActivities() {
    if (window.customerDetailsManager) {
        window.customerDetailsManager.refreshActivities();
    }
}

function closeConfirmModal() {
    if (window.customerDetailsManager) {
        window.customerDetailsManager.closeConfirmModal();
    }
}

function confirmAction() {
    if (window.customerDetailsManager) {
        window.customerDetailsManager.confirmAction();
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.customerDetailsManager = new CustomerDetailsManager();
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (window.customerDetailsManager) {
        window.customerDetailsManager.cleanup();
    }
});

// Add slide animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);