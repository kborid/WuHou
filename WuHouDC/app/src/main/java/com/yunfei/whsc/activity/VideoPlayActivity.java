package com.yunfei.whsc.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.yunfei.whsc.R;

/**
 * @author kborid
 * @date 2017/1/23 0023
 */
public class VideoPlayActivity extends AppCompatActivity {

    private VideoView video_lay;
    private TextView tv_center_title;
    private TextView tv_back;
    private String mTitle;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_videoplay_layout);
        dealIntent();
        findViews();
        setClickListener();
    }

    private void dealIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mTitle = bundle.getString("title");
        }
    }

    private void findViews() {
        tv_center_title = (TextView) findViewById(R.id.tv_center_title);
        tv_center_title.setText(mTitle);
        tv_back = (TextView) findViewById(R.id.tv_left_title_back);
        video_lay = (VideoView) findViewById(R.id.video_lay);
        video_lay.setMediaController(new MediaController(this));
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_3d);
        video_lay.setVideoURI(videoUri);
        video_lay.start();
        video_lay.requestFocus();
    }

    private void setClickListener() {
        video_lay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);

            }
        });

        video_lay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                video_lay.setVideoURI(videoUri);
                video_lay.start();
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        video_lay.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
