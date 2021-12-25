package com.example.arplanesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity {

    ArFragment arFragment;
    ModelRenderable skullRenerable;

    String arModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arModel = Uri.parse("android.resource://"
                + getApplicationContext().getPackageName()
                + "/raw/skull_model").toString();
        loadModel();

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_ux_fragment);

        if (arFragment != null) {
            arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());

                createModel(anchorNode);
            });
        }
    }

    private void loadModel() {
        try {
            ModelRenderable.builder()
                    .setSource(this,
                            RenderableSource.builder().setSource(
                                    this,
                                    Uri.parse(arModel),
                                    RenderableSource.SourceType.GLB)/*RenderableSource.SourceType.GLTF2)*/
                                    .setScale(0.01f)
                                    .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                                    .build())
                    .setRegistryId(arModel)
                    .build()
                    .thenAccept(renderable ->
                            skullRenerable = renderable
                    )
                    .exceptionally(throwable -> {
                        Log.e("Model", "can't load");
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createModel(AnchorNode anchorNode) {
        TransformableNode model = new TransformableNode(arFragment.getTransformationSystem());
        model.setParent(anchorNode);
        model.setRenderable(skullRenerable);
        model.select();


    }
}