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

package jp.tag.musicplayer.appearances.songs;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.content.AsyncTaskLoader;
import jp.tag.musicplayer.objects._Track;

public class SongsAsyncTaskLoader extends AsyncTaskLoader<List<_Track>> {

  private Context context;

  private static List<_Track> sTracks;

  public SongsAsyncTaskLoader(Context context) {
    super(context);
    this.context = context;
  }

  @Override
  public List<_Track> loadInBackground() {

    // 外部ストレージ読み出し
    ContentResolver cr = context.getContentResolver();
    Cursor cursor = cr.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,  // データの種類
        _Track.TRACK_PROJECTION, // 取得する内容 (null で全て)
        null, // フィルター条件 nullはフィルタリング無し
        null, // フィルター用のパラメータ
        "TITLE ASC"   // 並べ替え
        );
    cursor.moveToFirst();
    do {
      if ( cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) < 10000 ) {
        continue;
      }
      sTracks.add(new _Track(cursor));
    } while (cursor.moveToNext());
    cursor.close();

    // ソート
    Collections.sort(sTracks, new Comparator<_Track>() {
      @Override
      public int compare(_Track lhs, _Track rhs) {
        String buf1 = lhs.title;
        String buf2 = rhs.title;
        if (buf1.length() < 1) {
          return -1;
        }
        if (buf2.length() < 1) {
          return 1;
        }
        String buf1Initial = buf1.substring(0, 1).toUpperCase(Locale.US);
        String buf2Initial = buf2.substring(0, 1).toUpperCase(Locale.US);
        if (buf1Initial.compareTo(buf2Initial) < 0) {
          return -1;
        } else if (buf1Initial.compareTo(buf2Initial) > 0) {
          return 1;
        }
        return 0;
      }
    });

    return sTracks;
  }

}
