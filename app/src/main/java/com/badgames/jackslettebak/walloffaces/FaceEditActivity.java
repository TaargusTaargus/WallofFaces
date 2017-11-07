package com.badgames.jackslettebak.walloffaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.badgames.jackslettebak.image.EditContext;
import com.badgames.jackslettebak.image.FaceCropView;
import com.badgames.jackslettebak.image.FaceEditView;
import com.badgames.jackslettebak.utilities.Globals;
import com.badgames.jackslettebak.utilities.Globals.Group;

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

        public Bitmap getBitmap( Context context ) {
            return BitmapFactory.decodeResource( context.getResources(), drawable );
        }
    }


    private Group[] availableBackgrounds = Group.values();
    private Button crop, save;
    private Cropper cropBorder = Cropper.RECTANGLE;
    private FaceCropView cropView;
    private FaceEditView editView;
    private ImageView selectedBackground;
    private LinearLayout editLayout, backgroundLayout;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.crop_view_layout);


        loadCropLayout();
    }

    private void loadCropLayout() {
        crop = ( Button ) findViewById( R.id.face_crop_button );
        crop.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {
                loadTransparentLayout();
            }

        } );

        editLayout = ( LinearLayout ) findViewById( R.id.crop_image_layout );
        editLayout.addView(
                cropView = new FaceCropView(
                        this,
                        EditContext.IMAGE_TO_EDIT,
                        cropBorder.getBitmap( this )
                ),
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                )
        );

        ( ( Button ) findViewById( R.id.increase_crop_size_button ) )
                .setOnClickListener( new CropAdjustClickListener( 1 ) );

        ( ( Button ) findViewById( R.id.decrease_crop_size_button ) )
                .setOnClickListener( new CropAdjustClickListener( -1 ) );
    }

    private void loadTransparentLayout () {
        setContentView( R.layout.edit_view_layout);
        Group start = availableBackgrounds[ 0 ];
        editLayout = ( LinearLayout ) findViewById( R.id.edit_picture_layout );
        editLayout.addView(
                editView = new FaceEditView(
                        getApplicationContext(),
                        cropView.getCroppedBitmap(),
                        start.getBitmap( getApplicationContext() )
                )
        );
        editView.attachSeekBar( ( SeekBar ) findViewById( R.id.eraser_size ) );
        editView.requestDraw();

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
            EditContext.EDITTED_IMAGE = editView.getFinalImage();
            setResult( RESULT_OK, result );
            finish();
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
            editView.setBackgroundImage( background.getBitmap( getApplicationContext() ) );
            editView.requestDraw();
        }

    }

    private class CropAdjustClickListener implements View.OnClickListener {

        private Integer delta;

        public CropAdjustClickListener( Integer delta ) {
            this.delta = delta;
        }

        @Override
        public void onClick( View view ) {
            cropView.adjustCropSize( delta );
        }

    }

}
