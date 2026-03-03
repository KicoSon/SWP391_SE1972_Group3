<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết công việc - ${activity.title}</title>
        <link href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-detail.css">
    </head>
    <body>
    </head>
<body>
    
    <div class="container">
        <c:if test="${param.view != 'modal'}">
            <a href="${pageContext.request.contextPath}/sale/dashboard" class="back-button">
                <i class="fas fa-arrow-left"></i> Quay lại Dashboard
            </a>
        </c:if>
        

        <div class="detail-card">
            <div class="detail-header">
                <div class="header-content">
                    <div class="header-top">
                        <div class="header-left">
                            <div class="type-badge">
                                <c:choose>
                                    <c:when test="${activity.type == 'Call'}"><i class="fas fa-phone"></i></c:when>
                                    <c:when test="${activity.type == 'Email'}"><i class="fas fa-envelope"></i></c:when>
                                    <c:when test="${activity.type == 'Meeting'}"><i class="fas fa-users"></i></c:when>
                                    <c:otherwise><i class="fas fa-tasks"></i></c:otherwise>
                                </c:choose>
                            </div>
                            <div class="header-info">
                                <h1>${activity.title}</h1>
                                <div class="activity-id">Activity ID: <span>#${activity.id}</span> | Tạo bởi: <b>${activity.creatorName}</b></div>
                            </div>
                        </div>
                        <c:if test="${sessionScope.userSession != null 
                                      and sessionScope.userSession.userId == activity.createdBy}">
                              <div class="header-actions">
                                  <a href="${pageContext.request.contextPath}/activities/create?id=${activity.id}" class="btn btn-white" target="_parent">
                                      <i class="fas fa-pen"></i> Chỉnh sửa toàn bộ
                                  </a>
                              </div>
                        </c:if>
                    </div>
                    <div class="status-display">
                        <i class="fas fa-info-circle"></i> Trạng thái hiện tại: <b>${activity.status}</b>
                    </div>
                </div>
            </div>

            <div class="detail-body">
                <div class="section">
                    <h2 class="section-title"><i class="fas fa-info-circle"></i> Thông tin cơ bản</h2>
                    <div class="fields-grid">
                        <div class="field-group">
                            <div class="field-header">
                                <div class="field-label">Loại hoạt động</div>
                            </div>
                            <div class="field-value">${activity.type}</div>
                        </div>
                        <div class="field-group">
                            <div class="field-header">
                                <div class="field-label">Mức độ ưu tiên</div>
                            </div>
                            <div class="field-value" style="color: ${activity.priority == 'High' ? 'red' : 'inherit'}; font-weight: 700;">
                                ${activity.priority}
                            </div>
                        </div>
                    </div>

                    <div class="field-group">
                        <div class="field-header">
                            <div class="field-label">Mô tả công việc</div>

                        </div>
                        <div class="field-value"><c:out value="${empty activity.description ? 'Không có mô tả.' : activity.description}" /></div>
                    </div>

                    <c:if test="${not empty activity.outcomeNotes}">
                        <div class="field-group" style="background: #FFFBEB; border-color: #FCD34D;">
                            <div class="field-header">
                                <div class="field-label" style="color: #D97706;">Kết quả thực hiện (Outcome)</div>
                            </div>
                            <div class="field-value"><c:out value="${activity.outcomeNotes}" /></div>
                        </div>
                    </c:if>
                </div>

                <div class="section">
                    <h2 class="section-title"><i class="far fa-clock"></i> Chi tiết & Thời gian</h2>
                    <div class="fields-grid">
                        <div class="field-group">
                            <div class="field-header">
                                <div class="field-label">Hạn chót (Due Date)</div>
                            </div>
                            <div class="field-value large" style="color: #EF4444;">
                                <fmt:formatDate value="${activity.dueDate}" pattern="dd/MM/yyyy HH:mm" />
                            </div>
                        </div>

                        <div class="field-group">
                            <div class="field-header">
                                <div class="field-label">Trạng thái</div>
                            </div>
                            <div class="field-value">
                                <span class="status-badge status-${activity.status.toLowerCase().replace(' ', '-')}">${activity.status}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="section">
                    <h2 class="section-title"><i class="fas fa-link"></i> Đối tượng liên kết</h2>
                    <div class="fields-grid">
                        <c:if test="${not empty activity.customerName}">
                            <div class="customer-info">
                                <div class="customer-avatar"><i class="fas fa-building"></i></div>
                                <div class="customer-details">
                                    <p style="text-transform: uppercase; font-size: 12px; font-weight: bold;">Khách hàng</p>
                                    <h3>${activity.customerName}</h3>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${not empty activity.leadName}">
                            <div class="customer-info" style="background: #F0FDF4; border-color: #BBF7D0;">
                                <div class="customer-avatar" style="background: #22C55E;"><i class="fas fa-bullseye"></i></div>
                                <div class="customer-details">
                                    <p style="text-transform: uppercase; font-size: 12px; font-weight: bold;">Tiềm năng (Lead)</p>
                                    <h3>${activity.leadName}</h3>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${not empty activity.opportunityTitle}">
                            <div class="customer-info" style="background: #FEF2F2; border-color: #FECACA;">
                                <div class="customer-avatar" style="background: #EF4444;"><i class="fas fa-handshake"></i></div>
                                <div class="customer-details">
                                    <p style="text-transform: uppercase; font-size: 12px; font-weight: bold;">Cơ hội (Opportunity)</p>
                                    <h3>${activity.opportunityTitle}</h3>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>

                <div class="section">
                    <h2 class="section-title">
                        <i class="fas fa-users"></i> Nhân sự tham gia
                        <a href="${pageContext.request.contextPath}/activities/create?id=${activity.id}" style="margin-left: 10px; font-size: 14px;"><i class="fas fa-pen"></i></a>
                    </h2>
                    <div class="field-group">
                        <c:forEach items="${participants}" var="person">
                            <span class="participant-badge"><i class="fas fa-user-circle"></i> ${person}</span>
                        </c:forEach>
                        <c:if test="${empty participants}">Chưa có nhân sự nào được phân công.</c:if>
                        </div>
                    </div>

                    <div class="section">
                        <h2 class="section-title"><i class="fas fa-paperclip"></i> Tài liệu đính kèm (${attachments.size()})</h2>
                    <c:forEach items="${attachments}" var="file">
                        <div class="attachment-item">
                            <div style="display: flex; align-items: center; gap: 15px;">
                                <i class="fas fa-file-pdf" style="font-size: 24px; color: #EF4444;"></i>
                                <div>
                                    <div style="font-weight: 600; color: #111827;">${file.fileName}</div>
                                    <div style="font-size: 12px; color: #6B7280;">Up lúc: <fmt:formatDate value="${file.uploadedAt}" pattern="dd/MM HH:mm" /></div>
                                </div>
                            </div>
                            <a href="${pageContext.request.contextPath}/download?file=${file.filePath}" class="btn-download">
                                <i class="fas fa-download"></i> Tải về
                            </a>
                        </div>
                    </c:forEach>
                    <c:if test="${empty attachments}">
                        <div class="field-group" style="text-align: center; color: #6B7280;">Không có tài liệu nào đính kèm.</div>
                    </c:if>
                </div>

                <div class="section">
                    <h2 class="section-title"><i class="fas fa-history"></i> Lịch sử tạo</h2>
                    <div class="field-group" style="display: flex; gap: 40px;">
                        <div>
                            <div class="field-label" style="margin-bottom: 5px;">Tạo lúc</div>
                            <div class="field-value"><fmt:formatDate value="${activity.createdAt}" pattern="dd/MM/yyyy HH:mm" /></div>
                        </div>
                        <div>
                            <div class="field-label" style="margin-bottom: 5px;">Cập nhật cuối</div>
                            <div class="field-value"><fmt:formatDate value="${activity.updatedAt}" pattern="dd/MM/yyyy HH:mm" /></div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</body>
</html>