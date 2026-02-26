<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Chỉnh Sửa' : 'Thêm'} Quyền</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/permission-form.css">
</head>
<body>
    <div class="permission-form-container">
        <jsp:include page="../components/sidebar.jsp" />
        
        <div class="permission-form-content">
            <div class="page-header">
                <div class="header-left">
                    <a href="${pageContext.request.contextPath}/admin/permissions" class="btn-back">
                        <i class="fas fa-arrow-left"></i> Quay Lại
                    </a>
                    <h1>${isEdit ? 'Chỉnh Sửa Quyền' : 'Thêm Quyền Mới'}</h1>
                </div>
            </div>

            <!-- Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/permissions" method="post" class="permission-form">
                <input type="hidden" name="action" value="${isEdit ? 'edit' : 'add'}">
                <c:if test="${isEdit}">
                    <input type="hidden" name="id" value="${permission.id}">
                </c:if>

                <div class="form-section">
                    <h2><i class="fas fa-key"></i> Thông Tin Quyền</h2>
                    
                    <div class="form-group">
                        <label for="resource">
                            Resource (Tài nguyên) <span class="required">*</span>
                        </label>
                        <input type="text" 
                               id="resource" 
                               name="resource" 
                               value="${permission.resource}"
                               placeholder="Ví dụ: /admin/customers, /admin/orders, /api/products"
                               required>
                        <small class="form-hint">
                            <i class="fas fa-info-circle"></i> Đường dẫn hoặc tài nguyên cần kiểm soát quyền truy cập
                        </small>
                    </div>

                    <div class="form-group">
                        <label for="action_type">
                            Action (Hành động) <span class="required">*</span>
                        </label>
                        <select id="action_type" name="action_type" required>
                            <option value="">-- Chọn Hành Động --</option>
                            <option value="VIEW" ${permission.action == 'VIEW' ? 'selected' : ''}>VIEW (Xem)</option>
                            <option value="CREATE" ${permission.action == 'CREATE' ? 'selected' : ''}>CREATE (Tạo mới)</option>
                            <option value="EDIT" ${permission.action == 'EDIT' ? 'selected' : ''}>EDIT (Chỉnh sửa)</option>
                            <option value="DELETE" ${permission.action == 'DELETE' ? 'selected' : ''}>DELETE (Xóa)</option>
                            <option value="MANAGE" ${permission.action == 'MANAGE' ? 'selected' : ''}>MANAGE (Quản lý toàn bộ)</option>
                            <option value="APPROVE" ${permission.action == 'APPROVE' ? 'selected' : ''}>APPROVE (Phê duyệt)</option>
                            <option value="EXPORT" ${permission.action == 'EXPORT' ? 'selected' : ''}>EXPORT (Xuất dữ liệu)</option>
                        </select>
                        <small class="form-hint">
                            <i class="fas fa-info-circle"></i> Hành động được phép thực hiện trên tài nguyên
                        </small>
                    </div>

                    <div class="form-group">
                        <label for="description">
                            Mô Tả
                        </label>
                        <textarea id="description" 
                                  name="description" 
                                  rows="4"
                                  placeholder="Mô tả chi tiết về quyền này...">${permission.description}</textarea>
                        <small class="form-hint">
                            <i class="fas fa-info-circle"></i> Giải thích mục đích và phạm vi của quyền này
                        </small>
                    </div>
                </div>

                <!-- Roles Section -->
                <div class="form-section">
                    <h2><i class="fas fa-users-cog"></i> Gán Quyền Cho Roles</h2>
                    
                    <div class="roles-grid">
                        <c:forEach var="role" items="${availableRoles}">
                            <label class="role-checkbox">
                                <input type="checkbox" 
                                       name="roleIds" 
                                       value="${role.id}"
                                       <c:forEach var="assignedRole" items="${assignedRoles}">
                                           <c:if test="${assignedRole.id == role.id}">checked</c:if>
                                       </c:forEach>>
                                <div class="role-info">
                                    <span class="role-name">${role.name}</span>
                                    <c:if test="${not empty role.description}">
                                        <span class="role-desc">${role.description}</span>
                                    </c:if>
                                </div>
                            </label>
                        </c:forEach>
                    </div>
                    
                    <small class="form-hint">
                        <i class="fas fa-info-circle"></i> Chọn các role được phép sử dụng quyền này
                    </small>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> ${isEdit ? 'Cập Nhật' : 'Thêm Mới'}
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/permissions" class="btn-cancel">
                        <i class="fas fa-times"></i> Hủy Bỏ
                    </a>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
