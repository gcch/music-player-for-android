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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.appearances.albums.ListAlbumsArrayAdapter;
import jp.tag.musicplayer.objects._Album;

public class BrowseArtistDetailFragment extends ListFragment {

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    Activity activity = getActivity();
    //  containerWidth = container.getWidth();

    View v = inflater.inflate(R.layout.fragment_artist_detail, container, false);

    // 引き継いだ情報
    Bundle b = getArguments();
    long artistId = b.getLong("artistId");
    String artistName = b.getString("artistName");
    int albums = b.getInt("albums");
    int tracks = b.getInt("tracks");
    
    TextView tvArtistName =  (TextView) v.findViewById(R.id.textView_artistName);
    TextView tvAlbums = (TextView) v.findViewById(R.id.textView_albums);
    TextView tvTracks = (TextView) v.findViewById(R.id.textView_tracks);

    tvArtistName.setText(artistName);
    tvAlbums.setText(String.valueOf(albums) + " albums");
    tvTracks.setText(String.valueOf(tracks) + " tracks");

    List<_Album> albumsList = getAllAlbumsByArtist(activity, artistId);
    ListAlbumsArrayAdapter adapter = new ListAlbumsArrayAdapter(activity, albumsList);
    setListAdapter(adapter);
    
    return v;

  }

  // コールバック用
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
   * @param activity
   * @return
   */
  public static List<_Album> getAllAlbumsByArtist(Context activity, long artistId) {

    List<_Album> albums = new ArrayList<_Album>();

    // 外部ストレージ読み出し
    ContentResolver cr = activity.getContentResolver();
    String[] SELECTION_ARG = {""};
    SELECTION_ARG[0] = String.valueOf(artistId);
    Cursor cursor = cr.query(
        MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,  //データの種類
        _Album.ALBUM_PROJECTION, //取得する内容 (null で全て)
        MediaStore.Audio.Media.ARTIST_ID + "= ?", //フィルター条件 nullはフィルタリング無し
        SELECTION_ARG, //フィルター用のパラメータ
        "ALBUM ASC"   //並べ替え
        );
    if ( cursor != null && cursor.getCount() > 0 ) {
      cursor.moveToFirst();
      do {
        albums.add(new _Album(cursor));
      } while (cursor.moveToNext());
      cursor.close();
    }

    return albums;
  }




}
