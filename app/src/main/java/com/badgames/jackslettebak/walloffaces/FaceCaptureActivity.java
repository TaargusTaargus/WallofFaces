package com.badgames.jackslettebak.walloffaces;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.badgames.jackslettebak.game.GameContext;
import com.badgames.jackslettebak.image.EditContext;
import com.badgames.jackslettebak.image.ImagePack;
import com.badgames.jackslettebak.utilities.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

/**
 * Created by Jack Slettebak on 10/23/2017.
 */

public class FaceCaptureActivity extends AppCompatActivity {

    final int CAMERA_CAPTURE_CODE = 100;
    final int BROWSE_PICTURES_CODE = 101;
    final int EDIT_PICTURE_CODE = 102;
    final String URI_CAMERA_AUTHORITY = "com.badgames.wof";

    private Button browse, camera, save;
    private CapturedImagesListAdapter listAdapter;
    private ImagePack createdImages = new ImagePack();
    private ListView capturedImages;
    private Uri picUri;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.capture_activity_layout);

        browse = ( Button ) findViewById( R.id.face_capture_browse );
        browse.setOnClickListener( new BrowseOnClickListener() );

        camera = ( Button ) findViewById( R.id.face_capture_camera );
        camera.setOnClickListener( new CameraOnClickListener() );

        capturedImages = ( ListView ) findViewById( R.id.image_pack_list );
        capturedImages.setAdapter(
                listAdapter = new CapturedImagesListAdapter( this, createdImages )
        );

        save = ( Button ) findViewById( R.id.save_image_list_button );
        save.setOnClickListener( new SaveOnClickListener() );
    }

    @Override
    protected void onActivityResult( int req, int res, Intent data ) {
        if( res != RESULT_OK ) {
            String errorMessage = "Encountered an error in retrieving image -- try again!";
            Toast toast = Toast.makeText( getApplicationContext(), errorMessage, Toast.LENGTH_SHORT );
            toast.show();
        }
        else {
            switch( req ) {
                case BROWSE_PICTURES_CODE:
                    picUri = data.getData();
                case CAMERA_CAPTURE_CODE:
                    scanImageToBitmap();
                    Intent editPictureIntent = new Intent( this, FaceEditActivity.class );
                    editPictureIntent.setData( picUri );
                    startActivityForResult( editPictureIntent, EDIT_PICTURE_CODE );
                    break;
                case EDIT_PICTURE_CODE:
                    createdImages.addImage(
                            Bitmap.createScaledBitmap(
                                    EditContext.EDITTED_IMAGE,
                                    Math.max( GameContext.BLOCK_WIDTH, GameContext.BLOCK_HEIGHT ),
                                    Math.max( GameContext.BLOCK_WIDTH, GameContext.BLOCK_HEIGHT ),
                                    false
                            )
                    );
                    capturedImages.setAdapter(
                            listAdapter = new CapturedImagesListAdapter( getApplicationContext(), createdImages )
                    );
                    break;
            }

        }
    }

    private void scanImageToBitmap() {
        try {
            InputStream bitmapStream = getContentResolver().openInputStream( picUri );
            ExifInterface ei = new ExifInterface( bitmapStream );
            int orientation = ei.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
            );

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
            EditContext.IMAGE_TO_EDIT = bitmap;
        } catch( IOException e ) {
            Log.d( "IOException", "Was unable to load ExIf data on: " + e.getLocalizedMessage() );
        }
    }

    // adapters
    private class CapturedImagesListAdapter extends BaseAdapter {

        private Context context;
        private LinkedList< Bitmap > images;

        public CapturedImagesListAdapter( Context context, ImagePack images ) {
            this.context = context;
            this.images = new LinkedList< Bitmap >( images.values() );
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Bitmap getItem( int i ) {
            return images.get( i );
        }

        @Override
        public long getItemId( int i ) {
            return i;
        }

        @Override
        public View getView( int i, View view, ViewGroup viewGroup ) {
            RelativeLayout layout = ( RelativeLayout ) LayoutInflater
                                        .from( context )
                                        .inflate( R.layout.image_pack_item_layout, null );
            ( ( ImageView ) layout.findViewById( R.id.image_pack_image ) )
                                        .setImageBitmap( getItem( i ) );
            ( ( Button ) layout.findViewById( R.id.image_pack_remove ) )
                                        .setOnClickListener( new RemoveOnClickListener( getItem( i ) ) );
            ( ( Button ) layout.findViewById( R.id.image_pack_edit ) )
                    .setOnClickListener( new EditOnClickListener( getItem( i ) ) );
            return layout;
        }

    }


    // event listeners
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

    private class EditOnClickListener implements  View.OnClickListener {

        private Bitmap image;

        public EditOnClickListener( Bitmap image ) {
            this.image = image;
        }

        @Override
        public void onClick( View view ) {
            EditContext.IMAGE_TO_EDIT = image;
            Intent editPictureIntent = new Intent( getApplicationContext(), FaceEditActivity.class );
            editPictureIntent.setData( picUri );
            startActivityForResult( editPictureIntent, EDIT_PICTURE_CODE );
        }

    }

    private class RemoveOnClickListener implements  View.OnClickListener {

        private Bitmap image;

        public RemoveOnClickListener( Bitmap image ) {
            this.image = image;
        }

        @Override
        public void onClick( View view ) {
            createdImages.remove( image );
            capturedImages.setAdapter(
                    listAdapter = new CapturedImagesListAdapter( getApplicationContext(), createdImages )
            );
        }

    }

    private class SaveOnClickListener implements View.OnClickListener {

        @Override
        public void onClick( View view ) {
            GameContext.IMAGES = createdImages.getImagesArray();
            startActivity( new Intent( getApplicationContext(), GameActivity.class ) );
        }

    }

}
