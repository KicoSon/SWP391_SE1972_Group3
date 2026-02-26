package model.activity;

public class Opportunity {

    private int id;
    private String title;
    private int customerId;
    
    public Opportunity() {
    }

    public Opportunity(int id, String title, int customerId) {
        this.id = id;
        this.title = title;
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

}
