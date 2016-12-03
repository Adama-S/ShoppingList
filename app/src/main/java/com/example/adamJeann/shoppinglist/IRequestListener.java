package com.example.adamJeann.shoppinglist;

import org.json.JSONObject;

public interface IRequestListener {
    void onSuccess(JSONObject object);
    void onFail();

}
