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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.objects._Track;

public class ListSongsArrayAdapter extends ArrayAdapter<_Track> implements SectionIndexer {

  private Context context = null;

  // SectionIndexer 関連
  private HashMap<String, Integer> mapIndex;
  private String[] sections;

  /**
   * コンストラクタ
   * @param context
   * @param item
   */
  public ListSongsArrayAdapter(Context context, List<_Track> item) {
    super(context, 0, item);
    this.context = context;
  }

  /**
   * データのセット
   * @param tracks
   */
  public void setData(List<_Track> item) {
    clear();
    if (item != null) {
      // データの追加
      for (_Track track : item) {
        add(track);
      }

      // SectionIndexer の生成
      mapIndex = new LinkedHashMap<String, Integer>();
      for (int i = 0; i < item.size(); i++) {
        String target = item.get(i).title;  // ソート対象文字列の取得
        String ch = target.substring(0, 1);      // 先頭の文字
        ch = ch.toUpperCase(Locale.US);          // 大文字に
        if (!mapIndex.containsKey(ch)) {          // 未登録ならば
          mapIndex.put(ch, i);
        }
      }
      Set<String> sectionLetters = mapIndex.keySet();
      ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
      Collections.sort(sectionList);
      sections = new String[sectionList.size()];
      sectionList.toArray(sections);
    }
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    _Track track = getItem(position);
    ViewHolder holder;

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.listview_entry_song, null);
      holder = new ViewHolder();
      holder.tvTrackName = (TextView)convertView.findViewById(R.id.textView_trackName);
      holder.tvTrackArtist = (TextView)convertView.findViewById(R.id.textView_artistName);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

//    long dm = track.duration / 60000;
//    long ds = (track.duration - (dm * 60000)) / 1000;

    holder.tvTrackName.setText(track.title);
    holder.tvTrackArtist.setText(track.artist);

    return convertView;
  }

  static class ViewHolder {
    TextView tvTrackName;
    TextView tvTrackArtist;
  }

  /**
   * SectionIndexer:
   */
  @Override
  public Object[] getSections() {
    return sections;
  }

  /**
   * SectionIndexer: セクションのリスト位置を返す
   */
  @Override
  public int getPositionForSection(int sectionIndex) {
    return mapIndex.get(sections[sectionIndex]);
  }

  /**
   * SectionIndexer: リスト位置のセクションを返す
   */
  @Override
  public int getSectionForPosition(int position) {
    return 0;
  }
}
