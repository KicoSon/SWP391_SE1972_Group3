<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jsp/jstl/fmt" %>


<!DOCTYPE html>
<html lang="vi">

    <head>

        <meta charset="UTF-8">

        <title>Cập nhật Campaign</title>

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

            .preview-img{
                width:200px;
                border-radius:10px;
                margin-top:10px;
            }

        </style>

    </head>


    <body>

        <%@ include file="sidebar.jsp" %>


        <div class="main-content">


            <div class="form-card">


                <div class="d-flex justify-content-between mb-4">

                    <h3>

                        <i class="fas fa-pen"></i>

                        Cập nhật Campaign

                    </h3>


                    <a href="${pageContext.request.contextPath}/marketing/campaignmanagement"

                       class="btn btn-outline-primary">

                        Quay lại

                    </a>

                </div>


                <c:if test="${not empty error}">

                    <div class="alert alert-danger">

                        ${error}

                    </div>

                </c:if>


                <form action="${pageContext.request.contextPath}/marketingg/editCampaign"

                      method="post"

                      enctype="multipart/form-data">


                    <input type="hidden"

                           name="id"

                           value="${campaign.id}">


                    <input type="hidden"

                           name="oldBanner"

                           value="${campaign.bannerUrl}">



                    <div class="mb-3">

                        <label>Tên Campaign</label>

                        <input type="text"

                               name="name"

                               class="form-control"

                               value="${campaign.name}"

                               required>

                    </div>



                    <div class="mb-3">

                        <label>Mô tả</label>

                        <textarea name="description"

                                  class="form-control"

                                  rows="3">${campaign.description}</textarea>

                    </div>



                    <div class="mb-3">

                        <label>Banner hiện tại</label>

                        <br>

                        <img src="${pageContext.request.contextPath}${campaign.bannerUrl}"

                             class="preview-img">

                    </div>



                    <div class="mb-3">

                        <label>Đổi banner</label>

                        <input type="file"

                               name="banner"

                               class="form-control">

                    </div>



                    <div class="mb-3">

                        <label>Ngày bắt đầu</label>

                        <input type="date"

                               name="startDate"

                               class="form-control"

                               value="<fmt:formatDate value='${campaign.startDate}' pattern='yyyy-MM-dd'/>"

                               required>

                    </div>



                    <div class="mb-3">

                        <label>Ngày kết thúc</label>

                        <input type="date"

                               name="endDate"

                               class="form-control"

                               value="<fmt:formatDate value='${campaign.endDate}' pattern='yyyy-MM-dd'/>">

                    </div>



                    <div class="mb-3">

                        <label>Trạng thái</label>

                        <br>

                        <input type="radio"

                               name="status"

                               value="ACTIVE"

                               ${campaign.status == 'ACTIVE' ? 'checked' : ''}>

                        Hoạt động


                        <input type="radio"

                               name="status"

                               value="INACTIVE"

                               ${campaign.status == 'INACTIVE' ? 'checked' : ''}>

                        Ngừng


                    </div>


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
