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

package jp.tag.musicplayer.objects;

import java.io.File;
import java.io.IOException;

import org.cmc.music.metadata.MusicMetadata;
import org.cmc.music.metadata.MusicMetadataSet;
import org.cmc.music.myid3.MyID3;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class _Track {

  public long id = -1;
  public Uri uri = null;
  public String path = null;       // 実ファイルの場所

  public String title = "Title";   // タイトル名
  public long artistId = 0L;       // アーティストID
  public String artist = "Artist"; // アーティスト名
  public long albumId = 0L;        // アルバムID
  public String album = "Album";   // アルバム名

  public int trackNo = 0;          // アルバム内のトラック番号
  public long duration = 0L;       // 再生時間

  public String year = "N/A";      // 年
  public String genre = "N/A";     // ジャンル
  public String producer = "N/A";  // 製作者
  public String producerArtist = "N/A";  // 製作アーティスト
  public String composer = "N/A";  // 作曲者
  public String composer2 = "N/A"; // 作曲者 (2)
  public String format = "N/A";    // フォーマット
  public String encType = "N/A";   // エンコードタイプ
  public String bitrate = "N/A";   // ビットレート
  public boolean lossless = false; // ロスレスかどうか
  public String comment = "";      // コメント


  // 取り出すデータ
  public static final String[] TRACK_PROJECTION = {
      MediaStore.Audio.Media._ID,
      MediaStore.Audio.Media.DATA,
      MediaStore.Audio.Media.TITLE,
      MediaStore.Audio.Media.ALBUM,
      MediaStore.Audio.Media.ARTIST,
      MediaStore.Audio.Media.ALBUM_ID,
      MediaStore.Audio.Media.ARTIST_ID,
      MediaStore.Audio.Media.DURATION,
      MediaStore.Audio.Media.TRACK,
  };
  private static final String TAG = null;


  public _Track(Cursor cursor) {
    id    = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media._ID ));
    uri     = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
    path    = cursor.getString( cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

    title     = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.TITLE ));
    albumId   = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM_ID ));
    album     = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ALBUM ));
    artistId  = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST_ID ));
    artist    = cursor.getString( cursor.getColumnIndex( MediaStore.Audio.Media.ARTIST ));

    trackNo   = cursor.getInt( cursor.getColumnIndex( MediaStore.Audio.Media.TRACK ));
    duration  = cursor.getLong( cursor.getColumnIndex( MediaStore.Audio.Media.DURATION ));

    File src = new File(path);
    MusicMetadataSet srcSet = null;
    try {
      srcSet = new MyID3().read(src);  // メタデータの読み出し
      if (srcSet != null) {
        MusicMetadata metadata = (MusicMetadata)srcSet.getSimplified();
        year = metadata.getYear();
        genre = metadata.getGenre();
        producer = metadata.getProducer();
        producerArtist = metadata.getProducerArtist();
        composer = metadata.getComposer();
        composer2 = metadata.getComposer2();
        comment = metadata.getComment();
      }
    } catch (IOException e) {
    }

    AudioFile f;
    try {
      f = AudioFileIO.read(src);
//      Tag tag = f.getTag();
      AudioHeader h = f.getAudioHeader();
      format = h.getFormat();
      encType = h.getEncodingType();
      bitrate = h.getBitRate();
//      lossless = h.isLossless();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
