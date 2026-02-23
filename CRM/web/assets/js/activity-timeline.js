/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


// Delete Activity Function
function deleteActivity(id) {
    if (confirm('Bạn có chắc chắn muốn xóa activity này?')) {
        // Here you would send a DELETE request to your backend
        console.log('Deleting activity:', id);

        // For demo, just show success message
        alert('✅ Activity đã được xóa thành công!');

        // Reload page or remove row
        // window.location.reload();
    }
}

// Add smooth hover effect to rows
const tableRows = document.querySelectorAll('.timeline-table tbody tr');
tableRows.forEach((row, index) => {
    row.style.animationDelay = `${index * 0.1}s`;
});

// Optional: Load customer data dynamically
function loadCustomerData(customerId) {
    // This would fetch customer data from your backend
    console.log('Loading customer data for ID:', customerId);

    // Example: fetch('/api/customers/' + customerId)
    //   .then(response => response.json())
    //   .then(data => updateCustomerInfo(data));
}

// Check URL for customer ID
const urlParams = new URLSearchParams(window.location.search);
const customerId = urlParams.get('id') || '123';

// Load data on page load
window.addEventListener('DOMContentLoaded', () => {
    loadCustomerData(customerId);
});

// Add keyboard shortcut for New Activity (N key)
document.addEventListener('keydown', (e) => {
    if (e.key === 'n' || e.key === 'N') {
        if (!e.target.matches('input, textarea')) {
            window.location.href = 'create-edit-activity.html';
        }
    }
});

// Print functionality
function printTimeline() {
    window.print();
}

// Export to CSV functionality (bonus)
function exportToCSV() {
    const rows = Array.from(document.querySelectorAll('.timeline-table tbody tr'));
    let csv = 'Time,Type,Title,Description,Assigned To,Status,Creator\n';

    rows.forEach(row => {
        const cells = Array.from(row.querySelectorAll('td'));
        const rowData = [
            cells[0].textContent.trim(),
            cells[1].textContent.trim(),
            cells[2].textContent.trim(),
            cells[3].textContent.trim(),
            cells[4].textContent.trim(),
            cells[5].textContent.trim(),
            cells[6].textContent.trim()
        ];
        csv += rowData.join(',') + '\n';
    });

    const blob = new Blob([csv], {type: 'text/csv'});
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'customer-activities.csv';
    a.click();
}