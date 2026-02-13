<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết ticket #${ticket.id}</title>
 <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/support/agent/assets/css/ticket-details.css">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css" integrity="sha512-uyd8I+TiFqouBZfyqCkCmZJLdnOjFkMrDXLI4sdAlnXrhIRbkIuAeGHNirMRHkRkNvztNFVQVwQ9Ii84x0X5fQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico">
</head>
<body>
    <jsp:include page="/components/sidebar.jsp" />

    <div class="main-content">
        <div class="content-wrapper">
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

            <div class="ticket-header">
                <div class="ticket-header-top">
                    <div class="ticket-title">
                        <span class="status-badge ${ticket.status}" data-status-badge>${statusLabels[ticket.status] != null ? statusLabels[ticket.status] : ticket.status}</span>
                        <h1>${ticket.subject}</h1>
                        <div class="ticket-meta">
                            <span>#${ticket.id}</span>
                            <span><i class="fas fa-calendar-plus"></i> Tạo: <fmt:formatDate value="${ticket.createdAtDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                            <span><i class="fas fa-clock"></i> Cập nhật: <fmt:formatDate value="${ticket.updatedAtDate}" pattern="dd/MM/yyyy HH:mm" /></span>
                        </div>
                    </div>
                    <div class="ticket-actions">
                        <a href="${pageContext.request.contextPath}/support/agent/tickets" class="btn-secondary">
                            <i class="fas fa-arrow-left"></i>
                            Quay lại danh sách
                        </a>
                    </div>
                </div>
            </div>

            <div class="ticket-body">
                <div class="ticket-card">
                    <h3>Thông tin khách hàng</h3>
                    <div class="ticket-info-list">
                        <div class="info-row">
                            <span class="info-label">Khách hàng</span>
                            <span class="info-value">${ticket.customerName}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">Email</span>
                            <span class="info-value">${ticket.customerEmail}</span>
                        </div>
                        <c:if test="${not empty ticket.customerPhone}">
                            <div class="info-row">
                                <span class="info-label">Số điện thoại</span>
                                <span class="info-value">${ticket.customerPhone}</span>
                            </div>
                        </c:if>
                        <c:if test="${not empty ticket.customerCity or not empty ticket.customerProvince}">
                            <div class="info-row">
                                <span class="info-label">Khu vực</span>
                                <span class="info-value">
                                    <c:out value="${ticket.customerCity}" />
                                    <c:if test="${not empty ticket.customerCity and not empty ticket.customerProvince}">, </c:if>
                                    <c:out value="${ticket.customerProvince}" />
                                </span>
                            </div>
                        </c:if>
                        <c:if test="${not empty ticket.orderNumber}">
                            <div class="info-row">
                                <span class="info-label">Đơn hàng</span>
                                <span class="info-value">${ticket.orderNumber}</span>
                            </div>
                        </c:if>
                        <div class="info-row">
                            <span class="info-label">Độ ưu tiên</span>
                            <span class="info-value">
                                <span class="priority-pill ${ticket.priority}">
                                    <i class="fas fa-signal"></i>
                                    ${priorityLabels[ticket.priority] != null ? priorityLabels[ticket.priority] : ticket.priority}
                                </span>
                            </span>
                        </div>
                    </div>
                </div>

                <div>
                    <c:if test="${canEdit}">
                        <div class="update-card">
                            <h3>Cập nhật trạng thái</h3>
                            <form method="post" action="${pageContext.request.contextPath}/support/agent/tickets/details" enctype="multipart/form-data" class="update-form" data-ticket-update-form>
                                <input type="hidden" name="ticketId" value="${ticket.id}" />
                                <div class="form-group">
                                    <label for="status">Trạng thái</label>
                                    <select name="status" id="status" data-ticket-status>
                                        <c:forEach var="status" items="${availableStatuses}">
                                            <option value="${status}" ${status == ticket.status ? 'selected' : ''}>${statusLabels[status] != null ? statusLabels[status] : status}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label for="note">Ghi chú</label>
                                    <textarea name="note" id="note" placeholder="Mô tả hành động bạn đã thực hiện, kết quả, hoặc thông tin bổ sung cho khách hàng."></textarea>
                                </div>
                                <div class="form-group">
                                    <label for="evidence">Bằng chứng (nếu có)</label>
                                    <div class="file-input" data-evidence-label>
                                        <strong>Tải lên bằng chứng (ảnh, PDF, ZIP...)</strong>
                                        <span>Kích thước tối đa 15MB</span>
                                        <input type="file" name="evidence" id="evidence" accept="image/*,.pdf,.zip,.rar,.mp4" data-evidence-input />
                                    </div>
                                </div>
                                <div class="update-actions">
                                    <button type="submit" class="btn-primary">
                                        <i class="fas fa-save"></i>
                                        Lưu cập nhật
                                    </button>
                                </div>
                            </form>
                        </div>
                    </c:if>

                    <div class="history-card">
                        <h3>Lịch sử xử lý</h3>
                        <c:choose>
                            <c:when test="${not empty history}">
                                <div class="history-timeline">
                                    <c:forEach var="item" items="${history}">
                                        <div class="history-item">
                                            <div class="history-marker"></div>
                                            <div class="history-header">
                                                <div class="history-status">
                                                    <i class="fas fa-circle"></i>
                                                    ${statusLabels[item.status] != null ? statusLabels[item.status] : item.status}
                                                </div>
                                                <div class="history-date">
                                                    <fmt:formatDate value="${item.changedAtDate}" pattern="dd/MM/yyyy HH:mm" />
                                                </div>
                                            </div>
                                            <div class="history-note">
                                                <c:choose>
                                                    <c:when test="${not empty item.note}">
                                                        <c:out value="${item.note}" />
                                                    </c:when>
                                                    <c:otherwise>
                                                        <em>Không có ghi chú</em>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <c:if test="${not empty item.evidencePath}">
                                                <c:set var="fullPath" value="${item.evidencePath.startsWith('/') ? item.evidencePath : '/'.concat(item.evidencePath)}" />
                                                <a class="history-evidence" href="${pageContext.request.contextPath}${fullPath}" target="_blank" rel="noopener">
                                                    <i class="fas fa-paperclip"></i>
                                                    Xem bằng chứng
                                                </a>
                                            </c:if>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-history"></i>
                                    <p>Chưa có lịch sử xử lý cho ticket này.</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/sidebar.js"></script>
    <script src="${pageContext.request.contextPath}/support/agent/assets/js/ticket-details.js"></script>
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
