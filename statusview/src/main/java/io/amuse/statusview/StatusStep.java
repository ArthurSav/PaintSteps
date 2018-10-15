package io.amuse.statusview;

public class StatusStep {
  private int colorCircle;
  private int colorLine;
  private String text;
  private boolean useGradient;

  public StatusStep(int colorCircle, int colorLine) {
    this.colorCircle = colorCircle;
    this.colorLine = colorLine;
  }

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
}
