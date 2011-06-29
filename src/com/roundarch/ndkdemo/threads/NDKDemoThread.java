package com.roundarch.ndkdemo.threads;

import android.os.Handler;

public abstract class NDKDemoThread extends Thread {

	public final String NDKDEMOTHREAD_TAG = "NDKDemoThread";

	protected Handler currentThreadHandler;

	protected Handler mainHandler;

	protected Handler chainHandler;

	protected Boolean isRunning = false;

	public NDKDemoThread(Handler mainThreadHandler) {
		mainHandler = mainThreadHandler;
	}

	public Handler getHandler() {
		return currentThreadHandler;
	}

	protected abstract void initLooperAndHandler();

	public void setThreadChainHandler(Handler handler) {
		chainHandler = handler;
	}

	@Override
	public void run() {
		initLooperAndHandler();
	}
}
