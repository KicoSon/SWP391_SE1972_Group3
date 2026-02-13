<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách ticket</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/support/agent/assets/css/ticket-list.css">
 <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-uyd8I+TiFqouBZfyqCkCmZJLdnOjFkMrDXLI4sdAlnXrhIRbkIuAeGHNirMRHkRkNvztNFVQVwQ9Ii84x0X5fQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico">
</head>
<body>
    <jsp:include page="/components/sidebar.jsp" />

    <div class="main-content">
        <div class="content-wrapper">
            <div class="page-header">
                <div>
                    <h1><i class="fas fa-ticket-alt"></i> Ticket của tôi</h1>
                    <p>Theo dõi, lọc và xử lý các ticket được giao cho bạn.</p>
                </div>
                
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i>
                    <span>${successMessage}</span>
                </div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-circle-exclamation"></i>
                    <span>${errorMessage}</span>
                </div>
            </c:if>

            <div class="filters-card">
                <form method="get" action="${pageContext.request.contextPath}/support/agent/tickets" data-filters-form="true" data-base-action="${pageContext.request.contextPath}/support/agent/tickets">
                    <div class="filters-grid">
                        <div class="filter-field">
                            <label for="search">Tìm kiếm</label>
                            <input type="text" id="search" name="search" placeholder="Từ khóa, tên khách hàng, email, SĐT" value="${searchTerm != null ? fn:escapeXml(searchTerm) : ''}" />
                        </div>
                        <div style="margin-left: 30px" class="filter-field">
                            <label for="status">Trạng thái</label>
                            <select id="status" name="status">
                                <option value="">Tất cả</option>
                                <c:forEach var="status" items="${availableStatuses}">
                                    <option value="${status}" ${status == statusFilter ? 'selected' : ''}>${status}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filter-field">
                            <label for="priority">Độ ưu tiên</label>
                            <select id="priority" name="priority">
                                <option value="">Tất cả</option>
                                <c:forEach var="priority" items="${availablePriorities}">
                                    <option value="${priority}" ${priority == priorityFilter ? 'selected' : ''}>${priority}</option>
                                </c:forEach>
                            </select>
                        </div>
                       
                       
                        <div class="filter-actions">
                            <button type="submit" class="btn-primary">
                                <i class="fas fa-filter"></i>
                                Áp dụng
                            </button>
                            <button type="button" class="btn-secondary" data-reset-filters>
                                <i class="fas fa-rotate"></i>
                                Đặt lại
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <div class="ticket-card">
                <div class="table-header">
                    <h3>Danh sách ticket</h3>
                    <div class="page-size-selector">
                        <span>Hiển thị</span>
                        <form method="get" action="${pageContext.request.contextPath}/support/agent/tickets" data-page-size-form>
                            <select name="pageSize" data-page-size>
                                <c:forEach var="option" items="${pageSizeOptions}">
                                    <option value="${option}" ${option == pageSize ? 'selected' : ''}>${option}</option>
                                </c:forEach>
                            </select>
                            <input type="hidden" name="status" value="${statusFilter != null ? statusFilter : ''}" />
                            <input type="hidden" name="priority" value="${priorityFilter != null ? priorityFilter : ''}" />
                            <input type="hidden" name="search" value="${searchTerm != null ? fn:escapeXml(searchTerm) : ''}" />
                            <input type="hidden" name="sortBy" value="${sortBy != null ? sortBy : ''}" />
                            <input type="hidden" name="sortOrder" value="${sortOrder != null ? sortOrder : ''}" />
                            <input type="hidden" name="page" value="1" />
                        </form>
                        <span>mỗi trang</span>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${not empty tickets}">
                        <table class="ticket-table">
                            <thead>
                                <tr>
                                    <th>Mã</th>
                                    <th>Tiêu đề &amp; khách hàng</th>
                                    <th>Độ ưu tiên</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày tạo</th>
                                    <th>Cập nhật</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="ticket" items="${tickets}">
                                    <tr>
                                        <td>#${ticket.id}</td>
                                        <td>
                                            <div class="ticket-subject">${ticket.subject}</div>
                                            <div class="ticket-meta">
                                                <i class="fas fa-user"></i>
                                                ${ticket.customerName}
                                                <c:if test="${not empty ticket.customerPhone}">
                                                    • ${ticket.customerPhone}
                                                </c:if>
                                            </div>
                                        </td>
                                        <td>
                                            <span class="priority-pill ${ticket.priority}">
                                                <i class="fas fa-signal"></i>
                                                ${ticket.priority}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="status-badge ${ticket.status}">
                                                <i class="fas fa-circle"></i>
                                                ${ticket.status}
                                            </span>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${ticket.createdAtDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${ticket.updatedAtDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </td>
                                        <td class="actionss">
                                            <c:url var="detailUrl" value="/support/agent/tickets/details">
                                                <c:param name="id" value="${ticket.id}" />
                                            </c:url>
                                            <a href="${detailUrl}" title="Xem chi tiết">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <i class="fas fa-ticket"></i>
                            <h4>Chưa có ticket phù hợp</h4>
                            <p>Thử thay đổi bộ lọc hoặc tìm kiếm khác.</p>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="pagination-wrapper">
                    <div class="pagination-info">
                        <c:set var="startItem" value="${(currentPage - 1) * pageSize + 1}" />
                        <c:set var="endItem" value="${currentPage * pageSize}" />
                        <c:if test="${endItem > totalItems}">
                            <c:set var="endItem" value="${totalItems}" />
                        </c:if>
                        Hiển thị ${totalItems == 0 ? 0 : startItem} - ${endItem} trên ${totalItems} ticket
                    </div>
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <c:url var="prevUrl" value="/support/agent/tickets">
                                <c:param name="page" value="${currentPage - 1}" />
                                <c:param name="pageSize" value="${pageSize}" />
                                <c:if test="${not empty statusFilter}">
                                    <c:param name="status" value="${statusFilter}" />
                                </c:if>
                                <c:if test="${not empty priorityFilter}">
                                    <c:param name="priority" value="${priorityFilter}" />
                                </c:if>
                                <c:if test="${not empty searchTerm}">
                                    <c:param name="search" value="${searchTerm}" />
                                </c:if>
                                <c:if test="${not empty sortBy}">
                                    <c:param name="sortBy" value="${sortBy}" />
                                </c:if>
                                <c:if test="${not empty sortOrder}">
                                    <c:param name="sortOrder" value="${sortOrder}" />
                                </c:if>
                            </c:url>
                            <a href="${prevUrl}" aria-label="Trang trước">
                                <i class="fas fa-chevron-left"></i>
                            </a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="pageIndex">
                            <c:url var="pageUrl" value="/support/agent/tickets">
                                <c:param name="page" value="${pageIndex}" />
                                <c:param name="pageSize" value="${pageSize}" />
                                <c:if test="${not empty statusFilter}">
                                    <c:param name="status" value="${statusFilter}" />
                                </c:if>
                                <c:if test="${not empty priorityFilter}">
                                    <c:param name="priority" value="${priorityFilter}" />
                                </c:if>
                                <c:if test="${not empty searchTerm}">
                                    <c:param name="search" value="${searchTerm}" />
                                </c:if>
                                <c:if test="${not empty sortBy}">
                                    <c:param name="sortBy" value="${sortBy}" />
                                </c:if>
                                <c:if test="${not empty sortOrder}">
                                    <c:param name="sortOrder" value="${sortOrder}" />
                                </c:if>
                            </c:url>
                            <c:choose>
                                <c:when test="${pageIndex == currentPage}">
                                    <span class="active">${pageIndex}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageUrl}">${pageIndex}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <c:url var="nextUrl" value="/support/agent/tickets">
                                <c:param name="page" value="${currentPage + 1}" />
                                <c:param name="pageSize" value="${pageSize}" />
                                <c:if test="${not empty statusFilter}">
                                    <c:param name="status" value="${statusFilter}" />
                                </c:if>
                                <c:if test="${not empty priorityFilter}">
                                    <c:param name="priority" value="${priorityFilter}" />
                                </c:if>
                                <c:if test="${not empty searchTerm}">
                                    <c:param name="search" value="${searchTerm}" />
                                </c:if>
                                <c:if test="${not empty sortBy}">
                                    <c:param name="sortBy" value="${sortBy}" />
                                </c:if>
                                <c:if test="${not empty sortOrder}">
                                    <c:param name="sortOrder" value="${sortOrder}" />
                                </c:if>
                            </c:url>
                            <a href="${nextUrl}" aria-label="Trang tiếp">
                                <i class="fas fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
    <script src="${pageContext.request.contextPath}/support/agent/assets/js/ticket-list.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const ticketsLink = document.querySelector('a[href$="/support/agent/tickets"]');
            if (ticketsLink) {
                const navItem = ticketsLink.closest('.nav-item');
                if (navItem) {
                    navItem.classList.add('active');
                }
            }
        });
    </script>
</body>
</html>
