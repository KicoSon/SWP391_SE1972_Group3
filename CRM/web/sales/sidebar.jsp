<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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

    .sidebar-header p {
        margin: 6px 0 0;
        font-size: 13px;
        color: #888;
    }

    .sidebar-nav {
        flex: 1;
        padding: 20px 0;
        overflow-y: auto;
    }

    .nav-section {
        padding: 10px 25px 5px;
        font-size: 12px;
        font-weight: 600;
        text-transform: uppercase;
        color: #888;
        letter-spacing: 1px;
    }

    .sidebar-nav a {
        display: flex;
        align-items: center;
        padding: 12px 16px;
        margin: 4px 18px;
        color: #444;
        text-decoration: none;
        border-radius: 10px;
        font-weight: 500;
        font-size: 14px;
        transition: all 0.3s ease;
        position: relative;
        gap: 14px;
    }

    .sidebar-nav a i {
        font-size: 18px;
        width: 22px;
        text-align: center;
        color: var(--primary-color);
        transition: color 0.3s ease;
    }

    .sidebar-nav a:hover,
    .sidebar-nav a.active {
        background: var(--primary-gradient);
        color: #fff;
        transform: translateX(6px);
        box-shadow: 0 4px 10px rgba(102, 126, 234, 0.2);
    }

    .sidebar-nav a:hover i,
    .sidebar-nav a.active i {
        color: #fff;
    }

    .sidebar-footer {
        padding: 15px 20px;
        border-top: 1px solid rgba(0, 0, 0, 0.05);
        text-align: center;
        font-size: 13px;
        color: #666;
        background: rgba(255, 255, 255, 0.4);
    }

    .sidebar-footer a {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 10px;
        padding: 10px 15px;
        border-radius: 10px;
        text-decoration: none;
        color: #dc3545;
        font-size: 14px;
        font-weight: 500;
        transition: background 0.2s;
    }

    .sidebar-footer a:hover {
        background: rgba(220, 53, 69, 0.1);
    }

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

    .badge-count {
        background: #dc3545;
        color: white;
        border-radius: 10px;
        padding: 1px 7px;
        font-size: 11px;
        font-weight: 700;
        margin-left: auto;
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
            <h3><i class="fas fa-chart-line"></i> Sales CRM</h3>
            <p>
                <c:if test="${not empty sessionScope.userSession}">
                    ${sessionScope.userSession.staffInfo.fullName}
                </c:if>
            </p>
        </div>

        <nav class="sidebar-nav">
            <div class="nav-section">Dashboard</div>
            <a href="${pageContext.request.contextPath}/sales/dashboard"><i class="fas fa-tachometer-alt"></i> Tổng quan</a>
            <a href="${pageContext.request.contextPath}/sale/dashboard"><i class="fas fa-calendar-check"></i> Activity Dashboard</a>
            <div class="nav-section">Communication</div>
            <a href="${pageContext.request.contextPath}/emails/compose">
                <i class="fas fa-paper-plane"></i> Soạn Email
            </a>
            <div class="nav-section">Pipeline</div>
            <a href="${pageContext.request.contextPath}/sales/pipeline-board"><i class="fas fa-columns"></i> Kanban Board</a>
            <a href="${pageContext.request.contextPath}/sales/opportunities"><i class="fas fa-handshake"></i> Opportunities</a>
            <c:if test="${sessionScope.userSession.admin}">
                <a href="${pageContext.request.contextPath}/sales/pipeline-config"><i class="fas fa-sliders-h"></i> Cấu hình Pipeline</a>
            </c:if>

            <div class="nav-section">Báo giá & Đơn hàng</div>
            <a href="${pageContext.request.contextPath}/sales/products"><i class="fas fa-box"></i> Sản phẩm</a>

            <div class="nav-section">Leads</div>
            <a href="${pageContext.request.contextPath}/sales/my-leads"><i class="fas fa-user-tag"></i> Leads được giao</a>

<!--            <div class="nav-section">Marketing</div>
            <a href="${pageContext.request.contextPath}/marketing/campaignmanagement"><i class="fas fa-bullhorn"></i> Chiến dịch</a>
            <a href="${pageContext.request.contextPath}/marketing/leadmanagement"><i class="fas fa-user-plus"></i> Quản lý Leads</a>-->
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
        const links = document.querySelectorAll(".sidebar-nav a");

        if (toggleButton) {
            toggleButton.addEventListener("click", () => {
                sidebar.classList.toggle("active");
            });
        }

        // Highlight active link based on current URL
        const currentPath = window.location.pathname;
        links.forEach(link => {
            const href = link.getAttribute("href");
            if (href && currentPath.includes(href.split("/").pop())) {
                link.classList.add("active");
            }
        });
    });
</script>
