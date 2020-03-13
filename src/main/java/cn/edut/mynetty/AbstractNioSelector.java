package cn.edut.mynetty;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.edut.mynetty.pool.NioSelectorRunnablePool;

/**
 * 抽象selector线程类<br>
 * 一个线程 + 一个selector
 */
public abstract class AbstractNioSelector implements Runnable {

	/**
	 * 线程池
	 */
	private final Executor executor;

	/**
	 * 选择器
	 */
	protected Selector selector;

	/**
	 * 选择器wakenUp状态标记<br>
	 * 状态位,原子类
	 */
	protected final AtomicBoolean wakenUp = new AtomicBoolean();

	/**
	 * 任务队列
	 */
	private final Queue<Runnable> taskQueue = new ConcurrentLinkedDeque<Runnable>();

	/**
	 * 线程名称
	 */
	private String threadName;

	/**
	 * 线程管理对象
	 */
	protected NioSelectorRunnablePool selectorRunnablePool;

	AbstractNioSelector(Executor executor, String threadName, NioSelectorRunnablePool selectorRunnablePool) {
		this.executor = executor;
		this.threadName = threadName;
		this.selectorRunnablePool = selectorRunnablePool;
		openSelector();
	}

	/**
	 * 获取 selector并启动线程
	 */
	private void openSelector() {
		try {
			this.selector = Selector.open();
		} catch (IOException e) {
			throw new RuntimeException("Failed to create a selector.");
		}
		// 用线程池执行当前对象
		executor.execute(this);
	}

	@Override
	public void run() {
		// 给当前线程命名
		Thread.currentThread().setName(this.threadName);
		// 核心
		while (true) {
			try {

				wakenUp.set(false);
				// 当注册的事件到达时, 方法返回; 否则, 该方法会一直阻塞
				select(selector);
				// 处理任务队列
				processTaskQueue();
				// 对selector进行业务处理
				process(selector);
			} catch (Exception e) {
				// ignore for simple
			}
		}
	}

	/**
	 * select 抽象方法
	 * 
	 * @param selector
	 */
	protected abstract int select(Selector selector) throws IOException;

	/**
	 * 执行队列里的任务
	 */
	private void processTaskQueue() {
		for (;;) {
			final Runnable task = taskQueue.poll();
			if (task == null) {
				break;
			}
			task.run();
		}
	};

	/**
	 * 
	 * @param selector
	 */
	protected abstract void process(Selector selector) throws IOException;
}
