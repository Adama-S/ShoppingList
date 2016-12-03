package com.example.adamJeann.shoppinglist;

import org.json.JSONObject;

/**
 * Created by herve on 01/12/2016.
 */

public interface IRequestListener {
    public void onSuccess(JSONObject object);
    public void onFail();

}
