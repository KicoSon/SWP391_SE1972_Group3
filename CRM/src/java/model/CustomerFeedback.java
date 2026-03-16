package model;

/**
 * Model ánh xạ bảng customer_feedback
 *
 * Schema:
 *   customer_feedback(id, customer_id, rating, comments, created_at)
 *
 * Join field (không lưu trong bảng):
 *   customerName  ← customers.full_name
 */
public class CustomerFeedback {

    // ── Cột trong bảng customer_feedback ──────────────────────
    private int    id;
    private int    customerId;
    private int    rating;        // 1 – 5
    private String comments;
    private String createdAt;

    // ── Join field ────────────────────────────────────────────
    private String customerName;  

    // ── Constructors ─────────────────────────────────────────
    public CustomerFeedback() {}

    // ── Getters / Setters ─────────────────────────────────────
    public int    getId()                        { return id; }
    public void   setId(int id)                  { this.id = id; }

    public int    getCustomerId()                { return customerId; }
    public void   setCustomerId(int customerId)  { this.customerId = customerId; }

    public int    getRating()                    { return rating; }
    public void   setRating(int rating)          { this.rating = rating; }

    public String getComments()                  { return comments; }
    public void   setComments(String comments)   { this.comments = comments; }

    public String getCreatedAt()                 { return createdAt; }
    public void   setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCustomerName()                    { return customerName; }
    public void   setCustomerName(String customerName) { this.customerName = customerName; }

    // ── Helpers dùng trong JSP / Excel ───────────────────────

    /** "★★★☆☆" — filled + empty stars */
    public String getStarDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i <= rating ? "★" : "☆");
        }
        return sb.toString();
    }

    /** CSS class cho badge màu theo rating */
    public String getRatingClass() {
        switch (rating) {
            case 5:  return "rating-5";
            case 4:  return "rating-4";
            case 3:  return "rating-3";
            case 2:  return "rating-2";
            default: return "rating-1";
        }
    }

    /** Label mô tả mức độ hài lòng */
    public String getRatingLabel() {
        switch (rating) {
            case 5:  return "Rất hài lòng";
            case 4:  return "Hài lòng";
            case 3:  return "Bình thường";
            case 2:  return "Chưa hài lòng";
            default: return "Rất không hài lòng";
        }
    }
}