#include <stdbool.h>
#include <string.h>
#include <time.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <cpu-features.h>
#include <math.h>

#include <android/log.h>

#define APPNAME "NDKDemo"



jstring Java_com_roundarch_ndkdemo_threads_ProcessingThread_testString(JNIEnv* env, jobject thiz) {

	int i;

	for (i = 0; i < 2000000; ++i) {
		int x = 0;
	}

	return (*env)->NewStringUTF(env, "Native layer initialized.");
}

short* oldOffsetSamples = NULL;

jshortArray Java_com_roundarch_ndkdemo_threads_ProcessingThread_processSamples(JNIEnv* env, jobject thiz, jshortArray samples, jint bufSize) {

	//__android_log_print(ANDROID_LOG_VERBOSE, APPNAME, "Number of samples: %d", bufSize);

	if (oldOffsetSamples == NULL)
	{
		oldOffsetSamples = malloc(bufSize * sizeof(short));

		int r = 0;

		for (r = 0; r < bufSize; ++r)
		{
			oldOffsetSamples[r] = 0;
		}
	}

	int k;

	short* nativeSamples = (short*)(*env)->GetPrimitiveArrayCritical(env, samples, NULL);

	//We will need to make this into a jshortArray from a short* after we are done, to return it to the java layer.
	jshortArray retSamples = (*env)->NewShortArray(env, bufSize);
	jshort* outputSamples = (*env)->GetShortArrayElements(env, retSamples, 0);

	for (k = 0; k < bufSize; ++k)
	{
		outputSamples[k] = nativeSamples[k] + oldOffsetSamples[k];

		if (oldOffsetSamples != NULL)
		{
			oldOffsetSamples[k] = nativeSamples[k];
		}
	}

	(*env)->ReleaseShortArrayElements(env, retSamples, outputSamples, 0);
	(*env)->ReleasePrimitiveArrayCritical(env, samples, nativeSamples, 0);


	return retSamples;
}


