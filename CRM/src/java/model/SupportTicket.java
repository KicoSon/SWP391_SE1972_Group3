package model;

import java.sql.Timestamp;

public class SupportTicket {

    private int id;
    private int customerId;
    private Integer orderId;
    private String title;
    private String description;
    private String priority;   // Low, Medium, High, Urgent
    private String status;     // Open, In Progress, Resolved
    private int assignedTo;
    private Timestamp createdAt;
    private Timestamp updateAt;
    private String customerName;
    private String assignedName;

    public SupportTicket() {
    }

    public SupportTicket(int id, int customerId, Integer orderId, String title, String description, String priority, String status, int assignedTo, Timestamp createdAt, Timestamp updateAt) {
        this.id = id;
        this.customerId = customerId;
        this.orderId = orderId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    public SupportTicket(int id, int customerId, Integer orderId, String title, String description, String priority, String status, int assignedTo, Timestamp createdAt, Timestamp updateAt, String customerName, String assignedName) {
        this.id = id;
        this.customerId = customerId;
        this.orderId = orderId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.customerName = customerName;
        this.assignedName = assignedName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(int assignedTo) {
        this.assignedTo = assignedTo;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Timestamp updateAt) {
        this.updateAt = updateAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public void setAssignedName(String assignedName) {
        this.assignedName = assignedName;
    }
}
