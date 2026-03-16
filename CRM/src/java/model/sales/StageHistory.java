package model.sales;

import java.sql.Timestamp;

public class StageHistory {
    private int id;
    private int opportunityId;
    private String oldStage;
    private String newStage;
    private int changedBy;
    private Timestamp changedAt;
    private String notes;

    private String changedByName;

    public StageHistory() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOpportunityId() { return opportunityId; }
    public void setOpportunityId(int opportunityId) { this.opportunityId = opportunityId; }

    public String getOldStage() { return oldStage; }
    public void setOldStage(String oldStage) { this.oldStage = oldStage; }

    public String getNewStage() { return newStage; }
    public void setNewStage(String newStage) { this.newStage = newStage; }

    public int getChangedBy() { return changedBy; }
    public void setChangedBy(int changedBy) { this.changedBy = changedBy; }

    public Timestamp getChangedAt() { return changedAt; }
    public void setChangedAt(Timestamp changedAt) { this.changedAt = changedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getChangedByName() { return changedByName; }
    public void setChangedByName(String changedByName) { this.changedByName = changedByName; }
}
