/*
 * Copyright (C) 2017 The Android Open Source Project
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
package com.example.exoplayer;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  private SimpleExoPlayerView playerView;
  private SimpleExoPlayer player;
  boolean playWhenReady;
  int currentWindow;
  long playbackPosition;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);
    playerView = (SimpleExoPlayerView) findViewById(R.id.video_view);
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (Util.SDK_INT > 23) {
      initializePlayer();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    hideSystemUi();
    if ((Util.SDK_INT <= 23 || player == null)) {
      initializePlayer();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (Util.SDK_INT <= 23) {
      releasePlayer();
    }
  }

  private void releasePlayer() {
    if (player != null) {
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      playWhenReady = player.getPlayWhenReady();
      player.release();
      player = null;
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (Util.SDK_INT > 23) {
      releasePlayer();
    }
  }

  @SuppressLint("InlinedApi")
  private void hideSystemUi() {
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }

  private void initializePlayer() {


    player = ExoPlayerFactory.newSimpleInstance(
            new DefaultRenderersFactory(this),
            new DefaultTrackSelector(), new DefaultLoadControl());

      playerView.setPlayer(player);

      player.setPlayWhenReady(playWhenReady);
      player.seekTo(currentWindow, playbackPosition);

      Uri uri = Uri.parse(getString(R.string.media_url_mp3));
      MediaSource mediaSource = buildMediaSource(uri);
      player.prepare(mediaSource, true, false);

  }

  private MediaSource buildMediaSource(Uri uri) {
//    // these are reused for both media sources we create below

    DefaultExtractorsFactory extractorsFactory =
            new DefaultExtractorsFactory();
    DefaultHttpDataSourceFactory dataSourceFactory =
            new DefaultHttpDataSourceFactory( "user-agent");

    ExtractorMediaSource videoSource =
            new ExtractorMediaSource(uri, dataSourceFactory,
                    extractorsFactory, null, null);

    Uri audioUri = Uri.parse(getString(R.string.media_url_mp3));
    ExtractorMediaSource audioSource =
            new ExtractorMediaSource(audioUri, dataSourceFactory,
                    extractorsFactory, null, null);

    return new ConcatenatingMediaSource(audioSource, videoSource);

  }


}
