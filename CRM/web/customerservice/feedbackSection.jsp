<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
  feedbackSection.jsp — nhúng vào cuối ticketdetail.jsp:
      <%@ include file="feedbackSection.jsp" %>

  Servlet cần set:
    ticket           → SupportTicket
    existingFeedback → TicketFeedback | null
    canFeedback      → boolean
--%>

<style>
    .feedback-section { margin-top: 10px; }

    .feedback-card-header {
        background: linear-gradient(135deg, #f5a623, #f0932b);
        color: white;
        padding: 16px 22px;
        font-weight: 700; font-size: 15px;
        display: flex; align-items: center; gap: 10px;
    }

    .star-rating-group {
        display: flex; flex-direction: row-reverse;
        justify-content: flex-end; gap: 4px; margin: 10px 0 6px;
    }
    .star-rating-group input[type="radio"] { display: none; }
    .star-rating-group label {
        font-size: 38px; color: #ddd; cursor: pointer;
        transition: color 0.15s, transform 0.1s; user-select: none;
    }
    .star-rating-group label:hover,
    .star-rating-group label:hover ~ label,
    .star-rating-group input[type="radio"]:checked ~ label {
        color: #f5a623; transform: scale(1.12);
    }

    .feedback-textarea {
        width: 100%; box-sizing: border-box;
        border: 2px solid #e0e0e0; border-radius: 12px;
        padding: 11px 14px; font-family: 'Segoe UI', sans-serif;
        font-size: 14px; resize: vertical; min-height: 90px;
        outline: none; transition: border-color 0.3s;
    }
    .feedback-textarea:focus { border-color: #f5a623; }

    .btn-submit-feedback {
        background: linear-gradient(135deg, #f5a623, #f0932b);
        color: white; border: none; padding: 10px 26px;
        border-radius: 10px; font-size: 14px; font-weight: 700;
        cursor: pointer; margin-top: 14px;
        display: inline-flex; align-items: center; gap: 8px; transition: 0.3s;
    }
    .btn-submit-feedback:hover   { opacity: 0.85; transform: translateY(-2px); }
    .btn-submit-feedback:disabled { opacity: 0.5; cursor: not-allowed; transform: none; }

    .fb-stars-display { font-size: 28px; color: #f5a623; letter-spacing: 3px; }
    .fb-meta  { font-size: 13px; color: #999; margin: 4px 0 12px; }
    .fb-comment {
        background: #fffbf0; border-left: 4px solid #f5a623;
        border-radius: 0 10px 10px 0; padding: 13px 16px;
        font-size: 14px; color: #555; font-style: italic; line-height: 1.6;
    }
    .fb-no-comment { color: #bbb; font-style: italic; font-size: 13px; }

    .rating-badge-fb {
        display: inline-flex; align-items: center; gap: 5px;
        padding: 5px 14px; border-radius: 20px;
        font-weight: 700; font-size: 14px; color: white;
    }
    .rating-5 { background: #28a745; } .rating-4 { background: #5cb85c; }
    .rating-3 { background: #ffc107; color: #333; }
    .rating-2 { background: #fd7e14; } .rating-1 { background: #dc3545; }

    .alert-fb-ok  { background:#d4edda; color:#155724; border:1px solid #c3e6cb;
                    padding:11px 16px; border-radius:10px; margin-bottom:14px;
                    font-size:14px; display:flex; align-items:center; gap:8px; }
    .alert-fb-err { background:#f8d7da; color:#721c24; border:1px solid #f5c6cb;
                    padding:11px 16px; border-radius:10px; margin-bottom:14px;
                    font-size:14px; display:flex; align-items:center; gap:8px; }

    .fb-form-label { font-weight:600; font-size:14px; color:#444; display:block; margin-bottom:6px; }
    .rating-hint   { font-size:13px; color:#f5a623; font-weight:600; min-height:20px; margin-bottom:14px; }
    .char-count    { font-size:11px; color:#bbb; text-align:right; margin-top:3px; }
</style>

<div class="feedback-section">
    <div class="section-title">⭐ Đánh Giá Hỗ Trợ</div>

    <div class="card">
        <div class="feedback-card-header">
            <i class="fas fa-star"></i> Phản Hồi Của Khách Hàng
        </div>
        <div class="card-inner" style="padding:22px">

            <%-- Flash messages --%>
            <c:if test="${not empty sessionScope.feedbackSuccess}">
                <div class="alert-fb-ok">
                    <i class="fas fa-check-circle"></i> ${sessionScope.feedbackSuccess}
                </div>
                <c:remove var="feedbackSuccess" scope="session"/>
            </c:if>
            <c:if test="${not empty sessionScope.feedbackError}">
                <div class="alert-fb-err">
                    <i class="fas fa-exclamation-circle"></i> ${sessionScope.feedbackError}
                </div>
                <c:remove var="feedbackError" scope="session"/>
            </c:if>

            <%-- CASE 1: Customer có thể gửi feedback --%>
            <c:if test="${canFeedback}">
                <p style="color:#666;font-size:14px;margin:0 0 18px">
                    Ticket của bạn đã được giải quyết.
                    Hãy cho chúng tôi biết mức độ hài lòng của bạn!
                </p>

                <form action="${pageContext.request.contextPath}/customerservice/submitfeedback"
                      method="post">
                    <input type="hidden" name="ticketId" value="${ticket.id}">

                    <label class="fb-form-label">
                        <i class="fas fa-star" style="color:#f5a623"></i>
                        Mức độ hài lòng <span style="color:#dc3545">*</span>
                    </label>
                    <div class="star-rating-group">
                        <input type="radio" name="rating" id="s5" value="5" required>
                        <label for="s5" title="5 sao">★</label>
                        <input type="radio" name="rating" id="s4" value="4">
                        <label for="s4" title="4 sao">★</label>
                        <input type="radio" name="rating" id="s3" value="3">
                        <label for="s3" title="3 sao">★</label>
                        <input type="radio" name="rating" id="s2" value="2">
                        <label for="s2" title="2 sao">★</label>
                        <input type="radio" name="rating" id="s1" value="1">
                        <label for="s1" title="1 sao">★</label>
                    </div>
                    <div class="rating-hint" id="ratingHint"></div>

                    <label class="fb-form-label" for="fbComment">
                        <i class="fas fa-comment"></i> Nhận xét thêm (không bắt buộc)
                    </label>
                    <textarea id="fbComment" name="comments" class="feedback-textarea"
                              maxlength="1000"
                              placeholder="Chia sẻ trải nghiệm của bạn..."></textarea>
                    <div class="char-count"><span id="cc">0</span>/1000</div>

                    <button type="submit" class="btn-submit-feedback" id="fbSubmitBtn" disabled>
                        <i class="fas fa-paper-plane"></i> Gửi Đánh Giá
                    </button>
                    <p style="font-size:12px;color:#bbb;margin-top:8px">
                        <i class="fas fa-info-circle"></i>
                        Bạn chỉ có thể đánh giá 1 lần cho mỗi ticket.
                    </p>
                </form>

                <script>
                    (function() {
                        var hints = {1:"😞 Rất không hài lòng", 2:"😕 Chưa hài lòng",
                                     3:"😐 Bình thường", 4:"😊 Hài lòng", 5:"😄 Rất hài lòng!"};
                        document.querySelectorAll('input[name="rating"]').forEach(function(r) {
                            r.addEventListener('change', function() {
                                document.getElementById('ratingHint').textContent = hints[this.value];
                                document.getElementById('fbSubmitBtn').disabled = false;
                            });
                        });
                        document.getElementById('fbComment').addEventListener('input', function() {
                            document.getElementById('cc').textContent = this.value.length;
                        });
                    })();
                </script>
            </c:if>

            <%-- CASE 2: Đã có feedback — hiện kết quả --%>
            <c:if test="${not empty existingFeedback}">
                <div>
                    <span class="rating-badge-fb rating-${existingFeedback.rating}">
                        ${existingFeedback.rating} / 5
                        <i class="fas fa-star" style="font-size:11px"></i>
                    </span>
                    <span class="fb-stars-display" style="margin-left:12px">
                        ${existingFeedback.starDisplay}
                    </span>
                    <p class="fb-meta">
                        ${existingFeedback.ratingLabel} &nbsp;&middot;&nbsp;
                        Đánh giá bởi <strong>${existingFeedback.customerName}</strong>
                        lúc ${existingFeedback.createdAt}
                    </p>
                    <c:choose>
                        <c:when test="${not empty existingFeedback.comments}">
                            <div class="fb-comment">
                                <i class="fas fa-quote-left" style="color:#f5a623;margin-right:6px"></i>
                                ${existingFeedback.comments}
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="fb-no-comment">
                                <i class="fas fa-minus-circle"></i>
                                Khách hàng không để lại nhận xét.
                            </p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <%-- CASE 3: Chưa Resolved / không có quyền --%>
            <c:if test="${not canFeedback and empty existingFeedback}">
                <div style="text-align:center;padding:20px;color:#bbb">
                    <i class="fas fa-clock" style="font-size:30px;display:block;margin-bottom:10px"></i>
                    <c:choose>
                        <c:when test="${userSession.isStaff()}">
                            Chưa có đánh giá từ khách hàng cho ticket này.
                        </c:when>
                        <c:otherwise>
                            Bạn có thể đánh giá sau khi ticket được giải quyết.
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

        </div>
    </div>
</div>
