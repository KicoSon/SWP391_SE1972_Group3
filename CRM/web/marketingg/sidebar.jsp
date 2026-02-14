<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!-- FontAwesome Icons -->
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

<style>
    :root {
        --primary-gradient: linear-gradient(135deg, #667eea, #764ba2);
        --primary-color: #667eea;
        --sidebar-bg: rgba(255, 255, 255, 0.9);
        --sidebar-blur: blur(15px);
    }

    body {
        margin: 0;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    /* ========== SIDEBAR ========== */
    .sidebar {
        width: 260px;
        height: 100vh;
        position: fixed;
        top: 0;
        left: 0;
        background: var(--sidebar-bg);
        backdrop-filter: var(--sidebar-blur);
        box-shadow: 6px 0 20px rgba(0, 0, 0, 0.08);
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        transition: all 0.35s ease;
        z-index: 1000;
    }

    .sidebar.collapsed {
        transform: translateX(-100%);
    }

    /* Header */
    .sidebar-header {
        padding: 25px 20px;
        text-align: center;
        border-bottom: 1px solid rgba(0, 0, 0, 0.08);
    }

    .sidebar-header h3 {
        font-size: 22px;
        color: var(--primary-color);
        font-weight: 700;
        margin: 0;
        letter-spacing: 0.5px;
    }

    /* Navigation */
    .sidebar-nav {
        flex: 1;
        padding: 20px 0;
    }

    .nav-section {
        padding: 10px 25px 5px;
        font-size: 12px;
        font-weight: 600;
        text-transform: uppercase;
        color: #888;
        letter-spacing: 1px;
    }

    .nav-item {
        margin: 4px 18px;
    }

    .nav-link {
        display: flex;
        align-items: center;
        padding: 12px 16px;
        color: #444;
        text-decoration: none;
        border-radius: 10px;
        font-weight: 500;
        transition: all 0.3s ease;
        position: relative;
    }

    .nav-link i {
        margin-right: 14px;
        font-size: 18px;
        width: 22px;
        text-align: center;
        color: var(--primary-color);
        transition: color 0.3s ease;
    }

    .nav-link:hover, .nav-link.active {
        background: var(--primary-gradient);
        color: #fff;
        transform: translateX(6px);
        box-shadow: 0 4px 10px rgba(102, 126, 234, 0.2);
    }

    .nav-link:hover i, .nav-link.active i {
        color: #fff;
    }

    /* Footer */
    .sidebar-footer {
        padding: 15px 20px;
        border-top: 1px solid rgba(0, 0, 0, 0.05);
        text-align: center;
        font-size: 13px;
        color: #666;
        background: rgba(255, 255, 255, 0.4);
    }

    /* ========== TOGGLE BUTTON ========== */
    .sidebar-toggle {
        position: fixed;
        top: 20px;
        left: 20px;
        z-index: 1100;
        background: var(--primary-gradient);
        color: white;
        border: none;
        border-radius: 8px;
        padding: 10px 12px;
        cursor: pointer;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
        transition: all 0.3s ease;
        display: none;
    }

    .sidebar-toggle:hover {
        transform: scale(1.05);
    }

    @media (max-width: 992px) {
        .sidebar {
            transform: translateX(-100%);
        }
        .sidebar.active {
            transform: translateX(0);
        }
        .sidebar-toggle {
            display: block;
        }
    }

</style>

<!-- Toggle Button -->
<button class="sidebar-toggle" id="toggleSidebar">
    <i class="fas fa-bars"></i>
</button>

<!-- Sidebar -->
<div class="sidebar" id="sidebar">
    <div>
        <div class="sidebar-header">
            <h3><i class="fas fa-laptop-code"></i>Marketing Staff</h3>
        </div>

        <nav class="sidebar-nav">

            <div class="nav-section">Tổng quan</div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/AdminDashboardServlet" class="nav-link active">
                    <i class="fas fa-chart-line"></i> Thống kê tổng quan
                </a>
            </div>

            <div class="nav-section">Quản lý chính</div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/categoryManagement" class="nav-link">
                    <i class="fas fa-box"></i> Quản lý danh mục
                </a>

            </div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/productManagement" class="nav-link">
                    <i class="fas fa-box"></i> Quản lý sản phẩm
                </a>
            </div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/brandManagement" class="nav-link">
                    <i class="fas fa-box"></i> Quản lý thương hiệu
                </a>

            </div>

            <div class="nav-section">Hệ thống</div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/shipperManagement" class="nav-link">
                    <i class="fas fa-user-shield"></i> Quản lý shipper
                </a>
            </div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/staffManagement" class="nav-link">
                    <i class="fas fa-user-shield"></i> Quản lý nhân viên
                </a>
            </div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/userManagement" class="nav-link">
                    <i class="fas fa-users"></i> Quản lý khách hàng
                </a>
            </div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/reviewManagement" class="nav-link">
                    <i class="far fa-star"></i> Quản lý review
                </a>
            </div>
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}/admin/adminProfile" class="nav-link">
                    <i class="fas fa-user-shield"></i> Thông tin cá nhân
                </a>
            </div>
            
        </nav>
    </div>

    <div class="sidebar-footer">
        © 2026 CRM SYSTEM
    </div>
</div>

<!-- ========== SCRIPT ========== -->
<script>
    document.addEventListener("DOMContentLoaded", function () {
        const sidebar = document.getElementById("sidebar");
        const toggleButton = document.getElementById("toggleSidebar");
        const links = document.querySelectorAll(".nav-link");

        // Toggle sidebar visibility
        toggleButton.addEventListener("click", () => {
            sidebar.classList.toggle("active");
        });

        // Highlight active link
        const currentPage = window.location.pathname.split("/").pop();
        links.forEach(link => {
            const href = link.getAttribute("href");
            if (href.includes(currentPage)) {
                link.classList.add("active");
            }

            link.addEventListener("click", function () {
                links.forEach(l => l.classList.remove("active"));
                this.classList.add("active");
                localStorage.setItem("activeSidebar", this.getAttribute("href"));
            });
        });

        // Maintain active link after reload
        const currentActive = localStorage.getItem("activeSidebar");
        if (currentActive) {
            links.forEach(link => {
                if (link.getAttribute("href") === currentActive) {
                    link.classList.add("active");
                }
            });
        }
    });
</script>

