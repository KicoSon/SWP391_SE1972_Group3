package model.sales;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class SalesOrder {

    private int id;
    private int quotationId;
    private int opportunityId;
    private String orderCode;
    private String status;
    private BigDecimal totalAmount;
    private Date orderDate;
    private Date deliveryDate;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Transient fields
    private String customerName;
    private String quotationCode;

    public SalesOrder() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuotationId() { return quotationId; }
    public void setQuotationId(int quotationId) { this.quotationId = quotationId; }

    public int getOpportunityId() { return opportunityId; }
    public void setOpportunityId(int opportunityId) { this.opportunityId = opportunityId; }

    public String getOrderCode() { return orderCode; }
    public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public Date getDeliveryDate() { return deliveryDate; }
    public void setDeliveryDate(Date deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getQuotationCode() { return quotationCode; }
    public void setQuotationCode(String quotationCode) { this.quotationCode = quotationCode; }
}
