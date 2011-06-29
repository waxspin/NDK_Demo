package com.roundarch.ndkdemo.threads;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class PlaybackThread extends NDKDemoThread {

	public static final String PLAYBACK_THREAD_TAG = "PlaybackThread";

	protected AudioTrack player;

	public PlaybackThread(Handler mainThreadHandler) {
		super(mainThreadHandler);
	}

	@Override
	public void run() {
		Log.v(PLAYBACK_THREAD_TAG, "Playback thread is running.");
		// First, let's init the stream
		initializePlaybackHardware();

		// We need to make sure and call super here, so that we get the looper
		// and handler for inter-thread communication.
		super.run();
	}

	protected void initializePlaybackHardware() {
		int playBufSize = AudioTrack.getMinBufferSize(44100,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

		// Set up the playback track
		player = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
				playBufSize, AudioTrack.MODE_STREAM);
	}

	@Override
	protected void initLooperAndHandler() {
		// Create the message loop.
		Looper.prepare();
		currentThreadHandler = new Handler(Looper.myLooper(),
				new Handler.Callback() {

					public boolean handleMessage(Message msg) {
						Bundle procBundle = msg.getData();
						short[] playBackData = procBundle
								.getShortArray(ProcessingThread.PROCESSED_DATA);

						if (player != null && player.getState() == AudioTrack.STATE_INITIALIZED) {
							// Write the data to the stream that will play it.
							player.write(playBackData, 0,
									playBackData.length);

							if (player.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
								player.play();
							}
						}

						return false;
					}
				});
		// Start processing incoming messages. NOTE: We must stop this by
		// calling quit() at destruction.
		Looper.loop();
		// Set our running flag, so that we can know the state of looper/handler
		isRunning = true;
	}

	@Override
	public void interrupt() {
		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}
		if (isRunning) {			
			// Kill the current thread's looper.
			currentThreadHandler.getLooper().quit();
		}
		super.interrupt();
	}
}
