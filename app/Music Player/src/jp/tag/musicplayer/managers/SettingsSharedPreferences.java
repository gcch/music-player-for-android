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

package jp.tag.musicplayer.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import jp.tag.musicplayer.core.PlayerRepeatState;
import jp.tag.musicplayer.core.PlayerShuffleState;

public class SettingsSharedPreferences {

  private Context context;
  private SharedPreferences sharedPreferences = null;
  
  public SettingsSharedPreferences(Context context) {
    this.context = context;
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
  }
  
  /**
   * シャッフル
   */
  private PlayerShuffleState defaultShuffleState = PlayerShuffleState.OFF;
  private final String keyShuffleState = "keyShuffleState";
  public PlayerShuffleState getShuffleState() {
    return PlayerShuffleState.values()[sharedPreferences.getInt(keyShuffleState, defaultShuffleState.ordinal())];
  }
  public void putShuffleState(PlayerShuffleState s) {
    Editor editor = sharedPreferences.edit();
    editor.putInt(keyShuffleState, s.ordinal());
    editor.commit();
  }
  
  /**
   * リピート
   */
  private PlayerRepeatState defaultRepeatState = PlayerRepeatState.OFF;
  private final String keyRepeatState = "keyRepeatState";
  public PlayerRepeatState getRepeatState() {
    return PlayerRepeatState.values()[sharedPreferences.getInt(keyRepeatState, defaultRepeatState.ordinal())];
  }
  public void putRepeatState(PlayerRepeatState s) {
    Editor editor = sharedPreferences.edit();
    editor.putInt(keyRepeatState, s.ordinal());
    editor.commit();
  }
}
