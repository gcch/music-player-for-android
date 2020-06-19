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

package jp.tag.musicplayer.activities;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ListView;
import jp.tag.musicplayer.R;
import jp.tag.musicplayer.R.id;
import jp.tag.musicplayer.R.layout;
import jp.tag.musicplayer.appearances.BrowseAlbumDetailFragment;
import jp.tag.musicplayer.appearances.BrowseArtistDetailFragment;
import jp.tag.musicplayer.appearances.TabAdapter;
import jp.tag.musicplayer.appearances.albums.BrowseAlbumsFragment;
import jp.tag.musicplayer.appearances.artists.BrowseArtistsFragment;
import jp.tag.musicplayer.appearances.genres.BrowseGenresFragment;
import jp.tag.musicplayer.appearances.songs.BrowseSongsFragment;
import jp.tag.musicplayer.core.PlayerServiceController;
import jp.tag.musicplayer.objects._Album;
import jp.tag.musicplayer.objects._Artist;
import jp.tag.musicplayer.objects._Genre;
import jp.tag.musicplayer.objects._Track;

import com.astuetz.PagerSlidingTabStrip;

public class BrowserActivity extends FragmentActivity implements BrowseSongsFragment.OnListItemClickListener, BrowseAlbumsFragment.OnListItemClickListener, BrowseAlbumDetailFragment.OnListItemClickListener, BrowseArtistsFragment.OnListItemClickListener, BrowseArtistDetailFragment.OnListItemClickListener, BrowseGenresFragment.OnListItemClickListener {

  // PlayerService 関連
  PlayerServiceController controller = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_browser);

    // タブおよび ViewPager 周りの処理
    ViewPager vp = (ViewPager)findViewById(R.id.viewPager);
    vp.setAdapter(new TabAdapter(getSupportFragmentManager(), getResources()));
    vp.setCurrentItem(getIntent().getExtras().getInt("tabNo"));
    PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
    tabs.setViewPager(vp);
    
    // PlayerService 関連
    controller = new PlayerServiceController(this);
  }

  @Override
  public void onStart() {
    super.onStart();
    controller.bindService();
  }
  
  @Override
  public void onDestroy() {
    controller.unbindService();
    super.onDestroy();
  }
  
  /**
   * 子 Fragment の ListView 要素がタップされたときの処理
   */
  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    Object obj = l.getItemAtPosition(position);
    if (obj instanceof _Track) {  // Track が選ばれた時の処理 (曲の再生)
      ArrayList<_Track> tracklist = new ArrayList<_Track>();
      for (int i = 0; i < l.getCount(); i++) {
        tracklist.add((_Track)l.getItemAtPosition(i));
      }
      controller.service._Play(tracklist, position);  // 再生
    } else if (obj instanceof _Album) {  // Album が選ばれた時の処理 (アルバム詳細ページを開く)
      _Album alb = (_Album) obj;
      FragmentManager fm = getSupportFragmentManager();
      FragmentTransaction ft = fm.beginTransaction();
      BrowseAlbumDetailFragment f = new BrowseAlbumDetailFragment();
      Bundle bundle = new Bundle();
      bundle.putLong("albumId", alb.id);
      bundle.putString("albumArt", alb.albumArt);
      bundle.putString("albumTitle", alb.album);
      bundle.putString("albumArtist", alb.artist);
      bundle.putString("albumYear", alb.year);
      bundle.putInt("albumTracks", alb.tracks);
      f.setArguments(bundle);
      ft.addToBackStack(null);  // 直前の画面をスタックに追加 (ないと戻れない)
      ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
      ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
      ft.replace(R.id.browserContents, f);
      ft.commit();
    } else if (obj instanceof _Artist) {  // Artist が選ばれた時の処理 (アーティスト詳細ベージを開く)
      _Artist art = (_Artist) obj;
      FragmentManager fm = getSupportFragmentManager();
      FragmentTransaction ft = fm.beginTransaction();
      BrowseArtistDetailFragment f = new BrowseArtistDetailFragment();
      Bundle bundle = new Bundle();
      bundle.putLong("artistId", art.id);
      bundle.putString("artistName", art.artist);
      bundle.putInt("albums", art.albums);
      bundle.putInt("tracks", art.tracks);
      f.setArguments(bundle);
      ft.addToBackStack(null);  // 直前の画面をスタックに追加 (ないと戻れない)
      ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
      ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
      ft.replace(R.id.browserContents, f);
      ft.commit();
    } else if (obj instanceof _Genre) {  // Genre が選ばれた時の処理
      
    }
  }

}