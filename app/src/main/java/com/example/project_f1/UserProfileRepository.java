package com.example.project_f1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.project_f1.models.Driver;

public class UserProfileRepository {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_DRIVER  = 1;
    public static final int TYPE_GALLERY = 2;

    private static final String PREFS = "F1Prefs";
    private static final String KEY_TYPE = "profile_pic_type";
    private static final String KEY_RES  = "profile_pic_res";
    private static final String KEY_PATH = "profile_pic_path";

    public static void save(Context ctx, int type, int resId, String path) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putInt(KEY_TYPE, type)
                .putInt(KEY_RES, resId)
                .putString(KEY_PATH, path)
                .apply();
    }

    public static int getType(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_TYPE, TYPE_DEFAULT);
    }

    public static int getResId(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_RES, 0);
    }

    public static String getPath(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_PATH, null);
    }

    /**
     * Loads the profile picture into the given ImageView.
     * Priority: gallery file → driver res → favorite driver res → initials avatar
     */
    public static void loadInto(Context ctx, ImageView iv, int accentColor) {
        int type = getType(ctx);
        Object source = null;
        String driverId = null;

        if (type == TYPE_GALLERY) {
            source = getPath(ctx);
        } else if (type == TYPE_DRIVER) {
            int resId = getResId(ctx);
            if (resId != 0) {
                source = resId;
                // Try to find driverId for this resId to apply specific offsets
                for (Driver d : Driver.getAllDrivers()) {
                    if (d.photoResId == resId) {
                        driverId = d.id;
                        break;
                    }
                }
            }
        }

        // Fallback: favorite driver photo
        if (source == null) {
            driverId = FavoriteRepository.getFavoriteDriverId(ctx);
            if (driverId != null) {
                Driver d = Driver.getDriverById(driverId);
                if (d != null && d.photoResId != 0) {
                    source = d.photoResId;
                }
            }
        }

        if (source != null) {
            final String finalDriverId = driverId;
            final float dp = ctx.getResources().getDisplayMetrics().density;
            
            iv.setScaleType(ImageView.ScaleType.MATRIX);
            
            Glide.with(ctx)
                    .load(source)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            iv.post(() -> {
                                Matrix matrix = new Matrix();
                                float viewWidth = iv.getWidth();
                                float viewHeight = iv.getHeight();
                                float drawableWidth = resource.getIntrinsicWidth();
                                float drawableHeight = resource.getIntrinsicHeight();

                                float scale = viewWidth / drawableWidth;
                                if (drawableHeight * scale < viewHeight) {
                                    scale = viewHeight / drawableHeight;
                                }
                                
                                matrix.setScale(scale, scale);
                                
                                // Top-Crop (y=0) is default for 9:16 portrait images to keep face visible
                                float dx = 0;
                                // Specific horizontal adjustments
                                if (finalDriverId != null && (finalDriverId.equals("russell") || 
                                    finalDriverId.equals("albon") || finalDriverId.equals("antonelli"))) {
                                    dx = 12 * dp; // Slightly less than adapter due to smaller header size
                                }
                                
                                matrix.postTranslate(dx, 0);
                                iv.setImageMatrix(matrix);
                            });
                            return false;
                        }
                    })
                    .into(iv);
        } else {
            // Final fallback: initials avatar
            SharedPreferences prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String name = prefs.getString("user_name", "?");
            char initial = name.isEmpty() ? '?' : Character.toUpperCase(name.charAt(0));
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setImageBitmap(makeInitialsBitmap(initial, accentColor));
        }
    }

    private static Bitmap makeInitialsBitmap(char initial, int color) {
        Bitmap bmp = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        canvas.drawCircle(48, 48, 48, paint);
        paint.setColor(0xFFFFFFFF);
        paint.setTextSize(42);
        paint.setTextAlign(Paint.Align.CENTER);
        Rect bounds = new Rect();
        paint.getTextBounds(String.valueOf(initial), 0, 1, bounds);
        canvas.drawText(String.valueOf(initial), 48, 48 + bounds.height() / 2f, paint);
        return bmp;
    }
}
