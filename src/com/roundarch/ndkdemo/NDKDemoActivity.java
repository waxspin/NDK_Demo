package com.roundarch.ndkdemo;

import com.roundarch.ndkdemo.threads.PlaybackThread;
import com.roundarch.ndkdemo.threads.ProcessingThread;
import com.roundarch.ndkdemo.threads.RecorderThread;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class NDKDemoActivity extends Activity {

	public static final String LOGGING_TAG = "NDKDemoActivity";
	
	private Boolean threadsInitialized = false;

	private RecorderThread recThread;

	private PlaybackThread playThread;

	private ProcessingThread procThread;

	private Handler mainActivityHandler;

	private Handler recHandler;

	private Handler playHandler;

	private Handler processorHandler;

	private Button btnStart;

	private Button btnStop;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		btnStart = (Button) findViewById(R.id.btnStart);
		btnStop = (Button) findViewById(R.id.btnStop);

		btnStart.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Start recording after initing the other threads.
				startRecording();

				btnStop.setEnabled(true);
				btnStart.setEnabled(false);
			}
		});

		btnStop.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				stopRecording();
				btnStop.setEnabled(false);
				btnStart.setEnabled(true);
			}
		});

		// Set the appropriate buttons to enabled
		btnStart.setEnabled(true);
	}
	
	@Override
	protected void onPause() {
		RecorderThread.recording = threadsInitialized = false;
		destroyThreads();
		super.onPause();
		finish();
	}
	
	@Override
	protected void onStop() {
		RecorderThread.recording = threadsInitialized = false;
		destroyThreads();
		super.onStop();
		finish();
	}

	private void checkAndRestartThreads() {
		// We close them out if need be before
		// re-initializing the threads.
		destroyThreads();
		initializeThreads();
	}

	private void startRecording() {

		// Check if threads are setup, and restart them if need be.
		if (!threadsInitialized)
		{
			checkAndRestartThreads();
			threadsInitialized = true;
		}
		RecorderThread.recording = true;

		// Message msg = recHandler.obtainMessage();
		// msg.arg1 = RecordingThreadMessages.BEGIN_RECORDING.ordinal();
		// recHandler.sendMessage(msg);
	}

	private void stopRecording() {
		// Message msg = recHandler.obtainMessage();
		// msg.arg1 = RecordingThreadMessages.END_RECORDING.ordinal();
		// recHandler.sendMessage(msg);

		// Stop the recording
		RecorderThread.recording = false;
	}

	private void initializeThreads() {
		// Set up the handler
		mainActivityHandler = new Handler(new Handler.Callback() {

			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				return false;
			}
		});

		// Set up the threads with the handler
		recThread = new RecorderThread(mainActivityHandler);
		playThread = new PlaybackThread(mainActivityHandler);
		procThread = new ProcessingThread(mainActivityHandler);

		//Reset the recorder thread flag
		RecorderThread.running = RecorderThread.recording = true;
		
		// Run the new threads.
		recThread.start();
		playThread.start();
		procThread.start();
		
		// Store references to the thread handlers. NOTE: The while loop may not
		// be ideal, but since it takes a moment for the handlers to spin up, we
		// need to wait or we get a null.
		while (playHandler == null || processorHandler == null) {
			// recHandler = recThread.getHandler();
			playHandler = playThread.getHandler();
			processorHandler = procThread.getHandler();
		}

		// Chain the threads' handlers for communication.
		procThread.setThreadChainHandler(playHandler);
		recThread.setThreadChainHandler(processorHandler);
	}

	private void destroyThreads() {
		if (procThread != null) {
			procThread.interrupt();
			procThread = null;
		}

		if (recThread != null) {
			recThread.interrupt();
			recThread = null;
		}

		if (playThread != null) {
			playThread.interrupt();
			playThread = null;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		threadsInitialized = false;
		destroyThreads();
	}

	@Override
	public void onDestroy() {
		destroyThreads();
		super.onDestroy();
	}

}