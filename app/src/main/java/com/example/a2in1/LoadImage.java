package com.example.a2in1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView imgView;

    String log = getClass().getSimpleName();

    public LoadImage(ImageView view) {
        this.imgView = view;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        String urlLink = strings[0];
        Bitmap bitmap = null;

        try {
            URL url = new URL(urlLink);

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

//            InputStream stream = connection.getInputStream();
//            URL url = new URL(link);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            InputStream stream = conn.getInputStream();

            bitmap = BitmapFactory.decodeStream(stream, null,null);

            return bitmap;
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