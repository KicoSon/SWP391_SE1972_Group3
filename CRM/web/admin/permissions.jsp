<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Phân Quyền</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/permissions.css">
</head>
<body>
    <div class="permissions-container">
        <jsp:include page="../components/sidebar.jsp" />
        
        <div class="permissions-content">
            <div class="page-header">
                <div>
                    <h1>Quản Lý Phân Quyền</h1>
                    <p>Cấu hình quyền truy cập cho từng vai trò</p>
                </div>
            </div>

            <!-- Messages -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> ${successMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">
                    <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                </div>
            </c:if>

         

            <!-- All Permissions List -->
            <div class="permissions-section">
                <div class="section-header">
                    <h2><i class="fas fa-list"></i> Danh Sách Tất Cả Quyền</h2>
                    <c:if test="${canCreate}">
                        <a href="${pageContext.request.contextPath}/admin/permissions?action=add" class="btn-add">
                            <i class="fas fa-plus"></i> Thêm Quyền Mới
                        </a>
                    </c:if>
                </div>
                
                <!-- Search Box -->
                <div class="search-box">
                    <form method="get" action="${pageContext.request.contextPath}/admin/permissions">
                        <div class="search-input-group">
                            <i class="fas fa-search"></i>
                            <input type="text" 
                                   name="keyword" 
                                   placeholder="Tìm kiếm theo resource, action, mô tả..." 
                                   value="${keyword}">
                            <button type="submit" class="btn-search">
                                <i class="fas fa-search"></i> Tìm Kiếm
                            </button>
                            <c:if test="${not empty keyword}">
                                <a href="${pageContext.request.contextPath}/admin/permissions" class="btn-clear">
                                    <i class="fas fa-times"></i> Xóa
                                </a>
                            </c:if>
                        </div>
                    </form>
                </div>
                
                <div class="permissions-table-container">
                    <table class="permissions-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Resource (Tài nguyên)</th>
                                <th>Action (Hành động)</th>
                                <th>Mô Tả</th>
                                <th>Roles Được Phép</th>
                                <th>Thao Tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty permissions}">
                                    <tr>
                                        <td colspan="6" class="no-data">
                                            <i class="fas fa-inbox"></i>
                                            <p>Chưa có quyền nào trong hệ thống</p>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="perm" items="${permissions}">
                                        <tr>
                                            <td>${perm.id}</td>
                                            <td><code>${perm.resource}</code></td>
                                            <td><span class="action-badge">${perm.action}</span></td>
                                            <td>${perm.description}</td>
                                            <td class="roles-cell">
                                                <c:set var="hasRole" value="false"/>
                                                <c:forEach var="role" items="${roles}">
                                                    <c:set var="permissionList" value="${rolePermissions[role.id]}"/>
                                                    <c:if test="${not empty permissionList}">
                                                        <c:forEach var="permId" items="${permissionList}">
                                                            <c:if test="${permId == perm.id}">
                                                                <span class="role-tag">${role.name}</span>
                                                                <c:set var="hasRole" value="true"/>
                                                            </c:if>
                                                        </c:forEach>
                                                    </c:if>
                                                </c:forEach>
                                                <c:if test="${!hasRole}">
                                                    <span class="no-role">Chưa gán</span>
                                                </c:if>
                                            </td>
                                            <td class="actions">
                                                <c:if test="${canEdit}">
                                                    <a href="${pageContext.request.contextPath}/admin/permissions?action=edit&id=${perm.id}" 
                                                       class="btn-action btn-edit" title="Chỉnh sửa">
                                                        <i class="fas fa-edit"></i>
                                                    </a>
                                                </c:if>
                                                <c:if test="${canDelete}">
                                                    <a href="${pageContext.request.contextPath}/admin/permissions?action=delete&id=${perm.id}" 
                                                       class="btn-action btn-delete" 
                                                       title="Xóa"
                                                       onclick="return confirm('Bạn có chắc chắn muốn xóa quyền này?')">
                                                        <i class="fas fa-trash"></i>
                                                    </a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
                
                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/admin/permissions?page=${currentPage - 1}${not empty keyword ? '&keyword='.concat(keyword) : ''}" 
                               class="page-btn">
                                <i class="fas fa-chevron-left"></i> Trước
                            </a>
                        </c:if>
                        
                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <c:choose>
                                <c:when test="${i == currentPage}">
                                    <span class="page-btn active">${i}</span>
                                </c:when>
                                <c:when test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                    <a href="${pageContext.request.contextPath}/admin/permissions?page=${i}${not empty keyword ? '&keyword='.concat(keyword) : ''}" 
                                       class="page-btn">${i}</a>
                                </c:when>
                                <c:when test="${i == currentPage - 3 || i == currentPage + 3}">
                                    <span class="page-btn dots">...</span>
                                </c:when>
                            </c:choose>
                        </c:forEach>
                        
                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/admin/permissions?page=${currentPage + 1}${not empty keyword ? '&keyword='.concat(keyword) : ''}" 
                               class="page-btn">
                                Sau <i class="fas fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <!-- Modal for managing role permissions -->
    <div id="rolePermissionsModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2><i class="fas fa-shield-alt"></i> Quản Lý Quyền: <span id="modalRoleName"></span></h2>
                <button class="modal-close" onclick="closeModal()">&times;</button>
            </div>
            
            <form id="permissionsForm" method="post" action="${pageContext.request.contextPath}/admin/permissions">
                <input type="hidden" name="action" value="updateRolePermissions">
                <input type="hidden" name="roleId" id="modalRoleId">
                
                <div class="modal-body">
                    <div class="permissions-checklist">
                        <c:forEach var="perm" items="${permissions}">
                            <label class="permission-checkbox">
                                <input type="checkbox" 
                                       name="permissionIds" 
                                       value="${perm.id}"
                                       data-role-perm="${perm.id}">
                                <div class="perm-info">
                                    <strong><code>${perm.resource}</code> - ${perm.action}</strong>
                                    <small>${perm.description}</small>
                                </div>
                            </label>
                        </c:forEach>
                    </div>
                </div>
                
                <div class="modal-footer">
                    <button type="submit" class="btn-save">
                        <i class="fas fa-save"></i> Lưu Thay Đổi
                    </button>
                    <button type="button" class="btn-cancel" onclick="closeModal()">
                        <i class="fas fa-times"></i> Hủy
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        const rolePermissions = ${rolePermissions != null ? rolePermissions : '{}'};
        
        function openRolePermissions(roleId, roleName) {
            document.getElementById('modalRoleId').value = roleId;
            document.getElementById('modalRoleName').textContent = roleName;
            
            // Uncheck all checkboxes first
            document.querySelectorAll('input[name="permissionIds"]').forEach(cb => {
                cb.checked = false;
            });
            
            // Check permissions for this role
            if (rolePermissions[roleId]) {
                rolePermissions[roleId].forEach(permId => {
                    const checkbox = document.querySelector(`input[value="${permId}"]`);
                    if (checkbox) {
                        checkbox.checked = true;
                    }
                });
            }
            
            document.getElementById('rolePermissionsModal').style.display = 'block';
        }
        
        function closeModal() {
            document.getElementById('rolePermissionsModal').style.display = 'none';
        }
        
        // Close modal when clicking outside
        window.onclick = function(event) {
            const modal = document.getElementById('rolePermissionsModal');
            if (event.target == modal) {
                closeModal();
            }
        }
    </script>
</body>
</html>
