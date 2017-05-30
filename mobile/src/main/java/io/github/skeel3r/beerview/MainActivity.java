package io.github.skeel3r.beerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.os.SystemClock;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import android.os.Handler;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    TextView timeText;
    ListView mListView;

    boolean play = false;
    ToggleButton playPause;

    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    long progress = 0L;

    private Handler handler = new Handler();

    SeekBar simpleSeekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeText = (TextView) findViewById(R.id.time);


        mListView = (ListView) findViewById(R.id.list);
        String[] values = new String[] { "Android List View",
                "When Gandolf loves hobbits",
                "The entire fellowship is in the scene",
                "An Ork Dies",
                "A member of the fellowship dies",
                "Hobbits hide",
                "Another language is spoken",
                "Food is cooked/eaten"
        };

        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, values);
        mListView.setAdapter(adapter);
        mListView.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){
                view.setSelected(true);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        playPause = (ToggleButton) findViewById(R.id.playPause);

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();

                if (on) {
                    play = true;
                    progress = simpleSeekBar.getProgress()*1000;
                    startTime = SystemClock.uptimeMillis();
                    timeSwapBuff = 0L;
                    handler.postDelayed(updateTimerThread, 0);

                } else {
                    play = false;
                    timeSwapBuff += timeInMilliseconds;
                    handler.removeCallbacks(updateTimerThread);

                }
            }
        });

        // initiate  views
        simpleSeekBar = (SeekBar) findViewById(R.id.simpleSeekBar);
        simpleSeekBar.incrementProgressBy(1);
        simpleSeekBar.setMax(200);
        simpleSeekBar.setProgress(0);

        // perform seek bar change listener event used for getting the progress value
        simpleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                int seconds = simpleSeekBar.getProgress();
                int mins = seconds / 60;
                int secs = seconds % 60;
                updateText("" + mins + ":" + String.format("%02d", secs));
                if(secs > 25){
                    mListView.setSelection(1);
                }
                else{
                    mListView.setPressed(false);
                }

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                handler.removeCallbacks(updateTimerThread);
                int seconds = simpleSeekBar.getProgress();
                int mins = seconds / 60;
                int secs = seconds % 60;
                updateText("" + mins + ":" + String.format("%02d", secs));
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                timeInMilliseconds = SystemClock.uptimeMillis();
                if(play) {
                    progress = simpleSeekBar.getProgress()*1000;
                    startTime = SystemClock.uptimeMillis();
                    timeSwapBuff = 0L;
                    handler.postDelayed(updateTimerThread, 0);
                }
                else{
                    progress = simpleSeekBar.getProgress()*1000;
                    int seconds = simpleSeekBar.getProgress();
                    int mins = seconds / 60;
                    int secs = seconds % 60;
                    updateText("" + mins + ":" + String.format("%02d", secs));
                }
            }

        });



    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = progress + timeSwapBuff + timeInMilliseconds;

            int seconds = (int) (updatedTime / 1000);
            int mins = seconds / 60;
            int secs = seconds % 60;
            simpleSeekBar.setProgress(seconds);
            updateText("" + mins + ":" + String.format("%02d", secs));
            handler.postDelayed(this, 0);
        }

    };

    private void updateText(String text) {
        timeText.setText(text);
    };

}



