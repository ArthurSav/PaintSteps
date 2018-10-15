package com.arthursaveliev.paintsteps;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import io.amuse.statusview.StatusStep;
import io.amuse.statusview.StatusView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

  private StatusView statusView;
  private List<StatusStep> statusSteps;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    statusView = findViewById(R.id.statusView);
    Button btn = findViewById(R.id.button);

    redrawRandomSteps();
    btn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        redrawRandomSteps();
      }
    });
  }

  ///////////////////////////////////////////////////////////////////////////
  // Helpers
  ///////////////////////////////////////////////////////////////////////////

  public void redrawRandomSteps() {

    Random rnd = new Random();
    int step_count = rnd.nextInt(5) + 2;

    statusSteps = new ArrayList<>();
    for (int i = 0; i < step_count; i++) {
      statusSteps.add(new StatusStep(Color.GREEN, getRandomColor(), "Title is " + i));
    }

    statusView.setSteps(statusSteps);
    statusView.requestLayout();
    statusView.invalidate();
  }

  private int getRandomColor() {
    Random rnd = new Random();
    return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
  }
}
