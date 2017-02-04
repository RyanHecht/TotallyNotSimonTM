package net.ryanhecht.memorygame;

import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    Button by,bg,br,bb;
    TextView scoretext,hightext;
    Random r = new Random();
    ArrayList<Integer> pattern = new ArrayList<Integer>();
    ArrayList<Integer> upattern = new ArrayList<Integer>();
    int sr=-1;
    int sg=-1;
    int sb=-1;
    int sy=-1;
    int buzz=-1;
    int score=0;
    boolean pturn=false;
    private SoundPool soundPool;

    //number/color/note equivalence
    //1==blue==e
    //2==yellow==g
    //3==green==a
    //4==red==c
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        //load sounds
        AssetManager assets = getAssets();
        AssetFileDescriptor desc;
        soundPool = new SoundPool.Builder().setMaxStreams(10).build();
        try {
            desc = assets.openFd("c.ogg");
            sr = soundPool.load(desc, 0);

            desc = assets.openFd("a.ogg");
            sg = soundPool.load(desc, 0);

            desc = assets.openFd("e.ogg");
            sb = soundPool.load(desc, 0);

            desc = assets.openFd("g.ogg");
            sy = soundPool.load(desc, 0);

            desc = assets.openFd("buzz.ogg");
            buzz = soundPool.load(desc, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //initialize controls
        bb = (Button) findViewById(R.id.bb);
        by = (Button) findViewById(R.id.by);
        bg = (Button) findViewById(R.id.bg);
        br = (Button) findViewById(R.id.br);
        scoretext = (TextView) findViewById(R.id.score);
        scoretext.setText(""+score);
        SharedPreferences scores = getPreferences(MODE_PRIVATE);
        hightext = ((TextView) findViewById(R.id.highscore));
        hightext.setText("High Score: " + scores.getInt("high", 0));
    }

    //adds a
    public int getNewEntry() {
        return r.nextInt(4) + 1;
    }

    public void game(View v) {
        pattern.clear();
        upattern.clear();
        pattern.add(Integer.valueOf(getNewEntry()));
        dictatePattern();
        pturn=true;
        ((Button) findViewById(R.id.go)).setVisibility(View.INVISIBLE);
    }


    public void onClick(View v) {
        if(pturn) {
            switch (v.getId()) {
                case R.id.bb:
                    soundPool.play(sb, 1, 1, 0, 0, 1);
                    upattern.add(Integer.valueOf(1));
                    break;

                case R.id.by:
                    soundPool.play(sy, 1, 1, 0, 0, 1);
                    upattern.add(Integer.valueOf(2));
                    break;

                case R.id.bg:
                    soundPool.play(sg, 1, 1, 0, 0, 1);
                    upattern.add(Integer.valueOf(3));
                    break;

                case R.id.br:
                    soundPool.play(sr, 1, 1, 0, 0, 1);
                    upattern.add(Integer.valueOf(4));
                    break;
            }
            SharedPreferences scores = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor edit = scores.edit();
            if(!compareVals()) {
                soundPool.play(buzz, 1000f, 1000f, 0, 0, 1);
                pattern.clear();
                upattern.clear();
                pturn=false;
                Toast.makeText(getApplicationContext(), "Game over! You scored " +score,Toast.LENGTH_SHORT).show();
                score = 0;
                ((Button) findViewById(R.id.go)).setVisibility(View.VISIBLE);
            }
            else if(upattern.size() == pattern.size()) {
                upattern.clear();
                pturn=false;
                score++;
                edit.putInt("score", score);
                pattern.add(Integer.valueOf(getNewEntry()));
                dictatePattern();
            }
            int hi=scores.getInt("high", 0);
            if(score > hi) {
                edit.putInt("high",score);

            }
            scoretext.setText(""+score);
            edit.commit();
            hi=scores.getInt("high", 0);
            hightext.setText("High Score: " + hi);

        }
    }


    public boolean compareVals() {
        for(int i=0; i<upattern.size();i++) {
            if(upattern.get(i) != null && pattern.get(i) != null && !upattern.get(i).equals(pattern.get(i))) {
                return false;
            }
        }

        return true;
    }


    public void dictatePattern() {
        final Handler handler = new Handler();

        //
        class patternRunnable implements Runnable {
            int size;
            int i=0;
            patternRunnable(int pattern) {size=pattern;}
            @Override
            public void run() {
                bb.setBackgroundTintList(null);
                by.setBackgroundTintList(null);
                bg.setBackgroundTintList(null);
                br.setBackgroundTintList(null);
                ColorStateList colors = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_pressed}, //1
                                new int[]{android.R.attr.state_focused}, //2
                                new int[]{android.R.attr.state_focused, android.R.attr.state_pressed} //3
                        },
                        new int[] {
                                Color.argb(155, 80, 80, 80),
                                Color.argb(155, 80, 80, 80),
                                Color.argb(155, 80, 80, 80)
                        }
                );
                if(i<size) {
                    switch(pattern.get(i)) {
                        case 1:
                            soundPool.play(sb, 1, 1, 0, 0, 1);
                            bb.setBackgroundTintList(colors);
                            break;
                        case 2:
                            soundPool.play(sy, 1, 1, 0, 0, 1);
                            by.setBackgroundTintList(colors);
                            break;

                        case 3:
                            soundPool.play(sg, 1, 1, 0, 0, 1);
                            bg.setBackgroundTintList(colors);
                            break;

                        case 4:
                            soundPool.play(sr, 1, 1, 0, 0, 1);
                            br.setBackgroundTintList(colors);
                            break;
                    }
                    i++;
                    int delay=500;
                    if(size<10) delay-=(20*size);
                    else delay=300;
                    handler.postDelayed(this, delay);
                }
                else {
                    pturn=true;
                    handler.removeCallbacks(this);
                }
            }
        }
        handler.postDelayed(new patternRunnable(pattern.size()), 1000);
    }



}
