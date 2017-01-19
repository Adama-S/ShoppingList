package models;

import java.util.Date;

/**
 * Created by korhy on 19/01/2017.
 */

public class ShoppingList {

    private Integer id;

    private String name;

    private String createdDate;

    private Boolean completed;

    public ShoppingList(Integer id, String name, String createdDate, Boolean completed) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.completed = completed;
    }

    public ShoppingList(String name, String createdDate) {
        this.name = name;
        this.createdDate = createdDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return name + "\n " + createdDate;
    }
}
