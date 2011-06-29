package com.roundarch.ndkdemo.threads;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class RecorderThread extends NDKDemoThread {

	public static final String RECORDER_THREAD_TAG = "RecorderThread";

	public static final String RECORDER_DATA = "recorderData";

	public static Boolean recording = true;
	
	public static Boolean running = true;
	
	public static int bufferSize = 0;

	protected AudioRecord recorder;

	public RecorderThread(Handler mainThreadHandler) {
		super(mainThreadHandler);
	}

	@Override
	public void run() {
		// We purposefully avoid calling super here, as we are looping inside
		// the run method, rather than starting a looper.

		Log.v(RECORDER_THREAD_TAG, "Recorder thread running.");

		// We should listen for record triggers, and act accordingly.
		// First, let's set up an audio recorder.

		bufferSize = AudioRecord.getMinBufferSize(44100,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
				AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
				bufferSize);

		short[] audioData = new short[bufferSize / 2];

		while (running) {
			// Do stuff here.
			if (recording && recorder.getState() == AudioRecord.STATE_INITIALIZED) {
				if (recorder.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED) {
					recorder.startRecording();
				} else {
					// Here's where we need to dispatch out the recording data
					recorder.read(audioData, 0, bufferSize / 2);

					// We won't know about the chain handler when this first
					// starts up. We should make sure it exists before using it.
					if (chainHandler != null) {
						Bundle bundle = new Bundle();
						bundle.putShortArray(RECORDER_DATA, audioData);

						Message msg = chainHandler.obtainMessage();
						msg.setData(bundle);
						chainHandler.sendMessage(msg);
					} else {
						Log.v(RECORDER_THREAD_TAG,
								"We should try to get the handler now.");
					}
				}
			} else if (!recording) {
				if (recorder != null && recorder.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
					recorder.stop();
				}
			}
		}

	}

	@Override
	protected void initLooperAndHandler() {
		if (!isRunning) {
			// Create the message loop.
			Looper.prepare();
			currentThreadHandler = new Handler(Looper.myLooper(),
					new Handler.Callback() {

						public boolean handleMessage(Message msg) {

							return false;
						}
					});
			// Set our running flag, so that we can know the state of
			// looper/handler
			isRunning = true;

			// Start processing incoming messages. NOTE: We must stop this by
			// calling quit() at destruction.
			Looper.loop();
		}
	}

	@Override
	public void interrupt() {
		recording = running = false;
		
		if (recorder != null) {
			recorder.release();
			recorder = null;
		}
		
		super.interrupt();
	}

}
