package model.sales;

public class PipelineStage {

    private int id;
    private int pipelineId;
    private String stageName;
    private int orderIndex;
    private String color;
    private boolean isWon;
    private boolean isLost;

    public PipelineStage() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPipelineId() { return pipelineId; }
    public void setPipelineId(int pipelineId) { this.pipelineId = pipelineId; }

    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public boolean isWon() { return isWon; }
    public void setWon(boolean isWon) { this.isWon = isWon; }

    public boolean isLost() { return isLost; }
    public void setLost(boolean isLost) { this.isLost = isLost; }
}
