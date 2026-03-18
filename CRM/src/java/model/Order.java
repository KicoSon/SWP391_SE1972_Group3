package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
    private int id;
    private int quotationId;
    private String orderNumber;
    private int customerId;
    private BigDecimal totalAmount;
    private String status;
    private Timestamp createdAt;

    // Constructor rỗng
    public Order() {
    }

    // Constructor đầy đủ
    public Order(int id, int quotationId, String orderNumber, int customerId,
                  BigDecimal totalAmount, String status, Timestamp createdAt) {
        this.id = id;
        this.quotationId = quotationId;
        this.orderNumber = orderNumber;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(int quotationId) {
        this.quotationId = quotationId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}