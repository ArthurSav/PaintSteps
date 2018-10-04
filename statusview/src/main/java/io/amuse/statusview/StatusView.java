package io.amuse.statusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom view that shows status/progress
 *
 * helpful: https://developer.android.com/reference/android/graphics/PorterDuff.Mode
 */
public class StatusView extends View {

  private Rect textBounds;

  private Paint paint;
  private Paint paintText;
  private Paint paintCircle;
  private Paint paintShadow;
  private Paint bitmapPaint;

  private Typeface typeface;

  private List<StatusStep> statusSteps = new ArrayList<>();
  private static final PorterDuffXfermode modeSrc = new PorterDuffXfermode(PorterDuff.Mode.SRC);

  private float strokeWidth;
  private int radius;
  private int textSize;
  private int extraPadding;
  private int textBottomMargin;
  private int textColor;

  private int shadowAlpha;
  private boolean showShadow;
  private int shadowColor;
  private int shadowWidth;
  private Bitmap mBitmap;
  private Canvas mCanvas;

  public StatusView(Context context) {
    super(context);
    init(context, null, 0);
  }

  public StatusView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs, 0);
  }

  public StatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
    paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textBounds = new Rect();

    bitmapPaint.setDither(true);
    paint.setDither(true);
    paintText.setDither(true);
    paintShadow.setDither(true);
    paintCircle.setDither(true);

    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StatusView, 0, 0);
    try {
      strokeWidth = a.getDimensionPixelOffset(R.styleable.StatusView_stoke_width, 15);
      radius = a.getDimensionPixelOffset(R.styleable.StatusView_radius, 30);
      textSize = a.getDimensionPixelSize(R.styleable.StatusView_text_size, 30);
      extraPadding = a.getDimensionPixelOffset(R.styleable.StatusView_extra_padding, 0);
      textBottomMargin = a.getDimensionPixelOffset(R.styleable.StatusView_text_bottom_margin, 50);
      textColor = a.getColor(R.styleable.StatusView_text_color, Color.BLACK);

      shadowAlpha = (int) (100 * a.getFloat(R.styleable.StatusView_shadow_alpha, 1f));
      showShadow = a.getBoolean(R.styleable.StatusView_show_shadow, true);
      shadowColor = a.getColor(R.styleable.StatusView_shadow_color, Color.LTGRAY);
      shadowWidth = a.getDimensionPixelOffset(R.styleable.StatusView_shadow_width, 25);
    } finally {
      a.recycle();
    }
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    setMeasuredDimension(widthSize, heightSize);
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    createBitmap(w, h);
  }

  private void createBitmap(int w, int h){
    mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    mCanvas = new Canvas(mBitmap);
    if (statusSteps != null) {
      paintSteps(mCanvas, radius, strokeWidth, statusSteps);
    }
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    canvas.drawBitmap(mBitmap, 0, 0, bitmapPaint);
  }

  private void paintSteps(Canvas canvas, float radius, float strokeWidth, @NonNull List<StatusStep> steps) {

    float shadowRadius = showShadow ? radius + shadowWidth : radius;

    // configure text bounds
    float textWidth = 0;
    String text1 = steps.size() > 0 ? steps.get(0).getText() : null;
    String text2 = steps.size() - 1 > 0 ? steps.get(steps.size() - 1).getText() : null;
    paintText.setTextSize(textSize);

    if (text1 != null) {
      paintText.getTextBounds(text1, 0, text1.length(), textBounds);
      textWidth = textBounds.width();
    }
    if (text2 != null) {
      paintText.getTextBounds(text2, 0, text2.length(), textBounds);
      if (textBounds.width() > textWidth) {
        textWidth = textBounds.width();
      }
    }

    float sideMarginWidth = (textWidth / 2) > shadowRadius ? textWidth : shadowWidth * 2;
    float paddingWidth = extraPadding * 2;

    int steps_count = steps.size();
    int step_lines = steps_count - 1;

    // margin based on circle radius
    float x =  getWidth() - sideMarginWidth - paddingWidth;
    int y =  getHeight() / 2;

    float x_step = x / step_lines;
    float progressWidth = 0.0f + (sideMarginWidth / 2) + (paddingWidth / 2);

    //draw shadows
    if (showShadow) {
      float progressWidthShadow = 0.0f + (sideMarginWidth / 2) + (paddingWidth / 2);
      for (int i = 0; i < steps_count; i++) {

        float startFrom = progressWidthShadow;
        progressWidthShadow += x_step;

        // circle
        paintShadow.setColor(shadowColor);
        paintShadow.setStyle(Paint.Style.FILL_AND_STROKE);
        paintShadow.setStrokeWidth(shadowWidth);
        paintShadow.setXfermode(modeSrc);
        paintShadow.setAlpha(shadowAlpha);
        canvas.drawCircle(startFrom, y, radius, paintShadow);

        if (i <= step_lines) {

          //draw shadow line
          paintShadow.setStyle(Paint.Style.STROKE);
          paintShadow.setStrokeWidth(strokeWidth + shadowWidth);
          canvas.drawLine(startFrom, y, progressWidth, y, paintShadow);
        }
      }
    }

    // custom text typeface
    if (typeface != null) {
      paintText.setTypeface(typeface);
    }

    for (int i = 0; i < steps_count; i++) {
      StatusStep step = steps.get(i);

      float startFrom = progressWidth;
      progressWidth += x_step;

      if (i < step_lines) {
        paint.setColor(step.getColorLine());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawLine(startFrom, y, progressWidth, y, paint);
      }

      // draw circle
      paintCircle.setColor(step.getColorCircle());
      paintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
      paintCircle.setStrokeWidth(0);
      canvas.drawCircle(startFrom, y, radius, paintCircle);

      // draw text
      String txt = steps.get(i).getText();
      if (txt != null) {
        paintText.setColor(textColor);
        paintText.setTextSize(textSize);
        paintText.getTextBounds(txt, 0, txt.length(), textBounds);

        float textStartX = startFrom - (textBounds.width() / 2);
        float textStartY = y - radius - textBottomMargin;

        canvas.drawText(txt, textStartX, textStartY, paintText);
      }
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Setters
  ///////////////////////////////////////////////////////////////////////////

  public void setSteps(List<StatusStep> statusSteps) {
    this.statusSteps = statusSteps;
  }

  public void setStrokeWidth(float strokeWidth) {
    this.strokeWidth = strokeWidth;
  }

  public void setRadius(int radius) {
    this.radius = radius;
  }

  public void setTextSize(@DimenRes int textSize) {
    this.textSize = getResources().getDimensionPixelSize(textSize);
  }

  public void setExtraPadding(@DimenRes int extraPadding) {
    this.extraPadding = getResources().getDimensionPixelOffset(extraPadding);
  }

  public void setTypeface(Typeface typeface) {
    this.typeface = typeface;
  }

  public void setShadowAlpha(int shadowAlpha) {
    this.shadowAlpha = shadowAlpha;
  }

  public void setShowShadow(boolean showShadow) {
    this.showShadow = showShadow;
  }

  public void setShadowColor(int shadowColor) {
    this.shadowColor = shadowColor;
  }

  public void setShadowWidth(int shadowWidth) {
    this.shadowWidth = shadowWidth;
  }
}
