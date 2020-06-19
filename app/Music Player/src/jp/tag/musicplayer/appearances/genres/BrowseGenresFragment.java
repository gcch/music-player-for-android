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

package jp.tag.musicplayer.appearances.genres;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import jp.tag.musicplayer.appearances.artists.BrowseArtistsFragment.OnListItemClickListener;
import jp.tag.musicplayer.objects._Genre;

@SuppressLint("NewApi")
public class BrowseGenresFragment extends ListFragment {

  private Context context;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    context = getActivity();

    // スクロール関係
    ListView lv = getListView();
    lv.setFastScrollEnabled(true);  // 高速スクロール有効化
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {  // API v11+
      lv.setFastScrollAlwaysVisible(true);  // スクロールバーを常に表示
    }

    // アダプタの設定
    ListGenresArrayAdapter adapter = new ListGenresArrayAdapter(context, getAllGenres(context));
    setListAdapter(adapter);
  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);




  }

  //コールバック用
  public OnListItemClickListener mCallback;
  public interface OnListItemClickListener {
    public void onListItemClick(ListView l, View v, int position, long id);
  }
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof OnListItemClickListener) {
      mCallback = (OnListItemClickListener) activity;  // 親Activityとの紐付け
    }
  }
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    mCallback.onListItemClick(l, v, position, id);
  }


  /**
   * 
   * @param context
   * @return
   */
  public static List<_Genre> getAllGenres(Context context) {

    List<_Genre> genres = new ArrayList<_Genre>();

    // 外部ストレージ読み出し
    ContentResolver cr = context.getContentResolver();
    Cursor cursor = cr.query(
        MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,  // データの種類
        _Genre.GENRE_PROJECTION,  // 取得する内容 (null で全て)
        null,  // フィルター条件 nullはフィルタリング無し 
        null,  // フィルター用のパラメータ
        "NAME ASC"  // 並べ替え
        );
    if ( cursor != null && cursor.getCount() > 0 ) {
      cursor.moveToFirst();
      do {
        genres.add(new _Genre(cursor));
      } while (cursor.moveToNext());
      cursor.close();
    }

    // ソート
    Collections.sort(genres, new Comparator<_Genre>() {
      @Override
      public int compare(_Genre lhs, _Genre rhs) {
        String buf1 = lhs.name;
        String buf2 = rhs.name;
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
    
    return genres;
  }
  

}
