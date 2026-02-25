package model.sales;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class Opportunity {

    private int id;
    private String title;
    private Integer customerId;
    private Long leadId;
    private int assignedSalesId;
    private String stage;
    private String status;
    private BigDecimal expectedValue;
    private double closeProbability;
    private Date expectedCloseDate;
    private String source;
    private Integer campaignId;
    private String lostReason;
    private String notes;
    private int pipelineId;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Transient fields
    private String customerName;
    private String assignedSalesName;
    private String campaignName;

    public Opportunity() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Long getLeadId() { return leadId; }
    public void setLeadId(Long leadId) { this.leadId = leadId; }

    public int getAssignedSalesId() { return assignedSalesId; }
    public void setAssignedSalesId(int assignedSalesId) { this.assignedSalesId = assignedSalesId; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getExpectedValue() { return expectedValue; }
    public void setExpectedValue(BigDecimal expectedValue) { this.expectedValue = expectedValue; }

    public double getCloseProbability() { return closeProbability; }
    public void setCloseProbability(double closeProbability) { this.closeProbability = closeProbability; }

    public Date getExpectedCloseDate() { return expectedCloseDate; }
    public void setExpectedCloseDate(Date expectedCloseDate) { this.expectedCloseDate = expectedCloseDate; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Integer getCampaignId() { return campaignId; }
    public void setCampaignId(Integer campaignId) { this.campaignId = campaignId; }

    public String getLostReason() { return lostReason; }
    public void setLostReason(String lostReason) { this.lostReason = lostReason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public int getPipelineId() { return pipelineId; }
    public void setPipelineId(int pipelineId) { this.pipelineId = pipelineId; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getAssignedSalesName() { return assignedSalesName; }
    public void setAssignedSalesName(String assignedSalesName) { this.assignedSalesName = assignedSalesName; }

    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }

    @Override
    public String toString() {
        return "Opportunity{id=" + id + ", title='" + title + "', stage='" + stage + "', status='" + status + "'}";
    }
}
