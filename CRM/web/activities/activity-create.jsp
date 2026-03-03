<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><c:choose><c:when test="${not empty param.id}">Chỉnh Sửa Hoạt Động</c:when><c:otherwise>Tạo Hoạt Động Mới</c:otherwise></c:choose></title>

                <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=IBM+Plex+Sans:wght@400;500;600&display=swap" rel="stylesheet">
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

                    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-create.css">
    </head>
    <body>

        <jsp:include page="/sales/sidebar.jsp"/>

        <div class="main-content fade-in">
            <div class="container">
                <div class="browser-header"></div>
                <div class="form-wrapper">

                    <form id="activityForm" method="POST" action="${pageContext.request.contextPath}/activities/create" enctype="multipart/form-data">
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
                                <button type="button" class="btn btn-secondary" onclick="window.history.back()">Hủy</button>
                            </div>
                        </div>

                        <c:if test="${not empty error}">
                            <div style="padding: 10px 40px; color: red; background: #ffe6e6; font-weight: bold;">
                                ${error}
                            </div>
                        </c:if>

                        <div class="form-body">
                            <div class="form-grid">

                                <div class="form-row">
                                    <label class="form-label">Type:</label>
                                    <div>
                                        <select class="form-control" name="type" required>
                                            <option value="Call">Call</option>
                                            <option value="Meeting">Meeting</option>
                                            <option value="Email">Email</option>
                                            <option value="Task">Task</option>
                                            <option value="Note">Note</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Title: <span class="required">*</span></label>
                                    <div>
                                        <input type="text" class="form-control" name="title" placeholder="Nhập tiêu đề công việc..." required>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Description:</label>
                                    <div>
                                        <textarea class="form-control" name="description" placeholder="Mô tả chi tiết..."></textarea>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Date/Time: <span class="required">*</span></label>
                                    <div class="input-group">
                                        <div class="input-with-icon">
                                            <input type="date" class="form-control" name="date" required>
                                            <span class="input-icon">📅</span>
                                        </div>
                                        <div class="input-with-icon">
                                            <input type="time" class="form-control" name="time" required>
                                            <span class="input-icon">🕐</span>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Priority:</label>
                                    <div class="priority-group">
                                        <div class="priority-option">
                                            <input type="radio" id="p-high" name="priority" value="High" class="priority-radio">
                                            <label for="p-high" class="priority-label priority-high">
                                                <span class="priority-icon">🔴</span> High
                                            </label>
                                        </div>
                                        <div class="priority-option">
                                            <input type="radio" id="p-medium" name="priority" value="Medium" class="priority-radio" checked>
                                            <label for="p-medium" class="priority-label priority-medium">
                                                <span class="priority-icon">🟡</span> Medium
                                            </label>
                                        </div>
                                        <div class="priority-option">
                                            <input type="radio" id="p-low" name="priority" value="Low" class="priority-radio">
                                            <label for="p-low" class="priority-label priority-low">
                                                <span class="priority-icon">🟢</span> Low
                                            </label>
                                        </div>
                                    </div>
                                </div>

                                <div class="section-divider"></div>

                                <div class="form-row">
                                    <label class="form-label">Customer: <span class="required">*</span></label>
                                    <div class="input-group">
                                        <div class="select-with-icon">
                                            <span class="select-icon">👤</span>
                                            <select class="form-control" name="customer" id="customerSelect" onchange="filterRelatedTo()" >
                                                <option value="">-- Chọn Khách Hàng --</option>
                                                <c:forEach items="${customerList}" var="c">
                                                    <option value="${c.id}">${c.fullName}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Related To:</label>
                                    <div>
                                        <select class="form-control" name="related_to" id="relatedSelect">
                                            <option value="">-- Không liên kết --</option>
                                            <c:if test="${not empty oppList}">
                                                <optgroup label="Opportunities">
                                                    <c:forEach items="${oppList}" var="o">
                                                        <option value="opp-${o.id}" data-customer="${o.customerId}">💼 ${o.title}</option>
                                                    </c:forEach>
                                                </optgroup>
                                            </c:if>
                                            <c:if test="${not empty leads}">
                                                <optgroup label="Leads">
                                                    <c:forEach items="${leads}" var="l">
                                                        <option value="lead-${l.id}">🎯 ${l.fullName}</option>
                                                    </c:forEach>
                                                </optgroup>
                                            </c:if>
                                            <c:if test="${empty leads}">
                                                <optgroup label="Leads (Không có dữ liệu)">
                                                    <option value="" disabled>Chưa có lead nào</option>
                                                </optgroup>
                                            </c:if>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Owner:</label>
                                    <div>
                                        <div class="select-with-icon">
                                            <span class="select-icon">⭐</span>
                                            <select class="form-control" name="owner" required>
                                                <option value="">-- Chọn người chủ trì --</option>
                                                <c:forEach items="${staffList}" var="u">
                                                    <option value="${u.id}" ${sessionScope.userSession.staffInfo.id == u.id ? 'selected' : ''}>${u.fullName}</option> 
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Participants:</label>
                                    <div style="position: relative;">
                                        <div class="tags-container" id="tagsContainer">
                                            <input type="text" class="tags-input" id="participantInput" placeholder="Nhập tên nhân viên...">
                                        </div>
                                        <div class="participant-suggestions" id="participantSuggestions"></div>
                                        <input type="hidden" name="participantIds" id="participantIdsHidden">
                                        <p class="help-text">Thêm nhân viên phối hợp thực hiện.</p>
                                    </div>
                                </div>

                                <div class="form-row">
                                    <label class="form-label">Status:</label>
                                    <div>
                                        <select class="form-control" name="status">
                                            <option value="Planned">Planned</option>
                                            <option value="In Progress">In Progress</option>
                                            <option value="Completed">Completed</option>
                                            <option value="Overdue">Overdue</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="form-row full-width">
                                    <label class="form-label">Attachments:</label>
                                    <div>
                                        <div class="file-upload-area" id="fileUploadArea">
                                            <div class="upload-icon">📎</div>
                                            <div class="upload-text">Kéo thả file vào đây hoặc click để chọn</div>
                                            <div class="upload-subtext">Hỗ trợ: PDF, DOC, XLS, PPT, IMG, Audio (Max: 10MB)</div>
                                        </div>
                                        <input type="file" name="attachments" class="file-input" id="fileInput" multiple accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.mp3,.wav" style="display: none;">
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
            // Mảng danh sách nhân viên từ Server
            const allParticipantsData = [
            <c:forEach items="${staffList}" var="u" varStatus="loop">
            {
            id: '${u.id}',
                    name: '${u.fullName}',
                    role: '${u.department}'
            }${!loop.last ? ',' : ''}
            </c:forEach>
            ];
        </script>
        <script src="${pageContext.request.contextPath}/assets/js/activity-create.js"></script>
    </body>
</html>
</body>
</html>