package models;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.adamJeann.shoppinglist.CartActivity;
import com.example.adamJeann.shoppinglist.HomeActivity;
import com.example.adamJeann.shoppinglist.R;
import java.util.ArrayList;



/**
 * Created by korhy on 21/01/2017.
 */

public class ShoppingListAdapter extends ArrayAdapter<ShoppingList> {

    private final Context mContext;

    public ShoppingListAdapter(Context context, ArrayList<ShoppingList> shoppingLists) {
        super(context, 0, shoppingLists);
        this.mContext=context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ShoppingList sl = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list_item_1, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        // Populate the data into the template view using the data object
        tvName.setText(sl.name);
        tvDate.setText(sl.createdDate.toString());
        // Return the completed view to render on screen

        FloatingActionButton btDelete = (FloatingActionButton) convertView.findViewById(R.id.fabDelete);
        btDelete.setTag(sl);

        btDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                if(mContext instanceof HomeActivity)
                    ((HomeActivity) mContext).DeleteShoppingList(view);
            }
        });

        FloatingActionButton fabUpdate = (FloatingActionButton) convertView.findViewById(R.id.fabUpdate);
        fabUpdate.setTag(sl);
        fabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mContext instanceof HomeActivity)
                    ((HomeActivity) mContext).redirectUpdateList(view);
            }
        });

        return convertView;
    }

}
