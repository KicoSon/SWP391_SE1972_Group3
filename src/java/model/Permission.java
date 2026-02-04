package model;

/**
 * Permission model representing the permissions table
 */
public class Permission {
    private int id;
    private String name;
    private String resource;  // e.g., "admin/staff", "admin/customers"
    private String action;    // e.g., "view", "create", "edit", "delete"
    private String description;
    
    // Constructors
    public Permission() {}
    
    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public Permission(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    public Permission(String resource, String action, String description) {
        this.resource = resource;
        this.action = action;
        this.description = description;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getResource() {
        return resource;
    }
    
    public void setResource(String resource) {
        this.resource = resource;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", resource='" + resource + '\'' +
                ", action='" + action + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}