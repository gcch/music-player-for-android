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

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class PlayerServiceController {

  private static final String TAG = "PlayerServiceController";
  private Context context = null;
  public PlayerServiceController(Context context) {
    this.context = context;
  }

  public PlayerService service;
  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
      service = ((PlayerService.PlayerServiceBinder)binder).getService();
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {
      service = null;
    }
  };

  /**
   * サービスのバインド
   */
  public void bindService() {
//    Log.d(TAG, "bindService");
    Intent intent = new Intent(context, PlayerService.class);
    if (!isServiceRunning(context, PlayerService.class)) {  // サービスが起動していなければ、バインドする前に起動
      context.startService(intent);
    }
    context.bindService(intent, connection, Context.BIND_AUTO_CREATE);  // バインド
  }

  /**
   * バインド状況 (service != null ならば true)
   * @return
   */
  public boolean checkBindStatus() {
    boolean status = service != null ? true : false;
//    Log.d(TAG, "checkBindStatus: " + status);
    return status;
  }

  /**
   * サービスのアンバインド
   */
  public void unbindService() {
//    Log.d(TAG, "unbindService");
    context.unbindService(connection);  // アンバインド
  }

  /**
   * サービスの起動状態の確認
   * @param context
   * @param cls
   * @return
   */
  private boolean isServiceRunning(Context context, Class<?> cls) {
    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
    for (RunningServiceInfo info : services) {
      if (cls.getName().equals(info.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

}
