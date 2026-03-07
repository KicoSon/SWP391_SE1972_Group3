<%@ page contentType="text/html;charset=UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
        <%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <title>${opportunity.title} – Chi tiết Opportunity</title>
                <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
                <style>
                    body {
                        margin: 0;
                        font-family: "Segoe UI";
                        background: linear-gradient(135deg, #3a7bd5, #3a6073);
                        color: #333;
                    }

                    .main-content {
                        margin-left: 270px;
                        padding: 30px;
                        min-height: 100vh;
                    }

                    .card {
                        background: rgba(255, 255, 255, 0.95);
                        border-radius: 18px;
                        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
                        padding: 25px;
                        margin-bottom: 20px;
                    }

                    .btn {
                        border: none;
                        padding: 9px 15px;
                        border-radius: 8px;
                        font-size: 13px;
                        font-weight: 600;
                        cursor: pointer;
                        text-decoration: none;
                        display: inline-flex;
                        align-items: center;
                        gap: 6px;
                    }

                    .btn-primary {
                        background: linear-gradient(135deg, #667eea, #764ba2);
                        color: white;
                    }

                    .btn-success {
                        background: linear-gradient(135deg, #56ab2f, #a8e063);
                        color: white;
                    }

                    .btn-danger {
                        background: linear-gradient(135deg, #f093fb, #f5576c);
                        color: white;
                    }

                    .btn-warning {
                        background: linear-gradient(135deg, #f7971e, #ffd200);
                        color: #333;
                    }

                    .btn-info {
                        background: linear-gradient(135deg, #0dcaf0, #0d6efd);
                        color: white;
                    }

                    .header-row {
                        display: flex;
                        justify-content: space-between;
                        align-items: flex-start;
                        flex-wrap: wrap;
                        gap: 15px;
                        margin-bottom: 25px;
                    }

                    .info-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                        gap: 16px;
                    }

                    .info-item label {
                        font-size: 11px;
                        color: #888;
                        text-transform: uppercase;
                        font-weight: 700;
                        margin-bottom: 4px;
                        display: block;
                    }

                    .info-item span {
                        font-size: 15px;
                        font-weight: 600;
                        color: #333;
                    }

                    .stage-timeline {
                        display: flex;
                        align-items: center;
                        flex-wrap: wrap;
                        gap: 0;
                        margin: 20px 0;
                    }

                    .stage-step {
                        flex: 1;
                        min-width: 80px;
                        text-align: center;
                        padding: 8px 5px;
                        font-size: 11px;
                        font-weight: 700;
                        color: #fff;
                        position: relative;
                        cursor: default;
                    }

                    .stage-step.active {
                        transform: scale(1.05);
                        z-index: 1;
                        border-radius: 6px;
                    }

                    .stage-Qualification {
                        background: #6c757d;
                    }

                    .stage-Need.Analysis {
                        background: #0dcaf0;
                    }

                    .stage-Need\.Analysis {
                        background: #0dcaf0;
                    }

                    .stage-Product.Proposal {
                        background: #0d6efd;
                    }

                    .stage-Quotation {
                        background: #ffc107;
                        color: #000;
                    }

                    .stage-Negotiation {
                        background: #fd7e14;
                    }

                    .stage-Closed.Won {
                        background: #198754;
                    }

                    .stage-Closed.Lost {
                        background: #dc3545;
                    }

                    .section-title {
                        font-size: 16px;
                        font-weight: 700;
                        color: #333;
                        margin-bottom: 15px;
                        display: flex;
                        align-items: center;
                        gap: 8px;
                    }

                    table {
                        width: 100%;
                        border-collapse: collapse;
                    }

                    th {
                        background: #f3f4f7;
                        color: #555;
                        padding: 10px 14px;
                        font-size: 12px;
                        text-align: left;
                        font-weight: 700;
                    }

                    td {
                        padding: 10px 14px;
                        border-bottom: 1px solid #f0f0f0;
                        font-size: 14px;
                    }

                    .badge {
                        padding: 3px 9px;
                        border-radius: 12px;
                        font-size: 11px;
                        font-weight: 700;
                    }

                    .badge-draft {
                        background: #e3f2fd;
                        color: #1565c0;
                    }

                    .badge-approved {
                        background: #e8f5e9;
                        color: #2e7d32;
                    }

                    .badge-rejected {
                        background: #fce4ec;
                        color: #c62828;
                    }

                    .badge-sent {
                        background: #f3e5f5;
                        color: #6a1b9a;
                    }

                    .badge-pending {
                        background: #fff8e1;
                        color: #f57f17;
                    }

                    .modal-overlay {
                        display: none;
                        position: fixed;
                        inset: 0;
                        background: rgba(0, 0, 0, 0.5);
                        z-index: 2000;
                        align-items: center;
                        justify-content: center;
                    }

                    .modal-box {
                        background: white;
                        border-radius: 16px;
                        padding: 30px;
                        width: 400px;
                        max-width: 90vw;
                    }

                    .modal-box h4 {
                        margin: 0 0 20px;
                        font-size: 18px;
                    }

                    .form-group {
                        margin-bottom: 15px;
                    }

                    .form-group label {
                        font-size: 13px;
                        font-weight: 600;
                        color: #555;
                        margin-bottom: 5px;
                        display: block;
                    }

                    .form-group select,
                    .form-group textarea {
                        width: 100%;
                        padding: 9px;
                        border: 1px solid #ddd;
                        border-radius: 8px;
                        font-size: 14px;
                    }

                    .activity-item {
                        display: flex;
                        gap: 12px;
                        padding: 10px 0;
                        border-bottom: 1px solid #f0f0f0;
                    }

                    .activity-icon {
                        width: 36px;
                        height: 36px;
                        border-radius: 50%;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        font-size: 14px;
                        color: white;
                        flex-shrink: 0;
                    }

                    .type-Call {
                        background: #0dcaf0;
                    }

                    .type-Meeting {
                        background: #667eea;
                    }

                    .type-Task {
                        background: #fd7e14;
                    }

                    .type-Note {
                        background: #6c757d;
                    }

                    .type-Email {
                        background: #56ab2f;
                    }
                </style>
            </head>

            <body>
                <jsp:include page="/sales/sidebar.jsp" />
                <div class="main-content">

                    <div class="header-row">
                        <div>
                            <a href="${pageContext.request.contextPath}/sales/opportunities"
                                style="color:rgba(255,255,255,0.8);text-decoration:none;font-size:13px;">
                                <i class="fas fa-arrow-left"></i> Quay lại danh sách
                            </a>
                            <h2 style="color:white;margin:6px 0 0;font-size:22px;">${opportunity.title}</h2>
                        </div>
                        <div style="display:flex;gap:8px;flex-wrap:wrap;">
                            <a href="${pageContext.request.contextPath}/sales/opportunity-update?id=${opportunity.id}"
                                class="btn btn-warning"><i class="fas fa-edit"></i> Sửa</a>
                            <button class="btn btn-success"
                                onclick="document.getElementById('stageModal').style.display='flex'"><i
                                    class="fas fa-exchange-alt"></i> Đổi stage</button>
                            <button class="btn btn-danger"
                                onclick="document.getElementById('closeModal').style.display='flex'"><i
                                    class="fas fa-flag-checkered"></i> Đóng</button>
                            <c:if test="${isManager}">
                                <button class="btn btn-info"
                                    onclick="document.getElementById('assignModal').style.display='flex'"><i
                                        class="fas fa-user-tag"></i> Assign</button>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/sales/quotation-create?opportunityId=${opportunity.id}"
                                class="btn btn-primary"><i class="fas fa-file-invoice-dollar"></i> Tạo báo giá</a>
                        </div>
                    </div>

                    <!-- Stage Timeline -->
                    <div class="card" style="padding:15px 20px;">
                        <div class="stage-timeline">
                            <div
                                class="stage-step stage-Qualification    ${opportunity.stage == 'Qualification'    ? 'active' : ''}">
                                Qualification</div>
                            <div
                                class="stage-step stage-Need.Analysis    ${opportunity.stage == 'Need Analysis'    ? 'active' : ''}">
                                Need Analysis</div>
                            <div
                                class="stage-step stage-Product.Proposal ${opportunity.stage == 'Product Proposal' ? 'active' : ''}">
                                Product Proposal</div>
                            <div
                                class="stage-step stage-Quotation        ${opportunity.stage == 'Quotation'        ? 'active' : ''}">
                                Quotation</div>
                            <div
                                class="stage-step stage-Negotiation      ${opportunity.stage == 'Negotiation'      ? 'active' : ''}">
                                Negotiation</div>
                            <div
                                class="stage-step stage-Closed.Won       ${opportunity.stage == 'Closed Won'       ? 'active' : ''}">
                                Closed Won</div>
                            <div
                                class="stage-step stage-Closed.Lost      ${opportunity.stage == 'Closed Lost'      ? 'active' : ''}">
                                Closed Lost</div>
                        </div>
                    </div>

                    <!-- Info Grid -->
                    <div class="card">
                        <div class="section-title"><i class="fas fa-info-circle" style="color:#667eea"></i> Thông tin
                            chi tiết</div>
                        <div class="info-grid">
                            <div class="info-item"><label>Khách hàng</label><span>${not empty opportunity.customerName ?
                                    opportunity.customerName : 'Chưa liên kết'}</span></div>
                            <div class="info-item"><label>Sales phụ
                                    trách</label><span>${opportunity.assignedSalesName}</span></div>
                            <div class="info-item"><label>Stage</label><span>${opportunity.stage}</span></div>
                            <div class="info-item"><label>Status</label><span>${opportunity.status}</span></div>
                            <div class="info-item"><label>Giá trị dự kiến</label><span class="val-currency">
                                    <fmt:formatNumber value="${opportunity.expectedValue}" type="number"
                                        groupingUsed="true" /> đ
                                </span></div>
                            <div class="info-item"><label>Xác suất
                                    đóng</label><span>${opportunity.closeProbability}%</span></div>
                            <div class="info-item"><label>Ngày dự kiến đóng</label><span>
                                    <fmt:formatDate value="${opportunity.expectedCloseDate}" pattern="dd/MM/yyyy" />
                                </span></div>
                            <div class="info-item"><label>Nguồn</label><span>${opportunity.source}</span></div>
                            <c:if test="${not empty opportunity.campaignName}">
                                <div class="info-item"><label>Chiến dịch</label><span>${opportunity.campaignName}</span>
                                </div>
                            </c:if>
                            <c:if test="${not empty opportunity.lostReason}">
                                <div class="info-item"><label>Lý do thất bại</label><span
                                        style="color:#dc3545">${opportunity.lostReason}</span></div>
                            </c:if>
                        </div>
                        <c:if test="${not empty opportunity.notes}">
                            <div style="margin-top:16px;padding:14px;background:#f8f9ff;border-radius:10px;">
                                <label style="font-size:11px;color:#888;font-weight:700;text-transform:uppercase;">Ghi
                                    chú</label>
                                <p style="margin:6px 0 0">${opportunity.notes}</p>
                            </div>
                        </c:if>
                    </div>

                    <!-- Quotations -->
                    <div class="card">
                        <div class="section-title"><i class="fas fa-file-invoice-dollar" style="color:#667eea"></i> Báo
                            giá liên quan</div>
                        <c:choose>
                            <c:when test="${empty quotations}">
                                <p style="color:#aaa;text-align:center;">Chưa có báo giá nào. <a
                                        href="${pageContext.request.contextPath}/sales/quotation-create?opportunityId=${opportunity.id}">Tạo
                                        báo giá đầu tiên</a></p>
                            </c:when>
                            <c:otherwise>
                                <table>
                                    <thead>
                                        <tr>
                                            <th>Mã báo giá</th>
                                            <th>Phiên bản</th>
                                            <th>Status</th>
                                            <th>Tổng tiền</th>
                                            <th>Hết hạn</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="q" items="${quotations}">
                                            <tr>
                                                <td><strong>${q.quotationCode}</strong></td>
                                                <td>v${q.version}</td>
                                                <td><span class="badge badge-${q.status}">${q.status}</span></td>

                                                <td>
                                                    <fmt:formatNumber value="${q.totalAmount}" type="number"
                                                        groupingUsed="true" /> đ
                                                </td>
                                                <td>
                                                    <fmt:formatDate value="${q.validUntil}" pattern="dd/MM/yyyy" />
                                                </td>
                                                <td><a href="${pageContext.request.contextPath}/sales/quotation-detail?id=${q.id}"
                                                        class="btn btn-info"><i class="fas fa-eye"></i></a></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Activities -->
                    <div class="card">
                        <div class="section-title" style="justify-content:space-between;">
                            <span><i class="fas fa-tasks" style="color:#667eea"></i> Hoạt động</span>
                            <a href="${pageContext.request.contextPath}/sales/activity-create?opportunityId=${opportunity.id}"
                                class="btn btn-primary" style="font-size:12px;padding:7px 12px"><i
                                    class="fas fa-plus"></i> Thêm</a>
                        </div>
                        <c:choose>
                            <c:when test="${empty activities}">
                                <p style="color:#aaa;text-align:center;">Chưa có hoạt động nào.</p>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="act" items="${activities}">
                                    <div class="activity-item">
                                        <div class="activity-icon type-${act.type}">
                                            <c:choose>
                                                <c:when test="${act.type == 'Call'}"><i class="fas fa-phone"></i>
                                                </c:when>
                                                <c:when test="${act.type == 'Meeting'}"><i class="fas fa-users"></i>
                                                </c:when>
                                                <c:when test="${act.type == 'Task'}"><i class="fas fa-check-square"></i>
                                                </c:when>
                                                <c:when test="${act.type == 'Email'}"><i class="fas fa-envelope"></i>
                                                </c:when>
                                                <c:otherwise><i class="fas fa-sticky-note"></i></c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <strong>${act.title}</strong> <span
                                                style="background:#f0f0f0;padding:2px 7px;border-radius:10px;font-size:11px;">${act.type}</span>
                                            <p style="margin:4px 0 0;font-size:13px;color:#666">${act.description}</p>
                                            <small style="color:#aaa">
                                                <fmt:formatDate value="${act.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                            </small>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Stage Modal -->
                <div id="stageModal" class="modal-overlay" onclick="this.style.display='none'">
                    <div class="modal-box" onclick="event.stopPropagation()">
                        <h4><i class="fas fa-exchange-alt"></i> Đổi Stage</h4>
                        <form method="post" action="${pageContext.request.contextPath}/sales/opportunity-stage">
                            <input type="hidden" name="id" value="${opportunity.id}">
                            <div class="form-group">
                                <label>Stage mới</label>
                                <select name="newStage">
                                    <option value="Qualification" ${opportunity.stage=='Qualification' ? 'selected' : ''
                                        }>Qualification</option>
                                    <option value="Need Analysis" ${opportunity.stage=='Need Analysis' ? 'selected' : ''
                                        }>Need Analysis</option>
                                    <option value="Product Proposal" ${opportunity.stage=='Product Proposal'
                                        ? 'selected' : '' }>Product Proposal</option>
                                    <option value="Quotation" ${opportunity.stage=='Quotation' ? 'selected' : '' }>
                                        Quotation</option>
                                    <option value="Negotiation" ${opportunity.stage=='Negotiation' ? 'selected' : '' }>
                                        Negotiation</option>
                                    <option value="Closed Won" ${opportunity.stage=='Closed Won' ? 'selected' : '' }>
                                        Closed Won</option>
                                    <option value="Closed Lost" ${opportunity.stage=='Closed Lost' ? 'selected' : '' }>
                                        Closed Lost</option>
                                </select>
                            </div>
                            <div style="display:flex;gap:10px;justify-content:flex-end;">
                                <button type="button" class="btn btn-warning"
                                    onclick="document.getElementById('stageModal').style.display='none'">Hủy</button>
                                <button type="submit" class="btn btn-primary">Cập nhật</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Close Modal -->
                <div id="closeModal" class="modal-overlay" onclick="this.style.display='none'">
                    <div class="modal-box" onclick="event.stopPropagation()">
                        <h4><i class="fas fa-flag-checkered"></i> Đóng Opportunity</h4>
                        <form method="post" action="${pageContext.request.contextPath}/sales/opportunity-close">
                            <input type="hidden" name="id" value="${opportunity.id}">
                            <div class="form-group">
                                <label>Kết quả</label>
                                <select name="status"
                                    onchange="document.getElementById('lostReasonGroup').style.display=this.value=='Lost'?'block':'none'">
                                    <option value="Won">✅ Won – Thành công</option>
                                    <option value="Lost">❌ Lost – Thất bại</option>
                                </select>
                            </div>
                            <div id="lostReasonGroup" class="form-group" style="display:none">
                                <label>Lý do thất bại</label>
                                <textarea name="lostReason" rows="3" placeholder="Nhập lý do..."></textarea>
                            </div>
                            <div style="display:flex;gap:10px;justify-content:flex-end;">
                                <button type="button" class="btn btn-warning"
                                    onclick="document.getElementById('closeModal').style.display='none'">Hủy</button>
                                <button type="submit" class="btn btn-danger">Đóng Opportunity</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Assign Modal -->
                <c:if test="${isManager}">
                    <div id="assignModal" class="modal-overlay" onclick="this.style.display='none'">
                        <div class="modal-box" onclick="event.stopPropagation()">
                            <h4><i class="fas fa-user-tag"></i> Assign Sales</h4>
                            <form method="post" action="${pageContext.request.contextPath}/sales/opportunity-assign">
                                <input type="hidden" name="id" value="${opportunity.id}">
                                <div class="form-group">
                                    <label>Chọn Sales Staff</label>
                                    <select name="salesId">
                                        <c:forEach var="s" items="${staffList}">
                                            <option value="${s.id}" ${opportunity.assignedSalesId==s.id ? 'selected'
                                                : '' }>${s.fullName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div style="display:flex;gap:10px;justify-content:flex-end;">
                                    <button type="button" class="btn btn-warning"
                                        onclick="document.getElementById('assignModal').style.display='none'">Hủy</button>
                                    <button type="submit" class="btn btn-primary">Assign</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </c:if>
            </body>

            </html>