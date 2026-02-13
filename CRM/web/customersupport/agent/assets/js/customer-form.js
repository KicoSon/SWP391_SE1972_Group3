/**
 * Customer Form Enhancements
 * Handles client-side validation and UI interactions for customer add/edit form
 */

class CustomerFormManager {
    constructor() {
        this.form = document.getElementById('customerForm');
        this.mode = document.body.dataset.formMode || 'create';
        this.emailPattern = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
        this.phonePattern = /^\d{10}$/;
        if (this.form) {
            this.bindEvents();
        }
    }

    bindEvents() {
        this.form.addEventListener('submit', (event) => {
            this.clearClientErrors();
            const errors = this.validateForm();
            if (errors.length > 0) {
                event.preventDefault();
                this.renderErrors(errors);
                this.focusFirstError(errors);
            }
        });

        const inputs = this.form.querySelectorAll('input');
        inputs.forEach((input) => {
            input.addEventListener('input', () => {
                this.clearFieldError(input);
            });
        });

        const toggleInput = this.form.querySelector('#isActive');
        if (toggleInput) {
            toggleInput.addEventListener('change', () => this.updateToggleState(toggleInput));
            this.updateToggleState(toggleInput);
        }
    }

    validateForm() {
        const errors = [];
        const values = (name) => (this.form.elements[name] ? this.form.elements[name].value.trim() : '');

        const fullName = values('fullName');
        const email = values('email');
        const phone = values('phone');
        const address = values('address');
        const city = values('city');
        const province = values('province');
        const password = values('password');
        const confirmPassword = values('confirmPassword');

        if (!fullName) {
            errors.push({ field: 'fullName', message: 'Vui lòng nhập họ và tên' });
        }

        if (!email) {
            errors.push({ field: 'email', message: 'Vui lòng nhập email' });
        } else if (!this.emailPattern.test(email)) {
            errors.push({ field: 'email', message: 'Email không hợp lệ' });
        }

        if (!phone) {
            errors.push({ field: 'phone', message: 'Vui lòng nhập số điện thoại' });
        } else if (!this.phonePattern.test(phone)) {
            errors.push({ field: 'phone', message: 'Số điện thoại phải gồm 10 chữ số' });
        }

        if (!address) {
            errors.push({ field: 'address', message: 'Vui lòng nhập địa chỉ' });
        }
        if (!city) {
            errors.push({ field: 'city', message: 'Vui lòng nhập thành phố' });
        }
        if (!province) {
            errors.push({ field: 'province', message: 'Vui lòng nhập tỉnh/thành phố' });
        }

        const isEditMode = this.mode === 'edit';
        if (isEditMode) {
            if (password || confirmPassword) {
                if (password.length < 6) {
                    errors.push({ field: 'password', message: 'Mật khẩu mới phải có ít nhất 6 ký tự' });
                }
                if (password !== confirmPassword) {
                    errors.push({ field: 'confirmPassword', message: 'Mật khẩu nhập lại không khớp' });
                }
            }
        } else {
            if (!password) {
                errors.push({ field: 'password', message: 'Vui lòng nhập mật khẩu' });
            } else if (password.length < 6) {
                errors.push({ field: 'password', message: 'Mật khẩu phải có ít nhất 6 ký tự' });
            }
            if (!confirmPassword) {
                errors.push({ field: 'confirmPassword', message: 'Vui lòng nhập lại mật khẩu' });
            } else if (password !== confirmPassword) {
                errors.push({ field: 'confirmPassword', message: 'Mật khẩu nhập lại không khớp' });
            }
        }

        return errors;
    }

    renderErrors(errors) {
        errors.forEach(({ field, message }) => {
            const input = this.form.elements[field];
            if (!input) return;

            const formGroup = input.closest('.form-group');
            if (formGroup) {
                formGroup.classList.add('has-error');
                formGroup.dataset.clientError = 'true';

                let errorSpan = formGroup.querySelector('.client-error-message');
                if (!errorSpan) {
                    errorSpan = document.createElement('span');
                    errorSpan.className = 'error-message client-error-message';
                    formGroup.appendChild(errorSpan);
                }
                errorSpan.textContent = message;
            }
        });
    }

    clearClientErrors() {
        const existingMessages = this.form.querySelectorAll('.client-error-message');
        existingMessages.forEach((msg) => msg.remove());
        const groups = this.form.querySelectorAll('.form-group[data-client-error="true"]');
        groups.forEach((group) => {
            delete group.dataset.clientError;
            const hasServerError = group.querySelector('.error-message:not(.client-error-message)');
            if (!hasServerError) {
                group.classList.remove('has-error');
            }
        });
    }

    clearFieldError(input) {
        const formGroup = input.closest('.form-group');
        if (!formGroup) return;
        const clientMessage = formGroup.querySelector('.client-error-message');
        if (clientMessage) {
            clientMessage.remove();
        }
        if (formGroup.dataset.clientError === 'true') {
            const hasServerError = formGroup.querySelector('.error-message:not(.client-error-message)');
            if (!hasServerError) {
                formGroup.classList.remove('has-error');
            }
            delete formGroup.dataset.clientError;
        }
    }

    focusFirstError(errors) {
        if (errors.length === 0) {
            return;
        }
        const firstInput = this.form.elements[errors[0].field];
        if (firstInput && typeof firstInput.focus === 'function') {
            firstInput.focus({ preventScroll: false });
        }
    }

    updateToggleState(toggleInput) {
        const label = toggleInput.closest('.status-toggle');
        if (!label) return;
        const textEl = label.querySelector('.toggle-text');
        if (!textEl) return;

        const activeText = textEl.dataset.active || 'Đang hoạt động';
        const inactiveText = textEl.dataset.inactive || 'Tạm khóa';
        textEl.textContent = toggleInput.checked ? activeText : inactiveText;
        textEl.classList.toggle('active', toggleInput.checked);
        textEl.classList.toggle('inactive', !toggleInput.checked);
    }
}

// Initialize on DOM ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => new CustomerFormManager());
} else {
    new CustomerFormManager();
}
