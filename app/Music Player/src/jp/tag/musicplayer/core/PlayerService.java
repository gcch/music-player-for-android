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

package jp.tag.musicplayer.core;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.drawable;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.R.string;
import jp.tag.musicplayer.activities.BrowserActivity;
import jp.tag.musicplayer.appearances.MenuItems;
import jp.tag.musicplayer.appearances.albums.AlbumArtworksAsyncTask;
import jp.tag.musicplayer.managers.SettingsSharedPreferences;
import jp.tag.musicplayer.objects._Track;

public class PlayerService extends Service {

  private static final String TAG = "PlayerService";
  public static final String NOTIFY_TRACK_INFO_ACTION = "PlayerService_NotifyTrackInfo";
  public static final String NOTIFY_TRACK_TIME_ACTION = "PlayerService_NotifyTrackTime";

  private static MediaPlayer mp;
  private static ArrayList<_Track> tracklist;
  private static int tracklistPos;

  private PlayerState currentPlayerState = PlayerState.PLAY;
  private boolean isPlaying() {
    return (currentPlayerState == PlayerState.PLAY);
  }

  SettingsSharedPreferences preferences = null;
  private static PlayerRepeatState repeatState = PlayerRepeatState.OFF;
  private static PlayerShuffleState shuffleState = PlayerShuffleState.OFF;

  private int seekBarMax = 1000;

  private final IBinder binder = new PlayerServiceBinder();
  public class PlayerServiceBinder extends Binder {
    PlayerService getService() {
      return PlayerService.this;
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return binder;
  }
  @Override
  public boolean onUnbind(Intent intent) {
    return true;
  }

  /**
   * onCreate
   */
  @Override
  public void onCreate() {
    super.onCreate();
    // 保存データ読み出しの準備
    preferences = new SettingsSharedPreferences(getApplicationContext());
    shuffleState = preferences.getShuffleState();
    repeatState = preferences.getRepeatState();
  }

  /**
   * onStart
   */
  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
  }

  /**
   * onStartCommand
   */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    onStart(intent, startId);
    return START_STICKY;
  }

  /**
   * onDestroy
   */
  @Override
  public void onDestroy() {
    stopReceiver();
    stopSelf();
    super.onDestroy();
  }

  /**
   * |>
   * @param list
   * @param trackNo
   */
  public void _Play(ArrayList<_Track> list, int trackNo) {
    Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();

    // 再生リスト
    if (tracklist != null && !tracklist.isEmpty()) {
      tracklist.clear();
    }
    tracklist = new ArrayList<_Track>(list);
    tracklistPos = trackNo;  // 現在の再生位置

    // 初期化
    if (mp == null) {
      mp = new MediaPlayer();
    }

    // 既に再生中の場合にはまず停止
    if (mp != null && mp.isPlaying()) {
      mp.stop();
      mp.reset();
      currentPlayerState = PlayerState.PAUSE;
    }

    // データ読み込み
    try {
      mp.setDataSource(tracklist.get(tracklistPos).path);
      mp.prepare();
    } catch (Exception e) {
    }

//    stopTimerRunnable = true;
    broadcastTrackInfo();

    mp.setOnCompletionListener(new OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {  // ファイル終端到達時の処理
        //        Log.d(TAG, "onCompletion()");
        // 次の曲を再生
        if (mp.isPlaying()) {
          mp.stop();
        }
        mp.reset();

        if (repeatState == PlayerRepeatState.OFF) {  // リピート OFF --- 最後まで行ったら再生を終了
          if (++tracklistPos >= tracklist.size()) {
            _Stop();
            return;
          }
        } else if (repeatState == PlayerRepeatState.ONE) {  // 1曲ループ
          // 特に何もしない
        } else if (repeatState == PlayerRepeatState.ALL) {  // 終端まで行ったら頭に戻る
          tracklistPos = ++tracklistPos % tracklist.size();
        }

        try {
          mp.setDataSource(tracklist.get(tracklistPos).path);
          mp.prepare();
        } catch (Exception e) {
        }
        broadcastTrackInfo();
        updateNotification();  // 通知領域への通知
        mp.start();
      }
    });

    mp.start();
    currentPlayerState = PlayerState.PLAY;  // 再生状態を保持

    startNotification();  // 通知領域表示
    updateNotification(tracklist.get(tracklistPos));  // 通知領域への通知
  }

  /**
   * プレイヤーコンソール: || or |>
   */
  public PlayerState _PauseAndResume() {
    if (mp != null) {
      if (mp.isPlaying()) {
//        stopTimerRunnable = true;
        //        Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
        mp.pause();
        currentPlayerState = PlayerState.PAUSE;
      } else {
        startTimeBroadcast();
        //        Toast.makeText(getApplicationContext(), "Resume", Toast.LENGTH_SHORT).show();
        mp.start();
        currentPlayerState = PlayerState.PLAY;
      }
    } else {  // MediaPlayer が初期化サれている状態
      currentPlayerState = PlayerState.UNKNOW;
    }
    return currentPlayerState;
  }

  /**
   * プレイヤーコンソール: □
   */
  public void _Stop() {
    //    Log.d(TAG, "_Stop()");
    stopNotification();
//    stopTimerRunnable = true;
    if (mp != null) {
      //      Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
      if (mp.isPlaying()) {
        mp.stop();
      }
      mp.reset();
      mp.release();  // 完全に終了
      mp = null;
    }
    if (tracklist != null && !tracklist.isEmpty()) {
      tracklist.clear();
      tracklist = null;
    }
    broadcastTrackInfo();  // プレイヤ情報の初期化
    onDestroy();
  }

  /**
   * プレイヤーコンソール: |<<
   */
  public void _Rewind() {
    //    Toast.makeText(getApplicationContext(), "Rewind", Toast.LENGTH_SHORT).show();
    if (mp != null) {
      mp.pause();
      if (mp.getCurrentPosition() < 1000) {  // 現在の位置が 1 秒未満であったら、前の曲を再生
        if (mp.isPlaying()) {
          mp.stop();
        }
        mp.reset();
        try {
          if (--tracklistPos < 0) {  // 先頭トラックだったら、最終トラックに移動
            tracklistPos += tracklist.size();
          }
          mp.setDataSource(tracklist.get(tracklistPos).path);  // 次に再生する曲をセット
          mp.prepare();  // 準備
        } catch (Exception e) {
        }
        if (currentPlayerState == PlayerState.PLAY) {  // 再生状態だったら
          mp.start();
        }
        broadcastTrackInfo();
        updateNotification();
      } else {  // 現在の位置が 1 秒以降であったら、曲の先頭に移動
        mp.seekTo(0);
        if (currentPlayerState == PlayerState.PLAY) {  // 再生状態だったら
          mp.start();
        }
      }
    }
  }

  /**
   * プレイヤーコンソール: >>|
   */
  public void _Forward() {
    //    Log.d(TAG, "_Forward()");
    if (mp != null) {
//      stopTimerRunnable = true;

      // OnConplation とほぼ同じ。
      if (mp.isPlaying()) {
        mp.pause();
      }
      if (repeatState == PlayerRepeatState.OFF) {  // リピート OFF --- 最後まで行ったら再生を終了
        if (++tracklistPos >= tracklist.size()) {
          _Stop();
          return;
        }
      } else if (repeatState == PlayerRepeatState.ONE) {  // 1曲ループ

      } else if (repeatState == PlayerRepeatState.ALL) {  // 終端まで行ったら頭に戻る
        tracklistPos = ++tracklistPos % tracklist.size();
      }
      mp.reset();
      try {
        mp.setDataSource(tracklist.get(tracklistPos).path);
        mp.prepare();
      } catch (Exception e) {
      }
      broadcastTrackInfo();
      updateNotification();
      if (currentPlayerState == PlayerState.PLAY) {  // 再生状態だったら
        mp.start();
      }
    }
  }

  /**
   * プレイヤーコントロール: シーク
   * @param msec
   */
  public void _Seek(int progress) {
    if (mp != null) {
      int msec = progress * (int)(tracklist.get(tracklistPos).duration) / seekBarMax;
      mp.seekTo(msec);
    }
  }


  /**
   * プレイヤーコンソール: シャッフル
   * @return
   */
  public PlayerShuffleState _ChangeShuffleState() {
    if (shuffleState == PlayerShuffleState.OFF) {
      //      Toast.makeText(getApplicationContext(), "Shuffle: Songs", Toast.LENGTH_SHORT).show();
      shuffleState = PlayerShuffleState.ON;
    } else if (shuffleState == PlayerShuffleState.ON) {
      //      Toast.makeText(getApplicationContext(), "Shuffle: Off", Toast.LENGTH_SHORT).show();
      shuffleState = PlayerShuffleState.OFF;
    }
    preferences.putShuffleState(shuffleState);  // 保存
    return shuffleState;
  }

  /**
   * プレイヤーコンソール: リピート
   * @return
   */
  public PlayerRepeatState _ChangeRepeatState() {
    if (repeatState == PlayerRepeatState.OFF) {  // リピート OFF --- 最後まで行ったら再生を終了
      //      Toast.makeText(getApplicationContext(), "Repeat: One", Toast.LENGTH_SHORT).show();
      repeatState = PlayerRepeatState.ONE;
    } else if (repeatState == PlayerRepeatState.ONE) {  // 1曲ループ
      //      Toast.makeText(getApplicationContext(), "Repeat: All", Toast.LENGTH_SHORT).show();
      repeatState = PlayerRepeatState.ALL;
    } else if (repeatState == PlayerRepeatState.ALL) {  // 終端まで行ったら頭に戻る
      //      Toast.makeText(getApplicationContext(), "Repeat: Off", Toast.LENGTH_SHORT).show();
      repeatState = PlayerRepeatState.OFF;
    }
    preferences.putRepeatState(repeatState);  // 保存
    return repeatState;
  }

  /**
   * トラック情報のブロードキャスト
   */
  public void broadcastTrackInfo() {
    if (tracklist != null) {
      _Track track = tracklist.get(tracklistPos);
      Intent i = new Intent(NOTIFY_TRACK_INFO_ACTION);
      i.putExtra("playing", isPlaying());
      i.putExtra("trackName", track.title);
      i.putExtra("artistName", track.artist);
      i.putExtra("albumName", track.album);
      i.putExtra("albumId", track.albumId);
      //      Log.d(TAG, "AlbumId: " + track.albumId);
      long now = mp.getCurrentPosition();
      i.putExtra("progress", (int)(now * seekBarMax / track.duration));
      long dm = now / 60000;
      long ds = (now - (dm * 60000)) / 1000;
      i.putExtra("currentTime", String.format("%d:%02d", dm, ds));
      dm = track.duration / 60000;
      ds = (track.duration - (dm * 60000)) / 1000;
      i.putExtra("duration", String.format("%d:%02d", dm, ds));
      i.putExtra("trackNo", (tracklistPos + 1) + " of " + tracklist.size());
      i.putExtra("detailTitle", track.title);
      i.putExtra("detailArtist", track.artist);
      i.putExtra("detailAlbum", track.album);
      i.putExtra("detailYear", track.year);
      i.putExtra("detailGenre", track.genre);
      i.putExtra("detailProducer", track.producer);
      i.putExtra("detailProducerArtist", track.producerArtist);
      i.putExtra("detailComposer", track.composer);
      i.putExtra("detailComposer2", track.composer2);
      i.putExtra("detailComment", track.comment);
      i.putExtra("detailFormat", track.format);
      i.putExtra("detailEncType", track.encType);
      i.putExtra("detailBitrate", track.bitrate + "kbps");
      i.putExtra("detailPath", track.path);
      sendBroadcast(i);

      startTimeBroadcast();
    } else {  // プレイヤのリセット処理
      Intent i = new Intent(NOTIFY_TRACK_INFO_ACTION);
      String detail_value_default = getResources().getString(R.string.track_detail_value_default);
      i.putExtra("playing", isPlaying());
      i.putExtra("trackName", "No title");
      i.putExtra("artistName", "No artist");
      i.putExtra("albumName", "No album");
      i.putExtra("albumId", -1L);
      i.putExtra("progress", 0);
      i.putExtra("currentTime", "0:00");
      i.putExtra("duration", "0:00");
      i.putExtra("trackNo", "0 of 0");
      i.putExtra("detailTitle", "No title");
      i.putExtra("detailArtist", "No artist");
      i.putExtra("detailAlbum", detail_value_default);
      i.putExtra("detailYear", detail_value_default);
      i.putExtra("detailGenre", detail_value_default);
      i.putExtra("detailProducer", detail_value_default);
      i.putExtra("detailProducerArtist", detail_value_default);
      i.putExtra("detailComposer", detail_value_default);
      i.putExtra("detailComposer2", detail_value_default);
      i.putExtra("detailBitrate", detail_value_default);
      i.putExtra("detailComment", detail_value_default);
      i.putExtra("detailPath", detail_value_default);


      sendBroadcast(i);
    }
  }

  /**
   * 時間情報のブロードキャスト
   */
  private Handler handlerForTrackTime = new Handler();
  private Runnable r;
//  boolean stopTimerRunnable = false;
  private void startTimeBroadcast() {
//    stopTimerRunnable = false;
    r = new TimerRunnable();
    handlerForTrackTime.postDelayed(r, 1000);
  }

  public class TimerRunnable implements Runnable {
    public void run() {
//      Log.d(TAG, "TimerRunnable#run()");
      
      if (tracklist == null || tracklist.isEmpty()) {  // もし tracklist が null or 空だったら
//        Log.d(TAG, "TimerRunnable: tracklist is null or empty");
        return;
      }
      _Track track = tracklist.get(tracklistPos);
      Intent i = new Intent(NOTIFY_TRACK_TIME_ACTION);
      i.putExtra("playing", isPlaying());  // 再生状態を通知

      long now = mp.getCurrentPosition();
      long dm = now / 60000;
      long ds = (now - (dm * 60000)) / 1000;
      i.putExtra("currentTime", String.format("%d:%02d", dm, ds));
      i.putExtra("progress", (int)(now * seekBarMax / track.duration));
      sendBroadcast(i);

//      if (!stopTimerRunnable) {
      if (isPlaying()) {
        handlerForTrackTime.removeCallbacks(r);
        handlerForTrackTime.postDelayed(r, 1000);
      }
    }
  };


  // Notification IDs
  final int WIDGET_ID = 12;
  final int NOTIFICATION_ID = 1234;
  private Notification notification = null;
  private NotificationManager nm = null;
  private RemoteViews rv;
  /**
   * startNotification --- 通知領域への表示 (通知領域 + 設定画面呼び出し + 死なない Service)
   */
  private void startNotification() {

    // 通知をタップした時に開く画面
    Intent activityIntent = new Intent(getApplicationContext(), BrowserActivity.class);
    activityIntent.putExtra("tabNo", MenuItems.PLAYER.ordinal());
    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), WIDGET_ID, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    // RemoteView
    rv = new RemoteViews(getPackageName(), R.layout.notification_console);
    rv.setImageViewResource(R.id.imageView_notification_albumArt, R.drawable.ic_defalut_artwork_small);
    rv.setTextViewText(R.id.textView_notification_trackName, "Title");
    rv.setTextViewText(R.id.textView_notification_artistName, "Artist");
    Context context = getBaseContext();
    rv.setOnClickPendingIntent(R.id.button_notification_play_and_pause, PendingIntent.getBroadcast(context, 0, new Intent(PLAYER_PLAY_AND_PAUSE_ACTION), 0));
    rv.setOnClickPendingIntent(R.id.button_notification_rewind, PendingIntent.getBroadcast(context, 0, new Intent(PLAYER_REWIND_ACTION), 0));
    rv.setOnClickPendingIntent(R.id.button_notification_forward, PendingIntent.getBroadcast(context, 0, new Intent(PLAYER_FORWARD_ACTION), 0));

    // 通知領域に表示
    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
    builder.setContentIntent(contentIntent);       // タップ時に呼び出す Activity
    builder.setTicker(getResources().getString(R.string.notification_ticker));  // ステータスバーに表示されるテキスト
    builder.setContent(rv);
    builder.setContentTitle(getResources().getString(R.string.app_name));  // タイトル
    builder.setContentText(getResources().getString(R.string.notification_content_text));             // タイトル下の説明文
    builder.setSmallIcon(R.drawable.ic_launcher);     // アイコン
    builder.setWhen(0);   // 通知タイミング

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
      builder.setPriority(NotificationCompat.PRIORITY_LOW);
    }
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      builder.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
      builder.setCategory(NotificationCompat.CATEGORY_SERVICE);
    }

    notification = builder.build();   // (ビルド)
    //    notification.contentView = rv;
    notification.flags |= NotificationCompat.FLAG_NO_CLEAR;  // 常時表示

    // 通知マネージャ
    nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify(NOTIFICATION_ID, notification);

    // フォアグランドサービスとして開始
    startForeground(NOTIFICATION_ID, notification);

    initReceiver();
  }

  /**
   * 通知の停止
   */
  private void stopNotification() {
    //    Log.d(TAG, "stopNotification()");
    stopForeground(true);  // フォアグランドサービスの停止 (やらないとサービスを止められなくなる)
    if (nm != null) {
      nm.cancel(NOTIFICATION_ID);
      nm = null;
    }
  }

  /**
   * 通知領域の表示情報の更新
   */
  private void updateNotification() {
    if (tracklist != null) {  // 最新情報に更新
      updateNotification(tracklist.get(tracklistPos));
    } else {  // 元に戻す
      rv.setImageViewResource(R.id.imageView_notification_albumArt, R.drawable.ic_defalut_artwork_small);  // デフォルト
      rv.setTextViewText(R.id.textView_notification_trackName, "Title");
      rv.setTextViewText(R.id.textView_notification_artistName, "Artist");
      nm.notify(NOTIFICATION_ID, notification);  // 通知領域の更新を通知 (再描画)
    }
  }

  /**
   * 通知領域の表示情報の更新
   * @param track
   */
  private void updateNotification(_Track track) {
    // ジャケット
    rv.setImageViewResource(R.id.imageView_notification_albumArt, R.drawable.ic_defalut_artwork_small);  // デフォルト

    // URI の取得
    Uri albumUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), track.albumId);
    //    String uriPath = albumUri.getPath();  // 仮想パス

    // URI から実ファイルパスの取得
    ContentResolver contentResolver = getContentResolver();
    String[] columns = { MediaStore.Images.Media.DATA };
    Cursor cursor = contentResolver.query(albumUri, columns, null, null, null);
    cursor.moveToFirst();
    if (cursor.getCount() > 0) {  // もし存在していたら
      String path = cursor.getString(0);
      Bitmap b = AlbumArtworksAsyncTask.decodeBitmap(path, 144, 144);  // アルバムアートの生成 (サイズ指定も)
      rv.setImageViewBitmap(R.id.imageView_notification_albumArt, b);
    }
    cursor.close();

    // 曲名とアーティスト名
    rv.setTextViewText(R.id.textView_notification_trackName, track.title);
    rv.setTextViewText(R.id.textView_notification_artistName, track.artist);

    nm.notify(NOTIFICATION_ID, notification);  // 通知領域の更新を通知 (再描画)
  }



  private final static String PLAYER_PLAY_AND_PAUSE_ACTION = "jp.tag.musicplayer.PlayerService.PLAYER_PLAY_AND_PAUSE_ACTION";
  private final static String PLAYER_REWIND_ACTION = "jp.tag.musicplayer.PlayerService.PLAYER_REWIND_ACTION";
  private final static String PLAYER_FORWARD_ACTION = "jp.tag.musicplayer.PlayerService.PLAYER_FORWARD_ACTION";
  private static NotificationConsoleReceiver mReceiver;

  public void initReceiver() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(PLAYER_PLAY_AND_PAUSE_ACTION);
    filter.addAction(PLAYER_REWIND_ACTION);
    filter.addAction(PLAYER_FORWARD_ACTION);
    mReceiver = new NotificationConsoleReceiver();
    registerReceiver(mReceiver, filter);
  }

  public void stopReceiver() {
    unregisterReceiver(mReceiver);
    mReceiver = null;
  }


  private static boolean cancelerStatus = false;
  private static Handler cancelerHandler = new Handler();

  public class NotificationConsoleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (cancelerStatus) {
        return;
      }
      String action = intent.getAction();
      //      Log.d(TAG, "onReceive: " + action);
      if (action.equals(PLAYER_PLAY_AND_PAUSE_ACTION)) {
        PlayerState state = _PauseAndResume();
        changePlayAndPauseIcon(state);
      }
      if (action.equals(PLAYER_REWIND_ACTION)) {
        _Rewind();
      }
      if (action.equals(PLAYER_FORWARD_ACTION)) {
        _Forward();
      }
      cancelerStatus = true;

      // チャタリング防止
      Timer cancelerTimer;
      cancelerTimer = new Timer();
      cancelerTimer.schedule(new TimerTask() {
        @Override
        public void run() {
          cancelerHandler.post( new Runnable() {
            public void run() {
              cancelerStatus = false;  // 戻す
            }
          });
        }
      }, 100);

    }
    public NotificationConsoleReceiver() {
    }

  }

  /**
   * 通知領域の再生／一時停止ボタンのアイコンを変えます
   * @param state
   */
  public void changePlayAndPauseIcon(PlayerState state) {
    if (state == PlayerState.PAUSE) {
      rv.setInt(R.id.button_notification_play_and_pause, "setBackgroundResource", R.drawable.ic_console_play);
    } else {
      rv.setInt(R.id.button_notification_play_and_pause, "setBackgroundResource", R.drawable.ic_console_pause);
    }
    nm.notify(NOTIFICATION_ID, notification);
  }


}
