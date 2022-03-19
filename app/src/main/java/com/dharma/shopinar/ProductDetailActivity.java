package com.dharma.shopinar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dharma.shopinar.models.CartModel;
import com.dharma.shopinar.models.Item;
import com.dharma.shopinar.models.WishlistModel;
import com.dharma.shopinar.usersession.UserSession;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.ArCoreApk;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ProductDetailActivity extends AppCompatActivity {
    private static final String TAG = "ProductDetailActivity";
    TextView product_details_name, product_details_price, product_details_cutted_price, product_details_description;
    SliderLayout product_detail_image;
    CollapsingToolbarLayout toolbar;
    FloatingActionButton addToCart,addToWishlist;
    ElegantNumberButton picker;
    private static GoogleApiClient mGoogleApiClient;
//    internetConnectivity it;
    private static Context c;
    String itemId = "";
    FirebaseFirestore db;
    DocumentReference itemRef;
    TextView productArViewer;
    Item item;
    UserSession userSession;
    Boolean isInWishlist = false ,izInCart = false;

    private FirebaseAnalytics mFirebaseAnalytics;
//    @BindView(R.id.toolbar)
    public Toolbar toolBar;
    String phone_no="";
    String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_viewer);

//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //create new session object by passing application context
        userSession = new UserSession(getApplicationContext());

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String path = intent.getStringExtra("path");
        mPath = path;
        itemRef = db.document(path);

        product_details_name = findViewById(R.id.product_name);
        product_details_price = findViewById(R.id.product_price);
        product_details_cutted_price = findViewById(R.id.product_cutted_price);
        product_details_description = findViewById(R.id.product_description);
        product_detail_image = findViewById(R.id.product_image_slider);
        addToCart = findViewById(R.id.cart_btn);
        addToWishlist = findViewById(R.id.wishlist_btn);
        picker = findViewById(R.id.quantity_picker);
        setSupportActionBar(toolBar);


        getWhishlistStatus();
        getCartStatus();
//        updateWishlistIcon();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        productArViewer = findViewById(R.id.product_ar_viewer);
        IntentFilter in = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        it = new internetConnectivity();
//        registerReceiver(it, in);

        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                item = task.getResult().toObject(Item.class);
                inflateImageSlider(item);

                product_details_name.setText(item.getName());
                product_details_price.setText("₹ " + item.getPrice());
                product_details_cutted_price.setText("₹ " + item.getCuttedPrice());
                product_details_cutted_price.setPaintFlags(product_details_cutted_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                product_details_description.setText(item.getDescription());
//                if (item.getIsAR() && maybeEnableArButton()) {
                if (item.getIsAR() && maybeEnableArButton()) {
                    productArViewer.setVisibility(View.VISIBLE);
                    productArViewer.setEnabled(true);
                } else {
                    productArViewer.setVisibility(View.INVISIBLE);
                    if (item.getIsAR()) {
                        productArViewer.setVisibility(View.VISIBLE);
                        productArViewer.setEnabled(false);
                        productArViewer.setText("AR Not Supported");
                        productArViewer.setTextSize(12f);
                    }
                }
                picker.setRange(1, item.getQuantity());

                addToWishlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateWishlistIcon();
                        addToWishlist();
                    }
                });

//                toolbar.setTitle(item.getName());
            }
        });

    }

    private void updateWishlistIcon() {
        if(isInWishlist) {
            addToWishlist.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart_filled));
//            Toast.makeText(ProductDetailActivity.this, "Item Added to Wishlist",
//                    Toast.LENGTH_SHORT).show();
        }
        else {
            addToWishlist.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.heart_empty));
//            Toast.makeText(ProductDetailActivity.this, "Item removed from Wishlist",
//                    Toast.LENGTH_SHORT).show();
        }

    }

    private void updateCartIcon() {
        if(izInCart) {
            addToCart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cart_on));

//            addToCart.setBackgroundColor(I(R.color.md_deep_orange_300));
//            Toast.makeText(ProductDetailActivity.this, "Item Added to Wishlist",
//                    Toast.LENGTH_SHORT).show();
        }
        else {
            addToCart.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.cart_off));

//            addToCart.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(),R.color.white);
//            Toast.makeText(ProductDetailActivity.this, "Item removed from Wishlist",
//                    Toast.LENGTH_SHORT).show();
        }

    }

    private void addToWishlist() {
        char dl = '/';
        Vector<String> path = splitStrings(mPath,dl);
        String productType = path.get(0);
        String productId = path.get(1);

        isInWishlist = !isInWishlist;

//        Map<String, Object> wishlist = new HashMap<>();
//        wishlist.put("izInWishlist", isInWishlist);
//        wishlist.put("path", mPath);
//        wishlist.put("productType", productType);
//        wishlist.put("productId", productId);





        WishlistModel wishlistModel = new WishlistModel(isInWishlist,mPath,productType,productId);

        DocumentReference wishlistRef = db.collection("wishlist")
                .document(userSession.getUserID())
                .collection(productType)
                .document(productId);
        // we can use remove or delete function to delecte  document which will save some space
        wishlistRef.set(wishlistModel, SetOptions.merge());

        DocumentReference wishlistInUsersRef = db.collection("users")
                .document(userSession.getUserID())
                .collection("wishlist").document(productId);
        wishlistInUsersRef.set(wishlistModel);

        if(isInWishlist) {
            Toast.makeText(ProductDetailActivity.this, "Item Added to Wishlist",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(ProductDetailActivity.this, "Item removed from Wishlist",
                    Toast.LENGTH_SHORT).show();
        }

        updateWishlistIcon();

    }
    static Vector<String> splitStrings(String str, char dl)
    {
        String word = "";
        int num = 0;
        str = str + dl;
        int l = str.length();
        Vector<String> substr_list = new Vector<String>();
        for (int i = 0; i < l; i++)
        {
            if (str.charAt(i) != dl)
            {
                word = word + str.charAt(i);
            }
            else
            {
                if ((int) word.length() != 0)
                {
                    substr_list.add(word);
                }


                word = "";
            }
        }

        return substr_list;
    }

    void getWhishlistStatus(){
        char dl = '/';
        Vector<String> path = splitStrings(mPath,dl);
        String productType = path.get(0);
        String productId = path.get(1);

        DocumentReference wishlistRef = db.collection("wishlist")
                .document(userSession.getUserID())
                .collection(productType)
                .document(productId);

        wishlistRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Wishlist status " + document.getBoolean("izInWishlist"));
                        isInWishlist = document.getBoolean("izInWishlist");
                    } else {
                        isInWishlist = false;
                        Log.d(TAG, "No such document");
                    }
                } else {
                    isInWishlist = false;

                    Log.d(TAG, "get failed with ", task.getException());
                }

                updateWishlistIcon();
            }
        });

    }

    void getCartStatus(){
        char dl = '/';
        Vector<String> path = splitStrings(mPath,dl);
        String productType = path.get(0);
        String productId = path.get(1);

        DocumentReference wishlistRef = db.collection("cart")
                .document(userSession.getUserID())
                .collection(productType)
                .document(productId);

        wishlistRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Cart status " + document.getBoolean("izInCart"));
                        izInCart = document.getBoolean("izInCart");
                    } else {
                        izInCart = false;
                        Log.d(TAG, "No such document");
                    }
                } else {
                    izInCart = false;

                    Log.d(TAG, "get failed with ", task.getException());
                }

                updateCartIcon();
            }
        });

    }


    private void inflateImageSlider(Item item) {

        product_detail_image = findViewById(R.id.product_image_slider);

        ArrayList<String> listUrl = new ArrayList<>();

        int imageListSize = item.getImages().size();

        for(int i = 0;  i < imageListSize ; i++)
        {
            listUrl.add(item.getImages().get(i));
        }


//        RequestOptions requestOptions = new RequestOptions();
//        requestOptions.centerCrop();
        //.diskCacheStrategy(DiskCacheStrategy.NONE)
        //.placeholder(R.drawable.placeholder)
        //.error(R.drawable.placeholder);

        for (int i = 0; i < listUrl.size(); i++) {
            TextSliderView sliderView = new TextSliderView(this);
            // if you want show image only / without description text use DefaultSliderView instead

            // initialize SliderLayout
            sliderView
                    .image(listUrl.get(i))
//                    .setRequestOption(requestOptions)
                    .setProgressBarVisible(true);
//                    .setOnSliderClickListener(this);

            //add your extra information
            sliderView.bundle(new Bundle());
//            sliderView.getBundle().putString("extra", listName.get(i));
            product_detail_image.addSlider(sliderView);
        }

        // set Slider Transition Animation
//         mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);
        product_detail_image.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
        product_detail_image.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        product_detail_image.setCustomAnimation(new DescriptionAnimation());
        product_detail_image.setDuration(4000);
//        product_detail_image.addOnPageChangeListener(this);
    }


    public void Notifications(View view) {
//        startActivity(new Intent(ProfileActivity.this,NotificationActivity.class));
        startActivity(new Intent(ProductDetailActivity.this, NotificationActivity.class));
        finish();
    }

    public void viewCart(View view) {
//        startActivity(new Intent(ProfileActivity.this,NotificationActivity.class));
        startActivity(new Intent(ProductDetailActivity.this, Cart.class));
        finish();
    }

    boolean maybeEnableArButton() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Re-query at 5Hz while compatibility is checked in the background.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    maybeEnableArButton();
                }
            }, 200);
        }
        if (availability.isSupported()) {
            return true;
        }
        return false;
    }

    public void addToCart(View view) {

        izInCart = !izInCart;
        int quantity = Integer.parseInt(picker.getNumber()) ;

        if(izInCart){
            itemRef.update("quantity", item.getQuantity() - quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    try {
//                    new Database((getBaseContext()))
//                            .addToCart(new order(common.currentUser.getId(),
//                                    itemRef.getId(),
//                                    item.getName(),
//                                    item.getPrice(),
//                                    Integer.parseInt(picker.getNumber())));

                        // tpdo add cart value to firestore

                        char dl = '/';
                        Vector<String> path = splitStrings(mPath,dl);
                        String productType = path.get(0);
                        String productId = path.get(1);

                        CartModel cartModel = new CartModel(izInCart,mPath,productType,productId, quantity);

                        DocumentReference cartRef = db.collection("cart")
                                .document(userSession.getUserID())
                                .collection(productType)
                                .document(productId);
                        // we can use remove or delete function to delecte  document which will save some space
                        cartRef.set(cartModel, SetOptions.merge());

                        DocumentReference cartInUsersRef = db.collection("users")
                                .document(userSession.getUserID())
                                .collection("cart").document(productId);
                        cartInUsersRef.set(cartModel);

                        updateCartIcon();


                        Toast.makeText(ProductDetailActivity.this, "Item Added to Cart",
                                Toast.LENGTH_SHORT).show();
                        picker.setRange(1, item.getQuantity() - Integer.parseInt(picker.getNumber()));
                    } catch (Exception e) {
                        itemRef.update("quantity", item.getQuantity() + Integer.parseInt(picker.getNumber()));
                        Toast.makeText(ProductDetailActivity.this, "Something went Wrong while adding cart",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            itemRef.update("quantity", item.getQuantity() + quantity).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    try {
//                    new Database((getBaseContext()))
//                            .addToCart(new order(common.currentUser.getId(),
//                                    itemRef.getId(),
//                                    item.getName(),
//                                    item.getPrice(),
//                                    Integer.parseInt(picker.getNumber())));

                        // tpdo add cart value to firestore

                        char dl = '/';
                        Vector<String> path = splitStrings(mPath,dl);
                        String productType = path.get(0);
                        String productId = path.get(1);

                        CartModel cartModel = new CartModel(izInCart,mPath,productType,productId,0);

                        DocumentReference cartRef = db.collection("cart")
                                .document(userSession.getUserID())
                                .collection(productType)
                                .document(productId);
                        // we can use remove or delete function to delecte  document which will save some space
                        cartRef.set(cartModel, SetOptions.merge());

                        DocumentReference cartInUsersRef = db.collection("users")
                                .document(userSession.getUserID())
                                .collection("cart").document(productId);
                        cartInUsersRef.set(cartModel);

                        updateCartIcon();


                        Toast.makeText(ProductDetailActivity.this, "Item Removed from Cart",
                                Toast.LENGTH_SHORT).show();
                        picker.setRange(1, item.getQuantity() - Integer.parseInt(picker.getNumber()));
                    } catch (Exception e) {
                        itemRef.update("quantity", item.getQuantity() - Integer.parseInt(picker.getNumber()));
                        Toast.makeText(ProductDetailActivity.this, "Something went Wrong while removing cart",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


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

    public void viewInAR(View view) {
        productArViewer.setEnabled(true);
//        String modelURL;
//        Bundle bundle = new Bundle();
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
//        String id = itemRef.getId();
//        String name = item.getName();
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name + ": View in AR");
//        String asset = item.getImages().get(1);
//        String asset = item.getImages().get(0);
//        modelURL = item.getModelURL();
//        Intent intent = new Intent(getApplicationContext(), ARactivity.class);
//        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
//        intent.putExtra("modelURL", modelURL);
        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
        startActivity(intent);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//        startActivity(new Intent(ProductDetailActivity.this, MainActivity.class));
//    }

}






