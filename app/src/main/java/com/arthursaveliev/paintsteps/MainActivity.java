package com.arthursaveliev.paintsteps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import io.amuse.statusview.StatusView;
import io.amuse.statusview_app.R;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final StatusView statusView = findViewById(R.id.statusView);
    Button btn = findViewById(R.id.button);
    btn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        statusView.redrawSteps();
      }
    });
  }
}
