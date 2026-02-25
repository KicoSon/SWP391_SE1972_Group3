<<<<<<< HEAD
<%-- 
    Document   : activityCreate
    Created on : Feb 3, 2026, 4:16:28 PM
    Author     : Viehai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Create/Edit Activity</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=IBM+Plex+Sans:wght@400;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-create.css">
    </head>
    <body>
        <div class="container">
            <div class="browser-header"></div>

            <!-- Form -->
            <div class="form-wrapper">
                <form id="activityForm">
                    <!-- Form Header -->
                    <div class="form-header">
                        <h1 class="form-title">Create/Edit New Activity</h1>
                        <div class="header-actions">
                            <button type="submit" class="btn btn-primary">Save</button>
                            <button type="button" class="btn btn-secondary" onclick="window.history.back()">Cancel</button>
                        </div>
                    </div>

                    <!-- Form Body -->
                    <div class="form-body">
                        <div class="form-grid">

                            <!-- Type -->
                            <div class="form-row">
                                <label class="form-label">Type:</label>
                                <div>
                                    <select class="form-control" name="type" required>
                                        <option value="Call">Call</option>
                                        <option value="Task">Task</option>
                                        <option value="Email">Email</option>
                                        <option value="Note">Note</option>
                                        <option value="Meeting">Meeting</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Title -->
                            <div class="form-row">
                                <label class="form-label">
                                    Title: 
                                    <span class="required">*</span>
                                </label>
                                <div>
                                    <input type="text" class="form-control" name="title" placeholder="Nhập tiêu đề hoạt động" required>
                                </div>
                            </div>

                            <!-- Description -->
                            <div class="form-row">
                                <label class="form-label">Description:</label>
                                <div>
                                    <textarea class="form-control" name="description" placeholder="Mô tả chi tiết về hoạt động này..."></textarea>
                                </div>
                            </div>

                            <!-- Date/Time -->
                            <div class="form-row">
                                <label class="form-label">
                                    Date/Time:
                                    <span class="required">*</span>
                                </label>
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

                            <!-- Priority (NEW) -->
                            <div class="form-row">
                                <label class="form-label">Priority:</label>
                                <div class="priority-group">
                                    <div class="priority-option">
                                        <input type="radio" id="priority-high" name="priority" value="High" class="priority-radio">
                                        <label for="priority-high" class="priority-label priority-high">
                                            <span class="priority-icon">🔴</span>
                                            <span>Gấp</span>
                                        </label>
                                    </div>
                                    <div class="priority-option">
                                        <input type="radio" id="priority-medium" name="priority" value="Medium" class="priority-radio" checked>
                                        <label for="priority-medium" class="priority-label priority-medium">
                                            <span class="priority-icon">🟡</span>
                                            <span>Bình thường</span>
                                        </label>
                                    </div>
                                    <div class="priority-option">
                                        <input type="radio" id="priority-low" name="priority" value="Low" class="priority-radio">
                                        <label for="priority-low" class="priority-label priority-low">
                                            <span class="priority-icon">🟢</span>
                                            <span>Thấp</span>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <!-- Reminder (NEW) -->
                            <div class="form-row">
                                <label class="form-label">Reminder:</label>
                                <div class="reminder-section">
                                    <div class="reminder-checkbox-wrapper">
                                        <input type="checkbox" id="enableReminder" class="reminder-checkbox">
                                        <label for="enableReminder" class="reminder-label">Đặt nhắc nhở</label>
                                    </div>
                                    <div class="reminder-options" id="reminderOptions">
                                        <select class="form-control" name="reminder_type">
                                            <option value="15">15 phút trước</option>
                                            <option value="30">30 phút trước</option>
                                            <option value="60">1 giờ trước</option>
                                            <option value="1440">1 ngày trước</option>
                                            <option value="custom">Tùy chỉnh</option>
                                        </select>
                                        <input type="datetime-local" class="form-control" name="reminder_at" style="display: none;" id="customReminderTime">
                                    </div>
                                    <p class="help-text">Hệ thống sẽ gửi thông báo nhắc nhở cho bạn</p>
                                </div>
                            </div>

                            <div class="section-divider"></div>

                            <!-- Assigned To (Customer) -->
                            <div class="form-row">
                                <label class="form-label">
                                    Assigned To:
                                    <span class="required">*</span>
                                </label>
                                <div class="input-group">
                                    <div class="select-with-icon">
                                        <span class="select-icon">👤</span>
                                        <select class="form-control" name="customer" required>
                                            <option value="">Chọn khách hàng</option>
                                            <option value="customer-a">Customer A</option>
                                            <option value="customer-b">Customer B</option>
                                            <option value="customer-c">Customer C</option>
                                            <option value="customer-d">Customer D</option>
                                        </select>
                                    </div>
                                    <div class="select-with-icon">
                                        <span class="select-icon">👔</span>
                                        <select class="form-control" name="staff" required>
                                            <option value="">Chọn sale staff</option>
                                            <option value="staff-x">Sale staff X</option>
                                            <option value="nam">Nam</option>
                                            <option value="minh">Minh</option>
                                            <option value="viehai">Viehai</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <!-- Related To (Opportunity/Lead) - NEW -->
                            <div class="form-row">
                                <label class="form-label">Related To:</label>
                                <div>
                                    <select class="form-control" name="related_to">
                                        <option value="">Không liên kết</option>
                                        <optgroup label="Opportunities">
                                            <option value="opp-1">💼 Deal Laptop cho Công ty ABC - 50,000,000 VNĐ</option>
                                            <option value="opp-2">💼 Hợp đồng máy in - 30,000,000 VNĐ</option>
                                            <option value="opp-3">💼 Thiết bị văn phòng - 80,000,000 VNĐ</option>
                                        </optgroup>
                                        <optgroup label="Leads">
                                            <option value="lead-1">🎯 Lead: Công ty XYZ quan tâm PC Gaming</option>
                                            <option value="lead-2">🎯 Lead: Trường học cần máy chiếu</option>
                                        </optgroup>
                                    </select>
                                    <p class="help-text">Liên kết activity này với một cơ hội kinh doanh hoặc khách hàng tiềm năng</p>
                                </div>
                            </div>

                            <!-- Participants (Owner + Others) - NEW -->
                            <div class="form-row">
                                <label class="form-label">Owner:</label>
                                <div>
                                    <div class="select-with-icon">
                                        <span class="select-icon">⭐</span>
                                        <select class="form-control" name="owner" required>
                                            <option value="">Chọn người chủ trì</option>
                                            <option value="viehai">Viehai (Manager)</option>
                                            <option value="nam">Nam (Senior Sales)</option>
                                            <option value="minh">Minh (Sales)</option>
                                            <option value="lan">Lan (Sales)</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="form-row">
                                <label class="form-label">Participants:</label>
                                <div style="position: relative;">
                                    <div class="tags-container" id="tagsContainer">
                                        <input type="text" class="tags-input" id="participantInput" placeholder="Nhập tên để thêm người tham gia...">
                                    </div>
                                    <div class="participant-suggestions" id="participantSuggestions"></div>
                                    <p class="help-text">Thêm những người khác cần tham gia hoặc phối hợp trong activity này</p>
                                </div>
                            </div>

                            <!-- Status -->
                            <div class="form-row">
                                <label class="form-label">Status:</label>
                                <div>
                                    <select class="form-control" name="status">
                                        <option value="Pending">Pending</option>
                                        <option value="In Progress">In Progress</option>
                                        <option value="Completed">Completed</option>
                                        <option value="Cancelled">Cancelled</option>
                                        <option value="Overdue">Overdue</option>
                                    </select>
                                </div>
                            </div>

                            <div class="section-divider"></div>

                            <!-- Attachments (NEW) -->
                            <div class="form-row full-width">
                                <label class="form-label">Attachments:</label>
                                <div>
                                    <div class="file-upload-area" id="fileUploadArea">
                                        <div class="upload-icon">📎</div>
                                        <div class="upload-text">Kéo thả file vào đây hoặc click để chọn</div>
                                        <div class="upload-subtext">Hỗ trợ: PDF, DOC, XLS, PPT, IMG, Audio (Max: 10MB)</div>
                                    </div>
                                    <input type="file" class="file-input" id="fileInput" multiple accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.mp3,.wav">
                                    <div class="uploaded-files" id="uploadedFiles"></div>
                                </div>
                            </div>

                        </div>
                    </div>
                </form>
            </div>
            <script src="${pageContext.request.contextPath}/assets/js/activity-dashboard.js"></script>
    </body>
</html>
=======
<%-- 
    Document   : activityCreate
    Created on : Feb 3, 2026, 4:16:28 PM
    Author     : Viehai
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Create/Edit Activity</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&family=IBM+Plex+Sans:wght@400;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-create.css">
    </head>
    <body>
        <div class="container">
            <div class="browser-header"></div>

            <!-- Form -->
            <div class="form-wrapper">
                <form id="activityForm">
                    <!-- Form Header -->
                    <div class="form-header">
                        <h1 class="form-title">Create/Edit New Activity</h1>
                        <div class="header-actions">
                            <button type="submit" class="btn btn-primary">Save</button>
                            <button type="button" class="btn btn-secondary" onclick="window.history.back()">Cancel</button>
                        </div>
                    </div>

                    <!-- Form Body -->
                    <div class="form-body">
                        <div class="form-grid">

                            <!-- Type -->
                            <div class="form-row">
                                <label class="form-label">Type:</label>
                                <div>
                                    <select class="form-control" name="type" required>
                                        <option value="Call">Call</option>
                                        <option value="Task">Task</option>
                                        <option value="Email">Email</option>
                                        <option value="Note">Note</option>
                                        <option value="Meeting">Meeting</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Title -->
                            <div class="form-row">
                                <label class="form-label">
                                    Title: 
                                    <span class="required">*</span>
                                </label>
                                <div>
                                    <input type="text" class="form-control" name="title" placeholder="Nhập tiêu đề hoạt động" required>
                                </div>
                            </div>

                            <!-- Description -->
                            <div class="form-row">
                                <label class="form-label">Description:</label>
                                <div>
                                    <textarea class="form-control" name="description" placeholder="Mô tả chi tiết về hoạt động này..."></textarea>
                                </div>
                            </div>

                            <!-- Date/Time -->
                            <div class="form-row">
                                <label class="form-label">
                                    Date/Time:
                                    <span class="required">*</span>
                                </label>
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

                            <!-- Priority (NEW) -->
                            <div class="form-row">
                                <label class="form-label">Priority:</label>
                                <div class="priority-group">
                                    <div class="priority-option">
                                        <input type="radio" id="priority-high" name="priority" value="High" class="priority-radio">
                                        <label for="priority-high" class="priority-label priority-high">
                                            <span class="priority-icon">🔴</span>
                                            <span>Gấp</span>
                                        </label>
                                    </div>
                                    <div class="priority-option">
                                        <input type="radio" id="priority-medium" name="priority" value="Medium" class="priority-radio" checked>
                                        <label for="priority-medium" class="priority-label priority-medium">
                                            <span class="priority-icon">🟡</span>
                                            <span>Bình thường</span>
                                        </label>
                                    </div>
                                    <div class="priority-option">
                                        <input type="radio" id="priority-low" name="priority" value="Low" class="priority-radio">
                                        <label for="priority-low" class="priority-label priority-low">
                                            <span class="priority-icon">🟢</span>
                                            <span>Thấp</span>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <!-- Reminder (NEW) -->
                            <div class="form-row">
                                <label class="form-label">Reminder:</label>
                                <div class="reminder-section">
                                    <div class="reminder-checkbox-wrapper">
                                        <input type="checkbox" id="enableReminder" class="reminder-checkbox">
                                        <label for="enableReminder" class="reminder-label">Đặt nhắc nhở</label>
                                    </div>
                                    <div class="reminder-options" id="reminderOptions">
                                        <select class="form-control" name="reminder_type">
                                            <option value="15">15 phút trước</option>
                                            <option value="30">30 phút trước</option>
                                            <option value="60">1 giờ trước</option>
                                            <option value="1440">1 ngày trước</option>
                                            <option value="custom">Tùy chỉnh</option>
                                        </select>
                                        <input type="datetime-local" class="form-control" name="reminder_at" style="display: none;" id="customReminderTime">
                                    </div>
                                    <p class="help-text">Hệ thống sẽ gửi thông báo nhắc nhở cho bạn</p>
                                </div>
                            </div>

                            <div class="section-divider"></div>

                            <!-- Assigned To (Customer) -->
                            <div class="form-row">
                                <label class="form-label">
                                    Assigned To:
                                    <span class="required">*</span>
                                </label>
                                <div class="input-group">
                                    <div class="select-with-icon">
                                        <span class="select-icon">👤</span>
                                        <select class="form-control" name="customer" required>
                                            <option value="">Chọn khách hàng</option>
                                            <option value="customer-a">Customer A</option>
                                            <option value="customer-b">Customer B</option>
                                            <option value="customer-c">Customer C</option>
                                            <option value="customer-d">Customer D</option>
                                        </select>
                                    </div>
                                    <div class="select-with-icon">
                                        <span class="select-icon">👔</span>
                                        <select class="form-control" name="staff" required>
                                            <option value="">Chọn sale staff</option>
                                            <option value="staff-x">Sale staff X</option>
                                            <option value="nam">Nam</option>
                                            <option value="minh">Minh</option>
                                            <option value="viehai">Viehai</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <!-- Related To (Opportunity/Lead) - NEW -->
                            <div class="form-row">
                                <label class="form-label">Related To:</label>
                                <div>
                                    <select class="form-control" name="related_to">
                                        <option value="">Không liên kết</option>
                                        <optgroup label="Opportunities">
                                            <option value="opp-1">💼 Deal Laptop cho Công ty ABC - 50,000,000 VNĐ</option>
                                            <option value="opp-2">💼 Hợp đồng máy in - 30,000,000 VNĐ</option>
                                            <option value="opp-3">💼 Thiết bị văn phòng - 80,000,000 VNĐ</option>
                                        </optgroup>
                                        <optgroup label="Leads">
                                            <option value="lead-1">🎯 Lead: Công ty XYZ quan tâm PC Gaming</option>
                                            <option value="lead-2">🎯 Lead: Trường học cần máy chiếu</option>
                                        </optgroup>
                                    </select>
                                    <p class="help-text">Liên kết activity này với một cơ hội kinh doanh hoặc khách hàng tiềm năng</p>
                                </div>
                            </div>

                            <!-- Participants (Owner + Others) - NEW -->
                            <div class="form-row">
                                <label class="form-label">Owner:</label>
                                <div>
                                    <div class="select-with-icon">
                                        <span class="select-icon">⭐</span>
                                        <select class="form-control" name="owner" required>
                                            <option value="">Chọn người chủ trì</option>
                                            <option value="viehai">Viehai (Manager)</option>
                                            <option value="nam">Nam (Senior Sales)</option>
                                            <option value="minh">Minh (Sales)</option>
                                            <option value="lan">Lan (Sales)</option>
                                        </select>
                                    </div>
                                </div>
                            </div>

                            <div class="form-row">
                                <label class="form-label">Participants:</label>
                                <div style="position: relative;">
                                    <div class="tags-container" id="tagsContainer">
                                        <input type="text" class="tags-input" id="participantInput" placeholder="Nhập tên để thêm người tham gia...">
                                    </div>
                                    <div class="participant-suggestions" id="participantSuggestions"></div>
                                    <p class="help-text">Thêm những người khác cần tham gia hoặc phối hợp trong activity này</p>
                                </div>
                            </div>

                            <!-- Status -->
                            <div class="form-row">
                                <label class="form-label">Status:</label>
                                <div>
                                    <select class="form-control" name="status">
                                        <option value="Pending">Pending</option>
                                        <option value="In Progress">In Progress</option>
                                        <option value="Completed">Completed</option>
                                        <option value="Cancelled">Cancelled</option>
                                        <option value="Overdue">Overdue</option>
                                    </select>
                                </div>
                            </div>

                            <div class="section-divider"></div>

                            <!-- Attachments (NEW) -->
                            <div class="form-row full-width">
                                <label class="form-label">Attachments:</label>
                                <div>
                                    <div class="file-upload-area" id="fileUploadArea">
                                        <div class="upload-icon">📎</div>
                                        <div class="upload-text">Kéo thả file vào đây hoặc click để chọn</div>
                                        <div class="upload-subtext">Hỗ trợ: PDF, DOC, XLS, PPT, IMG, Audio (Max: 10MB)</div>
                                    </div>
                                    <input type="file" class="file-input" id="fileInput" multiple accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.mp3,.wav">
                                    <div class="uploaded-files" id="uploadedFiles"></div>
                                </div>
                            </div>

                        </div>
                    </div>
                </form>
            </div>
            <script src="${pageContext.request.contextPath}/assets/js/activity-dashboard.js"></script>
    </body>
</html>
>>>>>>> 4b51dd9a50776a4b81ba05bb7c5282ce94130783
