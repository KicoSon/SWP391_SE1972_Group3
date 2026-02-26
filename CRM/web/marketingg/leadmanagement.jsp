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

            /* STATUS COLOR */

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

                <h2>

                    <i class="fas fa-user-tie"></i>

                    Quản Lý Lead

                </h2>

                <a href="${pageContext.request.contextPath}/marketingg/addLead"
                   class="btn btn-primary">

                    <i class="fas fa-plus"></i>

                    Thêm Lead

                </a>

            </div>


            <!-- FILTER -->

            <form action="${pageContext.request.contextPath}/marketing/leadmanagement"
                  method="get"
                  class="filter-bar">


                <input type="text"
                       name="search"
                       placeholder="Tìm tên, email, phone..."
                       value="${param.search}">


                <select name="statusFilter">

                    <option value="">-- Lọc theo trạng thái --</option>

                    <option value="new"
                            ${param.statusFilter == 'new' ? 'selected' : ''}>
                        New
                    </option>


                    <option value="nurturing"
                            ${param.statusFilter == 'nurturing' ? 'selected' : ''}>
                        Nurturing
                    </option>


                    <option value="qualified"
                            ${param.statusFilter == 'qualified' ? 'selected' : ''}>
                        Qualified
                    </option>


                    <option value="assigned"
                            ${param.statusFilter == 'assigned' ? 'selected' : ''}>
                        Assigned
                    </option>


                    <option value="disqualified"
                            ${param.statusFilter == 'disqualified' ? 'selected' : ''}>
                        Disqualified
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

                                <th>Họ tên</th>

                                <th>Phone</th>

                                <th>Email</th>

                                <th>Campaign</th>
                                <th>Product Interest</th>

                                <th>Source</th>

                                <th>Status</th>

                                <th>Ngày tạo</th>

                            </tr>

                        </thead>


                        <tbody>

                            <c:forEach var="l" items="${leadList}">

                                <tr>

                                    <td>${l.id}</td>

                                    <td>${l.fullName}</td>

                                    <td>${l.phone}</td>

                                    <td>${l.email}</td>

                                    <td>${l.campaignName}</td>
                                    <td>${l.productInterest}</td>


                                    <td>${l.source}</td>


                                    <td>

                                        <span class="badge ${l.status}">

                                            ${l.status}

                                        </span>

                                    </td>


                                    <td>

                                        <fmt:formatDate value="${l.createdAt}"
                                                        pattern="dd/MM/yyyy HH:mm"/>

                                    </td>

                                </tr>

                            </c:forEach>

                        </tbody>

                    </table>



                    <c:if test="${empty leadList}">

                        <div style="text-align:center;padding:20px;color:#777;">

                            <i class="fas fa-info-circle"></i>

                            Không tìm thấy Lead nào.

                        </div>

                    </c:if>



                </div>

            </div>

        </div>

        <script>

            setTimeout(() => {

                let toast = document.getElementById("toastSuccess");

                if (toast) {

                    toast.style.opacity = "0";

                    toast.style.transform = "translateX(100px)";

                    setTimeout(() => toast.remove(), 500);

                }

            }, 5000);

        </script>
    </body>

</html>
