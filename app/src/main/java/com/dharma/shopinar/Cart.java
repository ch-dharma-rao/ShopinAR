package com.dharma.shopinar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dharma.shopinar.adapters.CartListItemsAdapter;
import com.dharma.shopinar.adapters.ListItemsAdapter;
import com.dharma.shopinar.models.CartListItem;
import com.dharma.shopinar.models.CartModel;
import com.dharma.shopinar.models.Item;
import com.dharma.shopinar.models.ListItem;
import com.dharma.shopinar.models.WishlistModel;
import com.dharma.shopinar.usersession.UserSession;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity {
    private static final String TAG = "Cart";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView recyclerView;
    private UserSession session;
    TextView checkout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Cart");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new UserSession(getApplicationContext());

        recyclerView = findViewById(R.id.cart_items_list);
        checkout = findViewById(R.id.checkout_btn);

        db = FirebaseFirestore.getInstance();

        List<CartModel> cartProductsList = new ArrayList<CartModel>();

        String userID = session.getUserID();
        db.collection("users").document(userID).collection("cart")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cartProductsList.add(document.toObject(CartModel.class));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            List<CartListItem> cartListItems = new ArrayList<>();
                            for (CartModel cartID : cartProductsList) {
                                if (cartID.isIzInCart()) {
                                    String productType = cartID.getProductType();
                                    String productId = cartID.getProductId();

                                    db.collection(productType).document(productId)
                                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Item item = document.toObject(Item.class);
                                                    String name = item.getName();
                                                    String id = cartID.getProductId();
                                                    String path = cartID.getPath();
//                                                    int no_of_items = cartID.getNo_of_items();
                                                    CartListItem cartListItem = new CartListItem(name,id,path,cartID,item);
                                                    cartListItems.add(cartListItem);
                                                    Log.e(TAG, "Item List " + cartListItems.get(0).getName());


                                                } else {
                                                    Log.d(TAG, "No such document");
                                                }
//

                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }


                                            CartListItemsAdapter adapter = new CartListItemsAdapter(getApplicationContext(), cartListItems);
                                            recyclerView.setLayoutManager(new LinearLayoutManager(Cart.this));
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
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewProfile(View view) {
        startActivity(new Intent(Cart.this, ProfileActivity.class));
    }

    public void checkout(View view) {
        Toast.makeText(Cart.this, "Checkout Option :Coming Soon", Toast.LENGTH_LONG).show();
    }



    public void Notifications(View view) {
        startActivity(new Intent(Cart.this, NotificationActivity.class));
    }
}