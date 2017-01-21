package models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by korhy on 19/01/2017.
 */

public class ShoppingList {

    public Integer id;

    public String name;

    public Date createdDate;

    public Boolean completed;

    public ShoppingList(Integer id, String name, Date createdDate, Boolean completed) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.completed = completed;
    }

    public ShoppingList(String name, Date createdDate) {
        this.name = name;
        this.createdDate = createdDate;
    }

    public ShoppingList(JSONObject object){
        try {
            this.id = object.getInt("id");
            this.name = object.getString("name");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.createdDate = dateFormat.parse(object.getString("created_date"));
            this.completed = Boolean.valueOf (object.getString("completed"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<ShoppingList> fromJson(JSONArray jsonObjects) {
        ArrayList<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                shoppingLists.add(new ShoppingList(jsonObjects.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return shoppingLists;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
