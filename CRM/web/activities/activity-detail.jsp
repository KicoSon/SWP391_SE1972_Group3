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
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        :root {
            --primary: #6366F1; --primary-hover: #4F46E5;
            --success: #10B981; --warning: #F59E0B; --danger: #EF4444; --info: #3B82F6;
            --gray-50: #F9FAFB; --gray-100: #F3F4F6; --gray-200: #E5E7EB;
            --gray-300: #D1D5DB; --gray-500: #6B7280; --gray-700: #374151; --gray-900: #111827;
        }

        body {
            font-family: 'Outfit', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 24px;
        }

        .container { max-width: 1200px; margin: 0 auto; }

        /* Back Button */
        .back-button {
            display: inline-flex; align-items: center; gap: 8px;
            background: white; color: var(--gray-700); padding: 12px 20px;
            border-radius: 10px; text-decoration: none; font-weight: 600;
            font-size: 14px; margin-bottom: 24px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
        }
        .back-button:hover { transform: translateX(-4px); box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15); }

        /* Main Card */
        .detail-card {
            background: white; border-radius: 20px; box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
            overflow: hidden; animation: slideIn 0.5s ease;
        }
        @keyframes slideIn { from { opacity: 0; transform: translateY(30px); } to { opacity: 1; transform: translateY(0); } }

        /* Header */
        .detail-header {
            background: linear-gradient(135deg, #6366F1 0%, #8B5CF6 100%);
            padding: 40px; color: white; position: relative; overflow: hidden;
        }
        .detail-header::before {
            content: ''; position: absolute; top: -50%; right: -20%;
            width: 400px; height: 400px; background: rgba(255, 255, 255, 0.1); border-radius: 50%;
        }
        .header-content { position: relative; z-index: 1; }
        .header-top { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; }
        .header-left { display: flex; align-items: center; gap: 20px; }
        
        .type-badge {
            width: 70px; height: 70px; background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px); border-radius: 16px; display: flex;
            align-items: center; justify-content: center; font-size: 32px;
            border: 2px solid rgba(255, 255, 255, 0.3);
        }
        
        .header-info h1 { font-size: 32px; font-weight: 800; margin-bottom: 8px; text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); }
        .activity-id { font-size: 14px; opacity: 0.9; font-weight: 500; }
        .header-actions { display: flex; gap: 12px; }

        .btn {
            padding: 12px 24px; border-radius: 10px; border: none; font-weight: 600;
            font-size: 14px; cursor: pointer; transition: all 0.3s ease;
            display: inline-flex; align-items: center; gap: 8px; text-decoration: none;
        }
        .btn-white { background: white; color: var(--primary); }
        .btn-white:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15); }

        .status-display {
            display: inline-flex; align-items: center; gap: 8px; padding: 10px 20px;
            background: rgba(255, 255, 255, 0.2); backdrop-filter: blur(10px);
            border-radius: 30px; font-weight: 600; font-size: 14px;
            border: 2px solid rgba(255, 255, 255, 0.3);
        }

        /* Body */
        .detail-body { padding: 40px; }
        .section { margin-bottom: 40px; }
        .section-title {
            font-size: 14px; font-weight: 800; color: var(--gray-500); text-transform: uppercase;
            letter-spacing: 1px; margin-bottom: 20px; display: flex; align-items: center; gap: 8px;
        }
        .section-title::before { content: ''; width: 4px; height: 20px; background: var(--primary); border-radius: 2px; }

        /* Field Group */
        .field-group {
            background: var(--gray-50); border: 2px solid var(--gray-200);
            border-radius: 12px; padding: 24px; margin-bottom: 20px; transition: all 0.3s ease;
        }
        .field-group:hover { border-color: var(--gray-300); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); }
        .field-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
        .field-label { font-size: 13px; font-weight: 700; color: var(--gray-500); text-transform: uppercase; letter-spacing: 0.5px; }
        
        /* Edit link style replaced the button */
        .edit-btn {
            background: none; border: none; color: var(--primary); font-size: 12px;
            font-weight: 700; cursor: pointer; padding: 6px 14px; border-radius: 6px;
            transition: all 0.2s ease; text-transform: uppercase; letter-spacing: 0.5px;
            text-decoration: none;
        }
        .edit-btn:hover { background: rgba(99, 102, 241, 0.1); }

        .field-value { font-size: 16px; color: var(--gray-900); font-weight: 500; line-height: 1.6; min-height: 24px; white-space: pre-wrap; }
        .field-value.large { font-size: 20px; font-weight: 600; }

        .fields-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }

        /* Customer Info / Links */
        .customer-info {
            display: flex; align-items: center; gap: 16px; padding: 20px;
            background: linear-gradient(135deg, #EEF2FF 0%, #E0E7FF 100%);
            border-radius: 12px; border: 2px solid #C7D2FE; margin-bottom: 15px;
        }
        .customer-avatar {
            width: 56px; height: 56px; background: var(--primary); color: white;
            border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24px; font-weight: 700;
        }

        /* Status Badge */
        .status-badge {
            display: inline-flex; align-items: center; gap: 8px; padding: 8px 16px;
            border-radius: 20px; font-size: 13px; font-weight: 700; text-transform: capitalize;
        }
        .status-completed { background: #D1FAE5; color: #065F46; }
        .status-in-progress { background: #DBEAFE; color: #1E40AF; }
        .status-planned { background: #FEF3C7; color: #92400E; }
        .status-overdue { background: #FEE2E2; color: #991B1B; }

        /* Custom Styles for Attachments & Participants */
        .participant-badge { display: inline-block; padding: 6px 14px; background: #e0e7ff; color: #4338ca; border-radius: 20px; font-size: 14px; margin-right: 8px; font-weight: 600; }
        .attachment-item {
            display: flex; align-items: center; justify-content: space-between; padding: 15px;
            background: #fff; border-radius: 8px; border: 1px solid #e2e8f0; margin-bottom: 10px;
            transition: all 0.3s;
        }
        .attachment-item:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.05); border-color: var(--primary); }
        .btn-download { background: var(--primary); color: white; padding: 8px 16px; border-radius: 8px; text-decoration: none; font-size: 13px; font-weight: 600; }
        .btn-download:hover { background: var(--primary-hover); }

        @media (max-width: 968px) { .fields-grid { grid-template-columns: 1fr; } }
    </style>
</head>
<body>
    <div class="container">
        <a href="${pageContext.request.contextPath}/sale/dashboard" class="back-button">
            <i class="fas fa-arrow-left"></i> Quay lại Dashboard
        </a>

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
                        <div class="header-actions">
                            <a href="${pageContext.request.contextPath}/activities/create?id=${activity.id}" class="btn btn-white">
                                <i class="fas fa-pen"></i> Chỉnh sửa toàn bộ
                            </a>
                        </div>
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