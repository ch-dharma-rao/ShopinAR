package com.dharma.shopinar.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.dharma.shopinar.ProductDetailActivity;
import com.dharma.shopinar.R;
import com.dharma.shopinar.models.CartListItem;
import com.dharma.shopinar.models.CartModel;
import com.dharma.shopinar.models.Item;
import com.dharma.shopinar.models.ListItem;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartListItemsAdapter extends RecyclerView.Adapter<CartListItemsAdapter.ViewHolder>{

    private final Context mContext;
    List<CartListItem> listItems ;
    private final LayoutInflater mLayoutInflater;


    public CartListItemsAdapter(Context mContext, List<CartListItem> listItems ) {
        this.mContext = mContext;
        this.listItems = listItems;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CartListItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListItemsAdapter.ViewHolder holder, int position) {
        holder.name = listItems.get(position).getName();
        holder.id = listItems.get(position).getId();
        holder.path = listItems.get(position).getPath();

        Item item = listItems.get(position).getItem();
        CartModel cartItem = listItems.get(position).getCartItem();
        String AR;
        if (item.getIsAR()) {
            AR = "AR";
        } else {
            AR = "";
        }
        if (!AR.equals("")) {
            TextDrawable drawable = TextDrawable.builder().buildRound(AR, R.color.colorPrimaryDark);
            holder.item_view_AR.setVisibility(View.VISIBLE);
        }

        int product_quantity = cartItem.getNo_of_items();
        double item_price = item.getPrice();
        double total_price = product_quantity * item_price;
        holder.item_view_name.setText(item.getName());
        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        holder.item_view_price.setText(fmt.format(item.getPrice()));
        holder.item_product_quantity.setText("x " + String.valueOf(product_quantity));
        holder.item_product_total_cost.setText(fmt.format(total_price));
        String url = item.getImages().get(0);
        Picasso.get()
                .load(url)
                .into(holder.item_view_image);

        getItemCount();

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_view_name, item_view_price, item_product_quantity, item_product_total_cost, item_view_AR;
        ImageView item_view_image;
        String path ;
        String id ;
        String name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_view_name = itemView.findViewById(R.id.product_title);
            item_view_price = itemView.findViewById(R.id.product_price);
            item_product_quantity = itemView.findViewById(R.id.product_quantity);
            item_product_total_cost = itemView.findViewById(R.id.product_total_cost);
            item_view_image = itemView.findViewById(R.id.product_image);
            item_view_AR = itemView.findViewById(R.id.product_ar_label);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                    Intent intent = new Intent(mContext, ProductDetailActivity.class);
                    intent.putExtra("path", path);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
