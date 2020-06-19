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

package jp.tag.musicplayer.appearances.albums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.drawable;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.objects._Album;

public class ListAlbumsArrayAdapter extends ArrayAdapter<_Album> implements SectionIndexer {

  private Context context = null;

  // SectionIndexer 関連
  private HashMap<String, Integer> mapIndex;
  private String[] sections;

  public ListAlbumsArrayAdapter(Context context, List<_Album> item) {
    super(context, 0, item);
    this.context = context;

    // SectionIndexer 関連
    mapIndex = new LinkedHashMap<String, Integer>();
    for (int i = 0; i < item.size(); i++) {
      String target = item.get(i).album;  // ソート対象文字列の取得
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

  @Override
  public View getView(int position, View convertView,ViewGroup parent) {

    _Album album = getItem(position);
    ViewHolder holder;

    if (convertView == null) {
      LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      convertView = inflater.inflate(R.layout.listview_entry_album, null);
      holder = new ViewHolder();
      holder.ivAlbumArt = (ImageView)convertView.findViewById(R.id.imageView_albumArt);
      holder.tvAlbumName = (TextView)convertView.findViewById(R.id.textView_albumName);
      holder.tvArtist = (TextView)convertView.findViewById(R.id.textView_artistName);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    String path = album.albumArt;
    holder.ivAlbumArt.setImageResource(R.drawable.ic_defalut_artwork_small);  // デフォルト
    if (path == null) {
      path = String.valueOf(R.drawable.ic_defalut_artwork_small);
      Bitmap bitmap = AlbumArtworksCache.getImage(path);
      if (bitmap == null) {
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_defalut_artwork_small);
        AlbumArtworksCache.setImage(path, bitmap);
      }
    }
    // アルバムアートワークの処理
    holder.ivAlbumArt.setTag(path);
    AlbumArtworksAsyncTask task = new AlbumArtworksAsyncTask(holder.ivAlbumArt, 200, 200);  // 画像のロードを別にする (サイズ指定も必要)
    task.execute(path);

    holder.tvAlbumName.setText(album.album);
    holder.tvArtist.setText(album.artist);


    return convertView;
  }

  static class ViewHolder {
    ImageView ivAlbumArt;
    TextView tvAlbumName;
    TextView tvArtist;
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
