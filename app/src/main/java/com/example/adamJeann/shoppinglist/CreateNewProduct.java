package com.example.adamJeann.shoppinglist;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import Util.MyAsyncTask;
import Util.Urls;
import models.ProductList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateNewProduct.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateNewProduct#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateNewProduct extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_TOKEN = "token";
    private static final String ARG_SHOPPING_LIST_ID = "shopping_list_id";
    private static final String ARG_PRODUCT_ID = "id";
    private static final String ARG_PRODUCT = "product";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText productName;
    private EditText productQuantity;
    private EditText productPrice;
    SharedPreferences sharedPreferences = null;
    private String token;
    private String shoppingListId;
    private String productId;
    private ProductList product;
    private String url;
    boolean cancel = false;
    View focusView = null;

    private OnFragmentInteractionListener mListener;

    public CreateNewProduct() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateNewProduct.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateNewProduct newInstance(String param1, String param2) {
        CreateNewProduct fragment = new CreateNewProduct();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            token = getArguments().getString(ARG_TOKEN);
            shoppingListId = getArguments().getString(ARG_SHOPPING_LIST_ID);
            System.out.println("shopping list id : " + shoppingListId);
            productId = getArguments().getString(ARG_PRODUCT_ID);
            System.out.println("product id : " + productId);
            product = getArguments().getParcelable(ARG_PRODUCT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_new_product, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Button button = (Button) view.findViewById(R.id.btn_create_new_product);


        productName = (EditText) view.findViewById(R.id.productName);
        productQuantity = (EditText) view.findViewById(R.id.productQuantity);
        productPrice = (EditText) view.findViewById(R.id.productPrice);

        if(product != null) {
            button.setText("Update product");
            productName.setText(product.getName());
            productQuantity.setText(product.getQuantity());
            String productPriceValue = Double.toString(product.getPrice());
            productPrice.setText(productPriceValue);
            updateProduct();
        }

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(product != null) {
                    updateProduct();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", token);
                    bundle.putString("shopping_list_id", shoppingListId);
                    ProductListFragment productListFragment = new ProductListFragment();
                    productListFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, productListFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    Toast toast = Toast.makeText(getActivity(), "Product successfully updated", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    createProduct();
                    Bundle bundle = new Bundle();
                    bundle.putString("token", token);
                    bundle.putString("shopping_list_id", shoppingListId);
                    bundle.putString("id", productId);
                    ProductListFragment productListFragment = new ProductListFragment();
                    productListFragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, productListFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public void createProduct() {

        final MyAsyncTask asyncTask = new MyAsyncTask();
        System.out.println("token : " + token);

        boolean cancel = false;
        View focusView = null;

        String productNameView = productName.getText().toString();

        if (TextUtils.isEmpty(productNameView)) {
            productName.setError(getString(R.string.error_field_required));
            focusView = productName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            changeCreationUrl();

            System.out.println(url);
            url = url.replace(" ", "");
            asyncTask.execute(url);

            asyncTask.setListener(new IRequestListener() {

                @Override
                public void onSuccess(JSONObject object) {
                    sharedPreferences = getActivity().getSharedPreferences("mySharedPreferences", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();


                    String productNameView;
                    String productQuantity;
                    String productPriceView = null;

                    try {
                        String codeTxt = object.getString("code");
                        int code = Integer.parseInt(codeTxt);

                        if (code == 0) {
                            JSONObject resultObject = object.getJSONObject("result");
                            productNameView = resultObject.getString("name");
                            productQuantity = resultObject.getString("quantity");
                            editor.putString("name", productNameView);
                            editor.putString("quantity", productQuantity);
                            String productPriceValue = productPriceView;
                            editor.putString("price", productPriceValue);
                            editor.apply();

                            Toast toast = Toast.makeText(getActivity(), "Product successfully added", Toast.LENGTH_LONG);
                            toast.show();
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
    }


    public void updateProduct(){
        final MyAsyncTask asyncTask = new MyAsyncTask();
        productName.setError(null);
        String productNameView = productName.getText().toString();
        String productQuantityView = productQuantity.getText().toString();
        String productPriceView = productPrice.getText().toString();

        if(TextUtils.isEmpty(productQuantityView)){
            productQuantity.setError(getString(R.string.error_field_required));
            focusView = productQuantity;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            changeUpdateUrl();
            url =  url.replace(" ","");
            System.out.println("l'url :" + url);
            asyncTask.execute(url);

            asyncTask.setListener(new IRequestListener() {
                @Override
                public void onSuccess(JSONObject object) {
                    sharedPreferences = getActivity().getSharedPreferences("mySharedPreferences", 0);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    try {
                        String codeTxt = object.getString("code");
                        int code = Integer.parseInt(codeTxt);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("bla bla bla erreur");
                    }
                }
                @Override
                public void onFail() {
                    Log.e("Error", "test error bla bla onFailed");
                }
            });
        }
    }


    public void changeCreationUrl(){

        String productNameView = productName.getText().toString();

        if(TextUtils.isEmpty(productQuantity.getText().toString())) {
            if(TextUtils.isEmpty(productPrice.getText().toString())){
                url = Urls.WS_CREATE_PRODUCT_URL + "?token=" + token + "&shopping_list_id=" + shoppingListId
                        + "&name=" + productNameView;
            } else {
                String productPriceView = productPrice.getText().toString();
                url = Urls.WS_CREATE_PRODUCT_URL + "?token=" + token + "&shopping_list_id=" + shoppingListId
                        + "&name=" + productNameView
                        + "&price=" + productPriceView;
            }
        } else {
            String productQuantityView = productQuantity.getText().toString();
            if(TextUtils.isEmpty(productPrice.getText().toString())){
                url = Urls.WS_CREATE_PRODUCT_URL + "?token=" + token + "&shopping_list_id=" + shoppingListId
                        + "&name=" + productNameView
                        + "&quantity=" + productQuantityView;
            } else {
                String productPriceView = productPrice.getText().toString();
                url = Urls.WS_CREATE_PRODUCT_URL + "?token=" + token + "&shopping_list_id=" + shoppingListId
                        + "&name=" + productNameView
                        + "&quantity=" + productQuantityView
                        + "&price=" + productPriceView;
            }
        }
    }

    public void changeUpdateUrl(){

        String productNameView = productName.getText().toString();

        String productQuantityView = productQuantity.getText().toString();
        Double productPriceView = Double.valueOf(productPrice.getText().toString());

        if(TextUtils.isEmpty(productQuantityView)){
            productQuantity.setError(getString(R.string.error_field_required));
            focusView = productQuantity;
            cancel = true;
        } else {
            if(TextUtils.isEmpty(productNameView)) {
                if(TextUtils.isEmpty(productQuantityView)){
                    url = Urls.WS_EDIT_PRODUCT_URL + "?token=" + token + "&id=" + productId
                            + "&price=" + productPriceView;
                } else {
                    url = Urls.WS_EDIT_PRODUCT_URL + "?token=" + token + "&id=" + productId
                            + "&quantity=" + productQuantityView
                            + "&price=" + productPriceView;
                }
            } else {
                if(TextUtils.isEmpty(productQuantityView)){
                    url = Urls.WS_EDIT_PRODUCT_URL + "?token=" + token + "&id=" + productId
                            + "&name=" + productNameView
                            + "&price=" + productPriceView;
                } else {
                    url = Urls.WS_EDIT_PRODUCT_URL + "?token=" + token + "&id=" + productId
                            + "&name=" + productNameView
                            + "&quantity=" + productQuantityView
                            + "&price=" + productPriceView;
                }
            }
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
