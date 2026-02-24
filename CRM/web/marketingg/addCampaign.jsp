<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thêm Campaign - CRM Admin</title>

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">


        <style>

            body {
                font-family: "Segoe UI", sans-serif;
                background: linear-gradient(135deg, #3a7bd5, #3a6073);
                margin: 0;
                color: #333;
            }

            .main-content {
                margin-left: 270px;
                padding: 40px;
                min-height: 100vh;
            }

            .form-card {
                background: rgba(255,255,255,0.95);
                border-radius: 20px;
                box-shadow: 0 10px 25px rgba(0,0,0,0.1);
                padding: 30px 40px;
                max-width: 800px;
                margin: 0 auto;
                animation: fadeIn 0.6s ease-in;
            }

            h2 {
                font-weight: 700;
                font-size: 26px;
                margin-bottom: 25px;
            }

            .form-label {
                font-weight: 600;
            }

            .form-control, .form-select {
                border-radius: 10px;
                padding: 10px;
            }

            .btn-outline-primary {
                border: 2px solid #667eea;
                color: #667eea;
                font-weight: 600;
                border-radius: 10px;
                padding: 10px 18px;
            }

            .btn-outline-primary:hover {
                background: #667eea;
                color: white;
            }

            .btn-submit {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
                font-weight: 600;
                border: none;
                padding: 12px 30px;
                border-radius: 10px;
            }

            .btn-submit:hover {
                opacity: 0.9;
            }

            .radio-group {
                display: flex;
                gap: 30px;
                margin-top: 8px;
            }

            @keyframes fadeIn {

                from {
                    opacity: 0;
                    transform: translateY(30px);
                }

                to {
                    opacity: 1;
                    transform: translateY(0);
                }

            }

        </style>

    </head>

    <body>

        <%@ include file="sidebar.jsp" %>


        <div class="main-content">


            <div class="form-card">
 <!-- ERROR MESSAGE -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger">
            ${error}
        </div>
    </c:if>

                <div class="d-flex justify-content-between align-items-center mb-4">


                    <h2>

                        <i class="fas fa-bullhorn"></i>

                        Thêm Campaign mới

                    </h2>


                    <a href="${pageContext.request.contextPath}/marketing/campaignmanagement"

                       class="btn btn-outline-primary">

                        <i class="fas fa-arrow-left"></i>

                        Quay lại

                    </a>


                </div>



                <form action="${pageContext.request.contextPath}/marketingg/addCampaign"

                      method="post"
                      enctype="multipart/form-data">



                    <!-- STAFF -->

                    <div class="row mb-3">

                        <div class="col-md-6">

                            <label class="form-label">Marketing Staff</label>
                            <input type="text"
                                   class="form-control"
                                   value="${sessionScope.userSession.displayName}"

                                   readonly>


                        </div>



                        <div class="col-md-6">

                            <label class="form-label">Campaign ID</label>

                            <input type="text"

                                   class="form-control"

                                   placeholder="Tự động sinh"

                                   disabled>

                        </div>

                    </div>



                    <!-- NAME -->

                    <div class="mb-3">

                        <label class="form-label">

                            Tên Campaign

                        </label>

                        <input type="text"

                               class="form-control"

                               name="name"

                               required>

                    </div>



                    <!-- DESCRIPTION -->

                    <div class="mb-3">

                        <label class="form-label">

                            Mô tả

                        </label>

                        <textarea class="form-control"

                                  name="description"

                                  rows="3"></textarea>

                    </div>



                    <!-- BANNER URL -->

                    <div class="mb-3">

                        <label class="form-label">

                            Banner URL

                        </label>

                        <input type="file"
                               class="form-control"
                               name="banner"
                               accept="image/*"
                               required>


                    </div>



                    <!-- START DATE -->

                    <div class="mb-3">

                        <label class="form-label">

                            Ngày bắt đầu

                        </label>

                        <input type="date"

                               class="form-control"

                               name="startDate">

                    </div>



                    <!-- END DATE -->

                    <div class="mb-3">

                        <label class="form-label">

                            Ngày kết thúc

                        </label>

                        <input type="date"

                               class="form-control"

                               name="endDate">

                    </div>



                    <!-- STATUS -->

                    <div class="mb-3">

                        <label class="form-label">

                            Trạng thái

                        </label>


                        <div class="radio-group">


                            <div>

                                <input type="radio"

                                       name="status"

                                       value="ACTIVE"

                                       checked>


                                Đang hoạt động


                            </div>


                            <div>

                                <input type="radio"

                                       name="status"

                                       value="INACTIVE">


                                Ngừng hoạt động


                            </div>


                        </div>


                    </div>



                    <!-- BUTTON -->


                    <div class="text-end">


                        <button type="submit"

                                class="btn-submit">


                            <i class="fas fa-check-circle"></i>


                            Lưu Campaign


                        </button>


                    </div>


                </form>


            </div>


        </div>


    </body>

</html>
