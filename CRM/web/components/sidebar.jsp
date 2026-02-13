<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Sidebar Component -->
<div class="sidebar" id="sidebar">
    <div class="sidebar-header">
        <div class="logo">
            <div class="logo-icon">
                <i class="fas fa-headset"></i>
            </div>
            <span class="logo-texts">FPT Supporter</span>
        </div>
       
    </div>
    
    <div class="sidebar-content">
        <!-- User Info -->
        <div class="user-info">
            <div class="user-avatar">
                <c:choose>
                    <c:when test="${userSession.userType == 'CUSTOMER'}">
                        <i class="fas fa-user"></i>
                    </c:when>
                    <c:otherwise>
                        <i class="fas fa-user-tie"></i>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="user-details">
                <div class="user-name">
                    ${userSession.displayName}
                </div>
                <div class="user-role">
                    <c:choose>
                        <c:when test="${userSession.userType == 'CUSTOMER'}">
                            Customer
                        </c:when>
                        <c:when test="${userSession.isAdmin()}">
                            Administrator
                        </c:when>
                        <c:otherwise>
                            Support Agent
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <!-- Navigation Items -->
        <nav class="sidebar-nav">
            <ul class="nav-list">
                <!-- CUSTOMER MENU (completely separate) -->
                <c:if test="${userSession.userType == 'CUSTOMER'}">
                    <li class="nav-item active">
                        <a href="${pageContext.request.contextPath}/customer/dashboard" class="nav-link">
                            <i class="fas fa-tachometer-alt"></i>
                            <span class="nav-text">Dashboard</span>
                        </a>
                    </li>
                    
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/customer/create-ticket" class="nav-link">
                            <i class="fas fa-plus-circle"></i>
                            <span class="nav-text">Tạo Phiếu Hỗ Trợ</span>
                        </a>
                    </li>
                    
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/customer/tickets" class="nav-link">
                            <i class="fas fa-ticket-alt"></i>
                            <span class="nav-text">Phiếu Hỗ Trợ Của Tôi</span>
                        </a>
                    </li>
                    
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/customer/orders" class="nav-link">
                            <i class="fas fa-shopping-cart"></i>
                            <span class="nav-text">Đơn Hàng</span>
                        </a>
                    </li>
                    
                    <li class="nav-divider"></li>
                    
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/profile" class="nav-link">
                            <i class="fas fa-user-cog"></i>
                            <span class="nav-text">Thông Tin Cá Nhân</span>
                        </a>
                    </li>
                </c:if>
                
                <!-- STAFF MENU (all non-customer users - consolidated) -->
                <c:if test="${userSession.userType != 'CUSTOMER'}">
                    <!-- Dashboard - Role-specific -->
                    <li class="nav-item">
                        <c:choose>
                            <c:when test="${userSession.isAdmin()}">
                                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-link">
                                    <i class="fas fa-tachometer-alt"></i>
                                    <span class="nav-text">Dashboard</span>
                                </a>
                            </c:when>
                            <c:when test="${userSession.isSupportManager()}">
                                <a href="${pageContext.request.contextPath}/support/manager/dashboard" class="nav-link">
                                    <i class="fas fa-tachometer-alt"></i>
                                    <span class="nav-text">Dashboard Manager</span>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/support/agent/dashboard" class="nav-link">
                                    <i class="fas fa-tachometer-alt"></i>
                                    <span class="nav-text">Dashboard</span>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                    
                    <!-- Common Staff Features: Support Section -->
                    <li class="nav-section-title">Hỗ trợ</li>
                    
                    <!-- Tickets - shown to all staff with role-appropriate labels and badges -->
                    <li class="nav-item">
                        <c:choose>
                            <c:when test="${userSession.isAdmin() || userSession.isSupportManager()}">
                                <a href="${pageContext.request.contextPath}/support/manager/tickets" class="nav-link">
                                    <i class="fas fa-headset"></i>
                                    <span class="nav-text">Tất cả Tickets</span>
                                    <c:if test="${not empty assignedTickets}">
                                        <span class="nav-badge">${assignedTickets}</span>
                                    </c:if>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/support/agent/tickets" class="nav-link">
                                    <i class="fas fa-ticket-alt"></i>
                                    <span class="nav-text">Tickets của tôi</span>
                                    <c:if test="${not empty assignedTickets}">
                                        <span class="nav-badge">${assignedTickets}</span>
                                    </c:if>
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </li>
                    
                   
                    
                    <c:if test="${userSession.isSupportAgent() && !userSession.isSupportManager() && !userSession.isAdmin()}">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/support/agent/tickets?status=pending" class="nav-link">
                                <i class="fas fa-clock"></i>
                                <span class="nav-text">Đang chờ</span>
                                <c:if test="${not empty pendingTickets}">
                                    <span class="nav-badge nav-badge-warning">${pendingTickets}</span>
                                </c:if>
                            </a>
                        </li>
                    </c:if>
                    
                    <!-- Common Management Section -->
                    <li class="nav-section-title">Quản lý</li>
                    
                    <!-- Customer Management - shown to all staff -->
                    <li class="nav-item">
                        <a href="${pageContext.request.contextPath}/managecustomer" class="nav-link">
                            <i class="fas fa-users"></i>
                            <span class="nav-text">Khách hàng</span>
                        </a>
                    </li>
                    
                    <!-- ADMIN-ONLY Features -->
                    <c:if test="${userSession.isAdmin()}">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/orders" class="nav-link">
                                <i class="fas fa-shopping-cart"></i>
                                <span class="nav-text">Đơn hàng</span>
                            </a>
                        </li>
                        
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/products" class="nav-link">
                                <i class="fas fa-box"></i>
                                <span class="nav-text">Sản phẩm</span>
                            </a>
                        </li>
                        
                        <li class="nav-divider"></li>
                        
                        <li class="nav-section-title">Quản trị</li>
                        
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/staff" class="nav-link">
                                <i class="fas fa-user-tie"></i>
                                <span class="nav-text">Nhân viên</span>
                            </a>
                        </li>
                        
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/departments" class="nav-link">
                                <i class="fas fa-building"></i>
                                <span class="nav-text">Phòng ban</span>
                            </a>
                        </li>
                        
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/roles" class="nav-link">
                                <i class="fas fa-user-shield"></i>
                                <span class="nav-text">Vai trò</span>
                            </a>
                        </li>
                        
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/permissions" class="nav-link">
                                <i class="fas fa-key"></i>
                                <span class="nav-text">Quyền</span>
                            </a>
                        </li>
                        
                        <li class="nav-divider"></li>
                        
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/reports" class="nav-link">
                                <i class="fas fa-chart-bar"></i>
                                <span class="nav-text">Báo cáo</span>
                            </a>
                        </li>
                    </c:if>
                    
                    <!-- MANAGER-ONLY Features (if not admin) -->
                    <c:if test="${userSession.isSupportManager() && !userSession.isAdmin()}">
                        <li class="nav-divider"></li>
                        
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/admin/staff" class="nav-link">
                                <i class="fas fa-user-tie"></i>
                                <span class="nav-text">Nhân viên</span>
                            </a>
                        </li>
                    </c:if>
                    
                    <!-- Profile - common to all staff -->
                    <li class="nav-divider"></li>
                    
                    <li class="nav-item">
                        <li class="nav-item">
                            <a href="${pageContext.request.contextPath}/profile" class="nav-link">
                                <i class="fas fa-chart-bar"></i>
                                <span class="nav-text"> Tài Khoản </span>
                            </a>
                        </li>
                    </li>
                </c:if>
                
                <li class="nav-item">
                    <a href="${pageContext.request.contextPath}/logout" class="nav-link nav-link-danger">
                        <i class="fas fa-sign-out-alt"></i>
                        <span class="nav-text">Logout</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
    
    <!-- Sidebar Footer -->
    <div class="sidebar-footer">
        <div class="footer-info">
            <div class="footer-text">
                <c:choose>
                    <c:when test="${userSession.userType == 'CUSTOMER'}">
                        Customer Portal
                    </c:when>
                    <c:when test="${userSession.isAdmin()}">
                        Admin Panel
                    </c:when>
                    <c:otherwise>
                        Support Agent Panel
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="footer-version">v2.0.1</div>
        </div>
    </div>
</div>

<!-- Sidebar Overlay (for mobile) -->
<div class="sidebar-overlay" id="sidebarOverlay"></div>
