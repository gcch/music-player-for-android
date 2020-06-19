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

public class _Genre {

  public long id = -1;
  public String name = "";


  // 取り出すデータ
  public static final String[] GENRE_PROJECTION = {
    MediaStore.Audio.Genres._ID,
    MediaStore.Audio.Genres.NAME
  };

  public _Genre(Cursor cursor) {
    id = cursor.getLong( cursor.getColumnIndex(MediaStore.Audio.Genres._ID) );
    name = cursor.getString( cursor.getColumnIndex(MediaStore.Audio.Genres.NAME) );
  }

}
