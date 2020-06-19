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

package jp.tag.musicplayer.appearances.playlists;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.R.string;
import jp.tag.musicplayer.objects._Playlist;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BrowsePlaylistsFragment extends ListFragment {

  private static final String TAG = "BrowsePlaylistsFragment";
  
  private Context context;
  private LayoutInflater layoutInflater;
  
  private String sharedPreferencesKeyForPlaylists = "keyPlaylists";
  private List<_Playlist> playlists;
  
  ListPlaylistsArrayAdapter adapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  //  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
  //    //return inflater.inflate(R.layout.fragment_browse_albums, container, false);
  //    return inflater.inflate(R.layout.fragment_equalizer, container, false);
  //  }

  
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    
    context = getActivity();
    layoutInflater = inflater;

    View v = inflater.inflate(R.layout.fragment_browse_playlists, container, false);

    // プレイリスト作成ボタン
    Button bCreatePlaylist = (Button)v.findViewById(R.id.button_createNewPlaylist);
    bCreatePlaylist.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        
        // ダイアログの表示
        DialogFragment dfCreateNewPlaylist = new CreateNewPlaylistDialogFragment(context, layoutInflater);
        dfCreateNewPlaylist.setRetainInstance(true);
        dfCreateNewPlaylist.show(getFragmentManager(), null);
        
      }
    });
    
    // プレイリストの読み出し
    playlists = loadPlaylists();
    
    // アダプタの設定
    adapter = new ListPlaylistsArrayAdapter(context, playlists);
    setListAdapter(adapter);
    
    
    return v;
  }

  @SuppressLint("NewApi")
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    
    // スクロール関係
    ListView lv = getListView();
    lv.setFastScrollEnabled(true);  // 高速スクロール有効化
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {  // API v11+
      lv.setFastScrollAlwaysVisible(true);  // スクロールバーを常に表示
    }
  }

  /**
   * プレイリストのリストの読み出し
   * @return
   */
  private List<_Playlist> loadPlaylists() {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    Gson gson = new Gson();
    String gsonStr = sp.getString(sharedPreferencesKeyForPlaylists, "");
    List<_Playlist> playlists = gson.fromJson(gsonStr, new TypeToken<ArrayList<_Playlist>>() {}.getType());
    if (playlists == null) {  // 初期化処理
      playlists = new ArrayList<_Playlist>();
      savePlaylists(playlists);
    }
    return playlists;
  }

  /**
   * プレイリストのリストの書き出し
   * @param playlists
   */
  private void savePlaylists(List<_Playlist> playlists) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

    Gson gson = new Gson();
    
    String gsonStr = gson.toJson(playlists);
    
    Editor editor = sp.edit();
    editor.putString(sharedPreferencesKeyForPlaylists, gsonStr);
    editor.commit();
  }

  
  
  
  /**
   * CreateNewPlaylistDialogFragment --- プレイリスト作成ダイアログ
   * @author ku, tag
   *
   */
  class CreateNewPlaylistDialogFragment extends DialogFragment {

    Context context;
    LayoutInflater inflater;

    public CreateNewPlaylistDialogFragment(Context context, LayoutInflater layoutInflater) {
      inflater = layoutInflater;
      this.context = context;
    }

    @SuppressLint("NewApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      
      final View v = inflater.inflate(R.layout.dialog_create_new_playlist, null);
      
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle(getResources().getString(R.string.dialog_create_new_playlist));
      builder.setView(v);

      // EditText
      final EditText et = (EditText) v.findViewById(R.id.editText_playlistName);

      // 確定が押されたらデータの更新
      builder.setPositiveButton(getResources().getString(R.string.dialog_create), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          String playlistName = et.getText().toString();  // 入力された文字列
          playlists.add(new _Playlist(playlistName));  // 追加
          savePlaylists(playlists);  // 一応保存
          adapter.notifyDataSetChanged();  // Adapter に通知し、List のアイテムの更新を促す
        }
      });
      // キャンセルが押されたらダイアログを閉じる
      builder.setNegativeButton(getResources().getString(R.string.dialog_abort), new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
          dismiss();
        }
      });

      return builder.create();
    }
    
    /**
     * onDestroyView --- 画面回転時対策用
     */
    @Override
    public void onDestroyView() {
      if (getDialog() != null && getRetainInstance()) {
        getDialog().setDismissMessage(null);
      }
      super.onDestroyView();
    }
  }
}
