package com.badgames.jackslettebak.walloffaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.badgames.jackslettebak.image.FaceCropView;
import com.badgames.jackslettebak.image.FaceEditView;
import com.badgames.jackslettebak.utilities.Globals;
import com.badgames.jackslettebak.utilities.Globals.Group;
import com.badgames.jackslettebak.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Jack Slettebak on 11/5/2017.
 */

public class FaceEditActivity extends Activity {

    public enum Cropper {
        RECTANGLE( R.drawable.rectangle_selector );

        Integer drawable;

        Cropper( Integer drawable ) {
            this.drawable = drawable;
        }

        public Bitmap getBitmap(Context context ) {
            return BitmapFactory.decodeResource( context.getResources(), drawable );
        }
    }

    public static Bitmap FINAL_IMAGE = null;
    public static final String PICTURE_URI_EXTRA_KEY = "picture";


    private Group[] availableBackgrounds = Group.values();
    private Button crop, save;
    private Cropper cropBorder = Cropper.RECTANGLE;
    private FaceCropView cropper;
    private FaceEditView transparentView;
    private ImageView selectedBackground;
    private LinearLayout editLayout, backgroundLayout;
    private Uri picUri;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.crop_image_layout );

        crop = ( Button ) findViewById( R.id.face_crop_button );
        crop.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {
                loadTransparentLayout();
            }

        } );

        editLayout = ( LinearLayout ) findViewById( R.id.crop_image_layout );
        picUri = getIntent().getData();
        loadCropLayout();
    }

    private void loadCropLayout() {
        try {
            InputStream bitmapStream = getContentResolver().openInputStream( picUri );
            ExifInterface ei = new ExifInterface( bitmapStream );
            int orientation = ei.getAttributeInt( ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED );

            Bitmap bitmap = MediaStore.Images.Media.getBitmap( getContentResolver(), picUri );
            switch ( orientation ) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = Utilities.rotateImage( bitmap, 90.f );
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = Utilities.rotateImage( bitmap, 180.f );
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = Utilities.rotateImage( bitmap, 270.f );
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    bitmap = Utilities.rotateImage( bitmap, 270.f );
            }
            editLayout.addView(
                    cropper = new FaceCropView(
                            this,
                            bitmap,
                            cropBorder.getBitmap( this )
                    ),
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    )
            );
        } catch( IOException e ) {
            Log.d( "IOException", "Was unable to load ExIf data on: " + e.getLocalizedMessage() );
        }
    }

    private void loadTransparentLayout () {
        setContentView( R.layout.transparent_image_layout );
        Group start = availableBackgrounds[ 0 ];
        editLayout = ( LinearLayout ) findViewById( R.id.edit_picture_layout );
        editLayout.addView(
                transparentView = new FaceEditView(
                        getApplicationContext(),
                        cropper.getCroppedBitmap(),
                        start.getBitmap( getApplicationContext() )
                )
        );
        transparentView.attachSeekBar( ( SeekBar ) findViewById( R.id.eraser_size ) );
        transparentView.requestDraw();

        backgroundLayout = ( LinearLayout ) findViewById( R.id.background_options );
        for( Group background : availableBackgrounds ) {
            ImageView imageView = new ImageView( this );
            imageView.setImageDrawable( background.getDrawable( this ) );
            imageView.setOnClickListener( new BackgroundOnClickListener( background, imageView ) );
            imageView.setLayoutParams( new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT ) );
            imageView.setPadding(
                    ( int ) Globals.SCREEN_DENSITY * 10, ( int ) Globals.SCREEN_DENSITY * 10,
                    ( int ) Globals.SCREEN_DENSITY * 10, ( int ) Globals.SCREEN_DENSITY * 10
            );
            backgroundLayout.addView( imageView );
            if( background == start )
                ( selectedBackground = imageView ).setBackgroundColor( Color.GRAY );
        }

        save = ( Button ) findViewById( R.id.save_image );
        save.setOnClickListener( new SaveOnClickListener() );
    }

    private class SaveOnClickListener implements View.OnClickListener {

        @Override
        public void onClick( View view ) {
            Intent result = new Intent();
            FINAL_IMAGE = transparentView.getFinalImage();
            setResult( RESULT_OK, result );
            finish();
            /*
            setContentView( R.layout.image_pack_creation_layout );
            capturedImages = ( ListView ) findViewById( R.id.image_pack_list );
            createdImages.addImage(
                    Bitmap.createScaledBitmap(
                            transparentView.getFinalImage(),
                            Globals.BLOCK_WIDTH,
                            Globals.BLOCK_HEIGHT,
                            false
                    )
            );
            capturedImages.setAdapter(
                    listAdapter = new FaceCaptureActivity.CapturedImagesListAdapter( getApplicationContext(), createdImages )
            );
            */
        }

    }

    private class BackgroundOnClickListener implements  View.OnClickListener {

        private Group background;
        private ImageView imageView;

        public BackgroundOnClickListener( Group background, ImageView imageView ) {
            this.background = background;
            this.imageView = imageView;
        }

        @Override
        public void onClick( View view ) {
            selectedBackground.setBackgroundColor( Color.TRANSPARENT );
            ( selectedBackground = imageView ).setBackgroundColor( Color.GRAY );
            transparentView.setBackgroundImage( background.getBitmap( getApplicationContext() ) );
            transparentView.requestDraw();
        }

    }

}
