package cn.edut.server;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class Server {

	public static void main(String[] args) {
		// 服务类
		ServerBootstrap bootstrap = new ServerBootstrap();

		ExecutorService bossExecutor = Executors.newCachedThreadPool();
		ExecutorService workerExecutor = Executors.newCachedThreadPool();

		// 设置niosocket工厂
		bootstrap.setFactory(new NioServerSocketChannelFactory(bossExecutor, workerExecutor));

		// 设置管道的工厂
		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			// 管道可以理解为:一堆过滤器
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				// 通常服务类都是xxx+s,如channels
				ChannelPipeline pipeline = Channels.pipeline();
				// 添加handler
				pipeline.addLast("decoder", new StringDecoder());
				pipeline.addLast("encoder", new StringEncoder());
				pipeline.addLast("helloHandler", new HelloHandler());
				return pipeline;
			}
		});

		// 为服务类绑定端口
		bootstrap.bind(new InetSocketAddress(10101));
		
		System.out.println("服务启动!");
	}
}
