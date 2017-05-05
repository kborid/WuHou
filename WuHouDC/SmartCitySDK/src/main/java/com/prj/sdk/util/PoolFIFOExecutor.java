package com.prj.sdk.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PoolFIFOExecutor {
	
	private static final int MAX_TASK_LIMIT = 3;
	private final static ThreadPoolExecutor mPoolExecutor;

	static {
		mPoolExecutor = new ThreadPoolExecutor(0, MAX_TASK_LIMIT, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * 执行命令
	 * @param data
	 * @param callback
	 */
	public static void exe(Runnable mRunnable) {		
		mPoolExecutor.execute(mRunnable);
	}

}
