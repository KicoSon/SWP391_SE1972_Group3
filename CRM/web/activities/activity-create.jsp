<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>
            <c:choose>
                <c:when test="${not empty param.id}">Chỉnh Sửa Hoạt Động</c:when>
                <c:otherwise>Tạo Hoạt Động Mới</c:otherwise>
            </c:choose>
        </title>

        <link
            href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=IBM+Plex+Sans:wght@400;500;600&display=swap"
            rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-create.css">
    </head>

    <body>

        <c:catch var="sidebarRenderError">
            <c:choose>
                <c:when test="${sessionScope.userSession.admin}">
                    <jsp:include page="/components/sidebar.jsp" />
                </c:when>
                <c:when test="${sessionScope.userSession.supportStaff}">
                    <jsp:include page="/customerservice/sidebar.jsp" />
                </c:when>
                <c:when test="${sessionScope.userSession.marketingStaff}">
                    <jsp:include page="/marketingg/sidebar.jsp" />
                </c:when>
                <c:otherwise>
                    <jsp:include page="/sales/sidebar.jsp" />
                </c:otherwise>
            </c:choose>
        </c:catch>

        <div class="main-content fade-in">
            <div class="container">
                <div class="browser-header"></div>
                <div class="form-wrapper">

                    <form id="activityForm" method="POST"
                          action="${pageContext.request.contextPath}/activities/create"
                          enctype="multipart/form-data">
                        <c:if test="${not empty param.id}">
                            <input type="hidden" name="id" value="${param.id}">
                        </c:if>

                        <div class="form-header">
                            <h1 class="form-title">
                                <c:choose>
                                    <c:when test="${not empty param.id}">Chỉnh Sửa Hoạt Động</c:when>
                                    <c:otherwise>Tạo Hoạt Động Mới</c:otherwise>
                                </c:choose>
                            </h1>
                            <div class="header-actions">
                                <button type="submit" class="btn btn-primary">
                                    <c:choose>
                                        <c:when test="${not empty param.id}">Cập Nhật</c:when>
                                        <c:otherwise>Lưu Hoạt Động</c:otherwise>
                                    </c:choose>
                                </button>
                                <button type="button" class="btn btn-secondary"
                                        onclick="window.history.back()">Hủy</button>
                            </div>
                        </div>

                        <c:if test="${not empty error}">
                            <div
                                style="padding: 10px 40px; color: red; background: #ffe6e6; font-weight: bold;">
                                ${error}
                            </div>
                        </c:if>

                        <%-- Banner cảnh báo khi quyền hạn chế --%>
                        <c:if test="${canEdit == 'LIMITED'}">
                            <div
                                style="padding: 10px 40px; color: #856404; background: #fff3cd; border-left: 4px solid #ffc107; font-weight: 500; display: flex; align-items: center; gap: 8px;">
                                <i class="fas fa-lock"></i>
                                Bạn là người tham gia công việc này. Bạn chỉ có thể thay đổi <strong>Trạng
                                    thái (Status)</strong>.
                            </div>
                        </c:if>

                        <c:if test="${canEdit == 'LIMITED'}">
                            <div style="padding: 16px 40px 0 40px;">
                                <label class="form-label" style="margin-bottom: 8px; display: block;">Trạng thái (Status):</label>
                                <c:choose>
                                    <c:when test="${activity.status == 'Completed'}">
                                        <input type="text" class="form-control" value="Completed" readonly
                                               style="background-color: #e9ecef; color: #198754; font-weight: bold; border-color: #198754;">
                                        <input type="hidden" name="status" value="Completed">
                                    </c:when>
                                    <c:otherwise>
                                        <select name="status" class="form-control">
                                            <option value="Planned" ${activity.status=='Planned' ? 'selected' : '' }>Planned</option>
                                            <option value="In Progress" ${activity.status=='In Progress' ? 'selected' : '' }>In Progress</option>
                                            <option value="Completed" ${activity.status=='Completed' ? 'selected' : '' }>Completed</option>
                                            <option value="Cancelled" ${activity.status=='Cancelled' ? 'selected' : '' }>Cancelled</option>
                                        </select>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:if>

                        <div class="form-body">
                            <div class="form-grid">

                                <div class="form-row">
                                    <label class="form-label">Type:</label>
                                    <div>
                                        <select class="form-control" name="type" ${canEdit=='LIMITED'
                                                                                   ? 'disabled' : 'required' }>
                                            <option value="Call" ${act.type=='Call' ? 'selected' : '' }>Call
                                            </option>
                                            <option value="Meeting" ${act.type=='Meeting' ? 'selected' : ''
                                                    }>Meeting</option>
                                            <option value="Email" ${act.type=='Email' ? 'selected' : '' }>
                                                Email</option>
                                            <option value="Task" ${act.type=='Task' ? 'selected' : '' }>Task
                                            </option>
                                            <option value="Note" ${act.type=='Note' ? 'selected' : '' }>Note
                                            </option>
                                        </select>
                                        <%-- Gửi hidden value khi bị disabled (disabled field không submit)
                                        --%>
                                        <c:if test="${canEdit == 'LIMITED'}">
                                            <input type="hidden" name="type" value="${act.type}">
                                        </c:if>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Title: <span class="required">*</span></label>
                                    <div>
                                        <input type="text" class="form-control" name="title"
                                               placeholder="Nhập tiêu đề công việc..." required
                                               value="${act.title}" ${canEdit=='LIMITED'
                                                        ? 'readonly style="background:#f0f0f0;"' : '' }>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Description:</label>
                                    <div>
                                        <textarea class="form-control" name="description"
                                                  placeholder="Mô tả chi tiết..." ${canEdit=='LIMITED'
                                                                                    ? 'readonly style="background:#f0f0f0;"' : ''
                                                                                    }>${act.description}</textarea>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Date/Time: <span
                                            class="required">*</span></label>
                                    <div class="input-group">
                                        <div class="input-with-icon">
                                            <input type="date" class="form-control" name="date"
                                                   ${canEdit=='LIMITED'
                                                     ? 'disabled style="background:#f0f0f0;"' : 'required' }>
                                            <span class="input-icon">📅</span>
                                        </div>
                                        <div class="input-with-icon">
                                            <input type="time" class="form-control" name="time"
                                                   ${canEdit=='LIMITED'
                                                     ? 'disabled style="background:#f0f0f0;"' : 'required' }>
                                            <span class="input-icon">🕐</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Priority:</label>
                                    <div class="priority-group">
                                        <div class="priority-option">
                                            <input type="radio" id="p-high" name="priority" value="High"
                                                   class="priority-radio" ${act.priority=='High' ? 'checked'
                                                                            : '' } ${canEdit=='LIMITED' ? 'disabled' : '' }>
                                            <label for="p-high" class="priority-label priority-high">
                                                <span class="priority-icon">🔴</span> High
                                            </label>
                                        </div>
                                        <div class="priority-option">
                                            <input type="radio" id="p-medium" name="priority" value="Medium"
                                                   class="priority-radio" ${empty act.priority ||
                                                                            act.priority=='Medium' ? 'checked' : '' }
                                                                            ${canEdit=='LIMITED' ? 'disabled' : '' }>
                                                   <label for="p-medium" class="priority-label priority-medium">
                                                       <span class="priority-icon">🟡</span> Medium
                                                   </label>
                                            </div>
                                            <div class="priority-option">
                                                <input type="radio" id="p-low" name="priority" value="Low"
                                                       class="priority-radio" ${act.priority=='Low' ? 'checked'
                                                                                : '' } ${canEdit=='LIMITED' ? 'disabled' : '' }>
                                                <label for="p-low" class="priority-label priority-low">
                                                    <span class="priority-icon">🟢</span> Low
                                                </label>
                                            </div>
                                            <c:if test="${canEdit == 'LIMITED'}">
                                                <input type="hidden" name="priority" value="${act.priority}">
                                            </c:if>
                                        </div>
                                    </div>

                                    <div class="section-divider"></div>

                                    <div class="form-row">
                                        <label class="form-label">Customer: <span style="color:#888; font-weight:normal;">(Tuỳ chọn)</span></label>
                                        <div class="input-group">
                                            <div class="select-with-icon">
                                                <span class="select-icon">👤</span>
                                                <select class="form-control" name="customer"
                                                        id="customerSelect" onchange="filterRelatedTo()"
                                                        ${canEdit=='LIMITED' ? 'disabled style="background:#f0f0f0;"' : '' }>
                                                    <option value="">-- Chọn Khách Hàng --</option>
                                                    <c:forEach items="${customerList}" var="c">
                                                        <option value="${c.id}">${c.fullName}</option>
                                                    </c:forEach>
                                                </select>
                                                <%-- Giữ giá trị khi bị disabled --%>
                                                <c:if
                                                    test="${canEdit == 'LIMITED' && not empty activity.customerId}">
                                                    <input type="hidden" name="customer"
                                                           value="${activity.customerId}">
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-row">
                                        <label class="form-label">Related To:</label>
                                        <div>
                                            <select class="form-control" name="related_to" id="relatedSelect"
                                                    ${canEdit=='LIMITED' ? 'disabled style="background:#f0f0f0;"'
                                                      : '' }>
                                                <option value="">-- Không liên kết --</option>
                                                <c:if test="${not empty oppList}">
                                                    <optgroup label="Opportunities">
                                                        <c:forEach items="${oppList}" var="o">
                                                            <option value="opp-${o.id}"
                                                                    data-customer="${o.customerId}">
                                                                💼 ${o.title}
                                                            </option>
                                                        </c:forEach>
                                                    </optgroup>
                                                </c:if>
                                                <c:if test="${not empty leads}">
                                                    <optgroup label="Leads">
                                                        <c:forEach items="${leads}" var="l">
                                                            <option value="lead-${l.id}">🎯 ${l.fullName}
                                                            </option>
                                                        </c:forEach>
                                                    </optgroup>
                                                </c:if>
                                                <c:if test="${empty leads}">
                                                    <optgroup label="Leads (Không có dữ liệu)">
                                                        <option value="" disabled>Chưa có lead nào</option>
                                                    </optgroup>
                                                </c:if>
                                            </select>
                                            <c:if
                                                test="${canEdit == 'LIMITED' && not empty activity.relatedTo}">
                                                <input type="hidden" name="related_to"
                                                       value="${activity.relatedTo}">
                                            </c:if>
                                        </div>
                                    </div>

                                    <div class="form-row">
                                        <label class="form-label">PIC (People in Charge):</label>
                                        <div>
                                            <div class="select-with-icon">
                                                <span class="select-icon">⭐</span>
                                                <select class="form-control" name="owner" ${canEdit=='LIMITED'
                                                                                            ? 'disabled style="background:#f0f0f0;"' : 'required' }>
                                                    <option value="">-- Chọn người phụ trách --</option>
                                                    <c:forEach items="${staffList}" var="u">
                                                        <option value="${u.id}" data-role="${not empty u.department ? u.department : 'Staff'}"
                                                                ${(not empty activityOwnerId and activityOwnerId == u.id)
                                                                  or (empty param.id and sessionScope.userSession.userId == u.id)
                                                                  ? 'selected' : '' }>${u.fullName}</option>
                                                    </c:forEach>
                                                </select>
                                                <c:if test="${canEdit == 'LIMITED'}">
                                                    <input type="hidden" name="owner"
                                                           value="${not empty activityOwnerId ? activityOwnerId : sessionScope.userSession.userId}">
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-row">
                                        <label class="form-label">Participants:</label>
                                        <div style="position: relative;">
                                            <div class="tags-container" id="tagsContainer">
                                                <input type="text" class="tags-input" id="participantInput"
                                                       placeholder="Nhập tên nhân viên...">
                                            </div>
                                            <div class="participant-suggestions" id="participantSuggestions">
                                            </div>
                                            <input type="hidden" name="participantIds"
                                                   id="participantIdsHidden">
                                            <p class="help-text">Thêm nhân viên phối hợp thực hiện.</p>
                                        </div>
                                    </div>

                                    <c:if test="${canEdit != 'LIMITED'}">
                                        <div class="form-group">
                                            <label class="form-label">Trạng thái (Status):</label>

                                            <c:choose>
                                                <%-- TRƯỜNG HỢP 1: Đã hoàn thành -> Hiện ô input chết (Không cho
                                                    bấm) --%>
                                                <%-- Lưu ý: Kiểm tra kỹ tên biến là 'activity' hay 'act' nhé
                                                --%>
                                                <c:when test="${activity.status == 'Completed'}">
                                                    <div style="position: relative;">
                                                        <input type="text" class="form-control"
                                                               value="Completed" readonly
                                                               style="background-color: #e9ecef; color: #198754; font-weight: bold; border-color: #198754;">
                                                        <i class="fas fa-check-circle"
                                                           style="position: absolute; right: 10px; top: 10px; color: #198754;"></i>
                                                    </div>

                                                    <%-- Vẫn phải gửi hidden để Server không bị null --%>
                                                    <input type="hidden" name="status"
                                                           value="Completed">
                                                    <small class="text-danger"
                                                           style="margin-top: 5px; display: block;">
                                                        <i class="fas fa-lock"></i> Công việc đã hoàn
                                                        thành, không thể thay đổi.
                                                    </small>
                                                </c:when>

                                                <%-- TRƯỜNG HỢP 2: Chưa hoàn thành -> Hiện Dropdown bình
                                                    thường --%>
                                                <c:otherwise>
                                                    <select name="status" class="form-control">
                                                        <option value="Planned"
                                                                ${activity.status=='Planned' ? 'selected'
                                                                  : '' }>Planned</option>
                                                        <option value="In Progress"
                                                                ${activity.status=='In Progress'
                                                                  ? 'selected' : '' }>In Progress</option>
                                                        <option value="Completed"
                                                                ${activity.status=='Completed' ? 'selected'
                                                                  : '' }>Completed</option>
                                                        <option value="Cancelled"
                                                                ${activity.status=='Cancelled' ? 'selected'
                                                                  : '' }>Cancelled</option>
                                                    </select>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </c:if>

                                    <div class="form-row full-width">
                                        <label class="form-label">Attachments:</label>
                                        <div>
                                            <div class="file-upload-area" id="fileUploadArea">
                                                <div class="upload-icon">📎</div>
                                                <div class="upload-text">Kéo thả file vào đây hoặc click để chọn
                                                </div>
                                                <div class="upload-subtext">Hỗ trợ: PDF, DOC, XLS, PPT, IMG,
                                                    Audio (Max: 10MB)</div>
                                            </div>
                                            <input type="file" name="attachments" class="file-input"
                                                   id="fileInput" multiple
                                                   accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.mp3,.wav"
                                                   style="display: none;">

                                            <c:if test="${not empty existingAttachments}">
                                                <div class="uploaded-files" id="existingAttachmentsList">
                                                    <c:forEach items="${existingAttachments}" var="file">
                                                        <div class="uploaded-file existing-file" data-attachment-id="${file.id}">
                                                            <input type="hidden" name="existingAttachmentIds" value="${file.id}">
                                                            <div class="file-info">
                                                                <div class="file-icon">📄</div>
                                                                <div class="file-details">
                                                                    <span class="file-name">${file.fileName}</span>
                                                                    <span class="file-size">Đã tải lên trước đó</span>
                                                                </div>
                                                            </div>
                                                            <button type="button" class="file-remove existing-file-remove" data-attachment-id="${file.id}">Bỏ file này</button>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </c:if>

                                            <div class="uploaded-files" id="uploadedFiles"></div>
                                        </div>
                                    </div>

                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <script>
                // Build staff list from owner dropdown to avoid JSP parser issues inside JS literals.
                const ownerOptions = document.querySelectorAll('select[name="owner"] option');
                const allParticipantsData = Array.from(ownerOptions)
                        .filter(option => option.value)
                        .map(option => ({
                                id: option.value,
                                name: option.textContent.trim(),
                                role: (option.dataset.role || 'Staff').trim()
                            }));

                // Participants sẽ được nạp từ API detail trong edit mode.
                const initialParticipantIds = [];
            </script>
            <script src="${pageContext.request.contextPath}/assets/js/activity-create.js"></script>
        </body>

    </html>