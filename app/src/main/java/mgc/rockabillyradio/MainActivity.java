package mgc.rockabillyradio;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    static Activity activity;

    // Screen background photo
    static ImageView background;

    //Strings for showing song data and detecting old album photo
    static String artist, track, album, albumOld;

    static TextView title_tv, artist_tv, track_tv;
    static CircularSeekBar volumeChanger;

    // Animation on bottom of the screen when stream is loaded
    static AVLoadingIndicatorView playing_animation;

    // Stram loading animation on the center of screen
    static AVLoadingIndicatorView loading_animation;

    // Button for start/stop playing audio
    static ImageButton control_button;

    // Boolean for check if play/pause button is activated
    boolean controlIsActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        initialise();
        setCustomFont();
        startListenVolume();
        new GetTrackInfo().execute();
        startRefreshing();
    }

    // Initialise all views, animations and set click listeners
    void initialise() {
        background = (ImageView) findViewById(R.id.bckg);
        title_tv = (TextView) findViewById(R.id.title_tv);
        track_tv = (TextView) findViewById(R.id.track_tv);
        artist_tv = (TextView) findViewById(R.id.artist_tv);
        volumeChanger = (CircularSeekBar) findViewById(R.id.circularSeekBar1);
        playing_animation = (AVLoadingIndicatorView) findViewById(R.id.playing_anim);
        playing_animation.setVisibility(View.GONE);
        loading_animation = (AVLoadingIndicatorView) findViewById(R.id.load_animation);
        control_button = (ImageButton) findViewById(R.id.control_button);
        control_button.setOnClickListener(controlButtonListener);
    }

    // Function for set custom font to title text view in toolbar
    void setCustomFont() {
        Typeface tf = Typeface.createFromAsset(this.getAssets(), "radio.ttf");
        title_tv.setTypeface(tf);
    }

    // Function for listen and change volume from seek bar to player
    void startListenVolume() {
        volumeChanger.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                Player.setVolume((100 - circularSeekBar.getProgress()) / 100f);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }

        });
    }

    // Function to set track and artist names to text views and change background photo
    public static void setSongData() {
        artist_tv.setText(artist);
        track_tv.setText(track);
        if (album != null) {
            if (album.length() < Const.SERVER_PHOTOS_COUNT) {
                Random rn = new Random();
                Bitmap bitmap = ((BitmapDrawable) background.getDrawable()).getBitmap();
                Drawable d = new BitmapDrawable(activity.getResources(), bitmap);
                Picasso.with(activity).load(Const.SERVER_PHOTO_PATH + (rn.nextInt(Const.SERVER_PHOTOS_COUNT) + 1) + Const.PHOTO_TYPE).placeholder(d).into(background);

            } else {
                Bitmap bitmap = ((BitmapDrawable) background.getDrawable()).getBitmap();
                Drawable d = new BitmapDrawable(activity.getResources(), bitmap);
                albumOld = album;
                Picasso.with(activity).load(album).placeholder(d).into(background);
            }
        }
    }

    // Service for background audio playing
    public void startPlayerService() {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    // Vibrate when click on control button
    public void vibrate() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(Const.VIBRATE_TIME);
    }

    public void startRefreshing()
    {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(Const.PHOTO_LOAD_REFRESH_TIME);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new GetTrackInfo().execute();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();
    }

    View.OnClickListener controlButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (controlIsActivated == false) {
                startPlayerService();
                control_button.setImageResource(R.drawable.pause);
                playing_animation.setVisibility(View.GONE);
                loading_animation.setVisibility(View.VISIBLE);
                control_button.setVisibility(View.GONE);
                controlIsActivated = true;
                vibrate();
            } else {
                Player.stop();
                control_button.setImageResource(R.drawable.play);
                playing_animation.setVisibility(View.GONE);
                loading_animation.setVisibility(View.VISIBLE);
                control_button.setVisibility(View.VISIBLE);
                controlIsActivated = false;
                vibrate();
            }
        }
    };

    @Override
    public void onBackPressed() {
        Player.stop();
        super.onBackPressed();
    }
}
