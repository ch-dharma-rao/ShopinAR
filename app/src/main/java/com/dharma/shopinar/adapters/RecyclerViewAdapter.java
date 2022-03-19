package com.dharma.shopinar.adapters;


import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.dharma.shopinar.R;
import com.dharma.shopinar.models.Item;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class RecyclerViewAdapter extends FirestoreRecyclerAdapter<Item, RecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener listener;

    public RecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<Item> options) {
        super(options);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Item model) {
        String AR;
        if (model.getIsAR()) {
            AR = "AR";
        } else {
            AR = "";
        }
        if (!AR.equals("")) {
            TextDrawable drawable = TextDrawable.builder().buildRound(AR, R.color.colorPrimaryDark);
            viewHolder.item_view_AR.setVisibility(View.VISIBLE);
        }
        viewHolder.item_view_name.setText(model.getName());
        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        viewHolder.item_view_price.setText(fmt.format(model.getPrice()));
        viewHolder.item_view_cutted_price.setText(fmt.format(model.getCuttedPrice()));
        String url = model.getImages().get(0);
        Picasso.get()
                .load(url)
                .into(viewHolder.item_view_image);

        getItemCount();
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int pos);
    }


    @Override
    public int getItemCount() {
        Log.e("Chairs Item Cont", String.valueOf(super.getItemCount()));
        return super.getItemCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_view_name, item_view_price, item_view_cutted_price, item_view_AR;
        ImageView item_view_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_view_name = itemView.findViewById(R.id.product_title);
            item_view_price = itemView.findViewById(R.id.product_price);
            item_view_cutted_price = itemView.findViewById(R.id.product_cutted_price);
            item_view_image = itemView.findViewById(R.id.product_image);
            item_view_AR = itemView.findViewById(R.id.product_ar_label);

            item_view_cutted_price.setPaintFlags(item_view_cutted_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(pos), pos);
                    }
                }
            });
        }
    }
}
