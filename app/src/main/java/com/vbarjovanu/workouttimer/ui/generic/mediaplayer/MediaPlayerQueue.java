package com.vbarjovanu.workouttimer.ui.generic.mediaplayer;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class MediaPlayerQueue {
    private Queue<QueueData> soundResourceIdsQueue;
    private final Context context;

    public MediaPlayerQueue(Context context) {
        this.context = context;
        this.soundResourceIdsQueue = new ArrayDeque<>();
    }

    public void play() {
        MediaPlayer mediaPlayer = null;
        QueueData object = this.soundResourceIdsQueue.poll();
        if (object != null) {

            if (object instanceof QueueResourceData) {
                mediaPlayer = MediaPlayer.create(this.context, ((QueueResourceData) object).getData());
            }
            if (object instanceof QueueUriData) {
                mediaPlayer = MediaPlayer.create(this.context, ((QueueUriData) object).getData());
            }
            if (mediaPlayer != null) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(object.getSpeed()));
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
        this.soundResourceIdsQueue.add(new QueueResourceData(soundId, 1.0f));
    }

    public void addSoundResource(int[] soundIds) {
        for (int id : soundIds) {
            this.soundResourceIdsQueue.add(new QueueResourceData(id, 1.0f));
        }
    }

    public void addSoundResource(List<Integer> soundIds, float speed) {
        for (Integer id : soundIds) {
            this.soundResourceIdsQueue.add(new QueueResourceData(id, speed));
        }
    }

    public void addSoundResource(List<Integer> soundIds) {
        this.addSoundResource(soundIds, 1.0f);
    }

    public void addSoundUri(Uri uri) {
        this.soundResourceIdsQueue.add(new QueueUriData(uri, 1.0f));
    }
}
