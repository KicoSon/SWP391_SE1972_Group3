<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

<style>
    :root {
        --primary-gradient: linear-gradient(135deg, #667eea, #764ba2);
        --primary-color: #667eea;
    }
    body { margin: 0; font-family: 'Segoe UI', sans-serif; }

    .sidebar {
        width: 260px; height: 100vh; position: fixed; top: 0; left: 0;
        background: rgba(255,255,255,0.93); backdrop-filter: blur(15px);
        box-shadow: 6px 0 20px rgba(0,0,0,0.1);
        display: flex; flex-direction: column; z-index: 1000;
    }
    .sidebar-header {
        padding: 20px;
        background: var(--primary-gradient);
        color: white; text-align: center;
    }
    .sidebar-header h3 { margin: 0; font-size: 18px; font-weight: 700; }
    .sidebar-header p  { margin: 4px 0 0; font-size: 12px; opacity: 0.85; }

    .sidebar-nav { flex: 1; padding: 15px 0; overflow-y: auto; }
    .nav-section { padding: 8px 20px 4px; font-size: 10px; font-weight: 700;
        color: #aaa; text-transform: uppercase; letter-spacing: 1px; }

    .sidebar-nav a {
        display: flex; align-items: center; gap: 12px;
        padding: 11px 20px; text-decoration: none;
        color: #555; font-size: 14px; transition: all 0.2s;
        border-left: 3px solid transparent;
    }
    .sidebar-nav a:hover, .sidebar-nav a.active {
        background: rgba(102,126,234,0.1);
        color: #667eea;
        border-left-color: #667eea;
    }
    .sidebar-nav a i { width: 18px; text-align: center; }

    .sidebar-footer {
        padding: 15px 20px;
        border-top: 1px solid rgba(0,0,0,0.08);
    }
    .sidebar-footer a {
        display: flex; align-items: center; gap: 10px;
        padding: 10px 15px; border-radius: 10px;
        text-decoration: none; color: #dc3545; font-size: 14px;
        transition: background 0.2s;
    }
    .sidebar-footer a:hover { background: rgba(220,53,69,0.1); }

    .badge-count {
        background: #dc3545; color: white;
        border-radius: 10px; padding: 1px 7px;
        font-size: 11px; font-weight: 700; margin-left: auto;
    }
</style>

<div class="sidebar">
    <div class="sidebar-header">
        <h3><i class="fas fa-chart-line me-1"></i> Sales CRM</h3>
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

        <div class="nav-section">Pipeline</div>
<a href="${pageContext.request.contextPath}/sales/pipeline-board"><i class="fas fa-columns"></i> Kanban Board</a>
        <a href="${pageContext.request.contextPath}/sales/opportunities"><i class="fas fa-handshake"></i> Opportunities</a>
        <a href="${pageContext.request.contextPath}/sales/pipeline-config"><i class="fas fa-sliders-h"></i> Cấu hình Pipeline</a>

        <div class="nav-section">Báo giá & Đơn hàng</div>
        <a href="${pageContext.request.contextPath}/sales/products"><i class="fas fa-box"></i> Sản phẩm</a>

        <div class="nav-section">Marketing</div>
        <a href="${pageContext.request.contextPath}/marketing/campaignmanagement"><i class="fas fa-bullhorn"></i> Chiến dịch</a>
        <a href="${pageContext.request.contextPath}/marketing/leadmanagement"><i class="fas fa-user-plus"></i> Leads</a>
    </nav>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Đăng xuất</a>
    </div>
</div>
