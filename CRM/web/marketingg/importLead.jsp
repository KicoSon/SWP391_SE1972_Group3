<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">

    <head>

        <meta charset="UTF-8">
        <title>Import Lead Excel - CRM Admin</title>

        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>

            body{
                font-family:"Segoe UI",sans-serif;
                background:linear-gradient(135deg,#3a7bd5,#3a6073);
                margin:0;
                color:#333;
            }

            .main-content{
                margin-left:270px;
                padding:40px;
                min-height:100vh;
            }

            .form-card{
                background:rgba(255,255,255,0.95);
                border-radius:20px;
                box-shadow:0 10px 25px rgba(0,0,0,0.1);
                padding:30px 40px;
                max-width:800px;
                margin:0 auto;
            }

            h2{
                font-weight:700;
                font-size:26px;
                margin-bottom:25px;
            }

            .form-label{
                font-weight:600;
            }

            .form-control,.form-select{
                border-radius:10px;
                padding:10px;
            }

            .btn-outline-primary{
                border:2px solid #667eea;
                color:#667eea;
                font-weight:600;
                border-radius:10px;
                padding:10px 18px;
            }

            .btn-outline-primary:hover{
                background:#667eea;
                color:white;
            }

            .btn-submit{
                background:linear-gradient(135deg,#667eea,#764ba2);
                color:white;
                font-weight:600;
                border:none;
                padding:12px 30px;
                border-radius:10px;
            }

            .btn-submit:hover{
                opacity:0.9;
            }

            .file-upload{
                border:2px dashed #ccc;
                padding:30px;
                text-align:center;
                border-radius:10px;
                cursor:pointer;
            }

            .file-upload:hover{
                border-color:#667eea;
            }

        </style>

    </head>

    <body>

        <%@ include file="sidebar.jsp" %>

        <div class="main-content">

            <div class="form-card">

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">
                        ${error}
                    </div>
                </c:if>

                <div class="d-flex justify-content-between align-items-center mb-4">

                    <h2>
                        <i class="fas fa-file-excel"></i>
                        Import Lead bằng Excel
                    </h2>

                    <a href="${pageContext.request.contextPath}/marketing/leadmanagement"
                       class="btn btn-outline-primary">

                        <i class="fas fa-arrow-left"></i>
                        Quay lại

                    </a>

                </div>

                <form action="${pageContext.request.contextPath}/marketingg/importLead"
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

                            <label class="form-label">Nguồn Lead</label>

                            <input type="text"
                                   class="form-control"
                                   value="Excel Import"
                                   readonly>

                            <input type="hidden"
                                   name="source"
                                   value="Excel">

                        </div>

                    </div>

                    <!-- CAMPAIGN -->

                    <div class="mb-3">

                        <label class="form-label">Campaign</label>

                        <select class="form-select"
                                name="campaignId"
                                required>

                            <option value="">-- Chọn Campaign --</option>

                            <c:forEach var="c" items="${campaignList}">

                                <option value="${c.id}">
                                    ${c.name}
                                </option>

                            </c:forEach>

                        </select>

                    </div>

                    <!-- FILE UPLOAD -->

                    <div class="mb-3">

                        <label class="form-label">Upload file Excel</label>

                        <div class="file-upload">

                            <i class="fas fa-file-excel fa-2x" style="color:#28a745"></i>

                            <p style="margin-top:10px">
                                Chọn file Excel (.xlsx)
                            </p>

                            <input type="file"
                                   name="excelFile"
                                   accept=".xlsx"
                                   class="form-control"
                                   required>

                        </div>

                    </div>

                    <!-- TEMPLATE -->

                    <div class="mb-3">

                        <a href="${pageContext.request.contextPath}/templates/lead_import_template.xlsx"
                           class="btn btn-outline-primary">

                            <i class="fas fa-download"></i>
                            Download Excel Template

                        </a>

                    </div>

                    <!-- BUTTON -->

                    <div class="text-end">

                        <button type="submit"
                                class="btn-submit">

                            <i class="fas fa-upload"></i>
                            Import Lead

                        </button>

                    </div>

                </form>

            </div>

        </div>

    </body>
</html>