package com.badgames.jackslettebak.utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.badgames.jackslettebak.editor.views.ColorPicker.Colors;

/**
 * Created by Jack Slettebak on 11/9/2017.
 */

public class ImageBuilder {

    public static final int BORDER_THICKNESS = 3;
    public static final int DEFAULT_SIZE_IMAGE = 100;

    public static Bitmap buildBackgroundImage( Colors color, PointF size ) {
        Bitmap background = Bitmap.createBitmap(
                ImageBuilder.DEFAULT_SIZE_IMAGE,
                ImageBuilder.DEFAULT_SIZE_IMAGE,
                Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas( background );
        Paint paint = new Paint();
        paint.setColor( color.getColorCode() );
        canvas.drawRect( 0.f, 0.f, size.x, size.y, paint );
        return background;
    }

    public static Bitmap buildBorderImage( Colors color, PointF size, PointF thickness ) {
        Bitmap border = Bitmap.createBitmap( ( int ) size.x, ( int ) size.y, Bitmap.Config.ARGB_8888 );
        border.setHasAlpha( true );
        Canvas canvas = new Canvas( border );

        Paint paint = new Paint();
        paint.setColor( color.getColorCode() );
        canvas.drawRect( 0.f, 0.f, size.x, size.y, paint );

        paint.setColor( Color.TRANSPARENT );
        paint.setXfermode( new PorterDuffXfermode( PorterDuff.Mode.CLEAR ) );
        canvas.drawRect(
                thickness.x,
                thickness.y,
                size.x - thickness.x,
                size.y - thickness.y,
                paint
        );
        return border;
    }

}
