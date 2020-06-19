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

package jp.tag.musicplayer.objects;

import android.database.Cursor;
import android.provider.MediaStore;

public class _Artist {

  public long id = -1;

  public String artist = "Artist";         // アーティスト名
  public String artistKey = "Artist Key";  // アーティストキー
  public int albums = 0;   // アルバム数
  public int tracks = 0;   // トラック数

  // 取り出すデータ
  public static final String[] ARTIST_PROJECTION = {
    MediaStore.Audio.Artists._ID,
    MediaStore.Audio.Artists.ARTIST,
    MediaStore.Audio.Artists.ARTIST_KEY,
    MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
    MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
  };


  public _Artist(Cursor cursor) {
    id = cursor.getLong(cursor.getColumnIndex( MediaStore.Audio.Artists._ID));
    artist = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Artists.ARTIST));
    artistKey = cursor.getString(cursor.getColumnIndex( MediaStore.Audio.Artists.ARTIST_KEY));
    albums = cursor.getInt(cursor.getColumnIndex( MediaStore.Audio.Artists.NUMBER_OF_ALBUMS));
    tracks = cursor.getInt(cursor.getColumnIndex( MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
  }

}
