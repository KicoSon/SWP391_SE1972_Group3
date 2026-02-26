<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Create Support Ticket</title>

        <style>
            body {
                margin: 0;
                font-family: 'Segoe UI', sans-serif;
                background: linear-gradient(135deg, #667eea, #764ba2);
            }

            .main-content {
                margin-left: 260px;
                min-height: 100vh;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 40px;
                box-sizing: border-box;
            }

            .container-box {
                width: 750px;
            }

            .card {
                background: white;
                padding: 30px;
                border-radius: 15px;
                box-shadow: 0 15px 35px rgba(0,0,0,0.15);
            }

            .header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                color: white;
                margin-bottom: 20px;
            }

            .row {
                display: flex;
                gap: 20px;
            }

            .col-6 {
                flex: 1;
            }

            label {
                font-weight: 600;
                display: block;
                margin-bottom: 6px;
            }

            input, select, textarea {
                width: 100%;
                padding: 10px;
                border-radius: 8px;
                border: 1px solid #ccc;
                margin-bottom: 15px;
                font-size: 14px;
            }

            textarea {
                resize: none;
            }

            .btn {
                padding: 8px 18px;
                border-radius: 8px;
                border: none;
                cursor: pointer;
                font-weight: 600;
                transition: 0.3s;
            }

            .btn-reset {
                background: #f1f1f1;
            }

            .btn-reset:hover {
                background: #ddd;
            }

            .btn-submit {
                background: linear-gradient(135deg, #667eea, #764ba2);
                color: white;
            }

            .btn-submit:hover {
                opacity: 0.9;
                transform: translateY(-2px);
            }

            .text-end {
                text-align: right;
            }

            .back-btn {
                background: white;
                color: #333;
                padding: 6px 14px;
                border-radius: 8px;
                text-decoration: none;
                font-weight: 600;
            }

            .back-btn:hover {
                background: #eee;
            }
        </style>
    </head>

    <body>
        <%@ include file="sidebar.jsp" %>
        <div class="main-content">
            <div class="container-box">

                <!-- Header -->
                <div class="header">
                    <h2>Create Support Ticket</h2>
                    <a href="${pageContext.request.contextPath}/customerservice/ticketlist"
                       class="back-btn">
                        ← Back
                    </a>
                </div>

                <!-- Card -->
                <div class="card">

                    <form action="${pageContext.request.contextPath}/customerservice/addticket"
                          method="post">

                        <div class="row">

                            <div class="col-6">
                                <label>Customer ID</label>
                                <input type="number"
                                       name="customerId"
                                       placeholder="Enter customer ID"
                                       required>
                            </div>

                            <div class="col-6">
                                <label>Priority</label>
                                <select name="priority">
                                    <option value="Low">Low</option>
                                    <option value="Medium">Medium</option>
                                    <option value="High">High</option>
                                    <option value="Urgent">Urgent</option>
                                </select>
                            </div>

                        </div>

                        <label>Title</label>
                        <input type="text"
                               name="title"
                               placeholder="Enter ticket title"
                               required>

                        <label>Description</label>
                        <textarea name="description"
                                  rows="4"
                                  placeholder="Describe the issue..."
                                  required></textarea>

                        <div class="text-end">
                            <button type="reset" class="btn btn-reset">
                                Reset
                            </button>

                            <button type="submit" class="btn btn-submit">
                                Create Ticket
                            </button>
                        </div>

                    </form>

                </div>

            </div>
        </div>


    </body>
</html>