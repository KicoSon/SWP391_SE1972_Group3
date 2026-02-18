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

            /* GIỮ NGUYÊN STYLE */

            body{
                margin:0;
                font-family:"Segoe UI";
                background:linear-gradient(135deg,#3a7bd5,#3a6073);
            }

            .main-content{
                margin-left:270px;
                padding:30px;
            }

            .header{
                background:white;
                padding:25px;
                border-radius:20px;
                box-shadow:0 10px 25px rgba(0,0,0,0.1);
                margin-bottom:30px;
                display:flex;
                justify-content:space-between;
            }

            .btn{
                padding:10px 16px;
                border-radius:8px;
                text-decoration:none;
                font-weight:600;
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

            .card{
                background:white;
                border-radius:20px;
                box-shadow:0 10px 30px rgba(0,0,0,0.1);
            }

            table{
                width:100%;
                border-collapse:collapse;
            }

            th,td{
                padding:12px;
                border-bottom:1px solid #eee;
            }

            .badge{
                padding:5px 10px;
                border-radius:15px;
                color:white;
                font-size:12px;
            }

            .badge.new{
                background:#17a2b8;
            }
            .badge.qualified{
                background:#28a745;
            }
            .badge.disqualified{
                background:#dc3545;
            }

        </style>

    </head>

    <body>

        <%@ include file="sidebar.jsp" %>


        <div class="main-content">


            <div class="header">

                <h2>

                    <i class="fas fa-user-tie"></i>

                    Quản Lý Lead

                </h2>


            </div>


            <!-- SEARCH -->

            <form action="${pageContext.request.contextPath}/marketing/leadmanagement"

                  method="get"

                  class="filter-bar">


                <input type="text"

                       name="search"

                       placeholder="Tìm tên, email, phone..."

                       value="${param.search}">


                <select name="statusFilter">

                    <option value="">-- Trạng thái --</option>

                    <option value="new">New</option>

                    <option value="qualified">Qualified</option>

                    <option value="disqualified">Disqualified</option>

                </select>


                <button class="btn btn-outline">

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

                        <div style="padding:20px;text-align:center">

                            Không có lead nào

                        </div>

                    </c:if>



                </div>

            </div>


        </div>


    </body>

</html>
