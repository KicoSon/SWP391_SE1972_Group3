/**
 * Customer List Management
 * Handles customer list interactions, filtering, pagination, and bulk operations
 */

class CustomerListManager {
    constructor() {
        this.selectedCustomers = new Set();
        this.contextPath = this.getContextPath();
        this.init();
    }

    /**
     * Initialize the customer list manager
     */
    init() {
        this.bindEvents();
        this.updateBulkActions();
        this.initializeAlerts();
    }

    /**
     * Get the context path for the application
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
        // Select all checkbox
        const selectAllCheckbox = document.getElementById('selectAll');
        if (selectAllCheckbox) {
            selectAllCheckbox.addEventListener('change', (e) => {
                this.toggleSelectAll(e.target.checked);
            });
        }

        // Individual customer checkboxes
        const customerCheckboxes = document.querySelectorAll('.customer-checkbox');
        customerCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', (e) => {
                this.toggleCustomerSelection(e.target.value, e.target.checked);
            });
        });

        // Page size selector
        const pageSizeSelect = document.getElementById('pageSize');
        if (pageSizeSelect) {
            pageSizeSelect.addEventListener('change', (e) => {
                this.changePageSize(e.target.value);
            });
        }

        // Search form auto-submit on filter change
        const filterSelects = document.querySelectorAll('.filter-group select');
        filterSelects.forEach(select => {
            select.addEventListener('change', () => {
                this.submitSearchForm();
            });
        });

        // Search input with debounce
        const searchInput = document.getElementById('searchTerm');
        if (searchInput) {
            let searchTimeout;
            searchInput.addEventListener('input', (e) => {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    if (e.target.value.length >= 3 || e.target.value.length === 0) {
                        this.submitSearchForm();
                    }
                }, 500);
            });
        }
    }

    /**
     * Toggle select all customers
     */
    toggleSelectAll(checked) {
        const customerCheckboxes = document.querySelectorAll('.customer-checkbox');
        
        customerCheckboxes.forEach(checkbox => {
            checkbox.checked = checked;
            if (checked) {
                this.selectedCustomers.add(checkbox.value);
            } else {
                this.selectedCustomers.delete(checkbox.value);
            }
        });

        this.updateBulkActions();
    }

    /**
     * Toggle individual customer selection
     */
    toggleCustomerSelection(customerId, checked) {
        if (checked) {
            this.selectedCustomers.add(customerId);
        } else {
            this.selectedCustomers.delete(customerId);
        }

        // Update select all checkbox
        const selectAllCheckbox = document.getElementById('selectAll');
        const customerCheckboxes = document.querySelectorAll('.customer-checkbox');
        const checkedCount = document.querySelectorAll('.customer-checkbox:checked').length;
        
        if (selectAllCheckbox) {
            selectAllCheckbox.checked = checkedCount === customerCheckboxes.length;
            selectAllCheckbox.indeterminate = checkedCount > 0 && checkedCount < customerCheckboxes.length;
        }

        this.updateBulkActions();
    }

    /**
     * Update bulk actions visibility and count
     */
    updateBulkActions() {
        const bulkActions = document.getElementById('bulkActions');
        const selectedCount = document.getElementById('selectedCount');
        
        if (bulkActions && selectedCount) {
            if (this.selectedCustomers.size > 0) {
                selectedCount.textContent = this.selectedCustomers.size;
                bulkActions.style.display = 'block';
            } else {
                bulkActions.style.display = 'none';
            }
        }
    }

    /**
     * Change page size
     */
    changePageSize(pageSize) {
        const currentUrl = new URL(window.location);
        currentUrl.searchParams.set('pageSize', pageSize);
        currentUrl.searchParams.set('page', '1'); // Reset to first page
        window.location.href = currentUrl.toString();
    }

    /**
     * Submit search form programmatically
     */
    submitSearchForm() {
        const searchForm = document.querySelector('.search-form');
        if (searchForm) {
            searchForm.submit();
        }
    }

    /**
     * Toggle customer status (active/inactive)
     */
    toggleCustomerStatus(customerId, newStatus) {
        const statusText = newStatus ? 'kích hoạt' : 'tạm khóa';
        const confirmMessage = `Bạn có chắc chắn muốn ${statusText} khách hàng này?`;
        
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
            const response = await fetch(`${this.contextPath}/support/agent/customers`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams({
                    customerId: customerId,
                    action: newStatus ? 'unban' : 'ban'
                })
            });

            if (response.ok) {
                this.showAlert('success', `${newStatus ? 'Kích hoạt' : 'Tạm khóa'} khách hàng thành công`);
                setTimeout(() => {
                    window.location.reload();
                }, 1000);
            } else {
                throw new Error('Request failed');
            }
        } catch (error) {
            console.error('Error toggling customer status:', error);
            this.showAlert('error', 'Đã xảy ra lỗi khi thay đổi trạng thái khách hàng');
        }
    }

    /**
     * Bulk toggle customer status
     */
    bulkToggleStatus(newStatus) {
        if (this.selectedCustomers.size === 0) {
            this.showAlert('warning', 'Vui lòng chọn ít nhất một khách hàng');
            return;
        }

        const statusText = newStatus ? 'kích hoạt' : 'tạm khóa';
        const confirmMessage = `Bạn có chắc chắn muốn ${statusText} ${this.selectedCustomers.size} khách hàng đã chọn?`;
        
        this.showConfirmModal(
            'Xác nhận thay đổi trạng thái hàng loạt',
            confirmMessage,
            () => this.performBulkStatusToggle(newStatus)
        );
    }

    /**
     * Perform bulk status toggle
     */
    async performBulkStatusToggle(newStatus) {
        try {
            const promises = Array.from(this.selectedCustomers).map(customerId => 
                fetch(`${this.contextPath}/support/agent/customers/edit`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        id: customerId,
                        action: newStatus ? 'activate' : 'deactivate'
                    })
                })
            );

            await Promise.all(promises);
            
            this.showAlert('success', `${newStatus ? 'Kích hoạt' : 'Tạm khóa'} ${this.selectedCustomers.size} khách hàng thành công`);
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        } catch (error) {
            console.error('Error bulk toggling customer status:', error);
            this.showAlert('error', 'Đã xảy ra lỗi khi thay đổi trạng thái khách hàng');
        }
    }

    /**
     * Bulk delete customers
     */
    bulkDelete() {
        if (this.selectedCustomers.size === 0) {
            this.showAlert('warning', 'Vui lòng chọn ít nhất một khách hàng');
            return;
        }

        const confirmMessage = `Bạn có chắc chắn muốn xóa ${this.selectedCustomers.size} khách hàng đã chọn? Hành động này không thể hoàn tác.`;
        
        this.showConfirmModal(
            'Xác nhận xóa hàng loạt',
            confirmMessage,
            () => this.performBulkDelete(),
            'danger'
        );
    }

    /**
     * Perform bulk delete
     */
    async performBulkDelete() {
        try {
            const promises = Array.from(this.selectedCustomers).map(customerId => 
                fetch(`${this.contextPath}/support/agent/customers/edit`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams({
                        id: customerId,
                        action: 'delete'
                    })
                })
            );

            await Promise.all(promises);
            
            this.showAlert('success', `Xóa ${this.selectedCustomers.size} khách hàng thành công`);
            setTimeout(() => {
                window.location.reload();
            }, 1000);
        } catch (error) {
            console.error('Error bulk deleting customers:', error);
            this.showAlert('error', 'Đã xảy ra lỗi khi xóa khách hàng');
        }
    }

    /**
     * Show confirmation modal
     */
    showConfirmModal(title, message, onConfirm, type = 'primary') {
        const modal = document.getElementById('confirmModal');
        const modalTitle = document.getElementById('modalTitle');
        const modalMessage = document.getElementById('modalMessage');
        const confirmButton = document.getElementById('confirmButton');

        if (modal && modalTitle && modalMessage && confirmButton) {
            modalTitle.textContent = title;
            modalMessage.textContent = message;
            
            // Set button style based on type
            confirmButton.className = `btn btn-${type}`;
            
            // Set confirm action
            confirmButton.onclick = () => {
                onConfirm();
                this.closeModal();
            };

            modal.style.display = 'block';
        }
    }

    /**
     * Close modal
     */
    closeModal() {
        const modal = document.getElementById('confirmModal');
        if (modal) {
            modal.style.display = 'none';
        }
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
        alert.innerHTML = `
            <i class="fas fa-${this.getAlertIcon(type)}"></i>
            ${message}
        `;

        // Insert alert at the top of main content
        const mainContent = document.querySelector('.content-wrapper');
        if (mainContent) {
            mainContent.insertBefore(alert, mainContent.firstChild);
        }

        // Auto remove after 5 seconds
        setTimeout(() => {
            alert.remove();
        }, 5000);
    }

    /**
     * Get alert icon based on type
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
     * Initialize alerts (auto-hide after 5 seconds)
     */
    initializeAlerts() {
        const alerts = document.querySelectorAll('.alert:not(.dynamic)');
        alerts.forEach(alert => {
            setTimeout(() => {
                alert.style.transition = 'opacity 0.3s ease';
                alert.style.opacity = '0';
                setTimeout(() => {
                    alert.remove();
                }, 300);
            }, 5000);
        });
    }
}

// Global functions for JSP onclick handlers
function toggleCustomerStatus(customerId, newStatus) {
    if (window.customerListManager) {
        window.customerListManager.toggleCustomerStatus(customerId, newStatus);
    }
}

function toggleSelectAll(checkbox) {
    if (window.customerListManager) {
        window.customerListManager.toggleSelectAll(checkbox.checked);
    }
}

function changePageSize(pageSize) {
    if (window.customerListManager) {
        window.customerListManager.changePageSize(pageSize);
    }
}

function bulkToggleStatus(newStatus) {
    if (window.customerListManager) {
        window.customerListManager.bulkToggleStatus(newStatus);
    }
}

function bulkDelete() {
    if (window.customerListManager) {
        window.customerListManager.bulkDelete();
    }
}

function closeModal() {
    if (window.customerListManager) {
        window.customerListManager.closeModal();
    }
}

function confirmAction() {
    // This will be handled by the specific confirm button onclick
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.customerListManager = new CustomerListManager();
});

// Handle modal close on background click
document.addEventListener('click', (e) => {
    const modal = document.getElementById('confirmModal');
    if (modal && e.target === modal) {
        if (window.customerListManager) {
            window.customerListManager.closeModal();
        }
    }
});

// Handle ESC key to close modal
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        const modal = document.getElementById('confirmModal');
        if (modal && modal.style.display === 'block') {
            if (window.customerListManager) {
                window.customerListManager.closeModal();
            }
        }
    }
});