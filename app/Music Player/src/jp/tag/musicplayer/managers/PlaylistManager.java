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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import jp.tag.musicplayer.appearances.BrowseAlbumDetailFragment;
import jp.tag.musicplayer.objects._Album;
import jp.tag.musicplayer.objects._Playlist;
import jp.tag.musicplayer.objects._Track;

public class PlaylistManager {

  Context context;
  ArrayList<_Track> tracklist;
  
  PlaylistManager(Context context, _Playlist playlist) {
    this.context = context;
    tracklist = playlist.tracklist;
  }
  
  /**
   * トラックの追加
   * @param track
   */
  public void addTrack(_Track track) {
    tracklist.add(track);
  }
  
  /**
   * トラックの追加
   * @param idx
   * @param track
   */
  public void addTrack(int idx, _Track track) {
    tracklist.add(idx, track);
  }
  
  /**
   * アルバム単位での追加
   * @param album
   */
  public void addTracks(_Album album) {
    List<_Track> tracks = BrowseAlbumDetailFragment.getTracksByAlbum(context, album.id);
    for (_Track track : tracks) {
      tracklist.add(track);
    }
  }
  
  /**
   * トラックリストの取得
   * @return
   */
  public ArrayList<_Track> getTracklist() {
    return tracklist;
  }
  
  /**
   * 曲数
   * @return
   */
  public int getTracks() {
    return tracklist.size();
  }
}
