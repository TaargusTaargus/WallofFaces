package com.badgames.jackslettebak.utilities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jack Slettebak on 10/29/2017.
 */

public class Utilities {

    public static Bitmap rotateImage( Bitmap source, float angle ) {
        Matrix matrix = new Matrix();
        matrix.postRotate( angle );
        return Bitmap.createBitmap( source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true );
    }

    public static InputStream bitmapToInputStream( Bitmap image ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress( Bitmap.CompressFormat.JPEG, 0, bos );
        return new ByteArrayInputStream( bos.toByteArray() );
    }

    public static File createImageFile( Context context ) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date() );
        String imageFileName = "JPEG_" + timeStamp + "_";
        File imagePath = new File( context.getFilesDir(), "images" );
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                imagePath      /* directory */
        );
        return image;
    }

    public static Uri writeImageFile( Context context, Bitmap bitmap ) throws IOException {
        File file = createImageFile( context );
        FileOutputStream fos = context.openFileOutput( file.getName(), Context.MODE_PRIVATE );

        // Writing the bitmap to the output stream
        bitmap.compress( Bitmap.CompressFormat.PNG, 100, fos );
        fos.close();
        return Uri.fromFile( file );
    }

}
