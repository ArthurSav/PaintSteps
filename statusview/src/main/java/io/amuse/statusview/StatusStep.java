package io.amuse.statusview;

import android.text.TextUtils;

public class StatusStep {
  private int colorCircle;
  private int colorLine;
  private String text;
  private boolean useGradient;

  public StatusStep(int colorCircle, int colorLine, String text, boolean useGradient) {
    this.colorCircle = colorCircle;
    this.colorLine = colorLine;
    this.text = text;
    this.useGradient = useGradient;
  }

  public int getColorCircle() {
    return colorCircle;
  }

  public int getColorLine() {
    return colorLine;
  }

  public String getText() {
    return text;
  }

  public boolean isUseGradient() {
    return useGradient;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof StatusStep) {
      StatusStep o = (StatusStep) obj;
      return colorCircle == o.getColorCircle() && colorLine == o.getColorLine() && TextUtils.equals(text, o.getText()) && useGradient == o.isUseGradient();
    }
    return super.equals(obj);
  }
}
