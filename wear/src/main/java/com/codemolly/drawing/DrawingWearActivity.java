package com.codemolly.drawing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayOutputStream;
import java.util.Set;


public class DrawingWearActivity extends Activity implements GoogleApiClient.OnConnectionFailedListener{
    private WatchDrawingView mDrawing;
    private static final int REQUEST_CODE_OPTIONS = 888;
    public static final String MESSAGE_PATH = "/com.codemolly.drawing/image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_wear);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mDrawing = (WatchDrawingView) stub.findViewById(R.id.watch_drawing);
                ImageView optionsButton = (ImageView) stub.findViewById(R.id.show_options);
                optionsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showOptions();
                    }
                });
            }
        });
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApiClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_OPTIONS) {
            if (resultCode == OptionsActivity.RESULT_CODE_CLEAR) {
                clearDrawing();
            } else if (resultCode == OptionsActivity.RESULT_CODE_SHARE) {
                sendDrawingToMoxy();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showOptions() {
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivityForResult(intent, REQUEST_CODE_OPTIONS);
    }

    public void sendDrawingToMoxy() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                shareBitmap();
                return null;
            }
        }.execute();

    }

    private void shareBitmap() {
        Bitmap doodle = mDrawing.getBitmap();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        doodle.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        byte[] byteArray = outStream.toByteArray();
        if (getNode() != null) {
            MessageApi.SendMessageResult sendResult = Wearable.MessageApi.sendMessage(mApiClient, getNode(), MESSAGE_PATH, byteArray).await();
            if (!sendResult.getStatus().isSuccess()) {
                Toast.makeText(this, "Failed to send", Toast.LENGTH_LONG).show();
            }
        }
    }

    private GoogleApiClient mApiClient;

    private String getNode() {
        CapabilityApi.GetCapabilityResult result = Wearable.CapabilityApi.getCapability(mApiClient, "send_text", CapabilityApi.FILTER_REACHABLE).await();

        CapabilityInfo info = result.getCapability();
        Set<Node> connectedNodes = info.getNodes();
        for (Node node : connectedNodes) {
            if (node.isNearby()) {
                return  node.getId();
            }
        }
        return null;
    }

    private void clearDrawing() {
        mDrawing.erase();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_LONG).show();
    }
}
