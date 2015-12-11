package com.codemolly.drawing;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.ExtractEditText;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mrand on 10/5/15.
 */
public class TextListenerService extends WearableListenerService {

    private GoogleApiClient mGoogleApiClient;
    public static final String MESSAGE_PATH = "/com.codemolly.drawing/image";


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String path = messageEvent.getPath();
        if (path.equalsIgnoreCase(MESSAGE_PATH)) {
            byte[] data = messageEvent.getData();
            Bitmap doodle = BitmapFactory.decodeByteArray(data, 0, data.length);
            String doopath = saveBitmap(doodle);
            if (doopath != null) {
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra("mms_body", "I doodled this on my watch!");
                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + doopath));
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sendIntent.setType("image/png");
                startActivity(sendIntent);
            }

        }
    }

    private String saveBitmap(Bitmap bitmap) {
        if (Environment.getExternalStorageState().equalsIgnoreCase("mounted")) {
            File imageFolder = new File(Environment.getExternalStorageDirectory(), "masterpieces");
            imageFolder.mkdirs();
            FileOutputStream out = null;
            File imageFile = new File(imageFolder, String.valueOf(System.currentTimeMillis()) + ".png");
            try {
                out = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e("Molly", "Error saving" + e.getLocalizedMessage());
            } finally {
                out = null;
            }
            return imageFile.getAbsolutePath();
        }
        return null;
    }
}
