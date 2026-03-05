<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Chi Tiết Khách Hàng</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <!-- Favicon -->
        <link rel="icon" type="image/png"
              href="${pageContext.request.contextPath}/assets/images/favicon.png">
        <style>
            body{
                margin:0;
                font-family:"Segoe UI",sans-serif;
                background:linear-gradient(135deg,#3a7bd5,#3a6073);
            }

            .main-content{
                margin-left:270px;
                padding:40px;
                min-height:100vh;
            }

            .detail-card{
                background:white;
                border-radius:20px;
                padding:40px;
                box-shadow:0 15px 40px rgba(0,0,0,0.15);
            }

            .top-section{
                display:flex;
                gap:40px;
                align-items:center;
                border-bottom:1px solid #eee;
                padding-bottom:30px;
                margin-bottom:30px;
            }

            .avatar{
                width:160px;
                height:160px;
                border-radius:50%;
                object-fit:cover;
                border:5px solid #667eea;
            }

            .customer-name{
                font-size:28px;
                font-weight:700;
            }

            .badge{
                padding:6px 14px;
                border-radius:20px;
                font-size:13px;
                color:white;
                display:inline-block;
                margin-top:8px;
            }

            .active{
                background:#28a745;
            }
            .inactive{
                background:#e74c3c;
            }

            .info-grid{
                display:grid;
                grid-template-columns: 1fr 1fr;
                gap:25px 50px;
            }

            .info-box{
                background:#f8f9ff;
                padding:20px;
                border-radius:12px;
            }

            .label{
                font-size:13px;
                color:#777;
                margin-bottom:5px;
            }

            .value{
                font-size:16px;
                font-weight:600;
            }

            .actions{
                margin-top:40px;
                display:flex;
                gap:15px;
            }

            .btn{
                padding:10px 18px;
                border-radius:8px;
                text-decoration:none;
                font-weight:600;
                font-size:14px;
            }

            .btn-edit{
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
            }

            .btn-back{
                border:2px solid #667eea;
                color:#667eea;
            }
        </style>
    </head>

    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <div class="detail-card">

                <div class="top-section">
                    <img src="${pageContext.request.contextPath}/${customer.profileURL}" class="avatar">

                    <div>
                        <div class="customer-name">
                            ${customer.fullName}
                        </div>

                        <div>
                            <div class="info-box">
                                <div class="label">Status</div>
                                <div class="value">#${customer.status}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="info-grid">

                    <div class="info-box">
                        <div class="label">Customer ID</div>
                        <div class="value">#${customer.id}</div>
                    </div>

                    <div class="info-box">
                        <div class="label">Email</div>
                        <div class="value">${customer.email}</div>
                    </div>

                    <div class="info-box">
                        <div class="label">Số điện thoại</div>
                        <div class="value">${customer.phone}</div>
                    </div>

                    <div class="info-box">
                        <div class="label">Tier</div>
                        <div class="value">Tier ${customer.tier}</div>
                    </div>

                    <div class="info-box">
                        <div class="label">Ngày tạo</div>
                        <div class="value">Ngày tạo ${customer.createdAt}</div>
                    </div>

                </div>

                <div class="actions">           
                    <a href="${pageContext.request.contextPath}/customerservice/customerlist"
                       class="btn btn-back">
                        <i class="fas fa-arrow-left"></i> Quay lại
                    </a>
                </div>

            </div>

        </div>

    </body>
</html>