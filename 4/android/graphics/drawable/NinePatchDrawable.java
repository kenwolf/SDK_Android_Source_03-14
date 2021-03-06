/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.graphics.drawable;

import android.graphics.*;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * A resizeable bitmap, with stretchable areas that you define. This type of image
 * is defined in a .png file with a special format, described in <a link="../../../resources.html#ninepatch">
 * Resources</a>.
 *
 */
public class NinePatchDrawable extends Drawable {
    private NinePatchState mNinePatchState;
    private NinePatch mNinePatch;
    private Rect mPadding;
    private Paint mPaint;
    private boolean mMutated;

    private int mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;

    // These are scaled to match the target density.
    private int mBitmapWidth;
    private int mBitmapHeight;
    
    NinePatchDrawable() {
    }

    /**
     * Create drawable from raw nine-patch data, not dealing with density.
     * @deprecated Use {@link #NinePatchDrawable(Resources, Bitmap, byte[], Rect, String)}
     * to ensure that the drawable has correctly set its target density.
     */
    public NinePatchDrawable(Bitmap bitmap, byte[] chunk, Rect padding, String srcName) {
        this(new NinePatchState(new NinePatch(bitmap, chunk, srcName), padding));
    }
    
    /**
     * Create drawable from raw nine-patch data, setting initial target density
     * based on the display metrics of the resources.
     */
    public NinePatchDrawable(Resources res, Bitmap bitmap, byte[] chunk,
            Rect padding, String srcName) {
        this(new NinePatchState(new NinePatch(bitmap, chunk, srcName), padding));
        if (res != null) {
            setTargetDensity(res.getDisplayMetrics());
            mNinePatchState.mTargetDensity = mTargetDensity;
        }
    }
    
    /**
     * Create drawable from existing nine-patch, not dealing with density.
     * @deprecated Use {@link #NinePatchDrawable(Resources, NinePatch)}
     * to ensure that the drawable has correctly set its target density.
     */
    public NinePatchDrawable(NinePatch patch) {
        this(new NinePatchState(patch, null));
    }

    /**
     * Create drawable from existing nine-patch, setting initial target density
     * based on the display metrics of the resources.
     */
    public NinePatchDrawable(Resources res, NinePatch patch) {
        this(new NinePatchState(patch, null));
        if (res != null) {
            setTargetDensity(res.getDisplayMetrics());
            mNinePatchState.mTargetDensity = mTargetDensity;
        }
    }

    private void setNinePatchState(NinePatchState state) {
        mNinePatchState = state;
        mNinePatch = state.mNinePatch;
        mPadding = state.mPadding;
        mTargetDensity = state.mTargetDensity;
        if (state.mDither) setDither(state.mDither);
        if (mNinePatch != null) {
            computeBitmapSize();
        }
    }

    /**
     * Set the density scale at which this drawable will be rendered. This
     * method assumes the drawable will be rendered at the same density as the
     * specified canvas.
     *
     * @param canvas The Canvas from which the density scale must be obtained.
     *
     * @see android.graphics.Bitmap#setDensity(int)
     * @see android.graphics.Bitmap#getDensity()
     */
    public void setTargetDensity(Canvas canvas) {
        setTargetDensity(canvas.getDensity());
    }

    /**
     * Set the density scale at which this drawable will be rendered.
     *
     * @param metrics The DisplayMetrics indicating the density scale for this drawable.
     *
     * @see android.graphics.Bitmap#setDensity(int)
     * @see android.graphics.Bitmap#getDensity()
     */
    public void setTargetDensity(DisplayMetrics metrics) {
        mTargetDensity = metrics.densityDpi;
        if (mNinePatch != null) {
            computeBitmapSize();
        }
    }

    /**
     * Set the density at which this drawable will be rendered.
     *
     * @param density The density scale for this drawable.
     *
     * @see android.graphics.Bitmap#setDensity(int)
     * @see android.graphics.Bitmap#getDensity()
     */
    public void setTargetDensity(int density) {
        mTargetDensity = density == 0 ? DisplayMetrics.DENSITY_DEFAULT : density;
        if (mNinePatch != null) {
            computeBitmapSize();
        }
    }

    private void computeBitmapSize() {
        final int sdensity = mNinePatch.getDensity();
        final int tdensity = mTargetDensity;
        if (sdensity == tdensity) {
            mBitmapWidth = mNinePatch.getWidth();
            mBitmapHeight = mNinePatch.getHeight();
        } else {
            mBitmapWidth = Bitmap.scaleFromDensity(mNinePatch.getWidth(),
                    sdensity, tdensity);
            mBitmapHeight = Bitmap.scaleFromDensity(mNinePatch.getHeight(),
                    sdensity, tdensity);
            Rect dest = mPadding;
            Rect src = mNinePatchState.mPadding;
            if (dest == src) {
                mPadding = dest = new Rect(src);
            }
            dest.left = Bitmap.scaleFromDensity(src.left, sdensity, tdensity);
            dest.top = Bitmap.scaleFromDensity(src.top, sdensity, tdensity);
            dest.right = Bitmap.scaleFromDensity(src.right, sdensity, tdensity);
            dest.bottom = Bitmap.scaleFromDensity(src.bottom, sdensity, tdensity);
        }
    }
    
    // overrides

    @Override
    public void draw(Canvas canvas) {
        mNinePatch.draw(canvas, getBounds(), mPaint);
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mNinePatchState.mChangingConfigurations;
    }
    
    @Override
    public boolean getPadding(Rect padding) {
        padding.set(mPadding);
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
        getPaint().setAlpha(alpha);
    }
    
    @Override
    public void setColorFilter(ColorFilter cf) {
        getPaint().setColorFilter(cf);
    }

    @Override
    public void setDither(boolean dither) {
        getPaint().setDither(dither);
    }

    @Override
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);

        TypedArray a = r.obtainAttributes(attrs, com.android.internal.R.styleable.NinePatchDrawable);

        final int id = a.getResourceId(com.android.internal.R.styleable.NinePatchDrawable_src, 0);
        if (id == 0) {
            throw new XmlPullParserException(parser.getPositionDescription() +
                    ": <nine-patch> requires a valid src attribute");
        }

        final boolean dither = a.getBoolean(
                com.android.internal.R.styleable.NinePatchDrawable_dither, false);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        if (dither) {
            options.inDither = false;
        }
        options.inScreenDensity = DisplayMetrics.DENSITY_DEVICE;

        final Rect padding = new Rect();        
        Bitmap bitmap = null;

        try {
            final TypedValue value = new TypedValue();
            final InputStream is = r.openRawResource(id, value);

            bitmap = BitmapFactory.decodeResourceStream(r, value, is, padding, options);

            is.close();
        } catch (IOException e) {
            // Ignore
        }

        if (bitmap == null) {
            throw new XmlPullParserException(parser.getPositionDescription() +
                    ": <nine-patch> requires a valid src attribute");
        } else if (bitmap.getNinePatchChunk() == null) {
            throw new XmlPullParserException(parser.getPositionDescription() +
                    ": <nine-patch> requires a valid 9-patch source image");
        }

        setNinePatchState(new NinePatchState(
                new NinePatch(bitmap, bitmap.getNinePatchChunk(), "XML 9-patch"), padding, dither));
        mNinePatchState.mTargetDensity = mTargetDensity;

        a.recycle();
    }


    public Paint getPaint() {
        if (mPaint == null) {
            mPaint = new Paint();
        }
        return mPaint;
    }

    /**
     * Retrieves the width of the source .png file (before resizing).
     */
    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    /**
     * Retrieves the height of the source .png file (before resizing).
     */
    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    @Override
    public int getMinimumWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getMinimumHeight() {
        return mBitmapHeight;
    }

    /**
     * Returns a {@link android.graphics.PixelFormat graphics.PixelFormat}
     * value of OPAQUE or TRANSLUCENT.
     */
    @Override
    public int getOpacity() {
        return mNinePatch.hasAlpha() || (mPaint != null && mPaint.getAlpha() < 255) ?
                PixelFormat.TRANSLUCENT : PixelFormat.OPAQUE;
    }

    @Override
    public Region getTransparentRegion() {
        return mNinePatch.getTransparentRegion(getBounds());
    }
    
    @Override
    public ConstantState getConstantState() {
        mNinePatchState.mChangingConfigurations = super.getChangingConfigurations();
        return mNinePatchState;
    }

    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mNinePatchState = new NinePatchState(mNinePatchState);
            mNinePatch = mNinePatchState.mNinePatch;
            mMutated = true;
        }
        return this;
    }

    final static class NinePatchState extends ConstantState {
        final NinePatch mNinePatch;
        final Rect mPadding;
        final boolean mDither;
        int mChangingConfigurations;
        int mTargetDensity = DisplayMetrics.DENSITY_DEFAULT;

        NinePatchState(NinePatch ninePatch, Rect padding) {
            this(ninePatch, padding, false);
        }

        NinePatchState(NinePatch ninePatch, Rect rect, boolean dither) {
            mNinePatch = ninePatch;
            mPadding = rect;
            mDither = dither;
        }

        NinePatchState(NinePatchState state) {
            mNinePatch = new NinePatch(state.mNinePatch);
            // Note we don't copy the padding because it is immutable.
            mPadding = state.mPadding;
            mDither = state.mDither;
            mChangingConfigurations = state.mChangingConfigurations;
            mTargetDensity = state.mTargetDensity;
        }

        @Override
        public Drawable newDrawable() {
            return new NinePatchDrawable(this);
        }
        
        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations;
        }
    }

    private NinePatchDrawable(NinePatchState state) {
        setNinePatchState(state);
    }
}

