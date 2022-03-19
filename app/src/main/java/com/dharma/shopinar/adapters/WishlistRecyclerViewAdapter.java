//package com.dharma.shopinar.adapters;
//
//
//import android.content.Context;
//import android.graphics.Paint;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.dharma.shopinar.R;
//import com.dharma.shopinar.models.Item;
//import com.dharma.shopinar.models.WishlistModel;
//import com.google.firebase.firestore.DocumentSnapshot;
//
//public class WishlistRecyclerViewAdapter extends RecyclerView.Adapter<WishlistRecyclerViewAdapter.ViewHolder> {
//
//    private final Context mContext;
////    private Item;
//    private OnItemClickListener listener;
//
//    public WishlistRecyclerViewAdapter(Context mContext) {
//        this.mContext = mContext;
//    }
//
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);
//        ViewHolder holder = new ViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//
//    }
//
//    public void setOnItemClickListener(OnItemClickListener listener) {
//        this.listener = listener;
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(DocumentSnapshot documentSnapshot, int pos);
//    }
//
//
//    @Override
//    public int getItemCount() {
//        Log.e("Chairs Item Cont", String.valueOf(super.getItemCount()));
//        return super.getItemCount();
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull WishlistModel model) {
////        Log.e("Wishlist",model.getProductId());
////        Log.e("Wishlist",model.getProductType());
////        Log.e("Wishlist",model.getPath());
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView item_view_name, item_view_price, item_view_cutted_price, item_view_AR;
//        ImageView item_view_image;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            item_view_name = itemView.findViewById(R.id.product_title);
//            item_view_price = itemView.findViewById(R.id.product_price);
//            item_view_cutted_price = itemView.findViewById(R.id.product_cutted_price);
//            item_view_image = itemView.findViewById(R.id.product_image);
//            item_view_AR = itemView.findViewById(R.id.product_ar_label);
//
//            item_view_cutted_price.setPaintFlags(item_view_cutted_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION && listener != null) {
//                        listener.onItemClick(getSnapshots().getSnapshot(pos), pos);
//                    }
//                }
//            });
//        }
//    }
//}
