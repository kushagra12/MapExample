package abhinav.hackdev.co.fetchmarker102;

import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
/**
 * Created by abhinav on 05/06/16.
 */
public class VideoPlayer extends AppCompatActivity implements OnPreparedListener{

    private EMVideoView emVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        String vid_url = getIntent().getStringExtra(MainActivity.VIDEO_URL);
        setupVideoView(vid_url);
    }

    private void setupVideoView(String vid_url) {
        emVideoView = (EMVideoView)findViewById(R.id.video_view);
        assert emVideoView != null;
        emVideoView.setOnPreparedListener(this);
        emVideoView.setVideoURI(Uri.parse("https://archive.org/download/Popeye_forPresident/Popeye_forPresident_512kb.mp4"));
    }

    @Override
    public void onPrepared() {
        emVideoView.start();
    }
}
