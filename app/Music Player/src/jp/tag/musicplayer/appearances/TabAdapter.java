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

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {

  private TabItems[] tabItems = null;
  private Resources r = null;
  
  public TabAdapter(FragmentManager fm, Resources r) {
    super(fm);
    tabItems = TabItems.values();
    this.r = r;
  }

  @Override
  public Fragment getItem(int i) {
//    return new BrowseSongsFragment();
    return tabItems[i].getFragment();
  }

  @Override
  public int getCount() {
    return tabItems.length;
  }

  @Override
  public CharSequence getPageTitle(int i) {
      return tabItems[i].getName(r);
  }
}
