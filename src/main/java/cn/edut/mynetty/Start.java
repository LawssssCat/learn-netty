package cn.edut.mynetty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.edut.mynetty.pool.NioSelectorRunnablePool;

/**
 * 启动函数
 */
public class Start {
	public static void main(String[] args) {
		
		ExecutorService bossExecutor = Executors.newCachedThreadPool();
		ExecutorService workerExecutor = Executors.newCachedThreadPool() ;
		
		//初始化线程
		NioSelectorRunnablePool nioSelectorRunnablePool = new NioSelectorRunnablePool(bossExecutor, workerExecutor);
		
		//获取服务类
		//绑定端口
		
		System.out.println("start");
	}
}
