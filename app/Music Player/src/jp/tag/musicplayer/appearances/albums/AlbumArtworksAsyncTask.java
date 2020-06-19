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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 *
 * @author Tag
 *
 */
public class AlbumArtworksAsyncTask extends AsyncTask<String, Void, Bitmap> {

  private static final String TAG = "AlbumArtworksAsyncTask";
  private String tag;
  private ImageView iv;

  private int artworkWidth = 72;
  private int artworkHeight = 72;

  public AlbumArtworksAsyncTask(ImageView iv, int width, int height) {
    super();
    this.tag = iv.getTag().toString();
    this.iv = iv;
    artworkWidth = width;
    artworkHeight = height;
  }

  @Override
  protected Bitmap doInBackground(String... params) {
    Bitmap bitmap = AlbumArtworksCache.getImage(params[0]);
    if(bitmap == null){
      bitmap = decodeBitmap(params[0], artworkWidth, artworkHeight);
      AlbumArtworksCache.setImage(params[0], bitmap);
    }
    return bitmap;
  }

  @Override
  protected void onPostExecute(Bitmap result) {
      if( tag.equals(iv.getTag()) ) {
        iv.setImageBitmap(result);
      }
  }

  public static Bitmap decodeBitmap(String path, int width, int height) {
//    Log.d(TAG, "path: " + path);
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);
    options.inSampleSize = calculateInSampleSize(options, width, height);
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeFile(path, options);
  }

  public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      if (width > height) {
        inSampleSize = Math.round((float)height / (float)reqHeight);
      } else {
        inSampleSize = Math.round((float)width / (float)reqWidth);
      }
    }
    return inSampleSize;
  }

}
