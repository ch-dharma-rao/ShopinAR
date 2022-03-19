package com.dharma.shopinar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.dharma.shopinar.adapters.ListItemsAdapter;
//import com.dharma.shopinar.adapters.WishlistRecyclerViewAdapter;
import com.dharma.shopinar.models.Item;
import com.dharma.shopinar.models.ListItem;
import com.dharma.shopinar.models.WishlistModel;
import com.dharma.shopinar.networksync.CheckInternetConnection;
import com.dharma.shopinar.usersession.UserSession;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Wishlist extends AppCompatActivity {


    private static final String TAG = "Wishlist";
    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    //to get user session data
    private UserSession session;
    private HashMap<String, String> user;
    private String name, email, photo, mobile;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    //Getting reference to Firebase Database
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference mDatabaseReference = database.getReference();
    private LottieAnimationView no_item;
    private FrameLayout activitycartlist;
    private LottieAnimationView emptycart;

    FirebaseAuth gAuth;
    FirestoreRecyclerOptions<WishlistModel> options;
    FirebaseAuth.AuthStateListener aL;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListItemsAdapter adapter;
    //    private RecyclerViewAdapter adapter, ARadapter, NonARadapter, searchAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    RecyclerView recyclerView;


    IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
    CheckInternetConnection cic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Wishlist");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new UserSession(getApplicationContext());

        recyclerView = findViewById(R.id.wishlist_items);

        db = FirebaseFirestore.getInstance();



        List<WishlistModel> wishlistProductsList = new ArrayList<WishlistModel>();

        String userID = session.getUserID();
        db.collection("users").document(userID).collection("wishlist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                wishlistProductsList.add(document.toObject(WishlistModel.class));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            List<ListItem> listItems = new ArrayList<>();
                            for (WishlistModel wishlistID : wishlistProductsList) {
                                if (wishlistID.isIzInWishlist()) {
                                    String productType = wishlistID.getProductType();
                                    String getProductId = wishlistID.getProductId();

                                    db.collection(productType).document(getProductId)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Item item = document.toObject(Item.class);
                                                    String name = item.getName();
                                                    String id = wishlistID.getProductId();
                                                    String path = wishlistID.getPath();
                                                    ListItem listItem = new ListItem(name,id,path,item);
                                                    listItems.add(listItem);
                                                    Log.e(TAG, "Item List " + listItems.get(0).getName());


                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }
//

                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }


                                            ListItemsAdapter adapter = new ListItemsAdapter(getApplicationContext(), listItems);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(Wishlist.this));
                                            recyclerView.setAdapter(adapter);

                                        }
                                    });

//                        WishlistModel product = documentSnapshot.toObject(WishlistModel.class);
////                    wishlistProdcutsDetailsList.add(product);
////                    Log.e("wishlistProdcutsDetailsList", String.valueOf(wishlistProdcutsDetailsList.get(0)));
//
//                    }
//
//                });
                                }
                            }

                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }

//                         getWishlistProductDetails(wishlistProductsList);

                    }
                })
        ;

//        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        Query query;
//        query = db.collection("users").document(session.getUserID()).collection("wishlist");
//
//        options = new FirestoreRecyclerOptions.Builder<WishlistModel>()
//                .setQuery(query, WishlistModel.class)
//                .build();
//
//        adapter = new WishlistRecyclerViewAdapter(options);
//


//        initRecyclerView();


    }

//    private void getWishlistProductDetails(List<WishlistModel> wishlistProductsList) {
//
//    }


//    private void initRecyclerView() {
//        recyclerView = findViewById(R.id.wishlist_items);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        clickListener();
//
////        checkItemCount();
//
//    }

//    private void clickListener(){
//        adapter.setOnItemClickListener(new WishlistRecyclerViewAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
//                Bundle bundle = new Bundle();
//                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//                String path = documentSnapshot.getReference().getPath();
//                String id = documentSnapshot.getId();
//                String name = documentSnapshot.get("name").toString();
//                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
//                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
//                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
//                intent.putExtra("path", path);
//                startActivity(intent);
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //    private void checkItemCount() {
//        View empty_list = findViewById(R.id.empty_list);
//
//        if (adapter.getItemCount() == 0) {
//            empty_list.setVisibility(View.VISIBLE);
//        } else {
//            empty_list.setVisibility(View.INVISIBLE);
//        }
//    }

    @Override
    protected void onStart() {
        super.onStart();
//        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        adapter.stopListening();
    }


    public void viewProfile(View view) {
        startActivity(new Intent(Wishlist.this, ProfileActivity.class));
    }

    public void viewCart(View view) {
        startActivity(new Intent(Wishlist.this, Cart.class));
    }


    public void Notifications(View view) {
        startActivity(new Intent(Wishlist.this, NotificationActivity.class));
    }

}

