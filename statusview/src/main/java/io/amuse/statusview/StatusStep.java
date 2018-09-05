package io.amuse.statusview;

public class StatusStep {
  private int colorCircle;
  private int colorLine;
  private String text;

  public StatusStep(int colorCircle, int colorLine) {
    this.colorCircle = colorCircle;
    this.colorLine = colorLine;
  }

  public StatusStep(int colorCircle, int colorLine, String text) {
    this.colorCircle = colorCircle;
    this.colorLine = colorLine;
    this.text = text;
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
}
