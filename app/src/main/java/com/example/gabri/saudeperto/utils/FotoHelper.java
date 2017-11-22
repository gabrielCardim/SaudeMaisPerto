package com.example.gabri.saudeperto.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FotoHelper {

    public static void setFotoUsuario(final Context ctx, final ImageView imageView, FirebaseStorage storage, FirebaseAuth auth) {
        FirebaseUser usuario = auth.getCurrentUser();
        if (usuario == null) return;
        Uri fotoUri = usuario.getPhotoUrl();
        if (fotoUri == null) return;

        String uuid = usuario.getUid();
        final File foto = new File(ctx.getFilesDir(), uuid + ".jpg");

        if (foto.exists()) {
            if (imageView == null) return;
            Bitmap bitmap = BitmapFactory.decodeFile(foto.getPath());
            Drawable imagemCircular = imagemCircular(ctx.getResources(), bitmap);
            imageView.setImageDrawable(imagemCircular);
        } else {
            StorageReference httpsReference = storage.getReferenceFromUrl(fotoUri.toString());
            final long ONE_MEGABYTE = 1024 * 1024;
            httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if (imageView != null) {
                    Drawable imagemCircular = imagemCircular(ctx.getResources(), bitmap);
                    imageView.setImageDrawable(imagemCircular);
                }

                try {
                    FileOutputStream outStream = new FileOutputStream(foto);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static Drawable imagemCircular(Resources res, int src) {
        Bitmap bmp = BitmapFactory.decodeResource(res, src);
        return imagemCircular(res, bmp);
    }

    public static Drawable imagemCircular(Resources res, int src, int inSampleSize) {
        BitmapFactory.Options opcoes = new BitmapFactory.Options();
        opcoes.inSampleSize = inSampleSize;
        Bitmap bmp = BitmapFactory.decodeResource(res, src, opcoes);
        return imagemCircular(res, bmp);
    }

    public static Drawable imagemCircular(Resources res, Bitmap bmp) {
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(res, bmp);
        drawable.setCircular(true);
        return drawable;
    }

    public static byte[] imageViewToByteArray(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}
