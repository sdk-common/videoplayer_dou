package com.dou.samples.mediaplayerdemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;

/**
 * Created by Dou on 2017/10/26.
 */

public class CustomMediaplayer extends FrameLayout implements TextureView.SurfaceTextureListener {

    private static final String TAG = CustomMediaplayer.class.getSimpleName();
    
    public static final int STATE_ERROR = -1; // 播放错误
    public static final int STATE_IDLE = 0; // 播放未开始
    public static final int STATE_PREPARING = 1; // 播放准备中
    public static final int STATE_PREPARED = 2; // 播放准备完成
    public static final int STATE_PLAYING = 3; // 正在播放
    public static final int STATE_PAUSE = 4; // 播放暂停
    public static final int STATE_BUFFERING_PLAYING = 5;// 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放)
    public static final int STATE_BUFFERING_PAUSE = 6; // 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停)
    public static final int STATE_COMPLETION = 7; // 播放完成

    public static final int MODE_NORMAL = 10; //
    public static final int MODE_FULL_SCREEN = 11; //
    public static final int MODE_SMAIL_WINDOW = 12; //

    public int mCurrentMode;

    Context mContext;

    FrameLayout mContainer;
    CustomMediaController mMediaController;
    TextureView mTextureView;

    MediaPlayer mMediaPlayer;

    int mCurrentState = 0;
    int mPlayMode = 10;

    String mUri;

    MediaPlayer.OnPreparedListener mOnPrepareListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            Log.d(TAG, "onPrepared: ");
            mediaPlayer.start();
            mCurrentState = STATE_PREPARED;
            mMediaController.setControllerState(mCurrentState);
        }
    };
    
    MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            Log.d(TAG, "onCompletion: ");
            mCurrentState = STATE_COMPLETION;
            mMediaController.setControllerState(mCurrentState);
        }
    };
    MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            Log.d(TAG, "onError: ");
            mCurrentState = STATE_ERROR;
            mMediaController.setControllerState(mCurrentState);
            return false;
        }
    };
    MediaPlayer.OnVideoSizeChangedListener mOnViedoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
            Log.d(TAG, "onVideoSizeChanged ——> width：" + width + "，height：" + height);
        }
    };
    MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
            Log.d(TAG, "onInfo: " + what);
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                mCurrentState = STATE_PREPARING;
                mMediaController.setControllerState(mCurrentState);
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                if (mCurrentState == STATE_PLAYING) {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    mMediaController.setControllerState(mCurrentState);
                } else if (mCurrentState == STATE_PAUSE) {
                    mCurrentState = STATE_BUFFERING_PAUSE;
                    mMediaController.setControllerState(mCurrentState);
                }
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    mMediaController.setControllerState(mCurrentState);
                }
                if (mCurrentState == STATE_BUFFERING_PAUSE) {
                    mCurrentState = STATE_PAUSE;
                    mMediaController.setControllerState(mCurrentState);
                }
            }
            return false;
        }
    };
    MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            Log.d(TAG, "onBufferingUpdate: ");
        }
    };


    public CustomMediaplayer(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomMediaplayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomMediaplayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        ViewGroup.LayoutParams lm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, lm);
    }

    public void setUp(String uri) {
        mUri = uri;
    }

    public void setController (CustomMediaController controller) {
        mMediaController = controller;
        mMediaController.setMediaPlayer(this);
        ViewGroup.LayoutParams lm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mMediaController, lm);
    }

    public void start() {
        Log.d(TAG, "start: start play");
        if (mCurrentState == STATE_IDLE || mCurrentState == STATE_ERROR || mCurrentState == STATE_COMPLETION) {
            initMediaPlayer();
            initTextureView();
            addTextView();
        }
    }

    public void reStart() {
        if (mCurrentState == STATE_PAUSE) {
            Log.d(TAG, "reStart: STATE_PAUSE");
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mMediaController.setControllerState(mCurrentState);
        } else if (mCurrentState == STATE_BUFFERING_PAUSE) {
            Log.d(TAG, "reStart: STATE_BUFFERING_PAUSE");
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            mMediaController.setControllerState(mCurrentState);
        }
    }

    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            Log.d(TAG, "pause: STATE_PLAYING");
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSE;
            mMediaController.setControllerState(mCurrentState);
        } else if (mCurrentState == STATE_BUFFERING_PLAYING) {
            Log.d(TAG, "pause: STATE_BUFFERING_PLAYING");
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSE;
            mMediaController.setControllerState(mCurrentState);
        }
    }

    private void addTextView() {
        mContainer.removeAllViews();
        ViewGroup.LayoutParams lm = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mTextureView, 0, lm);
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new TextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);

            mMediaPlayer.setOnPreparedListener(mOnPrepareListener);
            mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            mMediaPlayer.setOnErrorListener(mOnErrorListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mOnViedoSizeChangedListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        // surfaceTexture数据通道准备就绪，打开播放器
        openMediaPlayer(surfaceTexture);
    }

    private void openMediaPlayer(SurfaceTexture surfaceTexture) {
        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(mUri));
            mMediaPlayer.setSurface(new Surface(surfaceTexture));
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    /**
     * 全屏，将mContainer(内部包含mTextureView和mController)从当前容器中移除，并添加到android.R.content中.
     * 切换横屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期
     */
    public void enterFullScreen() {
        if (mCurrentMode == MODE_FULL_SCREEN) return;

        // 隐藏ActionBar、状态栏，并横屏
        Utils.hideActionBar(mContext);
        Utils.scanForActivity(mContext)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ViewGroup contentView = (ViewGroup) Utils.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        if (mCurrentMode == MODE_SMAIL_WINDOW) {
            contentView.removeView(mContainer);
        } else {
            this.removeView(mContainer);
        }
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);

        mCurrentMode = MODE_FULL_SCREEN;
        mMediaController.onPlayModeChanged(mCurrentMode);
        Log.d(TAG,"MODE_FULL_SCREEN");
    }

    /**
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     * 切换竖屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期.
     *
     * @return true退出全屏.
     */
    public boolean exitFullScreen() {
        if (mCurrentMode == MODE_FULL_SCREEN) {
            Utils.showActionBar(mContext);
            Utils.scanForActivity(mContext)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            ViewGroup contentView = (ViewGroup) Utils.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mCurrentMode = MODE_NORMAL;
            mMediaController.onPlayModeChanged(mCurrentMode);
            Log.d(TAG,"MODE_NORMAL");
            return true;
        }
        return false;
    }

    /**
     * 进入小窗口播放，小窗口播放的实现原理与全屏播放类似。
     */
    public void enterSmailWindow() {
        if (mCurrentMode == MODE_SMAIL_WINDOW) return;
        this.removeView(mContainer);

        ViewGroup contentView = (ViewGroup) Utils.scanForActivity(mContext)
                .findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                (int) (Utils.getScreenWidth(mContext) * 0.6f),
                (int) (Utils.getScreenWidth(mContext) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = Utils.dp2px(mContext, 8f);
        params.bottomMargin = Utils.dp2px(mContext, 8f);

        contentView.addView(mContainer, params);

        mCurrentMode = MODE_SMAIL_WINDOW;
        mMediaController.onPlayModeChanged(mCurrentMode);
        Log.d(TAG,"MODE_TINY_WINDOW");
    }

    /**
     * 退出小窗口播放
     */
    public boolean exitSmailWindow() {
        if (mCurrentMode == MODE_SMAIL_WINDOW) {
            ViewGroup contentView = (ViewGroup) Utils.scanForActivity(mContext)
                    .findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);

            mCurrentMode = MODE_NORMAL;
            mMediaController.onPlayModeChanged(mCurrentMode);
            Log.d(TAG,"MODE_NORMAL");
            return true;
        }
        return false;
    }

    public long getDuration () {
        return mMediaPlayer.getDuration();
    }

    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }
}
