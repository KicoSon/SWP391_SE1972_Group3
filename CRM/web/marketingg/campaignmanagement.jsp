<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Quản Lý Campaign</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <style>

            body {
                margin: 0;
                font-family: "Segoe UI", sans-serif;
                background: linear-gradient(135deg, #3a7bd5, #3a6073);
                color: #333;
                overflow-x: hidden;
                animation: fadeIn 0.8s ease-in-out;
            }

            @keyframes fadeIn {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .main-content {
                margin-left: 270px;
                padding: 30px;
                min-height: 100vh;
            }

            .header {
                background: rgba(255,255,255,0.95);
                backdrop-filter: blur(20px);
                padding: 25px 30px;
                border-radius: 20px;
                box-shadow: 0 10px 25px rgba(0,0,0,0.15);
                margin-bottom: 30px;
                display:flex;
                justify-content:space-between;
                align-items:center;
            }

            .header h2 {
                font-weight:700;
                font-size:26px;
            }

            .btn {
                border:none;
                padding:10px 16px;
                border-radius:8px;
                font-size:14px;
                font-weight:600;
                cursor:pointer;
                text-decoration:none;
            }

            .btn-primary {
                background: linear-gradient(135deg,#667eea,#764ba2);
                color:white;
            }

            .btn-outline {
                border:2px solid #667eea;
                color:#667eea;
                background:transparent;
            }

            .filter-bar {
                display:flex;
                gap:15px;
                margin-bottom:25px;
            }

            .filter-bar input,
            .filter-bar select {
                padding:10px;
                border-radius:8px;
                border:1px solid #ccc;
            }

            .card {
                background:white;
                border-radius:20px;
                box-shadow:0 10px 30px rgba(0,0,0,0.1);
                overflow:hidden;
            }

            table {
                width:100%;
                border-collapse:collapse;
            }

            th, td {
                padding:12px;
                border-bottom:1px solid rgba(0,0,0,0.1);
            }

            th {
                background: rgba(102,126,234,0.1);
            }

            .action-btns {
                display:flex;
                gap:8px;
            }

            .edit-btn {
                background:#4facfe;
                color:white;
                padding:6px 10px;
                border-radius:6px;
            }

            .delete-btn {
                background:#f5576c;
                color:white;
                padding:6px 10px;
                border-radius:6px;
            }

            .badge {
                border-radius:15px;
                padding:5px 10px;
                font-size:12px;
                color:white;
            }

            .badge.active {
                background:#28a745;
            }

            .badge.inactive {
                background:#e74c3c;
            }
.toast-success{

position:fixed;

top:20px;

right:30px;

background:linear-gradient(135deg,#00c851,#007e33);

color:white;

padding:16px 28px;

border-radius:12px;

box-shadow:0 8px 25px rgba(0,0,0,0.2);

font-weight:600;

z-index:9999;

animation:slideIn 0.5s ease;

}

.toast-success i{

margin-right:10px;

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

        </style>

    </head>
<c:if test="${not empty sessionScope.success}">

<div id="toastSuccess" class="toast-success">

    <i class="fas fa-check-circle"></i>

    ${sessionScope.success}

</div>

<c:remove var="success" scope="session"/>

</c:if>

    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">


            <div class="header">

                <h2>

                    <i class="fas fa-bullhorn"></i>

                    Quản Lý Campaign

                </h2>

                <a href="${pageContext.request.contextPath}/marketingg/addCampaign" class="btn btn-primary">

                    <i class="fas fa-plus"></i>

                    Thêm Campaign

                </a>

            </div>


            <form action="${pageContext.request.contextPath}/marketing/campaignmanagement"

                  method="get"

                  class="filter-bar">

                <input type="text"

                       name="search"

                       placeholder="Tìm theo tên campaign..."

                       value="${param.search}">


                <select name="statusFilter">

                    <option value="">-- Lọc theo trạng thái --</option>

                    <option value="ACTIVE"

                            ${param.statusFilter == 'ACTIVE' ? 'selected' : ''}>

                        Đang hoạt động

                    </option>

                    <option value="INACTIVE"

                            ${param.statusFilter == 'INACTIVE' ? 'selected' : ''}>

                        Ngừng hoạt động

                    </option>

                </select>


                <button type="submit" class="btn btn-outline">

                    <i class="fas fa-search"></i>

                    Tìm kiếm

                </button>

            </form>


            <div class="card">

                <div style="padding:20px">

                    <table>

                        <thead>

                            <tr>

                                <th>ID</th>
                                <th>Banner</th>
                                <th>Tên Campaign</th>

                                <th>Mô tả</th>

                                <th>Ngày bắt đầu</th>

                                <th>Ngày kết thúc</th>

                                <th>Trạng thái</th>

                                <th>Hành động</th>

                            </tr>

                        </thead>


                        <tbody>

                            <c:forEach var="c" items="${campaignList}">

                                <tr>

                                    <td>${c.id}</td>
                                    <td>
                                        <img src="${pageContext.request.contextPath}/${c.bannerUrl}"
                                             width="120">

                                    </td>

                                    <td>${c.name}</td>

                                    <td>${c.description}</td>

                                    <td>

                                        <fmt:formatDate value="${c.startDate}"

                                                        pattern="dd/MM/yyyy"/>

                                    </td>

                                    <td>

                                        <fmt:formatDate value="${c.endDate}"

                                                        pattern="dd/MM/yyyy"/>

                                    </td>


                                    <td>

                                        <span class="badge ${c.status == 'ACTIVE' ? 'active' : 'inactive'}">

                                            ${c.status == 'ACTIVE' ? 'Đang hoạt động' : 'Ngừng hoạt động'}

                                        </span>

                                    </td>


                                    <td>

                                        <div class="action-btns">

                                            <a href="${pageContext.request.contextPath}/marketingg/editCampaign?id=${c.id}"
                                               class="btn btn-primary">
                                                <i class="fas fa-edit"></i> Sửa
                                            </a>





                                        </div>

                                    </td>

                                </tr>

                            </c:forEach>

                        </tbody>

                    </table>


                    <c:if test="${empty campaignList}">

                        <div style="text-align:center;padding:20px;color:#777;">

                            <i class="fas fa-info-circle"></i>

                            Không tìm thấy campaign nào.

                        </div>

                    </c:if>


                </div>

            </div>


        </div>
<script>

setTimeout(()=>{

let toast=document.getElementById("toastSuccess");

if(toast){

toast.style.opacity="0";

toast.style.transform="translateX(100px)";

setTimeout(()=>toast.remove(),500);

}

},5000);

</script>

    </body
    

</html

