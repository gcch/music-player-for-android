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

package jp.tag.musicplayer.appearances;

import android.content.res.Resources;
import jp.tag.musicplayer.R;

public enum MenuItems {
  PLAYER(R.string.menu_player, R.drawable.ic_menu_player, "jp.tag.musicplayer.activities.BrowserActivity", TabItems.PLAYER),
  PLAYLISTS(R.string.menu_playlists, R.drawable.ic_menu_playlists, "jp.tag.musicplayer.activities.BrowserActivity", TabItems.PLAYLISTS),
  ARTISTS(R.string.menu_artists, R.drawable.ic_menu_artists, "jp.tag.musicplayer.activities.BrowserActivity", TabItems.ARTISTS),
  ALBUMS(R.string.menu_albums, R.drawable.ic_menu_albums, "jp.tag.musicplayer.activities.BrowserActivity", TabItems.ALBUMS),
  SONGS(R.string.menu_songs, R.drawable.ic_menu_songs, "jp.tag.musicplayer.activities.BrowserActivity", TabItems.SONGS),
  GENRES(R.string.menu_genres, R.drawable.ic_launcher, "jp.tag.musicplayer.activities.BrowserActivity", TabItems.GENRES),
  YEARS(R.string.menu_years, R.drawable.ic_launcher, "jp.tag.musicplayer.activities.BrowserActivity", null),
  SEARCH(R.string.menu_search, R.drawable.ic_launcher, "jp.tag.musicplayer.activities.SearchActivity", null),
  FOLDER(R.string.menu_folder, R.drawable.ic_launcher, "jp.tag.musicplayer.activities.BrowseFolderActivity", null),
  SETTINGS(R.string.menu_settings, R.drawable.ic_launcher, "jp.tag.musicplayer.activities.SettingsActivity", null);

  private int id_str, id_drawable;
  private String class_name;
  private TabItems tabItem;

  MenuItems(int id_str, int id_drawable, String class_name, TabItems tab) {
    this.id_str = id_str;
    this.id_drawable = id_drawable;
    this.class_name = class_name;
    this.tabItem = tab;
  }

  /**
   * 対応付けされた文字列を返します
   * @param r
   * @return
   */
  public String getString(Resources r) {
    return r.getString(this.id_str);
  }

  /**
   * 対応付けされた画像のIDを返します
   * @param r
   * @return
   */
  public int getDrawable(Resources r) {
    return this.id_drawable;
  }

  /**
   * 対応付けされたクラス名を返します
   * @param r
   * @return
   */
  public String getClassName(Resources r) {
    return this.class_name;
  }

  public TabItems getTabItems() {
    return this.tabItem;
  }
}
