package com.badgames.jackslettebak.image.image.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.badgames.jackslettebak.walloffaces.R;

/**
 * Created by Jack Slettebak on 11/8/2017.
 */

public class ColorPicker extends DialogFragment {

    public interface ColorPickerCallback {

        public void setColorFromPicker( Colors color );

    }

    public enum Colors {

        TRANSPARENT( Color.TRANSPARENT, "No Color" ),
        BLACK( Color.BLACK, "Black" ),
        DARK_BLUE( 0xff087ca3, "Dark Blue" ),
        DARK_GRAY( 0xff484848, "Dark Grey" ),
        DARK_GREEN( 0xff2d8e09, "Dark Green" ),
        DARK_ORANGE( 0xffa36008, "Dark Orange" ),
        DARK_PURPLE( 0xff6904a3, "Dark Purple" ),
        DARK_RED( 0xff680505, "Dark Red" ),
        DARK_YELLOW( 0xffaaa508, "Dark Yellow" ),
        LIGHT_BLUE( 0xff51bee2, "Light Blue" ),
        LIGHT_GREEN( 0xff78e251, "Light Green" ),
        LIGHT_ORANGE( 0xffea9c35, "Light Orange" ),
        LIGHT_PURPLE( 0xff6760ea, "Light Purple" ),
        LIGHT_RED( 0xffe24f4f, "Light Red" ),
        LIGHT_YELLOW( 0xffddd849, "Light Yellow" ),
        WHITE( Color.WHITE, "White" );

        Integer colorCode;
        String colorName;

        Colors( Integer colorCode, String colorName ) {
            this.colorCode = colorCode;
            this.colorName = colorName;
        }

        public Integer getColorCode() {
            return colorCode;
        }

        public String getColorName() {
            return colorName;
        }

    }

    private ColorPickerCallback callback;
    private Colors [] colorValues = Colors.values();

    @Override
    public void onAttach( Context caller ) {
        super.onAttach( caller );
            callback = ( ColorPickerCallback ) caller;
        try {

        } catch( ClassCastException e ) {
            Log.e( "ClassCastException", "This class does not implement the ColorPickerCallback interface." );
        }
    }

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState ) {

        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        LinearLayout layout = ( LinearLayout ) getActivity().getLayoutInflater()
                .inflate( R.layout.color_picker_dialog_layout , null );

        ListView colors = ( ListView ) layout.findViewById( R.id.color_picker_dialog_list );
        colors.setAdapter( new ColorListAdapter() );
        colors.setOnItemClickListener( new ColorSelectListener() );

        builder.setView( layout );

        Dialog dialog = builder.create();

        // fix for bug with KitKat dialogs
        dialog.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        return dialog;
    }

    private class ColorSelectListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            callback.setColorFromPicker( colorValues[ i ] );
            dismiss();
        }

    }

    private class ColorListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return colorValues.length;
        }

        @Override
        public Colors getItem( int i ) {
            return colorValues[ i ];
        }

        @Override
        public long getItemId( int i ) {
            return colorValues[ i ].getColorCode();
        }

        @Override
        public View getView( int i, View view, ViewGroup viewGroup ) {
            LinearLayout layout = ( LinearLayout ) getActivity().getLayoutInflater()
                    .inflate( R.layout.color_picker_dialog_list_item_layout , null );
            ( ( ImageView ) layout.findViewById( R.id.color_picker_dialog_list_image ) )
                    .setBackgroundColor( getItem( i ).getColorCode() );
            ( ( TextView ) layout.findViewById( R.id.color_picker_dialog_list_text ) )
                    .setText( getItem( i ).getColorName() );
            return layout;
        }

    }

}