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

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.R.string;

/**
 * メニュー項目生成用アダプタ
 * @author Tag
 *
 */
public class MenuAdapter extends BaseAdapter {

  private static final String TAG = "MenuAdapter";
  private Context context = null;
  private Resources res = null;
  private LayoutInflater layoutInflater = null;
  private MenuItems[] menuItems = null;

  /**
   * コンストラクタ
   */
  public MenuAdapter(Context context) {
    this.context = context;
    res = context.getResources();
    layoutInflater = LayoutInflater.from(context);
    menuItems = MenuItems.values();
  }

  /**
   * 要素数を返す
   */
  @Override
  public int getCount() {
    return menuItems.length;
  }

  /**
   * オブジェクトを返す
   */
  @Override
  public Object getItem(int position) {
    return menuItems[position];
  }

  /**
   * アイテムIDを返す
   */
  @Override
  public long getItemId(int position) {
    return position;
  }

  /**
   * View の追加処理
   */
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
//    Log.d(TAG, "getView");
    
    ViewHolder holder;
    MenuItems item = menuItems[position];
    
    if (convertView == null) {
      convertView = layoutInflater.inflate(R.layout.menu_item_cardview, null);
      holder = new ViewHolder();
      holder.fl = (FrameLayout)convertView.findViewById(R.id.frameLayout_menu_item);
      holder.iv = (ImageView)convertView.findViewById(R.id.imageView_menu_item);
      holder.tv = (TextView)convertView.findViewById(R.id.textView_menu_item);
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    // View にリンク先情報をタグ付け (OnClickListener のための準備)
    holder.fl.setTag(R.string.menu, item.getClassName(res));  // リンク先のクラス名をタグ付け
    TabItems tabItem = item.getTabItems();  // リンク先のタブ番号を取得
    if (tabItem != null) {
      holder.fl.setTag(R.id.tabs, tabItem.ordinal());  // リンク先のタブ番号をタグ付け
    }

    holder.fl.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClassName("jp.tag.musicplayer", (String)((FrameLayout)v).getTag(R.string.menu));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("tabNo", (Integer)((FrameLayout)v).getTag(R.id.tabs));
        context.startActivity(intent);
      }
    });
    holder.iv.setImageResource(item.getDrawable(res));
    holder.tv.setText(item.getString(res));

    return convertView;
  }
  
  static class ViewHolder {
    FrameLayout fl;
    ImageView iv;
    TextView tv;
  }
  
}

