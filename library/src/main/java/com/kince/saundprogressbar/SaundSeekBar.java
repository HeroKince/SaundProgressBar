/*
 * Copyright (C) 2018 kince
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kince.saundprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.SeekBar;

/**
 * An enhanced version of the ProgressBar which provides greater control over
 * how the progress bar is drawn and displayed. The motivation is to allow us to
 * customize the appearance of the progress bar more freely. We can now display
 * a rounded cap at the end of the progress bar and optionally show an overlay.
 * We can also present a progress indicator to show the percentage completed or
 * a more customized value by implementing the Formatter interface.
 *
 * @author kince
 */
public class SaundSeekBar extends SeekBar {

    private Drawable mIndicator;
    private int mOffset = 5;
    private TextPaint mTextPaint;
    private Formatter mFormatter;

    public SaundSeekBar(Context context) {
        this(context, null);
    }

    public SaundSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SaundSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // create a default progress bar indicator text paint used for drawing
        // the
        // text on to the canvas
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.density = getResources().getDisplayMetrics().density;
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setTextSize(10);
        mTextPaint.setFakeBoldText(true);

        // get the styleable attributes as defined in the xml
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SaundProgressBar, defStyle, 0);

        if (a != null) {
            mTextPaint.setTextSize(a.getDimension(
                    R.styleable.SaundProgressBar_textSize, 10));
            mTextPaint.setColor(a.getColor(
                    R.styleable.SaundProgressBar_textColor, Color.WHITE));

            int alignIndex = (a.getInt(R.styleable.SaundProgressBar_textAlign,
                    1));
            if (alignIndex == 0) {
                mTextPaint.setTextAlign(Align.LEFT);
            } else if (alignIndex == 1) {
                mTextPaint.setTextAlign(Align.CENTER);
            } else if (alignIndex == 2) {
                mTextPaint.setTextAlign(Align.RIGHT);
            }

            int textStyle = (a
                    .getInt(R.styleable.SaundProgressBar_textStyle, 1));
            if (textStyle == 0) {
                mTextPaint.setTextSkewX(0.0f);
                mTextPaint.setFakeBoldText(false);
            } else if (textStyle == 1) {
                mTextPaint.setTextSkewX(0.0f);
                mTextPaint.setFakeBoldText(true);
            } else if (textStyle == 2) {
                mTextPaint.setTextSkewX(-0.25f);
                mTextPaint.setFakeBoldText(false);
            }

            mIndicator = a.getDrawable(R.styleable.SaundProgressBar_progressIndicator);
            mOffset = (int) a.getDimension(R.styleable.SaundProgressBar_offset, 0);

            a.recycle();
        }
    }

    /**
     * Sets the drawable used as a progress indicator
     *
     * @param indicator
     */
    public void setProgressIndicator(Drawable indicator) {
        mIndicator = indicator;
    }

    /**
     * The text formatter is used for customizing the presentation of the text
     * displayed in the progress indicator. The default text format is X% where
     * X is [0,100]. To use the formatter you must provide an object which
     * implements the {@linkplain SaundSeekBar.Formatter} interface.
     *
     * @param formatter
     */
    public void setTextFormatter(Formatter formatter) {
        mFormatter = formatter;
    }

    /**
     * The additional offset is for tweaking the position of the indicator.
     *
     * @param offset
     */
    public void setOffset(int offset) {
        mOffset = offset;
    }

    /**
     * Set the text color
     *
     * @param color
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    /**
     * Set the text size.
     *
     * @param size
     */
    public void setTextSize(float size) {
        mTextPaint.setTextSize(size);
    }

    /**
     * Set the text bold.
     *
     * @param bold
     */
    public void setTextBold(boolean bold) {
        mTextPaint.setFakeBoldText(true);
    }

    /**
     * Set the alignment of the text.
     *
     * @param align
     */
    public void setTextAlign(Align align) {
        mTextPaint.setTextAlign(align);
    }

    /**
     * Set the paint object used to draw the text on to the canvas.
     *
     * @param paint
     */
    public void setPaint(TextPaint paint) {
        mTextPaint = paint;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // if we have an indicator we need to adjust the height of the view to
        // accomodate the indicator
//        if (mIndicator != null) {
//            final int width = getMeasuredWidth();
//            final int height = getMeasuredHeight() + getIndicatorHeight();
//
//            // make the view the original height + indicator height size
//            setMeasuredDimension(width, height);
//        }
    }

    private int getIndicatorWidth() {
        if (mIndicator == null) {
            return 0;
        }

        Rect r = mIndicator.copyBounds();
        int width = r.width();

        return width;
    }

    private int getIndicatorHeight() {
        if (mIndicator == null) {
            return 0;
        }

        Rect r = mIndicator.copyBounds();
        int height = r.height();

        return height;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        Drawable progressDrawable = getProgressDrawable();

        // If we have an indicator then we'll need to adjust the drawable bounds
        // for
        // the progress bar and its layers (if the drawable is a layer
        // drawable).
        // This will ensure the progress bar gets drawn in the correct position
        if (mIndicator != null) {
            if (progressDrawable instanceof LayerDrawable) {
                LayerDrawable d = (LayerDrawable) progressDrawable;

                for (int i = 0; i < d.getNumberOfLayers(); i++) {
                    d.getDrawable(i).getBounds().top = getIndicatorHeight();

                    // thanks to Dave [dave@pds-uk.com] for point out a bug
                    // which eats up
                    // a lot of cpu cycles. It turns out the issue was linked to
                    // calling
                    // getIntrinsicHeight which proved to be very cpu intensive.
                    d.getDrawable(i).getBounds().bottom = d.getDrawable(i)
                            .getBounds().height()
                            + getIndicatorHeight();
                }
            } else if (progressDrawable != null) {
                // It's not a layer drawable but we still need to adjust the
                // bounds
                progressDrawable.getBounds().top = mIndicator
                        .getIntrinsicHeight();
                // thanks to Dave[dave@pds-uk.com] -- see note above for
                // explaination.
                progressDrawable.getBounds().bottom = progressDrawable
                        .getBounds().height() + getIndicatorHeight();
            }
        }

        // update the size of the progress bar and overlay
        updateProgressBar();

        super.onDraw(canvas);

        // Draw the indicator to match the far right position of the progress
        // bar
        if (mIndicator != null) {
            canvas.save();
            int dx = 0;

            // get the position of the progress bar's right end
            if (progressDrawable instanceof LayerDrawable) {
                LayerDrawable d = (LayerDrawable) progressDrawable;
                Drawable progressBar = d.findDrawableByLayerId(R.id.progress);

                if (progressBar != null) {
                    dx = progressBar.getBounds().right;
                } else {
                    float progress = getProgress();
                    float totalProgress = 100;
                    float current = progressDrawable.getBounds().right * (progress / totalProgress);
                    dx = Math.round(current);
                }
            } else if (progressDrawable != null) {/**/
                dx = progressDrawable.getBounds().right;
            }

            // adjust for any additional offset
            dx = dx + getThumbOffset() / 4;

            // translate the canvas to the position where we should draw the
            // indicator
            canvas.translate(dx, 0);

            mIndicator.draw(canvas);

            canvas.drawText(
                    mFormatter != null ? mFormatter.getText(getProgress())
                            : Math.round(getScale(getProgress()) * 100.0f)
                            + "%", getIndicatorWidth() / 2,
                    getIndicatorHeight() / 2 + 1, mTextPaint);

            // restore canvas to original
            canvas.restore();
        }
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);

        // the setProgress super will not change the details of the progress bar
        // anymore so we need to force an update to redraw the progress bar
        invalidate();
    }

    private float getScale(int progress) {
        float scale = getMax() > 0 ? (float) progress / (float) getMax() : 0;
        return scale;
    }

    /**
     * Instead of using clipping regions to uncover the progress bar as the
     * progress increases we increase the drawable regions for the progress bar
     * and pattern overlay. Doing this gives us greater control and allows us to
     * show the rounded cap on the progress bar.
     */
    private void updateProgressBar() {
        Drawable progressDrawable = getProgressDrawable();

        if (progressDrawable instanceof LayerDrawable) {
            LayerDrawable d = (LayerDrawable) progressDrawable;

            final float scale = getScale(getProgress());

            // get the progress bar and update it's size
            Drawable progressBar = d.findDrawableByLayerId(R.id.progress);

            final int width = d.getBounds().right - d.getBounds().left;

            if (progressBar != null) {
                Rect progressBarBounds = progressBar.getBounds();
                progressBarBounds.right = progressBarBounds.left
                        + (int) (width * scale + 0.5f);
                progressBar.setBounds(progressBarBounds);
            }

            // get the pattern overlay
            Drawable patternOverlay = d.findDrawableByLayerId(R.id.pattern);

            if (patternOverlay != null) {
                if (progressBar != null) {
                    // we want our pattern overlay to sit inside the bounds of
                    // our progress bar
                    Rect patternOverlayBounds = progressBar.copyBounds();
                    final int left = patternOverlayBounds.left;
                    final int right = patternOverlayBounds.right;

                    patternOverlayBounds.left = (left + 1 > right) ? left
                            : left + 1;
                    patternOverlayBounds.right = (right > 0) ? right - 1
                            : right;
                    patternOverlay.setBounds(patternOverlayBounds);
                } else {
                    // we don't have a progress bar so just treat this like the
                    // progress bar
                    Rect patternOverlayBounds = patternOverlay.getBounds();
                    patternOverlayBounds.right = patternOverlayBounds.left
                            + (int) (width * scale + 0.5f);
                    patternOverlay.setBounds(patternOverlayBounds);
                }
            }
        }
    }

    /**
     * You must implement this interface if you wish to present a custom
     * formatted text to be used by the Progress Indicator. The default format
     * is X% where X [0,100]
     *
     * @author jsaund
     */
    public interface Formatter {
        public String getText(int progress);
    }

}