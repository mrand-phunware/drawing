package com.codemolly.drawing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.codemolly.drawing.view.DrawingView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FreeDraw extends AppCompatActivity {
    private final static int REQUEST_CHOOSE_IMAGE = 878;

    private DrawingView mView;
    private ImageView mBackground;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_draw);
        mView = (DrawingView)findViewById(R.id.main_view);
        mBackground = (ImageView)findViewById(R.id.background);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawer = (RelativeLayout)findViewById(R.id.drawerframe);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.open,
                R.string.close
        );
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        findViewById(R.id.delete_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.erase();
            }
        });
    }

    private void changeBackground() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();

                mBackground.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mBackground.setImageBitmap(scaleDownBitmap(selectedImage, mBackground.getHeight(), mBackground.getWidth()));

            } else {
                Toast.makeText(this, "No image chosen", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error encountered: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private Bitmap scaleDownBitmap(Uri imageUri, int goalHeight, int goalWidth) {
        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);
        try {
            imageStream = getContentResolver().openInputStream(imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int inSampleSize = 1;

        if (imageHeight > goalHeight || imageWidth > goalWidth) {
            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > goalHeight
                    && (halfWidth / inSampleSize) > goalWidth) {
                inSampleSize *= 2;
            }
        }
        options.inDensity = DisplayMetrics.DENSITY_MEDIUM;
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(imageStream, null, options);
    }

    private void saveMasterpiece() {
        View layout = findViewById(R.id.main_layout);
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Bitmap full = layout.getDrawingCache();
        if (Environment.getExternalStorageState().equalsIgnoreCase("mounted")) {
            File imageFolder = new File(Environment.getExternalStorageDirectory(), "masterpieces");
            imageFolder.mkdirs();
            FileOutputStream out = null;
            File imageFile = new File(imageFolder, String.valueOf(System.currentTimeMillis()) + ".png");
            try {
                out = new FileOutputStream(imageFile);
                full.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (IOException e) {
                Log.e("Molly", "Error saving" + e.getLocalizedMessage());
            } finally {
                out = null;
                MediaScannerConnection.scanFile(this, new String[] {imageFile.getAbsolutePath()}, null, null);
            }
        }
        layout.destroyDrawingCache();
        layout.setDrawingCacheEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_free_draw, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.draw_black:
                mView.setDrawingColor(ContextCompat.getColor(this, android.R.color.black));
                break;
            case R.id.draw_blue:
                mView.setDrawingColor(ContextCompat.getColor(this, android.R.color.holo_blue_bright));
                break;
            case R.id.draw_green:
                mView.setDrawingColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
                break;
            case R.id.choose_pic:
                changeBackground();
                break;
            case R.id.action_save:
                saveMasterpiece();
                break;
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(mDrawer)) {
                    mDrawerLayout.closeDrawer(mDrawer);
                } else {
                    mDrawerLayout.openDrawer(mDrawer);
                }
                break;
            default:
                mView.setDrawingColor(ContextCompat.getColor(this, android.R.color.white));
        }
        return true;
    }

}
