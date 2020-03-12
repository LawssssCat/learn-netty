package cn.edut.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO服务器
 */
public class NioServer {
	// 通道管理器
	private Selector selector;

	/**
	 * 获得一个 ServerSocket 通道, 并且对该通道做一些初始化的工作
	 * 
	 * @param port 绑定的端口号
	 * @throws IOException
	 */
	public void initServer(int port) throws IOException {
		// 获得一个ServerSocket通道
		ServerSocketChannel socketChannelServer = ServerSocketChannel.open();
		// 设置通道为非阻塞
		socketChannelServer.configureBlocking(false);
		// 将该通道对应的 SeverSocket 绑定到 port 端口
		socketChannelServer.socket().bind(new InetSocketAddress(port));
		// 获得一个通道管理器
		this.selector = Selector.open();
		// 将通道管理器和该通道绑定, 并且为该通道注册 SelectionKey.OP_ACCEPT 事件
		// 注册该事件后, 当该事件到达时, selector.select() 会返回
		// 如果该事件没有到达, selector.select() 会一直阻塞
		socketChannelServer.register(selector, SelectionKey.OP_ACCEPT);
	}

	/**
	 * 采用轮询的方式监听 selector 上是否有需要处理的事件。 如果有,则进行处理
	 */
	public void listen() throws IOException {
		System.out.println("服务端启动成功!");
		// 轮询访问 selector
		while (true) {
			// 当注册的事件到达时, 方法返回; 否则, 该方法会一直阻塞
			this.selector.select();
			// 获得 selector 中选中的项的迭代器, 选中的项为注册的事件
			Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
			while (iterator.hasNext()) {
				SelectionKey key = iterator.next();
				// 删除已经选中的 key, 以防重复处理
				iterator.remove();
				handler(key);
			}
		}
	}

	/**
	 * 处理请求
	 * 
	 * @param key selector 轮询出的被触发事件
	 * @throws IOException
	 */
	private void handler(SelectionKey key) throws IOException {
		if (key.isAcceptable()) {
			// 客户端请求连接事件 OP_ACCEPT
			handlerAccept(key);
		} else if (key.isReadable()) {
			// 获得了可读的事件 OP_READ
			handlerRead(key);
		}
	}

	/**
	 * 处理连接请求
	 * 
	 * @param key OP_ACCEPT 事件
	 * @throws IOException
	 */
	private void handlerAccept(SelectionKey key) throws IOException {
		ServerSocketChannel socketChannelServer = (ServerSocketChannel) key.channel();
		// 获得和客户端连接的通道
		SocketChannel channel = socketChannelServer.accept();
		// 设置成非阻塞
		channel.configureBlocking(false);

		// 在这里可以给客户端发送信息哦
		System.out.println("新的客户端连接");
		// 在和客户端连接成功后, 为了可以接收到客户端的信息, 需要给通道设置可读的权限
		channel.register(this.selector, SelectionKey.OP_READ);
	}

	/**
	 * 处理读的事件
	 * 
	 * @param key OP_READ
	 * @throws IOException
	 */
	private void handlerRead(SelectionKey key) throws IOException {
		// 服务器可读信息:得到事件发生的Socket通道
		SocketChannel channel = (SocketChannel) key.channel();
		// 创建读取的缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(10); // bytes
		// 跟传统socket类似
		int read = channel.read(buffer);
		if(read<=0) {
			System.out.println("客户端关闭");
			key.cancel();
			return ;
		}
		byte[] data = buffer.array();
		String msg = new String(data);
		System.out.println("服务端收到信息:" + msg);

		// 回写数据
		ByteBuffer outBuffer = ByteBuffer.wrap("好的".getBytes());
		// 将消息回送给客户端
			channel.write(outBuffer);
	}

	/**
	 * 启动服务端测试
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		NioServer server = new NioServer();
		server.initServer(8000);
		server.listen();
	}
}
