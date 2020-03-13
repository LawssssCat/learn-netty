package cn.edut.mynetty.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import cn.edut.mynetty.NioServerBoss;
import cn.edut.mynetty.NioServerWorker;

public class NioSelectorRunnablePool {

	/**
	 * boss线程数组
	 */
	private final AtomicInteger bossIndex = new AtomicInteger();
	private Boss[] bosses;

	/**
	 * worker线程数组
	 */
	private final AtomicInteger workerIndex = new AtomicInteger();
	private Worker[] workers;

	public NioSelectorRunnablePool(Executor bossExecutor, Executor workerExecutor) {
		// 默认初始化1个boss
		initBoss(bossExecutor, 1);
		// 初始化woker, 数量是当前核心线程数量*2(与cpu有关)
		initWorker(workerExecutor, Runtime.getRuntime().availableProcessors() * 2);
	}

	/**
	 * 初始化 boss 线程
	 * 
	 * @param bossExecutor
	 * @param count
	 */
	private void initBoss(Executor bossExecutor, int count) {
		this.bosses = new NioServerBoss[count];
		for (int i = 0; i < bosses.length; i++) {
			bosses[i] = new NioServerBoss(bossExecutor, "boss thread " + (i + 1), this);
		}
	}

	/**
	 * 初始化worker线程
	 * 
	 * @param workerExecutor
	 * @param count
	 */
	private void initWorker(Executor workerExecutor, int count) {
		this.workers = new NioServerWorker[count];
		for (int i = 0; i < workers.length; i++) {
			workers[i] = new NioServerWorker(workerExecutor, "worker thread " + (i + 1), this);
		}
	}
}
