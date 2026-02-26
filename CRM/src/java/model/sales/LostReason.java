package model.sales;

public class LostReason {

    private int id;
    private String reason;
    private boolean isActive;

    public LostReason() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean isActive) { this.isActive = isActive; }
}
