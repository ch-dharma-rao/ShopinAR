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
import com.dharma.shopinar.models.NotificationModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Locale;

public class NotificationRecyclerViewAdapter extends FirestoreRecyclerAdapter<NotificationModel, NotificationRecyclerViewAdapter.ViewHolder> {

    private OnItemClickListener listener;


    public NotificationRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<NotificationModel> options) {
        super(options);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notification_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Item model) {
//        String AR;
//        if (model.getIsAR()) {
//            AR = "AR";
//        } else {
//            AR = "";
//        }
//        if (!AR.equals("")) {
//            TextDrawable drawable = TextDrawable.builder().buildRound(AR, R.color.colorPrimaryDark);
//            viewHolder.item_view_AR.setVisibility(View.VISIBLE);
//        }
//        viewHolder.item_view_name.setText(model.getName());
//        Locale locale = new Locale("en", "IN");
//        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
//        viewHolder.item_view_price.setText(fmt.format(model.getPrice()));
//        viewHolder.item_view_cutted_price.setText(fmt.format(model.getCuttedPrice()));
//        String url = model.getImages().get(0);
//        Picasso.get()
//                .load(url)
//                .into(viewHolder.item_view_image);
//
//        getItemCount();
//    }


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

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull NotificationModel model) {
        holder.item_view_name.setText(model.getName());
        holder.item_view_description.setText(model.getDescription());
        Picasso.get()
                .load(model.getImageURL())
                .into(holder.item_view_image);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView item_view_name, item_view_description;
        ImageView item_view_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_view_name = itemView.findViewById(R.id.notification_name);

            item_view_image = itemView.findViewById(R.id.notification_image);
            item_view_description = itemView.findViewById(R.id.notification_description);

        }
    }
}
