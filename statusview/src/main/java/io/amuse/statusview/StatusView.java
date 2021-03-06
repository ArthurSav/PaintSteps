package io.amuse.statusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
  private Paint bitmapPaint;

  private Typeface typeface;

  private List<StatusStep> statusSteps = new ArrayList<>();

  private float strokeWidth;
  private int radius;
  private float textSize;
  private int extraPadding;
  private int textBottomMargin;
  private int textColor;

  private Bitmap mBitmap;
  private Canvas mCanvas;

  private int firstWidth;
  private int firstHeight;
  public int textScaleMinWidth; // min change in width before resizing text
  private boolean textScaleAutomatically; // if true, it will scale text down when downsizing during animations

  private boolean showLines;
  private boolean showText;
  private int circleDistance;

  private List<GradientDrawable> gradientDrawables;

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
    paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    textBounds = new Rect();

    bitmapPaint.setDither(true);
    paint.setDither(true);
    paintText.setDither(true);
    paintCircle.setDither(true);

    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StatusView, 0, 0);
    try {
      strokeWidth = a.getDimensionPixelOffset(R.styleable.StatusView_stoke_width, 15);
      radius = a.getDimensionPixelOffset(R.styleable.StatusView_radius, 30);
      textSize = a.getDimensionPixelSize(R.styleable.StatusView_text_size, 30);
      extraPadding = a.getDimensionPixelOffset(R.styleable.StatusView_extra_padding, 0);
      textBottomMargin = a.getDimensionPixelOffset(R.styleable.StatusView_text_bottom_margin, 50);
      textColor = a.getColor(R.styleable.StatusView_text_color, Color.BLACK);
      textScaleAutomatically = a.getBoolean(R.styleable.StatusView_text_scale_automatically, true);
      showLines = a.getBoolean(R.styleable.StatusView_show_lines, true);
      showText = a.getBoolean(R.styleable.StatusView_show_text, true);
      circleDistance = a.getDimensionPixelOffset(R.styleable.StatusView_circle_distance, 0);
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
    //createBitmap(w, h);
  }

  @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    paintSteps(canvas, radius, strokeWidth, statusSteps);
    //canvas.drawBitmap(mBitmap, 0, 0, bitmapPaint);
  }

  private void createBitmap(int w, int h){
    mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    mCanvas = new Canvas(mBitmap);
    if (statusSteps != null) {
      paintSteps(mCanvas, radius, strokeWidth, statusSteps);
    }
  }

  private void paintSteps(Canvas canvas, float radius, float strokeWidth, @NonNull List<StatusStep> steps) {

    int width = getWidth();
    int height = getHeight();

    if (firstWidth == 0) {
      firstWidth = width;
    }
    if (firstHeight == 0) {
      firstHeight = height;
    }

    float textWidth = 0;

    if (showText) {
      String text1 = steps.size() > 0 ? steps.get(0).getText() : null;
      String text2 = steps.size() - 1 > 0 ? steps.get(steps.size() - 1).getText() : null;

      // configure text bounds
      float updatedTextSize = textSize;

      // When the view is scaling down the text will scale down as well. Used for scene transitions in material design
      if (textScaleAutomatically) {
        textScaleMinWidth = (firstWidth / 4) * 3;
        if (width < textScaleMinWidth) {
          updatedTextSize = (width * textSize) / firstWidth;
          if (updatedTextSize < 5) updatedTextSize = 0;
        }
        else updatedTextSize = textSize;
      }
      paintText.setTextSize(updatedTextSize);

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
    }

    float sideMarginWidth = (textWidth / 2) > radius? textWidth: radius * 2;
    float paddingWidth = extraPadding * 2;

    int steps_count = steps.size();
    int step_lines = steps_count - 1;

    // margin based on circle radius
    float x =  width - sideMarginWidth - paddingWidth;
    int y =  height / 2;

    float x_step = showLines? x / step_lines: 2 * radius + circleDistance;
    float progressWidth = 0.0f + (sideMarginWidth / 2) + (paddingWidth / 2);

    // custom text typeface
    if (typeface != null) {
      paintText.setTypeface(typeface);
    }

    for (int i = 0; i < steps_count; i++) {
      StatusStep step = steps.get(i);

      float startFrom = progressWidth;
      progressWidth += x_step;

      if (showLines) {
        if (i < step_lines) {
          if (step.isUseGradient()) {
            int yWidth = (int) (strokeWidth / 2);
            GradientDrawable gradientLine = gradientDrawables.get(i);
            gradientLine.setBounds((int) startFrom, y - yWidth, (int) progressWidth, y + yWidth);
            gradientLine.setShape(GradientDrawable.RECTANGLE);
            gradientLine.draw(canvas);
          }
          else {
            paint.setColor(step.getColorLine());
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawLine(startFrom, y, progressWidth, y, paint);
          }
        }
      }

      // draw circle
      paintCircle.setColor(step.getColorCircle());
      paintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
      paintCircle.setStrokeWidth(0);
      canvas.drawCircle(startFrom, y, radius, paintCircle);

      // draw text
      if (showText) {
        String txt = steps.get(i).getText();
        if (txt != null) {
          paintText.setColor(textColor);
          paintText.getTextBounds(txt, 0, txt.length(), textBounds);

          float textStartX = startFrom - (textBounds.width() / 2);
          float textStartY = y + radius + textBottomMargin;

          canvas.drawText(txt, textStartX, textStartY, paintText);
        }
      }
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Setters
  ///////////////////////////////////////////////////////////////////////////

  public void setSteps(List<StatusStep> statusSteps) {
    this.statusSteps = statusSteps;

    if (statusSteps == null) return;
    if (gradientDrawables == null) gradientDrawables = new ArrayList<>();
    gradientDrawables.clear();

    int size = statusSteps.size();
    int step_lines = size - 1;
    for (int i = 0; i < size; i++) {
      StatusStep step = statusSteps.get(i);
      if (i < step_lines) {
        int color1 = step.getColorCircle();
        int color2 = statusSteps.get(i + 1).getColorCircle();
        GradientDrawable gradientLine = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,  new int[]{color1, color2});
        gradientDrawables.add(gradientLine);
      }
    }
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

  public void setShowLines(boolean showLines) {
    this.showLines = showLines;
  }

  public void setShowText(boolean showText) {
    this.showText = showText;
  }

  public void setCircleDistance(@DimenRes int circleDistance) {
    this.circleDistance = getResources().getDimensionPixelOffset(circleDistance);
  }
}
