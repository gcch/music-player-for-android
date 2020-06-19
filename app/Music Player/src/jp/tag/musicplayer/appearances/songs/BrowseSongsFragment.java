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
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import jp.tag.musicplayer.objects._Track;

/**
 *
 * AsyncTaskLoader と ListFragment
 * http://sarl-tokyo.com/wordpress/?p=414
 *
 * @author Tag
 *
 */
public class BrowseSongsFragment extends ListFragment implements LoaderCallbacks<List<_Track>>, AbsListView.OnScrollListener {

  private Context context;

  private ListSongsArrayAdapter adapter;
  private boolean mIsLoading = false;
  private int mCount = 0;

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

  @SuppressLint("NewApi")
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

    // アダプタ
    adapter = new ListSongsArrayAdapter(context, new ArrayList<_Track>());
    setListAdapter(adapter);

    // 非同期ロード関係
    //    lv.setOnScrollListener(new OnScrollListener() {  // スクロール時のリスナ
    //      @Override
    //      public void onScrollStateChanged(AbsListView view, int scrollState) {  // スクロール状態のチェック
    //
    //      }
    //      @Override
    //      public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {  // スクロール
    //        if (totalItemCount == firstVisibleItem + visibleItemCount) {
    //          // 追加読込する
    //
    //        }
    //      }
    //    });

    setListShown(false);
    mIsLoading = true;
    getLoaderManager().initLoader(0, null, this);

  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    mCallback.onListItemClick(l, v, position, id);
  }

  /**
   * LoaderCallbacks: ローダ生成処理
   */
  @Override
  public Loader<List<_Track>> onCreateLoader(int id, Bundle args) {
    return new SongsAsyncTaskLoader(getActivity());
  }

  /**
   * LoaderCallbacks: ロード終了時の処理
   */
  @Override
  public void onLoadFinished(Loader<List<_Track>> loader, List<_Track> list) {
    adapter.setData(list);
    mIsLoading = false;
    setListShown(true);
    mCount++;
  }

  /**
   * LoaderCallbacks: ロードリセット時の処理
   */
  @Override
  public void onLoaderReset(Loader<List<_Track>> loader) {
      adapter.setData(null);
  }

  /**
   * AbsListView.OnScrollListener:
   */
  @Override
  public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
      if (totalItemCount == firstVisibleItem + visibleItemCount) {
          additionalReading();
      }
  }

  /**
   * AbsListView.OnScrollListener:
   */
  @Override
  public void onScrollStateChanged(AbsListView view, int scrollState) {
  }

  /**
   * 追加読込
   */
  private void additionalReading() {
    if (mIsLoading) {
        return;
    }
    setListShown(false);  // ロード中表示を行う
    mIsLoading = true;    // ロード中
    getLoaderManager().initLoader(mCount, null, this);
  }
}
