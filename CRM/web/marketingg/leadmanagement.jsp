<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Lead</title>

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <style>
            /* === GIỮ NGUYÊN STYLE CỦA BẠN === */
            body{
                margin:0;
                font-family:"Segoe UI";
                background:linear-gradient(135deg,#3a7bd5,#3a6073);
                color:#333;
            }

            .main-content{
                margin-left:270px;
                padding:30px;
                min-height:100vh;
            }

            .header{
                background:rgba(255,255,255,0.95);
                padding:25px 30px;
                border-radius:20px;
                box-shadow:0 10px 25px rgba(0,0,0,0.15);
                margin-bottom:30px;
                display:flex;
                justify-content:space-between;
                align-items:center;
            }

            .header h2{
                font-weight:700;
                font-size:26px;
            }

            .btn{
                border:none;
                padding:10px 16px;
                border-radius:8px;
                font-size:14px;
                font-weight:600;
                cursor:pointer;
                text-decoration:none;
            }

            .btn-primary{
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
            }

            .btn-outline{
                border:2px solid #667eea;
                color:#667eea;
                background:transparent;
            }

            .filter-bar{
                display:flex;
                gap:15px;
                margin-bottom:25px;
            }

            .filter-bar input,
            .filter-bar select{
                padding:10px;
                border-radius:8px;
                border:1px solid #ccc;
            }

            .card{
                background:white;
                border-radius:20px;
                box-shadow:0 10px 30px rgba(0,0,0,0.1);
                overflow:hidden;
            }

            table{
                width:100%;
                border-collapse:collapse;
            }

            th,td{
                padding:12px;
                border-bottom:1px solid rgba(0,0,0,0.1);
            }

            th{
                background:rgba(102,126,234,0.1);
            }

            .badge{
                border-radius:15px;
                padding:5px 10px;
                font-size:12px;
                color:white;
            }

            .badge.new{
                background:#17a2b8;
            }
            .badge.nurturing{
                background:#ffc107;
                color:black;
            }
            .badge.qualified{
                background:#28a745;
            }
            .badge.assigned{
                background:#007bff;
            }
            .badge.disqualified{
                background:#dc3545;
            }

            .toast-success{
                position:fixed;
                top:20px;
                right:20px;
                background:linear-gradient(135deg,#28a745,#20c997);
                color:white;
                padding:15px 20px;
                border-radius:10px;
                box-shadow:0 5px 15px rgba(0,0,0,0.2);
                font-weight:600;
                display:flex;
                align-items:center;
                gap:10px;
                z-index:9999;
                animation:slideIn 0.5s ease;
            }

            @keyframes slideIn{
                from{
                    opacity:0;
                    transform:translateX(100px);
                }
                to{
                    opacity:1;
                    transform:translateX(0);
                }
            }
            .modal-box{
                display:grid;
                grid-template-columns:1fr 1fr;
                gap:15px;
                margin-top:15px;
            }

            .modal-item{
                background:#f8f9fa;
                padding:12px;
                border-radius:10px;
                font-size:14px;
            }

            .modal-item i{
                margin-right:6px;
                color:#667eea;
            }

            .modal-title{
                font-weight:600;
                color:#555;
                font-size:13px;
            }

            .modal-value{
                font-weight:600;
                margin-top:4px;
            }

            .status-badge{
                display:inline-block;
                padding:5px 10px;
                border-radius:12px;
                color:white;
                font-size:12px;
            }

            .status-new{
                background:#17a2b8;
            }
            .status-nurturing{
                background:#ffc107;
                color:black;
            }
            .status-qualified{
                background:#28a745;
            }
            .status-assigned{
                background:#007bff;
            }
            .status-disqualified{
                background:#dc3545;
            }
        </style>
    </head>

    <body>

        <c:if test="${not empty sessionScope.success}">
            <div id="toastSuccess" class="toast-success">
                <i class="fas fa-check-circle"></i>
                ${sessionScope.success}
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <div class="header">
                <h2><i class="fas fa-user-tie"></i> Quản Lý Lead</h2>

                <div style="display:flex;gap:10px">
                    <a href="${pageContext.request.contextPath}/marketingg/addLead" class="btn btn-primary">
                        <i class="fas fa-plus"></i> Thêm Lead
                    </a>

                    <a href="${pageContext.request.contextPath}/marketingg/importLead" class="btn btn-outline">
                        <i class="fas fa-file-excel"></i> Import Excel
                    </a>
                </div>
            </div>

            <!-- FILTER -->
            <form action="${pageContext.request.contextPath}/marketing/leadmanagement" method="get" class="filter-bar">
                <input type="text" name="search" placeholder="Tìm tên, email, phone..." value="${param.search}">

                <select name="statusFilter">
                    <option value="">-- Lọc theo trạng thái --</option>
                    <option value="new" ${param.statusFilter == 'new' ? 'selected' : ''}>New</option>
                    <option value="nurturing" ${param.statusFilter == 'nurturing' ? 'selected' : ''}>Nurturing</option>
                    <option value="qualified" ${param.statusFilter == 'qualified' ? 'selected' : ''}>Qualified</option>
                    <option value="assigned" ${param.statusFilter == 'assigned' ? 'selected' : ''}>Assigned</option>
                    <option value="disqualified" ${param.statusFilter == 'disqualified' ? 'selected' : ''}>Disqualified</option>
                </select>

                <button type="submit" class="btn btn-outline">
                    <i class="fas fa-search"></i> Tìm kiếm
                </button>
            </form>

            <div class="card">
                <div style="padding:20px">

                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Họ tên</th>
                                <th>Phone</th>
                                <th>Email</th>
                                <th>Campaign</th>
                                <th>Source</th>
                                <th>Status</th>
                                <th>Hành động</th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="l" items="${leadList}">
                                <tr 
                                    data-name="${l.fullName}"
                                    data-phone="${l.phone}"
                                    data-email="${l.email}"
                                    data-address="${l.address}"
                                    data-campaign="${l.campaignName}"
                                    data-product="${l.productInterest}"
                                    data-source="${l.source}"
                                    data-status="${l.status}"
                                    data-created="<fmt:formatDate value='${l.createdAt}' pattern='dd/MM/yyyy HH:mm'/>"
                                    data-createdby="${l.createdByName}"
                                    >

                                    <td>${l.id}</td>
                                    <td>${l.fullName}</td>
                                    <td>${l.phone}</td>
                                    <td>${l.email}</td>
                                    <td>${l.campaignName}</td>
                                    <td>${l.source}</td>

                                    <td>
                                        <!-- giữ nguyên form status -->
                                        <form action="${pageContext.request.contextPath}/marketing/updateLeadStatus" method="post">
                                            <input type="hidden" name="leadId" value="${l.id}"/>
                                            <select name="status"
                                                    class="badge ${l.status}"
                                                    onchange="this.form.submit()"
                                                    ${l.assignedSalesId != null ? 'disabled' : ''}>
                                                <option value="new" ${l.status=='new'?'selected':''}>New</option>
                                                <option value="nurturing" ${l.status=='nurturing'?'selected':''}>Nurturing</option>
                                                <option value="qualified" ${l.status=='qualified'?'selected':''}>Qualified</option>
                                                <option value="assigned" ${l.status=='assigned'?'selected':''} disabled>Assigned</option>
                                                <option value="disqualified" ${l.status=='disqualified'?'selected':''}>Disqualified</option>
                                            </select>
                                        </form>
                                    </td>

                                    <td>
                                        <button type="button" class="btn btn-outline btn-detail">
                                            <i class="fas fa-eye"></i>
                                        </button>

                                        <a href="${pageContext.request.contextPath}/marketingg/editLead?id=${l.id}"
                                           class="btn btn-primary">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>

                    <c:if test="${empty leadList}">
                        <div style="text-align:center;padding:20px;color:#777;">
                            <i class="fas fa-info-circle"></i> Không tìm thấy Lead nào.
                        </div>
                    </c:if>

                </div>
            </div>

        </div>

        <!-- MODAL -->
        <div id="leadModal" style="display:none;position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);z-index:9999;">

            <div style="background:white;width:600px;margin:6% auto;padding:25px;border-radius:18px;position:relative;">

                <span id="closeModal" style="position:absolute;right:15px;top:10px;cursor:pointer;font-size:20px;">&times;</span>

                <h3 style="margin-bottom:10px">
                    <i class="fas fa-user-circle"></i> Chi tiết Lead
                </h3>

                <div class="modal-box">

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-user"></i> Họ tên</div>
                        <div class="modal-value" id="m-name"></div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-phone"></i> Phone</div>
                        <div class="modal-value" id="m-phone"></div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-envelope"></i> Email</div>
                        <div class="modal-value" id="m-email"></div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-map-marker-alt"></i> Address</div>
                        <div class="modal-value" id="m-address"></div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-bullhorn"></i> Campaign</div>
                        <div class="modal-value" id="m-campaign"></div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-box"></i> Product</div>
                        <div class="modal-value" id="m-product"></div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-globe"></i> Source</div>
                        <div class="modal-value" id="m-source"></div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-flag"></i> Status</div>
                        <div class="modal-value">
                            <span id="m-status" class="status-badge"></span>
                        </div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-clock"></i> Ngày tạo</div>
                        <div class="modal-value" id="m-created"></div>
                    </div>

                    <div class="modal-item">
                        <div class="modal-title"><i class="fas fa-user-edit"></i> Người tạo</div>
                        <div class="modal-value" id="m-createdby"></div>
                    </div>

                </div>
            </div>
        </div>

        <script>
            // toast
            setTimeout(() => {
                let toast = document.getElementById("toastSuccess");
                if (toast) {
                    toast.style.opacity = "0";
                    toast.style.transform = "translateX(100px)";
                    setTimeout(() => toast.remove(), 500);
                }
            }, 5000);

            // modal
            const modal = document.getElementById("leadModal");
            const closeBtn = document.getElementById("closeModal");

            document.querySelectorAll(".btn-detail").forEach(btn => {
                btn.addEventListener("click", function () {
                    let row = this.closest("tr");

                    document.getElementById("m-name").innerText = row.dataset.name;
                    document.getElementById("m-phone").innerText = row.dataset.phone;
                    document.getElementById("m-email").innerText = row.dataset.email;
                    document.getElementById("m-address").innerText = row.dataset.address;
                    document.getElementById("m-campaign").innerText = row.dataset.campaign;
                    document.getElementById("m-product").innerText = row.dataset.product;
                    document.getElementById("m-source").innerText = row.dataset.source;
                    let statusEl = document.getElementById("m-status");
                    statusEl.innerText = row.dataset.status;

        // reset class
                    statusEl.className = "status-badge";

        // add class theo status
                    statusEl.classList.add("status-" + row.dataset.status);
                    document.getElementById("m-created").innerText = row.dataset.created;
                    document.getElementById("m-createdby").innerText = row.dataset.createdby;

                    modal.style.display = "block";
                });
            });

            closeBtn.onclick = () => modal.style.display = "none";

            window.onclick = function (e) {
                if (e.target === modal) {
                    modal.style.display = "none";
                }
            };
        </script>

    </body>
</html>