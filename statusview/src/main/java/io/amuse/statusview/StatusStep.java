package io.amuse.statusview;

class StatusStep {
  private int colorCircle;
  private int colorLine;

  public StatusStep(int colorCircle, int colorLine) {
    this.colorCircle = colorCircle;
    this.colorLine = colorLine;
  }

  public int getColorCircle() {
    return colorCircle;
  }

  public int getColorLine() {
    return colorLine;
  }
}
