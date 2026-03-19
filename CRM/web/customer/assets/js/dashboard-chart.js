// Ticket Status Chart
document.addEventListener('DOMContentLoaded', function() {
    const ctx = document.getElementById('ticketStatusChart');
    if (ctx) {
        // Get data from data attributes
        const openCount = parseInt(ctx.dataset.open) || 0;
        const assignedCount = parseInt(ctx.dataset.assigned) || 0;
        const inProgressCount = parseInt(ctx.dataset.inprogress) || 0;
        const pendingCount = parseInt(ctx.dataset.pending) || 0;
        const resolvedCount = parseInt(ctx.dataset.resolved) || 0;
        const closedCount = parseInt(ctx.dataset.closed) || 0;
        
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Mở','Đang xử lý','Đã giải quyết'],
                datasets: [{
                    label: 'Số lượng',
                    data: [openCount, inProgressCount, resolvedCount],
                    backgroundColor: [
                        'rgba(54, 162, 235, 0.8)',
                        'rgba(255, 159, 64, 0.8)',
                        'rgba(75, 192, 192, 0.8)',
                    ],
                    borderColor: [
                        'rgb(54, 162, 235)',
                        'rgb(255, 159, 64)',
                        'rgb(75, 192, 192)',
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 15,
                            font: {
                                size: 12
                            }
                        }
                    },
                    title: {
                        display: false
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                let label = context.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                label += context.parsed + ' phiếu';
                                return label;
                            }
                        }
                    }
                }
            }
        });
    }
});
