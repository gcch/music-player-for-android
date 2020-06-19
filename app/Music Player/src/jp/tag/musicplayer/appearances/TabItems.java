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
import android.support.v4.app.Fragment;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.string;
import jp.tag.musicplayer.appearances.albums.BrowseAlbumsFragment;
import jp.tag.musicplayer.appearances.artists.BrowseArtistsFragment;
import jp.tag.musicplayer.appearances.genres.BrowseGenresFragment;
import jp.tag.musicplayer.appearances.player.PlayerFragment;
import jp.tag.musicplayer.appearances.playlists.BrowsePlaylistsFragment;
import jp.tag.musicplayer.appearances.songs.BrowseSongsFragment;

public enum TabItems {
  /*
   * タブ項目
   */
  PLAYER(R.string.menu_player, PlayerFragment.class),
  PLAYLISTS(R.string.menu_playlists, BrowsePlaylistsFragment.class),
  ARTISTS(R.string.menu_artists, BrowseArtistsFragment.class),
  ALBUMS(R.string.menu_albums, BrowseAlbumsFragment.class),
  SONGS(R.string.menu_songs, BrowseSongsFragment.class),
  GENRES(R.string.menu_genres, BrowseGenresFragment.class);

  private int tag;
  private Class<?> fragment;

  TabItems(int tag, Class<?> fragment) {
    this.tag = tag;
    this.fragment = fragment;
  }

  /**
   * 
   * @param r
   * @return
   */
  public String getName(Resources r) {
    return r.getString(tag);
  }
  
  /**
   * 
   * @param r
   * @return
   */
  public Fragment getFragment() {
    try {
      return (Fragment)this.fragment.newInstance();
    } catch (Exception e) {
      return null;
    }
  }
  

}
