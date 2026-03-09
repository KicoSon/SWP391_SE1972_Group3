<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">

    <head>

        <meta charset="UTF-8">
        <title>Profile - CRM</title>

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>

            body{
                font-family:"Segoe UI",sans-serif;
                background:linear-gradient(135deg,#3a7bd5,#3a6073);
                margin:0;
            }

            .main-content{
                margin-left:270px;
                padding:40px;
                min-height:100vh;
            }

            .profile-card{
                background:rgba(255,255,255,0.95);
                border-radius:20px;
                box-shadow:0 10px 25px rgba(0,0,0,0.1);
                padding:35px;
                max-width:800px;
                margin:auto;
                animation:fadeIn .5s ease;
            }

            h2{
                font-weight:700;
                margin-bottom:30px;
            }

            .profile-item{
                display:flex;
                align-items:center;
                padding:15px 0;
                border-bottom:1px solid #eee;
            }

            .profile-item:last-child{
                border-bottom:none;
            }

            .icon-box{
                width:40px;
                height:40px;
                background:#eef2ff;
                border-radius:50%;
                display:flex;
                align-items:center;
                justify-content:center;
                margin-right:15px;
            }

            .label{
                font-weight:600;
                color:#555;
            }

            .value{
                font-size:16px;
            }

            .badge-active{
                background:#28a745;
            }

            .badge-inactive{
                background:#dc3545;
            }

            @keyframes fadeIn{

                from{
                    opacity:0;
                    transform:translateY(20px);
                }

                to{
                    opacity:1;
                    transform:translateY(0);
                }

            }

        </style>

    </head>

    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <div class="profile-card">

                <h2>
                    <i class="fas fa-user"></i>
                    Thông Tin Cá Nhân
                </h2>







                <!-- DISPLAY NAME -->

                <div class="profile-item">

                    <div class="icon-box">
                        <i class="fas fa-user"></i>
                    </div>

                    <div>

                        <div class="label">Tên hiển thị</div>

                        <div class="value">
                            ${sessionScope.userSession.displayName}
                        </div>

                    </div>

                </div>



                <!-- EMAIL -->

                <div class="profile-item">

                    <div class="icon-box">
                        <i class="fas fa-envelope"></i>
                    </div>

                    <div>

                        <div class="label">Email</div>

                        <div class="value">
                            ${sessionScope.userSession.staffInfo.email}
                        </div>

                    </div>

                </div>



                <!-- DEPARTMENT -->

                <div class="profile-item">

                    <div class="icon-box">
                        <i class="fas fa-building"></i>
                    </div>

                    <div>

                        <div class="label">Phòng ban</div>

                        <div class="value">
                            ${sessionScope.userSession.staffInfo.department}
                        </div>

                    </div>

                </div>



                <!-- ROLE -->

                <div class="profile-item">

                    <div class="icon-box">
                        <i class="fas fa-user-tag"></i>
                    </div>

                    <div>

                        <div class="label">Role</div>

                        <div class="value">

                            <c:forEach items="${sessionScope.userSession.roles}" var="r">
                                <span class="badge bg-primary me-1">${r.name}</span>
                            </c:forEach>

                        </div>

                    </div>

                </div>



                <!-- STATUS -->

                <div class="profile-item">

                    <div class="icon-box">
                        <i class="fas fa-circle-check"></i>
                    </div>

                    <div>

                        <div class="label">Trạng thái</div>

                        <div class="value">

                            <c:choose>

                                <c:when test="${sessionScope.userSession.staffInfo.active}">
                                    <span class="badge badge-active">Active</span>
                                </c:when>

                                <c:otherwise>
                                    <span class="badge badge-inactive">Inactive</span>
                                </c:otherwise>

                            </c:choose>

                        </div>

                    </div>

                </div>


            </div>

        </div>

    </body>
</html>