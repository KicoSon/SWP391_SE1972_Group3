<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">

    <head>

        <meta charset="UTF-8">

        <title>Assign Lead</title>

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <style>

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
                padding:20px;
                border-radius:15px;
                margin-bottom:20px;
            }

            .card{
                background:white;
                border-radius:15px;
                padding:20px;
            }

            table{
                width:100%;
                border-collapse:collapse;
            }

            th,td{
                padding:10px;
                border-bottom:1px solid #ddd;
            }

            .btn{
                padding:6px 12px;
                border:none;
                border-radius:6px;
                cursor:pointer;
            }

            .btn-primary{
                background:#667eea;
                color:white;
            }

            .badge{
                padding:5px 10px;
                border-radius:10px;
                color:white;
            }

            .badge.assigned{
                background:#007bff;
            }

            .badge.qualified{
                background:#28a745;
            }

        </style>

    </head>

    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <div class="header">

                <h2>

                    <i class="fas fa-user-check"></i>

                    Assign Lead to Sales

                </h2>

            </div>


            <div class="card">

                <table>

                    <thead>

                        <tr>

                            <th>ID</th>

                            <th>Name</th>

                            <th>Phone</th>

                            <th>Status</th>
                            <th>Address</th>
                            <th>Product Interest</th>
                            <th>Source</th>
                            <th>Sales</th>
                            <th>Action</th>

                        </tr>

                    </thead>

                    <tbody>

                        <c:forEach var="lead" items="${leadList}">

                            <tr>

                                <td>${lead.id}</td>

                                <td>${lead.fullName}</td>

                                <td>${lead.phone}</td>

                                <td>

                                    <span class="badge ${lead.status}">

                                        ${lead.status}

                                    </span>

                                </td>
                                <td>${lead.address}</td>


                                <td>${lead.productInterest}</td>

                                <td>${lead.source}</td>

                                <td>

                                    <c:choose>

                                        <c:when test="${lead.saleName != null}">

                                            <b>${lead.saleName}</b>

                                        </c:when>

                                        <c:otherwise>

                                            <span style="color:red">

                                                Chưa assign

                                            </span>

                                        </c:otherwise>

                                    </c:choose>

                                </td>


                                <td>

                                    <form action="${pageContext.request.contextPath}/marketing/assignLead"

                                          method="post">

                                        <input type="hidden"

                                               name="leadId"

                                               value="${lead.id}">


                                        <select name="saleId" required>

                                            <option value="">

                                                -- chọn sales --

                                            </option>

                                            <c:forEach var="s" items="${salesList}">

                                                <option value="${s.id}"

                                                        ${lead.assignedSalesId == s.id ? 'selected' : ''}>

                                                    ${s.fullName}

                                                </option>

                                            </c:forEach>

                                        </select>


                                        <button class="btn btn-primary">

                                            Assign

                                        </button>

                                    </form>

                                </td>


                            </tr>

                        </c:forEach>

                    </tbody>

                </table>

            </div>

        </div>

    </body>

</html>