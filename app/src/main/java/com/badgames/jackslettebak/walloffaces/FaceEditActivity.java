package com.badgames.jackslettebak.walloffaces;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.badgames.jackslettebak.game.game.utilities.GameContext;
import com.badgames.jackslettebak.image.image.views.ColorPicker;
import com.badgames.jackslettebak.image.image.views.ColorPicker.Colors;
import com.badgames.jackslettebak.image.EditContext;
import com.badgames.jackslettebak.image.image.views.FaceCropView;
import com.badgames.jackslettebak.image.image.views.FaceEditView;
import com.badgames.jackslettebak.utilities.ImageBuilder;

/**
 * Created by Jack Slettebak on 11/5/2017.
 */

public class FaceEditActivity extends Activity
    implements ColorPicker.ColorPickerCallback {

    private Button crop, save;
    private ColorPicker backgroundPicker, borderPicker;
    private FaceCropView cropView;
    private FaceEditView editView;
    private ImageButton background, border;
    private LinearLayout editLayout;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.crop_view_layout );

        loadCropView();
    }

    private void loadCropView() {
        crop = ( Button ) findViewById( R.id.face_crop_button );
        crop.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( View view ) {
                loadEditView();
            }

        } );

        editLayout = ( LinearLayout ) findViewById( R.id.crop_image_layout );
        editLayout.addView(
                cropView = new FaceCropView(
                        this,
                        EditContext.IMAGE_TO_EDIT,
                        ImageBuilder.buildBorderImage(
                                Colors.DARK_GRAY,
                                new PointF( GameContext.BLOCK_WIDTH, GameContext.BLOCK_HEIGHT ),
                                new PointF(
                                        GameContext.SCREEN_DENSITY * ImageBuilder.BORDER_THICKNESS,
                                        GameContext.SCREEN_DENSITY * ImageBuilder.BORDER_THICKNESS
                                )
                        )
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

    private void loadEditView() {
        setContentView( R.layout.edit_view_layout );

        background = ( ImageButton ) findViewById( R.id.background_color_button );
        background.setOnClickListener( new BackgroundOnClickListener() );

        border = ( ImageButton ) findViewById( R.id.border_color_button );
        border.setOnClickListener( new BorderOnClickListener() );

        editLayout = ( LinearLayout ) findViewById( R.id.edit_picture_layout );
        editLayout.addView(
                editView = new FaceEditView(
                        getApplicationContext(),
                        cropView.getCroppedImage()
                )
        );
        editView.attachSeekBar( ( SeekBar ) findViewById( R.id.eraser_size ) );

        save = ( Button ) findViewById( R.id.save_image );
        save.setOnClickListener( new SaveOnClickListener() );
    }

    public void setColorFromPicker( Colors color ) {
        if( backgroundPicker != null ) {
            Bitmap image = ImageBuilder.buildBackgroundImage(
                    color,
                    new PointF( GameContext.BLOCK_WIDTH, GameContext.BLOCK_HEIGHT )
            );

            editView.setBackgroundImage( image );
            background.setImageDrawable( new BitmapDrawable( getResources(), image ) );
        }

        if( borderPicker != null ) {
            Bitmap image = ImageBuilder.buildBorderImage(
                    color,
                    new PointF( GameContext.BLOCK_WIDTH, GameContext.BLOCK_HEIGHT ),
                    new PointF(
                            GameContext.SCREEN_DENSITY * ImageBuilder.BORDER_THICKNESS,
                            GameContext.SCREEN_DENSITY * ImageBuilder.BORDER_THICKNESS
                    )
            );

            editView.setBorderImage( image );
            border.setImageDrawable( new BitmapDrawable( getResources(), image ) );
        }

        backgroundPicker = borderPicker = null;
    }


    // event listeners
    private class BackgroundOnClickListener implements  View.OnClickListener {

        @Override
        public void onClick( View view ) {
            backgroundPicker = new ColorPicker();
            backgroundPicker.show( getFragmentManager(), "color_picker" );
        }

    }

    private class BorderOnClickListener implements  View.OnClickListener {

        @Override
        public void onClick( View view ) {
            borderPicker = new ColorPicker();
            borderPicker.show( getFragmentManager(), "color_picker" );
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

    private class SaveOnClickListener implements View.OnClickListener {

        @Override
        public void onClick( View view ) {
            Intent result = new Intent();
            EditContext.EDITTED_IMAGE = editView.getEdittedImage();
            setResult( RESULT_OK, result );
            finish();
        }

    }

}
