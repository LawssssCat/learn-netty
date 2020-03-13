package cn.edut.mynetty;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.Executor;

import cn.edut.mynetty.pool.Boss;
import cn.edut.mynetty.pool.NioSelectorRunnablePool;

public class NioServerBoss extends AbstractNioSelector implements Boss {

	public NioServerBoss(Executor executor, String threadName, NioSelectorRunnablePool selectorRunnablePool) {
		super(executor, threadName, selectorRunnablePool);
	}

	@Override
	protected int select(Selector selector) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void process(Selector selector) throws IOException {
		Set<SelectionKey> selectKeys = selector.selectedKeys();
		if (selectKeys.isEmpty()) {
			return;
		}
	}

}
