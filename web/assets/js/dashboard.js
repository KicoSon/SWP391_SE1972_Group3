/**
 * Dashboard JavaScript
 * Handles dashboard interactions, charts, and data refresh
 */

class Dashboard {
    constructor() {
        this.charts = {};
        this.refreshInterval = null;
        this.init();
    }
    
    init() {
        this.bindEvents();
        this.initCharts();
        this.startAutoRefresh();
        this.initMobileMenu();
    }
    
    bindEvents() {
        // Refresh button
        const refreshBtn = document.getElementById('refreshBtn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.refreshData();
            });
        }
        
        // User menu dropdown
        this.initUserMenu();
    }
    
    initMobileMenu() {
        const mobileMenuBtn = document.getElementById('mobileMenuBtn');
        if (mobileMenuBtn && window.sidebarManager) {
            mobileMenuBtn.addEventListener('click', (e) => {
                e.preventDefault();
                window.sidebarManager.toggle();
            });
        }
    }
    
    initUserMenu() {
        const userMenuBtn = document.querySelector('.user-menu-btn');
        const userMenuDropdown = document.querySelector('.user-menu-dropdown');
        
        if (userMenuBtn && userMenuDropdown) {
            let timeout;
            
            userMenuBtn.addEventListener('mouseenter', () => {
                clearTimeout(timeout);
                userMenuDropdown.style.opacity = '1';
                userMenuDropdown.style.visibility = 'visible';
                userMenuDropdown.style.transform = 'translateY(0)';
            });
            
            userMenuBtn.addEventListener('mouseleave', () => {
                timeout = setTimeout(() => {
                    userMenuDropdown.style.opacity = '0';
                    userMenuDropdown.style.visibility = 'hidden';
                    userMenuDropdown.style.transform = 'translateY(-10px)';
                }, 300);
            });
            
            userMenuDropdown.addEventListener('mouseenter', () => {
                clearTimeout(timeout);
            });
            
            userMenuDropdown.addEventListener('mouseleave', () => {
                userMenuDropdown.style.opacity = '0';
                userMenuDropdown.style.visibility = 'hidden';
                userMenuDropdown.style.transform = 'translateY(-10px)';
            });
        }
    }
    
    initCharts() {
        this.initPerformanceChart();
    }
    
    initPerformanceChart() {
        const ctx = document.getElementById('performanceChart');
        if (!ctx) return;
        
        const chartCtx = ctx.getContext('2d');
        const performanceData = window.dashboardData?.performanceChart || {};
        const labels = Array.isArray(performanceData.labels) ? performanceData.labels : [];
        const resolvedData = Array.isArray(performanceData.resolvedTickets) ? performanceData.resolvedTickets : [];
        const newData = Array.isArray(performanceData.newTickets) ? performanceData.newTickets : [];
        const pendingData = Array.isArray(performanceData.pendingTickets) ? performanceData.pendingTickets : [];

        const chartData = {
            labels,
            datasets: [
                {
                    label: 'Phiếu đã xử lý',
                    data: resolvedData,
                    borderColor: '#10b981',
                    backgroundColor: 'rgba(16, 185, 129, 0.12)',
                    tension: 0.4,
                    fill: true
                },
                {
                    label: 'Phiếu mới',
                    data: newData,
                    borderColor: '#667eea',
                    backgroundColor: 'rgba(102, 126, 234, 0.12)',
                    tension: 0.4,
                    fill: true
                },
                {
                    label: 'Phiếu đang chờ',
                    data: pendingData,
                    borderColor: '#f59e0b',
                    backgroundColor: 'rgba(245, 158, 11, 0.12)',
                    tension: 0.4,
                    fill: true
                }
            ]
        };
        
        const chartOptions = {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top',
                    labels: {
                        usePointStyle: true,
                        padding: 20,
                        font: {
                            size: 12,
                            family: 'Inter'
                        }
                    }
                },
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    backgroundColor: 'rgba(255, 255, 255, 0.95)',
                    titleColor: '#1e293b',
                    bodyColor: '#64748b',
                    borderColor: '#e2e8f0',
                    borderWidth: 1,
                    cornerRadius: 8,
                    padding: 12
                }
            },
            scales: {
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        font: {
                            size: 11,
                            family: 'Inter'
                        },
                        color: '#64748b'
                    }
                },
                y: {
                    beginAtZero: true,
                    grid: {
                        color: '#f1f5f9'
                    },
                    ticks: {
                        font: {
                            size: 11,
                            family: 'Inter'
                        },
                        color: '#64748b'
                    }
                }
            },
            interaction: {
                mode: 'nearest',
                axis: 'x',
                intersect: false
            }
        };
        
        this.charts.performance = new Chart(chartCtx, {
            type: 'line',
            data: chartData,
            options: chartOptions
        });
    }
    
    updateChartPeriod(period) {
        // Additional periods can be loaded from the server in future iterations.
    }
    
    showChartLoading() {
        const chart = document.getElementById('performanceChart');
        if (chart) {
            chart.style.opacity = '0.5';
            chart.style.pointerEvents = 'none';
        }
    }
    
    hideChartLoading() {
        const chart = document.getElementById('performanceChart');
        if (chart) {
            chart.style.opacity = '1';
            chart.style.pointerEvents = 'auto';
        }
    }
    
    refreshData() {
        const refreshBtn = document.getElementById('refreshBtn');
        const refreshIcon = refreshBtn?.querySelector('i');

        if (refreshIcon) {
            refreshIcon.classList.add('refreshing');
        }

        window.location.reload();
    }
    
    showRefreshSuccess() {
        // Create and show success toast
        const toast = document.createElement('div');
        toast.className = 'toast toast-success';
        toast.innerHTML = `
            <i class="fas fa-check-circle"></i>
            <span>Dashboard refreshed successfully</span>
        `;
        
        document.body.appendChild(toast);
        
        // Animate in
        setTimeout(() => {
            toast.classList.add('show');
        }, 100);
        
        // Remove after 3 seconds
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => {
                document.body.removeChild(toast);
            }, 300);
        }, 3000);
    }
    
    startAutoRefresh() {
        // Refresh data every 5 minutes
        this.refreshInterval = setInterval(() => {
            this.refreshData();
        }, 5 * 60 * 1000);
    }
    
    stopAutoRefresh() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
            this.refreshInterval = null;
        }
    }
    
    // Animation utilities
    animateValue(element, start, end, duration = 1000) {
        if (!element) return;
        
        const startTime = performance.now();
        const startValue = parseInt(start);
        const endValue = parseInt(end);
        const changeValue = endValue - startValue;
        
        const animate = (currentTime) => {
            const elapsedTime = currentTime - startTime;
            const progress = Math.min(elapsedTime / duration, 1);
            
            // Easing function (ease-out)
            const easedProgress = 1 - Math.pow(1 - progress, 3);
            
            const currentValue = Math.round(startValue + (changeValue * easedProgress));
            element.textContent = currentValue.toLocaleString();
            
            if (progress < 1) {
                requestAnimationFrame(animate);
            }
        };
        
        requestAnimationFrame(animate);
    }
    
    // Cleanup method
    destroy() {
        this.stopAutoRefresh();
        
        // Destroy charts
        Object.values(this.charts).forEach(chart => {
            if (chart && typeof chart.destroy === 'function') {
                chart.destroy();
            }
        });
        
        this.charts = {};
    }
}

// Toast notification styles
const toastStyles = `
    .toast {
        position: fixed;
        top: 20px;
        right: 20px;
        background: white;
        border: 1px solid #e2e8f0;
        border-radius: 12px;
        padding: 16px 20px;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
        display: flex;
        align-items: center;
        gap: 12px;
        z-index: 9999;
        transform: translateX(100%);
        transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        max-width: 400px;
        font-size: 0.9rem;
        font-weight: 500;
    }
    
    .toast.show {
        transform: translateX(0);
    }
    
    .toast-success {
        border-left: 4px solid #10b981;
        color: #059669;
    }
    
    .toast-success i {
        color: #10b981;
    }
    
    @media (max-width: 768px) {
        .toast {
            top: 10px;
            right: 10px;
            left: 10px;
            max-width: none;
            transform: translateY(-100%);
        }
        
        .toast.show {
            transform: translateY(0);
        }
    }
`;

// Add toast styles to head
const styleSheet = document.createElement('style');
styleSheet.textContent = toastStyles;
document.head.appendChild(styleSheet);

// Initialize dashboard when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.dashboard = new Dashboard();
});

// Cleanup on page unload
window.addEventListener('beforeunload', () => {
    if (window.dashboard) {
        window.dashboard.destroy();
    }
});

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = Dashboard;
}