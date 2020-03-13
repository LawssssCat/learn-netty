package cn.edut.mynetty;

import java.util.concurrent.Executor;

import cn.edut.mynetty.pool.NioSelectorRunnablePool;
import cn.edut.mynetty.pool.Worker;

public class NioServerWorker extends AbstractNioSelector implements Worker {

	public NioServerWorker(Executor executor, String threadName, NioSelectorRunnablePool selectorRunnablePool) {
		super(executor, threadName, selectorRunnablePool);
	}

}
