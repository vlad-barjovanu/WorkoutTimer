package com.vbarjovanu.workouttimer.ui.generic.mediaplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class MediaPlayerQueue {
    private Queue<Object> soundResourceIdsQueue;
    private final Context context;

    public MediaPlayerQueue(Context context) {
        this.context = context;
        this.soundResourceIdsQueue = new ArrayDeque<>();
    }

    public void play() {
        MediaPlayer mediaPlayer = null;
        Object object = this.soundResourceIdsQueue.poll();
        if (object != null) {
            if (object instanceof Integer) {
                mediaPlayer = MediaPlayer.create(this.context, (Integer) object);
            }
            if (object instanceof Uri) {
                mediaPlayer = MediaPlayer.create(this.context, (Uri) object);
            }
            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    MediaPlayerQueue.this.play();
                });
            } else {
                this.play();
            }
        }
    }

    public void addSoundResource(int soundId) {
        this.soundResourceIdsQueue.add(soundId);
    }

    public void addSoundResource(int[] soundId) {
        for (int id : soundId) {
            this.soundResourceIdsQueue.add(id);
        }
    }

    public void addSoundResource(List<Integer> soundId) {
        this.soundResourceIdsQueue.addAll(soundId);
    }

    public void addSoundUri(Uri uri) {
        this.soundResourceIdsQueue.add(uri);
    }
}
