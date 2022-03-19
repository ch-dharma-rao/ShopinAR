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

import com.dharma.shopinar.adapters.NotificationRecyclerViewAdapter;
import com.dharma.shopinar.adapters.RecyclerViewAdapter;
import com.dharma.shopinar.models.Item;
import com.dharma.shopinar.models.NotificationModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class NotificationActivity extends AppCompatActivity {

    RecyclerView recyclerView ;
    private NotificationRecyclerViewAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAnalytics mFirebaseAnalytics;
    FirestoreRecyclerOptions<NotificationModel> options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Notification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = FirebaseFirestore.getInstance();



        Query query;
        query = db.collection("notification");

        options = new FirestoreRecyclerOptions.Builder<NotificationModel>()
                .setQuery(query, NotificationModel.class)
                .build();

        adapter = new NotificationRecyclerViewAdapter(options);



        initRecyclerView();
    }



    private void initRecyclerView() {
        recyclerView = findViewById(R.id.notification_items);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        clickListener();

//        checkItemCount();

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
        startActivity(new Intent(NotificationActivity.this, ProfileActivity.class));
    }

    public void viewCart(View view) {
        startActivity(new Intent(NotificationActivity.this, Cart.class));
    }


}