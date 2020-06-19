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

import java.util.HashMap;

import android.graphics.Bitmap;

public class AlbumArtworksCache {

  private static HashMap<String,Bitmap> cache = new HashMap<String,Bitmap>();

  public static Bitmap getImage(String key) {
    if (cache.containsKey(key)) {
      return cache.get(key);
    }
    return null;
  }

  public static void setImage(String key, Bitmap image) {
    cache.put(key, image);
  }
}
