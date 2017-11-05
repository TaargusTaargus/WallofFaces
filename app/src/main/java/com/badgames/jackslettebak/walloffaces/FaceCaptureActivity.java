package com.badgames.jackslettebak.walloffaces;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.badgames.jackslettebak.image.FaceCropView;
import com.badgames.jackslettebak.image.FaceTransparentView;
import com.badgames.jackslettebak.utilities.Globals;
import com.badgames.jackslettebak.utilities.Globals.Background;
import com.badgames.jackslettebak.utilities.Utilities;

import java.io.IOException;
import java.io.InputStream;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

/**
 * Created by Jack Slettebak on 10/23/2017.
 */

public class FaceCaptureActivity extends AppCompatActivity {

    public enum Cropper {
        RECTANGLE( R.drawable.rectangle_selector );

        Integer drawable;

        Cropper( Integer drawable ) {
            this.drawable = drawable;
        }

        public Bitmap getBitmap( Context context ) {
            return BitmapFactory.decodeResource( context.getResources(), drawable );
        }
    }

    final String URI_CAMERA_AUTHORITY = "com.badgames.wof";
    final int CAMERA_CAPTURE_CODE = 100;
    final int BROWSE_PICTURES_CODE = 101;

    private Background [] availableBackgrounds = Background.values();
    private Button browse, camera, crop, save;
    private Cropper cropBorder;
    private FaceCropView cropper;
    private FaceTransparentView transparentView;
    private ImageView selectedBackground;
    private LinearLayout editLayout, backgroundLayout;
    private Uri picUri;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_face_capture );

        browse = ( Button ) findViewById( R.id.face_capture_browse );
        browse.setOnClickListener( new BrowseOnClickListener() );

        camera = ( Button ) findViewById( R.id.face_capture_camera );
        camera.setOnClickListener( new CameraOnClickListener() );
    }

    @Override
    protected void onActivityResult( int req, int res, Intent data ) {
        if( res != RESULT_OK ) {
            String errorMessage = "Encountered an error in retrieving image -- try again!";
            Toast toast = Toast.makeText( getApplicationContext(), errorMessage, Toast.LENGTH_SHORT );
            toast.show();
        }
        else {
            setContentView( R.layout.crop_image_layout );
            editLayout = ( LinearLayout ) findViewById( R.id.crop_image_layout );
            crop = ( Button ) findViewById( R.id.face_crop_button );
            cropBorder = Cropper.RECTANGLE;
            switch( req ) {
                case BROWSE_PICTURES_CODE:
                    picUri = data.getData();
                case CAMERA_CAPTURE_CODE:
                    loadImage();
                    break;
            }
            crop.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick( View view ) {
                    loadTransparentLayout();
                }
            } );
        }
    }

    private void loadImage() {
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
            editLayout.addView( cropper = new FaceCropView( this, bitmap, cropBorder.getBitmap( this ) ),
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
        crop.setVisibility( View.GONE );
        editLayout.removeAllViews();
        setContentView( R.layout.transparent_image_layout );


        Background start = availableBackgrounds[ 0 ];

        editLayout = ( LinearLayout ) findViewById( R.id.edit_picture_layout );
        editLayout.addView(
                transparentView = new FaceTransparentView(
                        getApplicationContext(),
                        cropper.getCroppedBitmap(),
                        start.getBitmap( getApplicationContext() )
                )
        );
        transparentView.attachSeekBar( ( SeekBar ) findViewById( R.id.eraser_size ) );
        transparentView.requestDraw();

        backgroundLayout = ( LinearLayout ) findViewById( R.id.background_options );
        for( Background background : availableBackgrounds ) {
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

    private class BackgroundOnClickListener implements  View.OnClickListener {

        private Background background;
        private ImageView imageView;

        public BackgroundOnClickListener( Background background, ImageView imageView ) {
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

    private class BrowseOnClickListener implements View.OnClickListener {

        @Override
        public void onClick( View view ) {
            Intent intent = new Intent();
            intent.setType( "image/*" );
            intent.setAction( Intent.ACTION_GET_CONTENT );
            startActivityForResult( Intent.createChooser( intent, "Select Picture" ), BROWSE_PICTURES_CODE );
        }

    }

    private class CameraOnClickListener implements View.OnClickListener {

        @Override
        public void onClick( View view ) {
            try {
                //use standard intent to capture an image
                Intent captureIntent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                //captureIntent.setData( picUri );
                captureIntent.addFlags( FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION );
                //we will handle the returned data in onActivityResult
                try {
                    captureIntent.putExtra( MediaStore.EXTRA_OUTPUT, picUri = FileProvider.getUriForFile(
                            getApplicationContext(),
                            URI_CAMERA_AUTHORITY,
                            Utilities.createImageFile( getApplicationContext() )
                    ) );
                } catch ( IOException e ) {
                    Log.d( "IOException", "Could not open file: " + e.getLocalizedMessage() );
                }
                startActivityForResult( captureIntent, CAMERA_CAPTURE_CODE );
            }
            catch( ActivityNotFoundException anfe ){
                //display an error message
                String errorMessage = "Whoops - your device doesn't support capturing images!";
                Toast toast = Toast.makeText( getApplicationContext(), errorMessage, Toast.LENGTH_SHORT );
                toast.show();
            }
        }

    }

    private class SaveOnClickListener implements View.OnClickListener {

        @Override
        public void onClick( View view ) {
            ImageView tmp = new ImageView( getApplicationContext() );
            tmp.setImageBitmap( transparentView.getFinalImage() );
            addContentView(tmp, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            setContentView( tmp );
        }

    }

}
