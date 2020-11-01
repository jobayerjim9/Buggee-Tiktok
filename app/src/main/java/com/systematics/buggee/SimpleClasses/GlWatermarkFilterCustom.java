package com.systematics.buggee.SimpleClasses;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.daasuu.gpuv.egl.filter.GlOverlayFilter;
import com.daasuu.gpuv.egl.filter.GlWatermarkFilter;

public class GlWatermarkFilterCustom extends GlOverlayFilter {

    private Bitmap bitmap;
    private GlWatermarkFilter.Position position = GlWatermarkFilter.Position.LEFT_TOP;

    public GlWatermarkFilterCustom(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public GlWatermarkFilterCustom(Bitmap bitmap, GlWatermarkFilter.Position position) {
        this.bitmap = bitmap;
        this.position = position;
    }

    @Override
    protected void drawCanvas(Canvas canvas) {
        if (bitmap != null && !bitmap.isRecycled()) {
            switch (position) {
                case LEFT_TOP:
                    canvas.drawBitmap(bitmap, 0, 0, null);
                    break;
                case LEFT_BOTTOM:
                    canvas.drawBitmap(bitmap, 0, canvas.getHeight() - bitmap.getHeight(), null);
                    break;
                case RIGHT_TOP:
                    canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth(), 0, null);
                    break;
                case RIGHT_BOTTOM:

                    canvas.drawBitmap(bitmap, canvas.getWidth() - bitmap.getWidth() - 30, canvas.getHeight() - bitmap.getHeight() - 30, null);
                    break;
            }
        }
    }

    public enum Position {
        LEFT_TOP,
        LEFT_BOTTOM,
        RIGHT_TOP,
        RIGHT_BOTTOM
    }
}
