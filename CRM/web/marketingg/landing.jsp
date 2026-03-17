<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Landing Page</title>

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">

        <style>
            body{
                margin:0;
                font-family:"Segoe UI";
                background:#f5f7fa;
                padding-top:70px;
            }

            /* HEADER */
            .header{
                position:fixed;
                top:0;
                width:100%;
                display:flex;
                justify-content:space-between;
                align-items:center;
                padding:15px 40px;
                background:rgba(255,255,255,0.9);
                backdrop-filter:blur(10px);
                box-shadow:0 4px 15px rgba(0,0,0,0.1);
                z-index:1000;
            }

            .logo{
                font-weight:700;
                font-size:20px;
                color:#667eea;
            }

            .menu a{
                margin-left:20px;
                text-decoration:none;
                color:#333;
                font-weight:500;
            }

            .menu a:hover{
                color:#667eea;
            }

            /* HERO */
            .hero{
                height:75vh;
                display:flex;
                align-items:center;
                justify-content:center;
                color:white;
                text-align:center;
                background-size:cover;
                background-position:center;
                position:relative;
                cursor:pointer;
            }

            .hero::after{
                content:"";
                position:absolute;
                width:100%;
                height:100%;
                background:linear-gradient(135deg,rgba(0,0,0,0.5),rgba(102,126,234,0.6));
                top:0;
                left:0;
            }

            .hero-content{
                position:relative;
                z-index:2;
            }

            .hero h1{
                font-size:42px;
                margin-bottom:10px;
            }

            /* GRID */
            .container{
                padding:50px;
            }

            .grid{
                display:grid;
                grid-template-columns:repeat(auto-fit,minmax(280px,1fr));
                gap:25px;
            }

            .card{
                background:white;
                border-radius:15px;
                overflow:hidden;
                box-shadow:0 10px 25px rgba(0,0,0,0.1);
                cursor:pointer;
                transition:0.3s;
            }

            .card:hover{
                transform:translateY(-10px) scale(1.03);
            }

            .card img{
                width:100%;
                height:180px;
                object-fit:cover;
            }

            .card-body{
                padding:15px;
            }

            /* MODAL */
            .modal{
                display:none;
                position:fixed;
                width:100%;
                height:100%;
                background:rgba(0,0,0,0.6);
                top:0;
                z-index:9999;
            }

            .modal-box{
                background:white;
                width:420px;
                margin:8% auto;
                padding:25px;
                border-radius:15px;
                animation:fadeIn 0.4s ease;
                position:relative;
            }

            .close{
                position:absolute;
                right:15px;
                top:10px;
                font-size:20px;
                cursor:pointer;
            }

            input{
                width:100%;
                padding:10px;
                margin:10px 0;
                border-radius:8px;
                border:1px solid #ccc;
            }

            button{
                width:100%;
                padding:12px;
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
                border:none;
                border-radius:8px;
                font-weight:600;
                cursor:pointer;
            }

            /* FOOTER */
            .footer{
                background:#1e1e2f;
                color:white;
                padding:40px 20px;
                text-align:center;
            }

            /* TOAST */
            .toast{
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 14px 22px;
                border-radius: 10px;
                color: white;
                font-weight: 500;
                box-shadow: 0 5px 20px rgba(0,0,0,0.2);
                z-index: 99999;
                animation: slideIn 0.4s ease;
            }

            .toast.success{
                background: linear-gradient(135deg,#28a745,#43d17a);
            }

            .toast.error{
                background: linear-gradient(135deg,#dc3545,#ff6b6b);
            }

            @keyframes slideIn{
                from{
                    transform: translateX(100%);
                    opacity: 0;
                }
                to{
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            .footer{
                background: linear-gradient(135deg, #667eea, #764ba2);
                color:white;
                padding:50px 40px 20px;
            }

            .footer-container{
                display:grid;
                grid-template-columns:repeat(auto-fit,minmax(200px,1fr));
                gap:30px;
                margin-bottom:20px;
            }

            .footer-col h3, .footer-col h4{
                margin-bottom:15px;
            }

            .footer-col p{
                color:#ccc;
                font-size:14px;
            }

            .footer-col a{
                display:block;
                color:#ccc;
                text-decoration:none;
                margin:6px 0;
                font-size:14px;
                transition:0.3s;
            }

            .footer-col a:hover{
                color:#667eea;
                transform:translateX(5px);
            }

            .social i{
                font-size:18px;
                margin-right:12px;
                cursor:pointer;
                transition:0.3s;
            }

            .social i:hover{
                color:#667eea;
                transform:scale(1.2);
            }

            .footer-bottom{
                text-align:center;
                border-top:1px solid rgba(255,255,255,0.1);
                padding-top:15px;
                font-size:13px;
                color:#aaa;
            }
            .header{
    position:fixed;
    top:0;
    width:100%;
    display:flex;
    justify-content:space-between;
    align-items:center;
    padding:15px 40px;
    background: linear-gradient(135deg, rgba(102,126,234,0.9), rgba(118,75,162,0.9));
    backdrop-filter: blur(10px);
    box-shadow:0 4px 20px rgba(0,0,0,0.15);
    z-index:1000;
}

.logo{
    font-weight:700;
    font-size:20px;
    color:white;
    display:flex;
    align-items:center;
    gap:8px;
}

.menu a{
    margin:0 12px;
    text-decoration:none;
    color:white;
    font-weight:500;
    font-size:14px;
    transition:0.3s;
}

.menu a i{
    margin-right:5px;
}

.menu a:hover{
    opacity:0.8;
    transform:translateY(-2px);
}

.actions .btn-login{
    padding:8px 16px;
    border:none;
    border-radius:20px;
    background:white;
    color:#667eea;
    font-weight:600;
    cursor:pointer;
    transition:0.3s;
}

.actions .btn-login:hover{
    transform:scale(1.05);
    box-shadow:0 5px 15px rgba(0,0,0,0.2);
}
        </style>
    </head>

    <body>

       <!-- HEADER -->
<div class="header">

    <div class="logo">
        <i class="fas fa-bullhorn"></i> CRM Marketing
    </div>

    <div class="menu">
        <a href="#"><i class="fas fa-home"></i> Trang chủ</a>
        <a href="#"><i class="fas fa-bullhorn"></i> Campaign</a>
        <a href="#"><i class="fas fa-phone"></i> Liên hệ</a>
    </div>

    <div class="actions">
    </div>

</div>

        <!-- HERO -->
        <c:if test="${not empty campaignList}">
            <div class="hero"
                 style="background-image:url('${pageContext.request.contextPath}/${campaignList[0].bannerUrl}')"
                 onclick="openForm('${campaignList[0].id}', '${campaignList[0].name}')">

                <div class="hero-content">
                    <h1>${campaignList[0].name}</h1>
                    <p>${campaignList[0].description}</p>
                </div>

            </div>
        </c:if>

        <!-- GRID -->
        <div class="container">
            <div class="section-title"> <h2>🔥 Chiến dịch nổi bật</h2> </div>
            <div class="grid">
                <c:forEach var="c" items="${campaignList}">
                    <div class="card" onclick="openForm('${c.id}', '${c.name}')">
                        <img src="${pageContext.request.contextPath}/${c.bannerUrl}">
                        <div class="card-body">
                            <h3>${c.name}</h3>
                            <p>${c.description}</p>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- MODAL -->
        <div id="leadModal" class="modal">
            <div class="modal-box">

                <span class="close" onclick="closeModal()">&times;</span>

                 <h3 id="campaignName"></h3>
                 <h2>Để lại thông tin bên dưới</h2>

                <form action="${pageContext.request.contextPath}/submitLead" method="post">
                    <input type="hidden" id="campaignId" name="campaignId">

                    <input type="text" name="name" placeholder="Họ tên" value="${sessionScope.form_name}" required>
                    <input type="text" name="phone" placeholder="SĐT" value="${sessionScope.form_phone}" required>
                    <input type="email" name="email" placeholder="Email" value="${sessionScope.form_email}">
                    <input type="text" name="address" placeholder="Địa chỉ" value="${sessionScope.form_address}">
                    <input type="text" name="productInterest" placeholder="Sản phẩm" value="${sessionScope.form_product}">

                    <button type="submit">Đăng ký</button>
                </form>

            </div>
        </div>

        <!-- FOOTER -->
        <!-- FOOTER -->
        <div class="footer">

            <div class="footer-container">

                <!-- COL 1 -->
                <div class="footer-col">
                    <h3><i class="fas fa-bullhorn"></i> CRM Marketing</h3>
                    <p>Giải pháp quản lý khách hàng & chiến dịch hiệu quả </p>
                </div>

                <!-- COL 2 -->
                <div class="footer-col">
                    <h4>Liên kết</h4>
                    <a href="#">Trang chủ</a>
                    <a href="#">Campaign</a>
                    <a href="#">Liên hệ</a>
                </div>

                <!-- COL 3 -->
                <div class="footer-col">
                    <h4>Liên hệ</h4>
                    <p><i class="fas fa-envelope"></i> crm@email.com</p>
                    <p><i class="fas fa-phone"></i> 0337730377</p>
                </div>

                <!-- COL 4 -->
                <div class="footer-col">
                    <h4>Mạng xã hội</h4>
                    <div class="social">
                        <i class="fab fa-facebook"></i>
                        <i class="fab fa-google"></i>
                        <i class="fab fa-linkedin"></i>
                    </div>
                </div>

            </div>

            <div class="footer-bottom">
                © 2026 CRM System. All rights reserved.
            </div>

        </div>

        <script>
            function openForm(id, name) {
                document.getElementById("leadModal").style.display = "block";
                document.getElementById("campaignId").value = id;
                document.getElementById("campaignName").innerText = name;
            }

            function closeModal() {
                document.getElementById("leadModal").style.display = "none";
            }

            window.onclick = function (e) {
                if (e.target.id === "leadModal") {
                    closeModal();
                }
            };

            // auto hide toast
            setTimeout(() => {
                const s = document.getElementById("toast-success");
                const e = document.getElementById("toast-error");
                if (s)
                    s.style.display = "none";
                if (e)
                    e.style.display = "none";
            }, 4000);
        </script>

        <!-- TOAST -->
        <c:if test="${not empty sessionScope.success}">
            <div id="toast-success" class="toast success">
                ${sessionScope.success}
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <c:if test="${not empty sessionScope.error}">
            <div id="toast-error" class="toast error">
                ${sessionScope.error}
            </div>
        </c:if>

        <!-- AUTO REOPEN MODAL -->
        <c:if test="${not empty sessionScope.error}">
            <script>
                window.onload = function () {
                    openForm(
                            '${sessionScope.form_campaignId}',
                            'Vui lòng sửa thông tin'
                            );
                }
            </script>
        </c:if>

        <!-- CLEAR FORM -->
        <c:remove var="form_name" scope="session"/>
        <c:remove var="form_phone" scope="session"/>
        <c:remove var="form_email" scope="session"/>
        <c:remove var="form_address" scope="session"/>
        <c:remove var="form_product" scope="session"/>
        <c:remove var="form_campaignId" scope="session"/>
        <c:remove var="error" scope="session"/>

    </body>
</html>