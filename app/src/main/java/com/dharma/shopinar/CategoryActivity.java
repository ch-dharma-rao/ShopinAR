package com.dharma.shopinar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.dharma.shopinar.adapters.RecyclerViewAdapter;
import com.dharma.shopinar.models.Item;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView recyclerView ;
    private RecyclerViewAdapter adapter,ARadapter, NonARadapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAnalytics mFirebaseAnalytics;
    FirestoreRecyclerOptions<Item> options;

    String category = "chairs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Bundle bundle = getIntent().getExtras();
        category = bundle.getString("CATEGORY");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = FirebaseFirestore.getInstance();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Query query;
        query = db.collection(category);

        options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();

        adapter = new RecyclerViewAdapter(options);



        initRecyclerView();



    }


    private void initRecyclerView() {
        recyclerView = findViewById(R.id.category_items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        clickListener();

//        checkItemCount();

    }

    private void clickListener(){
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int pos) {
                Bundle bundle = new Bundle();
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                String path = documentSnapshot.getReference().getPath();
                String id = documentSnapshot.getId();
                String name = documentSnapshot.get("name").toString();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                intent.putExtra("path", path);
                startActivity(intent);
            }
        });
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
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    public void viewProfile(View view) {
        startActivity(new Intent(CategoryActivity.this, ProfileActivity.class));
    }

    public void viewCart(View view) {
        startActivity(new Intent(CategoryActivity.this, Cart.class));
    }


    public void Notifications(View view) {
        startActivity(new Intent(CategoryActivity.this, NotificationActivity.class));
    }

}