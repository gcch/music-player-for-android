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

public class _Album {

  public long id = -1;

  public String album = "Album";     // アルバム名
  public String albumArt = "Album Art";
  public String albumKey = "Album Key";

  public int tracks = 0;          // アルバム内のトラック数

  public String artist = "Artist"; // アーティスト名

  public String year = "0000";     // 年

  // 取り出すデータ
  public static final String[] ALBUM_PROJECTION = {
    MediaStore.Audio.Albums._ID,
    MediaStore.Audio.Albums.ALBUM,
    MediaStore.Audio.Albums.ALBUM_ART,
    MediaStore.Audio.Albums.ALBUM_KEY,
    MediaStore.Audio.Albums.NUMBER_OF_SONGS,
    MediaStore.Audio.Albums.ARTIST
  };

  public _Album(Cursor cursor) {
    id = cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Albums._ID));

    album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
    albumArt = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
    albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY));
    tracks = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));

    artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
  }

}
