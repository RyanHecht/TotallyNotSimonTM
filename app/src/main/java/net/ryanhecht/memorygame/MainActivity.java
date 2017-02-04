package net.ryanhecht.memorygame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ryanh on 3/30/2016.
 */
public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences scores = getPreferences(MODE_PRIVATE);
        ((TextView) findViewById(R.id.high)).setText("Current High Score: " + scores.getInt("high", 0));
    }

    public void startGame(View v) {
        Intent inent = new Intent(this, GameActivity.class);
        startActivity(inent);

    }
}
