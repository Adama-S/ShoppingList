package Util;

import android.os.AsyncTask;
import android.widget.Toast;

import com.example.adamJeann.shoppinglist.IRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by herve on 01/12/2016.
 */

public class MyAsyncTask extends AsyncTask<String, Void, String> {

    private IRequestListener listener;

    /**
     * The system calls this to perform work in a worker thread and
     * delivers it the parameters given to AsyncTask.execute()
     */
    protected String doInBackground(String... urls) {
        URL url;
        StringBuilder sb = null;
        String result = null;
        try {
            url = new URL(urls[0]);

            System.out.println("Urls : -- " + Arrays.toString(urls));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            BufferedReader br;
            conn.connect();

            if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            } else {
                br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));
            }

            sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);

            }
            br.close();

            result = sb.toString();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    protected void onPostExecute(String result) {

        if (result != null && listener != null) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                listener.onSuccess(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
         //   Toast toast = Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
         //   toast.show();
        } else {
            listener.onFail();
         //   Toast toast = Toast.makeText(getApplicationContext(), "You succefully register", Toast.LENGTH_LONG);
         //   toast.show();
        }
    }

    public void setListener(IRequestListener listener){this.listener = listener;}

    public IRequestListener getListener(){return listener;}
}