package model;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

public class SupportTicket {

    private int       id;
    private int       customerId;
    private Integer   orderId;
    private String    title;
    private String    description;
    private String    priority;    // Low, Medium, High, Urgent
    private String    status;      // Open, In Progress, Resolved
    private int       assignedTo;
    private Timestamp createdAt;
    private Timestamp updateAt;
    private String    customerName;
    private String    assignedName;

    public SupportTicket() {}

    // ── SLA threshold theo priority (giờ) ────────────────────
    // Urgent: 4h | High: 24h | Medium: 72h | Low: 168h (7 ngày)
    private static final int SLA_URGENT = 4;
    private static final int SLA_HIGH   = 24;
    private static final int SLA_MEDIUM = 72;
    private static final int SLA_LOW    = 168;

    /**
     * Trả về ngưỡng SLA (giờ) theo priority của ticket này.
     */
    public int getSlaHoursLimit() {
        if (priority == null) return SLA_LOW;
        switch (priority.toLowerCase()) {
            case "urgent": return SLA_URGENT;
            case "high":   return SLA_HIGH;
            case "medium": return SLA_MEDIUM;
            default:       return SLA_LOW;
        }
    }

    /**
     * Ticket có đang quá hạn SLA không?
     *
     * Điều kiện: status còn Open hoặc In Progress
     *            VÀ thời gian từ lúc tạo đến nay vượt ngưỡng SLA
     *
     * Ticket Resolved không tính overdue — đã xong rồi.
     */
    public boolean isOverdue() {
        if (createdAt == null) return false;
        if ("Resolved".equals(status)) return false;

        long hoursElapsed = Duration.between(
                createdAt.toLocalDateTime(),
                LocalDateTime.now()
        ).toHours();

        return hoursElapsed > getSlaHoursLimit();
    }

    /**
     * Số giờ đã trôi qua kể từ khi tạo ticket.
     */
    public long getHoursElapsed() {
        if (createdAt == null) return 0;
        return Duration.between(
                createdAt.toLocalDateTime(),
                LocalDateTime.now()
        ).toHours();
    }

    /**
     * Text hiển thị thời gian còn lại hoặc đã quá hạn.
     * Dùng trong JSP tooltip.
     */
    public String getSlaStatusText() {
        if ("Resolved".equals(status)) return "Đã giải quyết";
        long elapsed = getHoursElapsed();
        long limit   = getSlaHoursLimit();
        if (elapsed > limit) {
            return "Quá hạn " + (elapsed - limit) + " giờ";
        }
        long remaining = limit - elapsed;
        if (remaining < 1) return "Sắp quá hạn";
        return "Còn " + remaining + " giờ";
    }

    /**
     * CSS class cho SLA badge.
     */
    public String getSlaBadgeClass() {
        if ("Resolved".equals(status)) return "sla-resolved";
        long elapsed  = getHoursElapsed();
        long limit    = getSlaHoursLimit();
        long pct      = limit > 0 ? elapsed * 100 / limit : 100;
        if (pct >= 100) return "sla-overdue";
        if (pct >= 75)  return "sla-warning";
        return "sla-ok";
    }

    // ── Getters / Setters ─────────────────────────────────────

    public int getId()                      { return id; }
    public void setId(int id)               { this.id = id; }

    public int getCustomerId()              { return customerId; }
    public void setCustomerId(int v)        { this.customerId = v; }

    public Integer getOrderId()             { return orderId; }
    public void setOrderId(Integer v)       { this.orderId = v; }

    public String getTitle()                { return title; }
    public void setTitle(String v)          { this.title = v; }

    public String getDescription()          { return description; }
    public void setDescription(String v)    { this.description = v; }

    public String getPriority()             { return priority; }
    public void setPriority(String v)       { this.priority = v; }

    public String getStatus()               { return status; }
    public void setStatus(String v)         { this.status = v; }

    public int getAssignedTo()              { return assignedTo; }
    public void setAssignedTo(int v)        { this.assignedTo = v; }

    public Timestamp getCreatedAt()         { return createdAt; }
    public void setCreatedAt(Timestamp v)   { this.createdAt = v; }

    public Timestamp getUpdateAt()          { return updateAt; }
    public void setUpdateAt(Timestamp v)    { this.updateAt = v; }

    public String getCustomerName()         { return customerName; }
    public void setCustomerName(String v)   { this.customerName = v; }

    public String getAssignedName()         { return assignedName; }
    public void setAssignedName(String v)   { this.assignedName = v; }
}