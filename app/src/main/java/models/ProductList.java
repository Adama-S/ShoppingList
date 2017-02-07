package models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by herve on 29/01/2017.
 */

public class ProductList  implements Serializable, Parcelable {
    public Integer id;

    public String name;

    public String quantity;

    public Double price;

    public ProductList(JSONObject jsonObject) {
        try {
            this.id = jsonObject.getInt("id");
            this.name = jsonObject.getString("name");
            this.quantity = jsonObject.getString("quantity");
            this.price = jsonObject.getDouble("price");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    protected ProductList(Parcel in) {
        name = in.readString();
        quantity = in.readString();
        price = in.readDouble();
    }

    public static final Creator<ProductList> CREATOR = new Creator<ProductList>() {
        @Override
        public ProductList createFromParcel(Parcel in) {
            return new ProductList(in);
        }

        @Override
        public ProductList[] newArray(int size) {
            return new ProductList[size];
        }
    };

    public static ArrayList<ProductList> fromJson(JSONArray productListArray) {
        ArrayList<ProductList> productList = new ArrayList<ProductList>();
        for (int i = 0; i < productListArray.length(); i++) {
            try {
                productList.add(new ProductList(productListArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return productList;
    }

    public int getId() {
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


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(quantity);
        dest.writeDouble(price);
    }

}
