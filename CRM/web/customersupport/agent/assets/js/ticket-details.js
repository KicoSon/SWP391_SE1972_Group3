document.addEventListener('DOMContentLoaded', () => {
    const statusSelect = document.querySelector('[data-ticket-status]');
    const historyItems = document.querySelectorAll('.history-item');
    const form = document.querySelector('[data-ticket-update-form]');
    const evidenceInput = document.querySelector('[data-evidence-input]');
    const evidenceLabel = document.querySelector('[data-evidence-label]');

    if (statusSelect) {
        statusSelect.addEventListener('change', (event) => {
            const badge = document.querySelector('[data-status-badge]');
            if (!badge) {
                return;
            }

            badge.classList.remove('OPEN', 'PENDING', 'IN_PROGRESS', 'RESOLVED');
            const value = event.target.value;
            const selectedOption = event.target.selectedOptions ? event.target.selectedOptions[0] : null;
            const label = selectedOption ? selectedOption.textContent.trim() : value.replace('_', ' ');
            badge.textContent = label;
            badge.classList.add(value);
        });
    }

    if (historyItems.length > 0) {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('visible');
                }
            });
        }, {
            threshold: 0.15
        });

        historyItems.forEach(item => observer.observe(item));
    }

    if (evidenceInput && evidenceLabel) {
        evidenceInput.addEventListener('change', () => {
            const fileName = evidenceInput.files && evidenceInput.files.length > 0
                ? evidenceInput.files[0].name
                : 'Tải lên bằng chứng (ảnh, PDF, ZIP...)';
            evidenceLabel.querySelector('strong').textContent = fileName;
        });
    }

    if (form) {
        form.addEventListener('submit', (event) => {
            const statusValue = statusSelect ? statusSelect.value : '';
            const note = form.querySelector('[name="note"]').value.trim();

            if (!statusValue) {
                event.preventDefault();
                alert('Vui lòng chọn trạng thái mới cho ticket.');
                return;
            }

            if (!note) {
                event.preventDefault();
                alert('Vui lòng nhập ghi chú cập nhật.');
                return;
            }
        });
    }
});
