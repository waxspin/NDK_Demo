package com.roundarch.ndkdemo.threads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class ProcessingThread extends NDKDemoThread {

	public static final String PROCESSING_TAG = "ProcessingThread";

	public static final String PROCESSED_DATA = "processedData";

	static {
		System.loadLibrary("ndkdemo");
	}

	public ProcessingThread(Handler mainThreadHandler) {
		super(mainThreadHandler);
		Log.v(PROCESSING_TAG, "Native test: " + testString());
	}

	@Override
	public void run() {
		Log.v(PROCESSING_TAG, "Processor thread running.");
		// We need to make sure and call super here, so that we get the looper
		// and handler for inter-thread communication.
		super.run();
	}

	// Native method declarations
	public native String testString();

	public native short[] processSamples(short[] samples, int bufSize);

	@Override
	protected void initLooperAndHandler() {
		// Create the message loop.
		Looper.prepare();
		currentThreadHandler = new Handler(Looper.myLooper(),
				new Handler.Callback() {

					public boolean handleMessage(Message msg) {
						Bundle audioBundle = msg.getData();
						short[] audioData = audioBundle
								.getShortArray(RecorderThread.RECORDER_DATA);

						// Here's where we'll call the C functionality.
						short[] processedData = processSamples(audioData, audioData.length);

						// And transmit the processed data to the playback
						// thread
						Message msgProcData = chainHandler.obtainMessage();
						Bundle bundle = new Bundle();
						bundle.putShortArray(PROCESSED_DATA, processedData);
						msgProcData.setData(bundle);

						chainHandler.sendMessage(msgProcData);

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
		if (isRunning) {
			// Kill the current thread's looper.
			currentThreadHandler.getLooper().quit();
		}
		super.interrupt();
	}

}
