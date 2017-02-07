package models;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.adamJeann.shoppinglist.ProductListActivity;
import com.example.adamJeann.shoppinglist.R;

import java.util.ArrayList;

/**
 * Created by herve on 29/01/2017.
 */

public class ProductListAdapter extends ArrayAdapter<ProductList> {

    private final Context mContext;

    public ProductListAdapter(Context context, ArrayList<ProductList> productList) {
        super(context, 0, productList);
        this.mContext=context;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final ProductList productList = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_single_product, parent, false);
        }


        TextView productName = (TextView) convertView.findViewById(R.id.productName);
        TextView productQuantity = (TextView) convertView.findViewById(R.id.productQuantity);

        productName.setText(productList.name);
        productQuantity.setText(productList.quantity);

        FloatingActionButton btDelete = (FloatingActionButton) convertView.findViewById(R.id.fabDelete);
        btDelete.setTag(productList);

        btDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                if(mContext instanceof ProductListActivity)
                    ((ProductListActivity) mContext).deleteProduct(view);
            }
        });

        FloatingActionButton fabUpdate = (FloatingActionButton) convertView.findViewById(R.id.fabUpdate);
        fabUpdate.setTag(productList);
        fabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContext instanceof ProductListActivity)
                    ((ProductListActivity) mContext).updateProduct(view);
            }
        });

        return convertView;
    }


}
