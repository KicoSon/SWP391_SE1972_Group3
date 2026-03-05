/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

// Search functionality

const searchInput = document.querySelector('.search-input');
const tableRows = document.querySelectorAll('.activity-table tbody tr');

searchInput.addEventListener('input', (e) => {
    const searchTerm = e.target.value.toLowerCase();

    tableRows.forEach(row => {
        const text = row.textContent.toLowerCase();
        row.style.display = text.includes(searchTerm) ? '' : 'none';
    });
});

// Filter functionality
const filterBtn = document.querySelector('.btn-primary');
const typeFilter = document.querySelector('select');

filterBtn.addEventListener('click', () => {
    const selectedType = typeFilter.value;

    tableRows.forEach(row => {
        if (selectedType === 'All') {
            row.style.display = '';
        } else {
            const rowType = row.querySelector('.activity-type span:last-child').textContent;
            row.style.display = rowType === selectedType ? '' : 'none';
        }
    });
});

// Pagination
const prevBtn = document.querySelector('.page-btn:first-child');
const nextBtn = document.querySelector('.page-btn:last-child');
const pageInfo = document.querySelector('.page-info');

let currentPage = 1;
const totalPages = 3;

nextBtn.addEventListener('click', () => {
    if (currentPage < totalPages) {
        currentPage++;
        updatePagination();
    }
});

prevBtn.addEventListener('click', () => {
    if (currentPage > 1) {
        currentPage--;
        updatePagination();
    }
});

function updatePagination() {
    pageInfo.textContent = `Page ${currentPage} / ${totalPages}`;
    prevBtn.disabled = currentPage === 1;
    nextBtn.disabled = currentPage === totalPages;
}

// Add hover effects to chart bars
const chartBars = document.querySelectorAll('.chart-bar');
chartBars.forEach(bar => {
    bar.addEventListener('click', () => {
        alert('Chart interaction coming soon!');
    });
});

// Animation on scroll
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.style.animation = 'fadeIn 0.5s ease forwards';
        }
    });
}, observerOptions);

document.querySelectorAll('.summary-section').forEach(el => observer.observe(el));
    
function openDetailModal(id) {
    // Gắn URL kèm tham số view=modal
    document.getElementById('detailIframe').src = '/CRM/activities/detail?id=' + id + '&view=modal';
    document.getElementById('detailModalOverlay').style.display = 'flex';
    document.body.style.overflow = 'hidden'; // Khóa cuộn trang chính
}

function closeDetailModal() {
    document.getElementById('detailModalOverlay').style.display = 'none';
    document.getElementById('detailIframe').src = ''; // Xóa src để giải phóng iframe
    document.body.style.overflow = 'auto'; // Mở lại cuộn trang chính
}

// Auto-hide toast notification
setTimeout(() => {
    let toast = document.getElementById("toastSuccess");
    if (toast) {
        toast.style.opacity = "0";
        toast.style.transform = "translateX(100px)";
        toast.style.transition = "all 0.5s ease";
        setTimeout(() => toast.remove(), 500);
    }
}, 5000);
