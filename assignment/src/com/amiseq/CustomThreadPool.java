package com.amiseq;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadPool {
	private AtomicBoolean execution;
	private static AtomicInteger poolCount = new AtomicInteger(0);
	private ConcurrentLinkedQueue<Runnable> tasks;
	private List<CustomThreadPoolThread> threads;

	private CustomThreadPool(int threadCount) {
		poolCount.incrementAndGet();
		this.tasks = new ConcurrentLinkedQueue<>();
		this.execution = new AtomicBoolean(true);
		this.threads = new ArrayList<>();
		for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
			CustomThreadPoolThread thread = new CustomThreadPoolThread(
					"CustomThreadPool" + poolCount.get() + " :: Thread-" + threadIndex, this.execution, this.tasks);
			thread.start();
			this.threads.add(thread);
		}
	}

	public static CustomThreadPool getInstance() {
		return getInstance(Runtime.getRuntime().availableProcessors());
	}

	public static CustomThreadPool getInstance(int threadCount) {
		return new CustomThreadPool(threadCount);
	}

	public void execute(Runnable runnable) {
		if (this.execution.get()) {
			tasks.add(runnable);
		} else {
			throw new IllegalStateException("Threadpool terminating, Unable to execute runnable");
		}
	}

	public void awaitTermination() throws TimeoutException {
		if (this.execution.get()) {
			throw new IllegalStateException("Threadpool not terminated before awaiting termination");
		}
		while (true) {
			boolean flag = true;
			for (Thread thread : threads) {
				if (thread.isAlive()) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new ThreadpoolException(e);
			}
		}
	}

	public void terminateAndStop() {
		tasks.clear();
		stop();
	}

	public void stop() {
		execution.set(false);
	}
	
	private class ThreadpoolException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public ThreadpoolException(Throwable cause) {
			super(cause);
		}
	}

	private class CustomThreadPoolThread extends Thread {
		private AtomicBoolean execute;
		private ConcurrentLinkedQueue<Runnable> runnables;

		public CustomThreadPoolThread(String name, AtomicBoolean execute, ConcurrentLinkedQueue<Runnable> runnables) {
			super(name);
			this.execute = execute;
			this.runnables = runnables;
		}

		@Override
		public void run() {
			try {
				while (execute.get() || !runnables.isEmpty()) {
					Runnable runnable;
					while ((runnable = runnables.poll()) != null) {
						runnable.run();
					}
					Thread.sleep(1);
				}
			} catch (RuntimeException | InterruptedException e) {
				throw new ThreadpoolException(e);
			}
		}
	}
}
