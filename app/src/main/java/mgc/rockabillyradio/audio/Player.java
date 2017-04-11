package mgc.rockabillyradio.audio;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;

import mgc.rockabillyradio.MainActivity;
import mgc.rockabillyradio.R;

/**
 * Created by kolyas on 01.10.2016.
 */

public class Player {

    static ExoPlayer exoPlayer;
    static TrackRenderer audioRenderer;

    public static void start(String URL, Context context)
    {
        if(exoPlayer!=null)
        {
            exoPlayer.stop();
        }
        Uri URI = Uri.parse(URL);
        FrameworkSampleSource sampleSource = new FrameworkSampleSource(context,URI, null);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);
        exoPlayer = ExoPlayer.Factory.newInstance(1);
        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // This state if player is ready to work and loaded all data
                if(playbackState == 4)
                {
                    MainActivity.playing_animation.setVisibility(View.VISIBLE);
                    MainActivity.loading_animation.setVisibility(View.GONE);
                    MainActivity.control_button.setVisibility(View.VISIBLE);
                    MainActivity.control_button.setImageResource(R.drawable.pause);
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });
    }

    public static void stop()
    {
        if(exoPlayer!=null) {
            exoPlayer.stop();
        }
    }

    public static void setVolume(float volume)
    {
        if(exoPlayer!= null) {
            exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, volume);
        }
    }
}
