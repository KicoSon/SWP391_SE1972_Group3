<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Chỉnh Sửa' : 'Thêm'} Nhân Viên</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/staff-form.css">
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/components/sidebar.jsp" />
    
    <div class="staff-form-content">
            <div class="page-header">
                <div class="header-left">
                    <a href="${pageContext.request.contextPath}/admin/staff" class="btn-back">
                        <i class="fas fa-arrow-left"></i> Quay Lại
                    </a>
                    <h1>${isEdit ? 'Chỉnh Sửa Nhân Viên' : 'Thêm Nhân Viên Mới'}</h1>
                </div>
            </div>

            <!-- Messages -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/admin/staff" method="post" class="staff-form">
                <input type="hidden" name="action" value="${isEdit ? 'edit' : 'add'}">
                <c:if test="${isEdit}">
                    <input type="hidden" name="id" value="${staff.id}">
                </c:if>

                <!-- Personal Information -->
                <div class="form-section">
                    <h2><i class="fas fa-user"></i> Thông Tin Cá Nhân</h2>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="fullName">
                                Họ và Tên <span class="required">*</span>
                            </label>
                            <input type="text" 
                                   id="fullName" 
                                   name="fullName" 
                                   value="${staff.fullName}"
                                   placeholder="Nguyễn Văn A"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="phone">
                                Số Điện Thoại <span class="required">*</span>
                            </label>
                            <input type="tel" 
                                   id="phone" 
                                   name="phone" 
                                   value="${staff.phone}"
                                   placeholder="0912345678"
                                   pattern="[0-9]{10,11}"
                                   required>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="email">
                                Email <span class="required">*</span>
                            </label>
                            <input type="email" 
                                   id="email" 
                                   name="email" 
                                   value="${staff.email}"
                                   placeholder="staff@example.com"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="departmentId">
                                Phòng Ban <span class="required">*</span>
                            </label>
                            <select id="departmentId" name="departmentId" required>
                                <option value="">-- Chọn Phòng Ban --</option>
                                <c:forEach var="dept" items="${departments}">
                                    <option value="${dept.id}" 
                                            ${staff.departmentId == dept.id ? 'selected' : ''}>
                                        ${dept.name}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- Roles Section -->
                <div class="form-section">
                    <h2><i class="fas fa-user-shield"></i> Vai Trò</h2>
                    
                    <div class="form-group">
                        <label>Chọn vai trò cho nhân viên (không bao gồm ADMIN)</label>
                        <div class="roles-grid">
                            <c:forEach var="role" items="${availableRoles}">
                                <label class="role-checkbox">
                                    <input type="checkbox" 
                                           name="roleIds" 
                                           value="${role.id}"
                                           <c:forEach var="staffRole" items="${staffRoles}">
                                               <c:if test="${staffRole.id == role.id}">checked</c:if>
                                           </c:forEach>>
                                    <span class="role-name">${role.name}</span>
                                    <c:if test="${not empty role.description}">
                                        <small class="role-desc">${role.description}</small>
                                    </c:if>
                                </label>
                            </c:forEach>
                        </div>
                        <small class="form-hint">
                            <i class="fas fa-info-circle"></i> Vai trò ADMIN chỉ có thể được quản lý bởi super admin
                        </small>
                    </div>
                </div>

                <!-- Account Information -->
                <div class="form-section">
                    <h2><i class="fas fa-key"></i> Thông Tin Tài Khoản</h2>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="password">
                                Mật Khẩu ${isEdit ? '' : '<span class="required">*</span>'}
                            </label>
                            <div class="password-field">
                                <input type="password" 
                                       id="password" 
                                       name="password" 
                                       placeholder="${isEdit ? 'Để trống nếu không muốn thay đổi' : 'Tối thiểu 6 ký tự'}"
                                       minlength="6"
                                       ${isEdit ? '' : 'required'}>
                                <button type="button" class="toggle-password" onclick="togglePassword('password')">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                            <c:if test="${not isEdit}">
                                <small class="form-hint">Mật khẩu phải có ít nhất 6 ký tự</small>
                            </c:if>
                        </div>

                        <div class="form-group">
                            <label for="confirmPassword">
                                Xác Nhận Mật Khẩu ${isEdit ? '' : '<span class="required">*</span>'}
                            </label>
                            <div class="password-field">
                                <input type="password" 
                                       id="confirmPassword" 
                                       name="confirmPassword" 
                                       placeholder="${isEdit ? 'Để trống nếu không muốn thay đổi' : 'Nhập lại mật khẩu'}"
                                       ${isEdit ? '' : 'required'}>
                                <button type="button" class="toggle-password" onclick="togglePassword('confirmPassword')">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Account Status (Edit Only) -->
                <c:if test="${isEdit}">
                    <div class="form-section">
                        <h2><i class="fas fa-toggle-on"></i> Trạng Thái Tài Khoản</h2>
                        
                        <div class="form-group">
                            <label class="checkbox-label">
                                <input type="checkbox" 
                                       name="isActive" 
                                       value="true"
                                       ${staff.active ? 'checked' : ''}>
                                <span>Tài khoản đang hoạt động</span>
                            </label>
                            <small class="form-hint">
                                Bỏ chọn để vô hiệu hóa tài khoản nhân viên
                            </small>
                        </div>
                    </div>
                </c:if>

                <!-- Form Actions -->
                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> ${isEdit ? 'Cập Nhật' : 'Thêm Mới'}
                    </button>
                    <a href="${pageContext.request.contextPath}/admin/staff" class="btn-cancel">
                        <i class="fas fa-times"></i> Hủy Bỏ
                    </a>
                </div>
            </form>
        </div>
    </div>

    <script>
        // Toggle password visibility
        function togglePassword(fieldId) {
            const field = document.getElementById(fieldId);
            const button = field.nextElementSibling;
            const icon = button.querySelector('i');
            
            if (field.type === 'password') {
                field.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                field.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        }

        // Form validation
        document.querySelector('.staff-form').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            
            // Check if passwords match
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Mật khẩu xác nhận không khớp!');
                return false;
            }
            
            // Check password length (if provided)
            if (password && password.length < 6) {
                e.preventDefault();
                alert('Mật khẩu phải có ít nhất 6 ký tự!');
                return false;
            }
            
            return true;
        });
    </script>
</body>
</html>