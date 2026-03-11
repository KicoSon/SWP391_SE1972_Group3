<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Soạn Email mới</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/activity-create.css">
        <link href="https://cdn.quilljs.com/1.3.6/quill.snow.css" rel="stylesheet">
    </head>
    <body>
        <jsp:include page="/sales/sidebar.jsp" />
        <div class="main-content">
            <div class="container" style="max-width: 800px;">
                <div class="form-wrapper">
                    <div class="form-header">
                        <h1 class="form-title">Soạn Email Gửi Khách Hàng</h1>
                    </div>

                    <form action="compose" method="POST" id="emailForm" enctype="multipart/form-data" style="padding: 30px;">
                        <input type="hidden" name="activityId" value="${sourceActivityId}">
                        <div class="form-grid">
                            <div class="form-row">
                                <label class="form-label">Người nhận:</label>

                                <c:choose>
                                    <%-- TRƯỜNG HỢP 1: Có khách hàng từ Activity (Khóa, không cho chọn) --%>
                                    <c:when test="${not empty fixedCustomer}">
                                        <div class="form-control" style="background: #f3f4f6; font-weight: 600; border-color: #10b981; display: flex; align-items: center; gap: 10px;">
                                            <i class="fas fa-user-check" style="color: #10b981;"></i> 
                                            ${fixedCustomer.fullName} (${fixedCustomer.email})
                                        </div>
                                        <input type="hidden" name="recipientId" value="customer_${fixedCustomer.id}">
                                    </c:when>
                                    
                                    <%-- TRƯỜNG HỢP 1.5: Có tiềm năng (Lead) từ Activity --%>
                                    <c:when test="${not empty fixedLead}">
                                        <div class="form-control" style="background: #f3f4f6; font-weight: 600; border-color: #10b981; display: flex; align-items: center; gap: 10px;">
                                            <i class="fas fa-user-check" style="color: #10b981;"></i> 
                                            ${fixedLead.fullName} (${fixedLead.email})
                                        </div>
                                        <input type="hidden" name="recipientId" value="lead_${fixedLead.id}">
                                    </c:when>

                                    <%-- TRƯỜNG HỢP 2: Soạn mail trực tiếp (Hiện danh sách chọn) --%>
                                    <c:otherwise>
                                        <select name="recipientId" class="form-control" required style="width: 100%;">
                                            <option value="">-- Chọn Người nhận --</option>
                                            <optgroup label="Khách hàng (Customers)">
                                                <c:forEach items="${customers}" var="c">
                                                    <c:if test="${not empty c.email}">
                                                        <option value="customer_${c.id}">${c.fullName} (${c.email})</option>
                                                    </c:if>
                                                </c:forEach>
                                            </optgroup>
                                            <optgroup label="Tiềm năng (Leads)">
                                                <c:forEach items="${leads}" var="l">
                                                    <c:if test="${not empty l.email}">
                                                        <option value="lead_${l.id}">${l.fullName} (${l.email})</option>
                                                    </c:if>
                                                </c:forEach>
                                            </optgroup>
                                        </select>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="form-row">
                                <label class="form-label">Tiêu đề:</label>
                                <input type="text" name="subject" class="form-control" placeholder="Nhập tiêu đề email..." required>
                            </div>

                            <div class="form-row full-width">
                                <label class="form-label">Nội dung:</label>
                                <div id="editor" style="height: 300px; background: white;"></div>
                                <input type="hidden" name="content" id="contentInput">
                            </div>
                        </div>

                        <div style="text-align: right; margin-top: 20px;">
                            <button type="button" class="btn btn-secondary" onclick="window.history.back()">Hủy</button>
                            <button type="submit" class="btn btn-primary" onclick="prepareSubmit()">
                                <i class="fas fa-paper-plane"></i> Gửi Mail Ngay
                            </button>
                        </div>

                        <div class="form-row full-width" style="margin-top: 20px;">
                            <label class="form-label"><i class="fas fa-paperclip"></i> Đính kèm tài liệu:</label>
                            <input type="file" name="attachments" class="form-control" multiple>
                            <small style="color: #666;">Giữ phím Ctrl để chọn nhiều file.</small>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.quilljs.com/1.3.6/quill.js"></script>
        <script>
                                var quill = new Quill('#editor', {
                                    theme: 'snow',
                                    placeholder: 'Viết nội dung email tại đây...'
                                });

                                function prepareSubmit() {
                                    // Copy nội dung từ editor vào hidden input trước khi submit
                                    document.getElementById('contentInput').value = quill.root.innerHTML;
                                    document.getElementById('emailForm').submit();
                                }
        </script>
    </body>
</html>