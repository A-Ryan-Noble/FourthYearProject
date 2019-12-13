package com.example.a2in1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;

public class LoadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView imgView;

    public LoadImage(ImageView view) {
        this.imgView = view;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlLink = strings[0];
        Bitmap bitmap = null;

        try {
            InputStream input = new java.net.URL(urlLink).openStream();
            bitmap = BitmapFactory.decodeStream(input);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imgView.setImageBitmap(bitmap);
    }
}