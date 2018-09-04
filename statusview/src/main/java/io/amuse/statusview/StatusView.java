package io.amuse.statusview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
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

  private Paint paint;
  private List<StatusStep> statusSteps = new ArrayList<>();

  private float strokeWidth;
  private int radius;

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

  private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
        R.styleable.StatusView,
        0, 0);

    try {
      strokeWidth = a.getDimensionPixelOffset(R.styleable.StatusView_stoke_width, 15);
      radius = a.getDimensionPixelOffset(R.styleable.StatusView_radius, 30);
    } finally {
      a.recycle();
    }

    generateRandomSteps();
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);

    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);

    setMeasuredDimension(widthSize, heightSize);
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    paintSteps(canvas, radius, strokeWidth, statusSteps);
  }

  private void paintSteps(Canvas canvas, float radius, float strokeWidth, @NonNull List<StatusStep> steps){
    int steps_count = steps.size();
    int step_lines = steps_count - 1;

    // margin based on circle radius
    float x = canvas.getWidth() - (radius * 2);
    int y = canvas.getHeight() / 2;

    float x_step = x / step_lines;
    float progressWidth = 0.0f + radius;

    for (int i = 0; i < steps_count; i++) {
      StatusStep step = steps.get(i);

      float startFrom = progressWidth;
      progressWidth += x_step;

      if (i < step_lines) {

        // draw colored line
        paint.setColor(step.getColorLine());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.OVERLAY));
        canvas.drawLine(startFrom, y, progressWidth, y, paint);
      }

      // draw circle
      paint.setColor(step.getColorCircle());
      paint.setStyle(Paint.Style.FILL_AND_STROKE);
      paint.setStrokeWidth(0);
      paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
      canvas.drawCircle(startFrom,  y , radius, paint);
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

  ///////////////////////////////////////////////////////////////////////////
  // Helpers
  ///////////////////////////////////////////////////////////////////////////

  public void redrawRandomSteps(){
    generateRandomSteps();
    invalidate();
  }

  private void generateRandomSteps(){
    Random rnd = new Random();
    int step_count = rnd.nextInt(10);

    statusSteps = new ArrayList<>();
    for (int i = 0; i < step_count; i++) {
      statusSteps.add(new StatusStep(Color.GREEN, getRandomColor()));
    }
  }

  private int getRandomColor(){
    Random rnd = new Random();
    return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
  }
}
