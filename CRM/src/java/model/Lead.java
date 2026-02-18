package model;

import java.util.Date;

public class Lead {

    private long id;

    private String fullName;
    private String phone;
    private String email;
    private String address;

    private String productInterest;

    private String source;

    private String status;

    private Long campaignId;

    private Long assignedSalesId;

    private Long createdBy;

    private Date createdAt;

    private Date updatedAt;

    // field phụ (JOIN campaigns)
    private String campaignName;


    // ===== Constructor rỗng =====

    public Lead() {
    }


    // ===== Constructor đầy đủ =====

    public Lead(long id, String fullName, String phone, String email,
            String address, String productInterest, String source,
            String status, Long campaignId, Long assignedSalesId,
            Long createdBy, Date createdAt, Date updatedAt) {

        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.productInterest = productInterest;
        this.source = source;
        this.status = status;
        this.campaignId = campaignId;
        this.assignedSalesId = assignedSalesId;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    // ===== Getter Setter =====

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getProductInterest() {
        return productInterest;
    }

    public void setProductInterest(String productInterest) {
        this.productInterest = productInterest;
    }


    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }


    public Long getAssignedSalesId() {
        return assignedSalesId;
    }

    public void setAssignedSalesId(Long assignedSalesId) {
        this.assignedSalesId = assignedSalesId;
    }


    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }


    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }


    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

}
