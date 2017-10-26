package com.dou.samples.mediaplayerdemo;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Dou on 2017/10/26.
 */

public class CustomMediaController extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    Context mContext;
    CustomMediaplayer mMediaPlayer;

    int mCurrentState;

    private ImageView mImage;
    private ImageView mCenterStart;

    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;
    private LinearLayout mBatteryTime;
    private ImageView mBattery;
    private TextView mTime;

    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private TextView mClarity;
    private ImageView mFullScreen;

    private TextView mLength;

    private LinearLayout mLoading;
    private TextView mLoadText;

    private LinearLayout mChangePositon;
    private TextView mChangePositionCurrent;
    private ProgressBar mChangePositionProgress;

    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;

    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;

    private LinearLayout mError;
    private TextView mRetry;

    private LinearLayout mCompleted;
    private TextView mReplay;
    private TextView mShare;

    boolean mIsTopBottomVisible;

    public CustomMediaController(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomMediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomMediaController(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(mContext).inflate(R.layout.view_media_controller, this, true);

        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.image);

        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        mBatteryTime = (LinearLayout) findViewById(R.id.battery_time);
        mBattery = (ImageView) findViewById(R.id.battery);
        mTime = (TextView) findViewById(R.id.time);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);
        mClarity = (TextView) findViewById(R.id.clarity);
        mLength = (TextView) findViewById(R.id.length);

        mLoading = (LinearLayout) findViewById(R.id.loading);
        mLoadText = (TextView) findViewById(R.id.load_text);

        mChangePositon = (LinearLayout) findViewById(R.id.change_position);
        mChangePositionCurrent = (TextView) findViewById(R.id.change_position_current);
        mChangePositionProgress = (ProgressBar) findViewById(R.id.change_position_progress);

        mChangeBrightness = (LinearLayout) findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = (ProgressBar) findViewById(R.id.change_brightness_progress);

        mChangeVolume = (LinearLayout) findViewById(R.id.change_volume);
        mChangeVolumeProgress = (ProgressBar) findViewById(R.id.change_volume_progress);

        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);

        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);
        mShare = (TextView) findViewById(R.id.share);

        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mClarity.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);
    }

    public void setMediaPlayer(CustomMediaplayer mediaPlayer) {
        this.mMediaPlayer = mediaPlayer;
    }

    public void setControllerState(int state) {
        this.mCurrentState = state;
    }


    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            if (mMediaPlayer.mCurrentState == CustomMediaplayer.STATE_IDLE) {
                mMediaPlayer.start();
            }
        } else if (v == mBack) {
            if (mMediaPlayer.mCurrentMode == CustomMediaplayer.MODE_FULL_SCREEN) {
                mMediaPlayer.exitFullScreen();
            } else if (mMediaPlayer.mCurrentMode == CustomMediaplayer.MODE_SMAIL_WINDOW) {
                mMediaPlayer.exitSmailWindow();
            }
        } else if (v == mRestartPause) {
            if (mMediaPlayer.mCurrentState == CustomMediaplayer.STATE_PLAYING || mMediaPlayer.mCurrentState == CustomMediaplayer.STATE_BUFFERING_PLAYING) {
                mMediaPlayer.pause();
            } else if (mMediaPlayer.mCurrentState == CustomMediaplayer.STATE_PAUSE || mMediaPlayer.mCurrentState == CustomMediaplayer.STATE_BUFFERING_PAUSE) {
                mMediaPlayer.reStart();
            }
        } else if (v == mFullScreen) {
            if (mMediaPlayer.mCurrentMode == CustomMediaplayer.MODE_NORMAL || mMediaPlayer.mCurrentMode == CustomMediaplayer.MODE_SMAIL_WINDOW) {
                mMediaPlayer.enterFullScreen();
            }
        } else if (v == mRetry) {
            mMediaPlayer.reStart();
        } else if (v == mReplay) {
            mMediaPlayer.reStart();
        } else if (v == mShare) {
            Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show();
        } else if (this == v) {
            setTopBottomVisible(!mIsTopBottomVisible);
        }
    }

    /**
     * 设置top、bottom的显示和隐藏
     *
     * @param visible true显示，false隐藏.
     */
    private void setTopBottomVisible(boolean visible) {
        mTop.setVisibility(visible ? View.VISIBLE : View.GONE);
        mBottom.setVisibility(visible ? View.VISIBLE : View.GONE);
        mIsTopBottomVisible = visible;
        if (visible) {
            if (mMediaPlayer.mCurrentState != CustomMediaplayer.STATE_PAUSE && mMediaPlayer.mCurrentState != CustomMediaplayer.STATE_BUFFERING_PAUSE) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    CountDownTimer mDismissTopBottomCountDownTimer;

    /**
     * 开启top、bottom自动消失的timer
     */
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    /**
     * 取消top、bottom自动消失的timer
     */
    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mMediaPlayer.mCurrentState == CustomMediaplayer.STATE_PAUSE || mMediaPlayer.mCurrentState == CustomMediaplayer.STATE_BUFFERING_PAUSE) {
            mMediaPlayer.reStart();
        }
        int position = (int) (mMediaPlayer.getDuration() * seekBar.getProgress() / 100f);
        mMediaPlayer.seekTo(position);
        startDismissTopBottomTimer();
    }

    public void onPlayModeChanged(int playMode) {
        switch (playMode) {
            case CustomMediaplayer.MODE_NORMAL:
                mBack.setVisibility(View.GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                mFullScreen.setVisibility(View.VISIBLE);
                mClarity.setVisibility(View.GONE);
                mBatteryTime.setVisibility(View.GONE);
//                if (hasRegisterBatteryReceiver) {
//                    mContext.unregisterReceiver(mBatterReceiver);
//                    hasRegisterBatteryReceiver = false;
//                }
                break;
            case CustomMediaplayer.MODE_FULL_SCREEN:
                mBack.setVisibility(View.VISIBLE);
                mFullScreen.setVisibility(View.GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);
//                if (clarities != null && clarities.size() > 1) {
//                    mClarity.setVisibility(View.VISIBLE);
//                }
                mBatteryTime.setVisibility(View.VISIBLE);
//                if (!hasRegisterBatteryReceiver) {
//                    mContext.registerReceiver(mBatterReceiver,
//                            new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//                    hasRegisterBatteryReceiver = true;
//                }
                break;
            case CustomMediaplayer.MODE_SMAIL_WINDOW:
                mBack.setVisibility(View.VISIBLE);
                mClarity.setVisibility(View.GONE);
                break;
        }
    }
}
