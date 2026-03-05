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
        font-family:"Segoe UI",sans-serif;
        background:linear-gradient(135deg,#3a7bd5,#3a6073);
        color:#333;
    }

    .main-content{
        margin-left:270px;
        padding:40px;
        min-height:100vh;
    }

    .header{
        background:rgba(255,255,255,0.95);
        backdrop-filter:blur(20px);
        padding:25px 30px;
        border-radius:20px;
        margin-bottom:30px;
        box-shadow:0 10px 30px rgba(0,0,0,0.15);
        display:flex;
        align-items:center;
        justify-content:space-between;
    }

    .header h2{
        margin:0;
        font-size:24px;
        font-weight:700;
        color:#333;
    }

    .card{
        background:white;
        border-radius:20px;
        padding:25px;
        box-shadow:0 15px 35px rgba(0,0,0,0.15);
        animation:fadeIn 0.6s ease-in-out;
    }

    @keyframes fadeIn{
        from{opacity:0;transform:translateY(15px);}
        to{opacity:1;transform:translateY(0);}
    }

    table{
        width:100%;
        border-collapse:collapse;
        overflow:hidden;
        border-radius:15px;
    }

    thead{
        background:linear-gradient(135deg,#667eea,#764ba2);
        color:white;
    }

    th{
        padding:14px;
        text-align:left;
        font-weight:600;
        font-size:14px;
    }

    td{
        padding:14px;
        border-bottom:1px solid #eee;
        font-size:14px;
    }

    tbody tr{
        transition:all 0.2s ease;
    }

    tbody tr:hover{
        background:#f5f7ff;
        transform:scale(1.003);
    }

    .btn{
        padding:7px 14px;
        border:none;
        border-radius:8px;
        cursor:pointer;
        font-weight:600;
        font-size:13px;
        transition:all 0.3s ease;
    }

    .btn-primary{
        background:linear-gradient(135deg,#667eea,#764ba2);
        color:white;
    }

    .btn-primary:hover{
        opacity:0.9;
        transform:translateY(-2px);
        box-shadow:0 6px 15px rgba(0,0,0,0.2);
    }

    select{
        padding:6px 10px;
        border-radius:8px;
        border:1px solid #ccc;
        font-size:13px;
        transition:0.3s;
    }

    select:focus{
        border-color:#667eea;
        outline:none;
        box-shadow:0 0 0 3px rgba(102,126,234,0.2);
    }

    .badge{
        padding:6px 12px;
        border-radius:20px;
        font-size:12px;
        font-weight:600;
        text-transform:capitalize;
    }

    .badge.ASSIGNED{
        background:#007bff;
        color:white;
    }

    .badge.QUALIFIED{
        background:#28a745;
        color:white;
    }

    .badge.NEW{
        background:#ffc107;
        color:#333;
    }

    .badge.REJECTED{
        background:#dc3545;
        color:white;
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