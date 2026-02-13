document.addEventListener('DOMContentLoaded', () => {
    const sidebarToggle = document.getElementById('mobileMenuBtn');
    const sidebar = document.querySelector('.sidebar');

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });
    }

    const filtersForm = document.querySelector('[data-filters-form]');
    const resetButton = document.querySelector('[data-reset-filters]');

    if (resetButton && filtersForm) {
        const originalAction = filtersForm.getAttribute('data-base-action');
        resetButton.addEventListener('click', () => {
            window.location.href = originalAction || filtersForm.action;
        });
    }

    const pageSizeSelect = document.querySelector('[data-page-size]');
    const pageSizeForm = pageSizeSelect ? pageSizeSelect.closest('form') : null;
    if (pageSizeSelect && pageSizeForm) {
        pageSizeSelect.addEventListener('change', () => {
            pageSizeForm.submit();
        });
    }
});
