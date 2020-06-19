/*
 * Copyright (C) 2016 tag
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.tag.musicplayer.appearances.player;

import java.io.InputStream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.drawable;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.core.PlayerRepeatState;
import jp.tag.musicplayer.core.PlayerService;
import jp.tag.musicplayer.core.PlayerServiceController;
import jp.tag.musicplayer.core.PlayerShuffleState;
import jp.tag.musicplayer.core.PlayerState;
import jp.tag.musicplayer.managers.SettingsSharedPreferences;
import android.widget.TextView;


public class PlayerFragment extends Fragment {

  protected static final String TAG = "PlayerFragment";

  Resources res = null;
  Activity activity;

  // PlayerService 関連
  PlayerServiceController controller = null;

  private ImageView ivAlbumArt;
  private ScrollView svPlayerInfoArea;
  private LinearLayout llTrackInfoArea;
  private TextView tvTrackName, tvArtistName, tvAlbumName, tvCurrentTime, tvDuration, tvTrackNo;
  private SeekBar sb;
  private Button bPlayPause, bStop, bRewind, bForward, bShuffle, bRepeat;

  private View viewTrackInfo;
  private TextView tvInfoTitle, tvInfoArtist, tvInfoAlbum, tvInfoYear, tvInfoGenre, tvInfoProducer, tvInfoProducerArtist, tvInfoComposer, tvInfoComposer2, tvInfoComment, tvInfoFormat, tvInfoEncType, tvInfoBitrate, tvInfoPath;
  
  private boolean touchingSeekBar = false;

  /**
   * コントロール
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    //    Log.d(TAG, "onCreate()");
    super.onCreate(savedInstanceState);
    // PlayerService 関連
    activity = getActivity();
    controller = new PlayerServiceController(activity);
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    //    Log.d(TAG, "onCreateView()");
    View v = inflater.inflate(R.layout.fragment_player, container, false);

    ivAlbumArt = (ImageView)v.findViewById(R.id.imageView_albumArt);

    svPlayerInfoArea = (ScrollView)v.findViewById(R.id.scrollView_player_info_area);
    
    llTrackInfoArea = (LinearLayout)v.findViewById(R.id.linearLayout_console_track_info_area);
    
    tvTrackName = (TextView)v.findViewById(R.id.textView_console_trackName);
    tvArtistName = (TextView)v.findViewById(R.id.textView_console_artistName);
    tvAlbumName = (TextView)v.findViewById(R.id.textView_console_albumName);

    sb = (SeekBar)v.findViewById(R.id.seekBar_console_progress);
    tvCurrentTime = (TextView)v.findViewById(R.id.textView_console_time_current);
    tvDuration = (TextView)v.findViewById(R.id.textView_console_time_duration);
    tvTrackNo = (TextView)v.findViewById(R.id.textView_console_track_number);

    bPlayPause = (Button)v.findViewById(R.id.button_console_play_and_pause);
    bStop = (Button)v.findViewById(R.id.button_console_stop);
    bRewind = (Button)v.findViewById(R.id.button_console_rewind);
    bForward = (Button)v.findViewById(R.id.button_console_forward);
    bShuffle = (Button)v.findViewById(R.id.button_console_shuffle);
    bRepeat = (Button)v.findViewById(R.id.button_console_repeat);

    // 保存データ読み出しの準備
    SettingsSharedPreferences preferences = new SettingsSharedPreferences(getActivity());

    // 詳細エリアの追加
    viewTrackInfo = inflater.inflate(R.layout.fragment_player_track_detail, container, false);
    svPlayerInfoArea.addView(viewTrackInfo);
    
    // 詳細エリアの情報
    tvInfoTitle = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_title_value);
    tvInfoArtist = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_artist_value);
    tvInfoAlbum = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_album_value);
    tvInfoYear = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_year_value);
    tvInfoGenre = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_genre_value);
    tvInfoProducer = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_producer_value);
    tvInfoProducerArtist = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_producerArtist_value);
    tvInfoComposer = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_composer_value);
    tvInfoComposer2 = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_composer2_value);
    tvInfoComment = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_comment_value);
    tvInfoFormat = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_format_value);
    tvInfoEncType = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_encType_value);
    tvInfoBitrate = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_bitrate_value);
    tvInfoPath = (TextView)viewTrackInfo.findViewById(R.id.textView_track_detail_path_value);
    
    
    llTrackInfoArea.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int visibility = View.GONE;
        ivAlbumArt.setAlpha(255);
        if (svPlayerInfoArea.getVisibility() == View.GONE) {  // 非表示状態であるなら、表示状態に
          visibility = View.VISIBLE;
          ivAlbumArt.setAlpha(100);
        }
        svPlayerInfoArea.setVisibility(visibility);
      }
    });
    
    
    // SeekBar
    sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {
        touchingSeekBar = true;
      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        if (controller.checkBindStatus()) {
          controller.service._Seek(sb.getProgress());
        }
        touchingSeekBar = false;
      }
    });

    // |> and ||
    bPlayPause.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (controller.checkBindStatus()) {
          PlayerState state = controller.service._PauseAndResume();  // 状態切替
          if (state == PlayerState.PLAY) {  // 再生状態ならば
            bPlayPause.setBackgroundResource(R.drawable.ic_console_pause);  // アイコンを || に
          } else if (state == PlayerState.PAUSE) {  // 一時停止状態ならば
            bPlayPause.setBackgroundResource(R.drawable.ic_console_play);  // アイコンを |> に
          }
        }
      }
    });

    // □
    bStop.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (controller.checkBindStatus()) {
          controller.service._Stop();
        }
      }
    });

    // |<<
    bRewind.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (controller.checkBindStatus()) {
          controller.service._Rewind();
        }
      }
    });

    // >>|
    bForward.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (controller.checkBindStatus()) {
          controller.service._Forward();
        }
      }
    });

    // シャッフル
    int shuffleResId = R.drawable.ic_console_shuffle_off;
    if (preferences.getShuffleState() == PlayerShuffleState.ON) {
      shuffleResId = R.drawable.ic_console_shuffle_on;
    }
    bShuffle.setBackgroundResource(shuffleResId);
    bShuffle.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (controller.checkBindStatus()) {
          PlayerShuffleState state = controller.service._ChangeShuffleState();
          if (state == PlayerShuffleState.OFF) {
            bShuffle.setBackgroundResource(R.drawable.ic_console_shuffle_off);
          } else if (state == PlayerShuffleState.ON) {
            bShuffle.setBackgroundResource(R.drawable.ic_console_shuffle_on);
          }
        }
      }
    });

    // リピート
    int repeatResId = R.drawable.ic_console_repeat_off;
    if (preferences.getRepeatState() == PlayerRepeatState.ONE) {
      repeatResId = R.drawable.ic_console_repeat_one;
    } else if (preferences.getRepeatState() == PlayerRepeatState.ALL) {
      repeatResId = R.drawable.ic_console_repeat_all;
    }
    bRepeat.setBackgroundResource(repeatResId);
    bRepeat.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (controller.checkBindStatus()) {
          PlayerRepeatState state = controller.service._ChangeRepeatState();
          if (state == PlayerRepeatState.OFF) {
            bRepeat.setBackgroundResource(R.drawable.ic_console_repeat_off);
          } else if (state == PlayerRepeatState.ONE) {
            bRepeat.setBackgroundResource(R.drawable.ic_console_repeat_one);
          } else if (state == PlayerRepeatState.ALL) {
            bRepeat.setBackgroundResource(R.drawable.ic_console_repeat_all);
          }
        }
      }
    });

    return v;
  }

  @Override
  public void onStart() {
    //    Log.d(TAG, "onStart()");
    super.onStart();
    controller.bindService();  // バインド

    // 音楽情報のリクエスト
    mHandler = new Handler();
    mHandler.post(runnable);
  }

  @Override
  public void onResume() {
    //    Log.d(TAG, "onResume()");
    registerReceivers();
    super.onResume();

  }

  @Override
  public void onPause() {
    //    Log.d(TAG, "onPause()");
    unregisterReceivers();
    super.onPause();
  }
  @Override
  public void onDestroy() {
    controller.unbindService();  // アンバインド
    super.onDestroy();
  }

  /**
   * プレイヤに表示する情報の配信リクエスト (バインド処理完了待ちをする)
   */
  private Handler mHandler;
  private Runnable runnable = new Runnable(){
    public void run() {
      if (controller.checkBindStatus()) {  // 準備ができていたら
        controller.service.broadcastTrackInfo();  // プレイヤに表示する情報の配信リクエスト
      } else {  // まだ準備ができていなかったら
        mHandler.postDelayed(runnable, 100);  // もう一度待つ
      }
    }
  };

  /**
   * 音楽情報周りのレシーバ
   */
  private BroadcastReceiver mTrackInfoReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, final Intent intent) {
      //      Log.d(TAG, "onReceive (Info)");
      tvTrackName.setText(intent.getStringExtra("trackName"));
      tvArtistName.setText(intent.getStringExtra("artistName"));
      tvAlbumName.setText(intent.getStringExtra("albumName"));

      bPlayPause.setBackgroundResource( intent.getBooleanExtra("playing", false) ? R.drawable.ic_console_pause : R.drawable.ic_console_play );  // アイコンを || or |> に

      Uri albumArtUri = Uri.parse("content://media/external/audio/albumart");
      long albumId = intent.getLongExtra("albumId", -1L);
      if (albumId != -1L) {
        Uri albumUri = ContentUris.withAppendedId(albumArtUri, albumId);
        ContentResolver cr = activity.getContentResolver();
        try {
          InputStream is = cr.openInputStream(albumUri);
          Bitmap albumArtBmp = BitmapFactory.decodeStream(is);
          ivAlbumArt.setImageBitmap(albumArtBmp);
        } catch (Exception e) {
          ivAlbumArt.setImageResource(R.drawable.ic_defalut_artwork_large);
        }
      } else {
        ivAlbumArt.setImageResource(R.drawable.ic_defalut_artwork_large);
      }

      sb.setProgress(intent.getIntExtra("progress", 0));
      tvCurrentTime.setText(intent.getStringExtra("currentTime"));
      tvDuration.setText(intent.getStringExtra("duration"));
      tvTrackNo.setText(intent.getStringExtra("trackNo"));
      
      tvInfoTitle.setText(intent.getStringExtra("detailTitle"));
      tvInfoArtist.setText(intent.getStringExtra("detailArtist"));
      tvInfoAlbum.setText(intent.getStringExtra("detailAlbum"));
      tvInfoYear.setText(intent.getStringExtra("detailYear"));
      tvInfoGenre.setText(intent.getStringExtra("detailGenre"));
      tvInfoProducer.setText(intent.getStringExtra("detailProducer"));
      tvInfoProducerArtist.setText(intent.getStringExtra("detailProducerArtist"));
      tvInfoComposer.setText(intent.getStringExtra("detailComposer"));
      tvInfoComposer2.setText(intent.getStringExtra("detailComposer2"));
      tvInfoFormat.setText(intent.getStringExtra("detailFormat"));
      tvInfoEncType.setText(intent.getStringExtra("detailEncType"));
      tvInfoBitrate.setText(intent.getStringExtra("detailBitrate"));
      tvInfoComment.setText(intent.getStringExtra("detailComment"));
      tvInfoPath.setText(intent.getStringExtra("detailPath"));
      
    }
  };

  /**
   * 再生時間周りのレシーバ
   */
  private BroadcastReceiver mTrackTimeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, final Intent intent) {
      //      Log.d(TAG, "onReceive (Time)");
      bPlayPause.setBackgroundResource( intent.getBooleanExtra("playing", false) ? R.drawable.ic_console_pause : R.drawable.ic_console_play);  // アイコンを || or |> に
      tvCurrentTime.setText(intent.getStringExtra("currentTime"));
      if (!touchingSeekBar) {  // SeekBar に触れているときには、変更しない
        sb.setProgress(intent.getIntExtra("progress", 0));
      }
    }
  };

  /**
   * レシーバの登録
   */
  public void registerReceivers() {
    IntentFilter filterTrackInfo = new IntentFilter();
    filterTrackInfo.addAction(PlayerService.NOTIFY_TRACK_INFO_ACTION);
    activity.registerReceiver(mTrackInfoReceiver, filterTrackInfo);

    IntentFilter filterTrackTime = new IntentFilter();
    filterTrackTime.addAction(PlayerService.NOTIFY_TRACK_TIME_ACTION);
    activity.registerReceiver(mTrackTimeReceiver, filterTrackTime);
  }

  /**
   * レシーバの登録解除
   */
  public void unregisterReceivers() {
    activity.unregisterReceiver(mTrackInfoReceiver);
    activity.unregisterReceiver(mTrackTimeReceiver);
  }

}
