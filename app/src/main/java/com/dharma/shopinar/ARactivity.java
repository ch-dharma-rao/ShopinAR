package com.dharma.shopinar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class ARactivity extends AppCompatActivity {


    private ProgressBar progressBar,buildCircularProgress;
    private TextView textProgress,textBuildProgress;
    private FloatingActionButton downloadButton,deleteButton;
    private boolean isDeleteOn = false;
    private String modelURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_activity);

        Intent intent = getIntent();

        modelURL = intent.getStringExtra("modelURL");

        FirebaseApp.initializeApp(this);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference modelRef1 = storage.getReference().child("out.glb");

        progressBar = findViewById(R.id.downloadProgress);
        buildCircularProgress = findViewById(R.id.buildProgress);
        textProgress = findViewById(R.id.textProgress);
        textBuildProgress = findViewById(R.id.textBuildProgress);
        downloadButton = findViewById(R.id.downloadBtn);
        deleteButton = findViewById(R.id.deleteBtn);

        progressBar.setVisibility(View.GONE);
        textProgress.setVisibility(View.GONE);
        buildCircularProgress.setVisibility(View.GONE);
        textBuildProgress.setVisibility(View.GONE);



        downloadButton.show();

        ArFragment arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.arFragment);


        findViewById(R.id.deleteBtn)
                .setOnClickListener(v -> {
                    isDeleteOn = !isDeleteOn;
                    Context context =getApplicationContext();
                    if(isDeleteOn){
                        deleteButton.setImageDrawable(ContextCompat.getDrawable( context,R.drawable.delete_on));
                            Toast.makeText(this, "Delete Mode On", Toast.LENGTH_SHORT).show();

                    }else{
                        deleteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.delete_off));
                        Toast.makeText(this, "Delete Mode Off", Toast.LENGTH_SHORT).show();


                    }
                        });

        findViewById(R.id.downloadBtn)
                .setOnClickListener(v -> {

                   downloadModels(storage);

                });





        downloadModels(storage);


        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {

//            AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
//            anchorNode.setRenderable(renderable);
//            arFragment.getArSceneView().getScene().addChild(anchorNode);

            AnchorNode anchorNode = new AnchorNode(hitResult.createAnchor());
            TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());

            transformableNode.getScaleController().setMaxScale(1.2f);
            transformableNode.getScaleController().setMinScale(0.9f);
            transformableNode.setParent(anchorNode);
            transformableNode.setRenderable(renderable);
            arFragment.getArSceneView().getScene().addChild(anchorNode);
            transformableNode.select();
            transformableNode.setOnTapListener((hitTestResult,  Event) ->
            {
                if(isDeleteOn)
                {
                    Node nodeToRemove = hitTestResult.getNode();
                    anchorNode.removeChild(nodeToRemove );
                }

            });

        });
    }

    private ModelRenderable renderable;


    void downloadModels(FirebaseStorage storage){
        try {
            File file = File.createTempFile("out2", "glb");
            StorageReference modelRef = storage.getReference().child("out2.glb");



            progressBar.setVisibility(View.VISIBLE);
            textProgress.setVisibility(View.VISIBLE);
            downloadButton.hide();

            modelRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    downloadButton.show();
                    progressBar.setVisibility(View.GONE);
                    textProgress.setVisibility(View.GONE);
                    textBuildProgress.setVisibility(View.VISIBLE);
                    buildCircularProgress.setVisibility(View.VISIBLE);
                    buildModel(file);
                }


            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                    double progress = (100*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                    textProgress.setText(progress + " %");
                }
            });

        } catch (IOException e) {
            downloadButton.show();
            progressBar.setVisibility(View.GONE);
            textProgress.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void buildModel(File file) {

        RenderableSource renderableSource = RenderableSource
                .builder()
                .setSource(this, Uri.parse(modelURL), RenderableSource.SourceType.GLB)
                .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                .build();

        ModelRenderable
                .builder()
                .setSource(this, renderableSource)
                .setRegistryId(file.getPath())
                .build()
                .thenAccept(modelRenderable -> {
                    textBuildProgress.setVisibility(View.GONE);
                    buildCircularProgress.setVisibility(View.GONE);

                    Toast.makeText(this, "Build Completed", Toast.LENGTH_SHORT).show();;
                    renderable = modelRenderable;
                });

    }

}