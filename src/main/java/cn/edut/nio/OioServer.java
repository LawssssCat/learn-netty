package cn.edut.nio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OioServer {

	public static void main(String[] args) throws IOException {

		ExecutorService pool = Executors.newCachedThreadPool();
		// 创建 Socket 服务, 监听10101端口
		ServerSocket server = new ServerSocket(10101);
		System.out.println("服务器启动!");
		while (true) {
			// 获取一个套接字(阻塞1)
			final Socket socket = server.accept();
			System.out.println("来了一个新客户端!");
			pool.execute(new Runnable() {
				@Override
				public void run() {
					// 业务处理
					handler(socket);
				}
			});
		}
	}

	/**
	 * 读取数据
	 */
	private static void handler(Socket socket) {
		try {
			byte[] bytes = new byte[1024];
			InputStream in = socket.getInputStream();
			while (true) {
				// 读取数据(阻塞2)
				int read = in.read(bytes);
				if (read != -1) {
					System.out.println(new String(bytes, 0, read));
				} else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				System.out.println("socket关闭");
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
