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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.drawable;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.appearances.albums.AlbumArtworksAsyncTask;
import jp.tag.musicplayer.appearances.songs.ListSongsArrayAdapter;
import jp.tag.musicplayer.objects._Track;

public class BrowseAlbumDetailFragment extends ListFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//    containerWidth = container.getWidth();
    
    View v = inflater.inflate(R.layout.fragment_album_detail, container, false);

    Activity activity = getActivity();

    // 引き継いだ情報
    Bundle b = getArguments();
    long albunId = b.getLong("albumId");
    String albumTitle = b.getString("albumTitle");
    String albumArtist = b.getString("albumArtist");
    int albumTracks = b.getInt("albumTracks");
    String albumArt = b.getString("albumArt");
    
    // 各種 View
    ImageView ivAlbumArt =  (ImageView) v.findViewById(R.id.imageView_albumArt);
    TextView tvAlbumName =  (TextView) v.findViewById(R.id.textView_albumName);
    TextView tvArtistName = (TextView) v.findViewById(R.id.textView_artistName);
    TextView tvTracks = (TextView) v.findViewById(R.id.textView_tracks);

    // View にテキストをセット
    tvAlbumName.setText(albumTitle);
    tvArtistName.setText(albumArtist);
    tvTracks.setText(String.valueOf(albumTracks) + " tracks");

    // アートワークの処理
    ivAlbumArt.setImageResource(R.drawable.ic_defalut_artwork_small);
    if(albumArt != null){
      ivAlbumArt.setTag(albumArt);
      AlbumArtworksAsyncTask task = new AlbumArtworksAsyncTask(ivAlbumArt, 300, 300);
      task.execute(albumArt);
    }

    // 
    List<_Track> tracks = getTracksByAlbum(getActivity(), albunId);
    ListSongsArrayAdapter adapter = new ListSongsArrayAdapter(activity, tracks);
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
  
//  private int containerWidth;
//  
//  @Override
//  public Animaton onCreateAnimation(int transit, boolean enter, int nextAnim) {
//      //＊＊＊FragmentTransactionにTRANSIT_FRAGMENT_OPENを指定しておくと、遷移時にはTRANSIT_FRAGMENT_OPEN、Back時にはTRANSIT_FRAGMENT_CLOSEが渡される＊＊＊
//      if (transit == FragmentTransaction.TRANSIT_FRAGMENT_OPEN) {
//          if (enter) {
//              return ObjectAnimator.ofFloat(getView(), "x", containerWidth, 0.0f);
//          } else {
//              return ObjectAnimator.ofFloat(getView(), "x", 0.0f, -containerWidth);
//          }
//      } else if (transit == FragmentTransaction.TRANSIT_FRAGMENT_CLOSE) {
//          if (enter) {
//              return ObjectAnimator.ofFloat(getView(), "x", -containerWidth, 0.0f);
//          } else {
//              return ObjectAnimator.ofFloat(getView(), "x", 0.0f, containerWidth);
//          }
//      }
//
//      return super.onCreateAnimation(transit, enter, nextAnim);
//  }
  
  /**
   * アルバム内の曲を取得
   * @param activity
   * @param albumID
   * @return
   */
  public static List<_Track> getTracksByAlbum(Context activity, long albumId) {

    List<_Track> tracks = new ArrayList<_Track>();
    ContentResolver resolver = activity.getContentResolver(); 
    String[] SELECTION_ARG = {""};
    SELECTION_ARG[0] = String.valueOf(albumId);
    Cursor cursor = resolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        _Track.TRACK_PROJECTION,
        MediaStore.Audio.Media.ALBUM_ID + "= ?",
        SELECTION_ARG,
        null
        );

    if ( cursor != null && cursor.getCount() > 0 ) {  // 実際不要
      cursor.moveToFirst();
      do {
        if ( cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) < 3000 ) { continue; }
        tracks.add(new _Track(cursor));
      } while (cursor.moveToNext());
      cursor.close();
    }

    return tracks;
  }

}
