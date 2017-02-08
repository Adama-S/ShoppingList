package com.example.adamJeann.shoppinglist;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import Util.MyAsyncTask;
import models.ProductList;
import models.ProductListAdapter;

import static Util.Urls.WS_REMOVE_PRODUCT_URL;

public class ProductListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener, CreateNewProduct.OnFragmentInteractionListener, ProductListFragment.OnFragmentInteractionListener {

    String token;
    ListView listView;
    int shoppingListId;
    int productId;
    private EditText productName;
    private EditText mProductQuantity;
    SharedPreferences sharedPreferences;
    ProductListAdapter mArrayAdapter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_product);

        sharedPreferences = getSharedPreferences("mySharedPreference", MODE_PRIVATE);
        token = sharedPreferences.getString("tokenUser", null);

        listView = (ListView) findViewById(R.id.listProduct);

        Bundle b = getIntent().getExtras();
        shoppingListId = b.getInt("id");
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        bundle.putString("shopping_list_id", String.valueOf(shoppingListId));

        Fragment fragment = new ProductListFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.my_shopping_lists) {
            Intent intent = new Intent(ProductListActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
            finish();
    }


    public void deleteProduct(View v) {
        final ProductList productList = (ProductList) v.getTag();
        int id = productList.getId();

        final MyAsyncTask asyncTask = new MyAsyncTask();

        String url;
        url = WS_REMOVE_PRODUCT_URL + "?token=" + token + "&id=" + id;
        asyncTask.execute(url);
        asyncTask.setListener(new IRequestListener() {
            @Override
            public void onSuccess(JSONObject object) {
                try {
                    String codeTxt = object.getString("code");
                    int code = Integer.parseInt(codeTxt);

                    if (code == 0) {

                        listView.invalidateViews();
                        Bundle bundle = new Bundle();
                        bundle.putString("token", token);
                        bundle.putString("shopping_list_id", String.valueOf(shoppingListId));
                        ProductListFragment productListFragment = new ProductListFragment();
                        productListFragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, productListFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }else{
                        System.out.println("List view else"+ listView.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail() {
                Log.e("Error", "test error onFailed");
            }
        });
    }


    public void updateProduct(View view) {

        final ProductList product = (ProductList) view.getTag();
        int id = product.getId();

        System.out.println("product id : " + id);

        Bundle b = getIntent().getExtras();
        shoppingListId = b.getInt("id");
        Bundle bundle = new Bundle();
        bundle.putString("token", token);
        bundle.putString("shopping_list_id", String.valueOf(shoppingListId));
        bundle.putString("id", String.valueOf(id));
        bundle.putParcelable("product", product);

        Fragment fragment = new CreateNewProduct();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ProductList Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


}
