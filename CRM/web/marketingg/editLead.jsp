<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">

    <head>

        <meta charset="UTF-8">

        <title>Cập nhật Lead</title>

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">


        <style>

            body{
                font-family:"Segoe UI";
                background:linear-gradient(135deg,#3a7bd5,#3a6073);
            }

            .main-content{
                margin-left:270px;
                padding:40px;
            }

            .form-card{
                background:white;
                border-radius:20px;
                padding:30px;
                max-width:800px;
                margin:auto;
                box-shadow:0 10px 25px rgba(0,0,0,0.1);
            }

            .btn-submit{
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
                border:none;
                padding:12px 30px;
                border-radius:10px;
            }

        </style>

    </head>


    <body>

        <%@ include file="sidebar.jsp" %>


        <div class="main-content">


            <div class="form-card">


                <div class="d-flex justify-content-between mb-4">

                    <h3>

                        <i class="fas fa-user-edit"></i>

                        Cập nhật Lead

                    </h3>


                    <a href="${pageContext.request.contextPath}/marketing/leadmanagement"

                       class="btn btn-outline-primary">

                        Quay lại

                    </a>

                </div>



                <c:if test="${not empty error}">

                    <div class="alert alert-danger">

                        ${error}

                    </div>

                </c:if>



                <form action="${pageContext.request.contextPath}/marketingg/editLead"

                      method="post">


                    <input type="hidden"

                           name="id"

                           value="${lead.id}">



                    <!-- FULL NAME -->

                    <div class="mb-3">

                        <label>Họ và tên</label>

                        <input type="text"

                               name="fullName"

                               class="form-control"

                               value="${lead.fullName}"

                               required>

                    </div>



                    <!-- PHONE -->

                    <div class="mb-3">

                        <label>Số điện thoại</label>

                        <input type="text"

                               name="phone"

                               class="form-control"

                               value="${lead.phone}">

                    </div>



                    <!-- EMAIL -->

                    <div class="mb-3">

                        <label>Email</label>

                        <input type="email"

                               name="email"

                               class="form-control"

                               value="${lead.email}">

                    </div>



                    <!-- ADDRESS -->

                    <div class="mb-3">

                        <label>Địa chỉ</label>

                        <input type="text"

                               name="address"

                               class="form-control"

                               value="${lead.address}">

                    </div>



                    <!-- PRODUCT -->

                    <div class="mb-3">

                        <label>Sản phẩm quan tâm</label>

                        <input type="text"

                               name="productInterest"

                               class="form-control"

                               value="${lead.productInterest}">

                    </div>







                    <!-- CAMPAIGN -->

                    <div class="mb-3">

                        <label>Campaign</label>

                        <select name="campaignId"

                                class="form-select"

                                <c:if test="${lead.status != 'new' and lead.status != 'nurturing'}">
                                    disabled
                                </c:if>
                                >

                            <option value="">-- Chọn Campaign --</option>

                            <c:forEach var="c" items="${campaignList}">

                                <option value="${c.id}"

                                        <c:if test="${c.id == lead.campaignId}">
                                            selected
                                        </c:if>
                                        >

                                    ${c.name}

                                </option>

                            </c:forEach>

                        </select>


                        <c:if test="${lead.status != 'new' and lead.status != 'nurturing'}">

                            <small class="text-muted">

                                Không thể đổi campaign khi lead đã được assign

                            </small>

                        </c:if>

                    </div>



                    <!-- BUTTON -->

                    <div class="text-end">

                        <button class="btn-submit">

                            <i class="fas fa-save"></i>

                            Cập nhật

                        </button>

                    </div>



                </form>


            </div>


        </div>


    </body>

</html>