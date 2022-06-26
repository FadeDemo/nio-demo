package org.fade.demo.niodemo.jdknio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 选择器例子
 *
 * @author fade
 * @date 2022/06/26
 */
public class SelectorExample {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 200, 5,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                x -> new Thread(x, "selector-example-" + UUID.randomUUID()));
        executor.execute(new ServerRunnable());
        Thread.sleep(1000);
        executor.execute(new ClientRunnable());
        executor.shutdown();
    }

    static class ServerRunnable implements Runnable {

        @Override
        public void run() {
            try {
                // 创建选择器
                Selector selector = Selector.open();
                System.out.println("server: Selector is open for making connection: " + selector.isOpen());
                // 获取ServerSocketChannel并注册
                ServerSocketChannel channel = ServerSocketChannel.open();
                InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8080);
                channel.bind(hostAddress);
                channel.configureBlocking(false);
                int ops = channel.validOps();
                channel.register(selector, ops, null);
                for (;;) {
                    System.out.println("server: Waiting for the select operation...");
                    int noOfKeys = selector.select();
                    System.out.println("server: The Number of selected keys are: " + noOfKeys);
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> itr = selectedKeys.iterator();
                    while (itr.hasNext()) {
                        SelectionKey ky = itr.next();
                        if (ky.isAcceptable()) {
                            // The new client connection is accepted
                            SocketChannel client = channel.accept();
                            client.configureBlocking(false);
                            // The new connection is added to a selector
                            client.register(selector, SelectionKey.OP_READ);
                            System.out.println("server: The new connection is accepted from the client: " + client);
                        } else if (ky.isReadable()) {
                            // Data is read from the client
                            SocketChannel client = (SocketChannel) ky.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(256);
                            client.read(buffer);
                            String output = new String(buffer.array()).trim();
                            System.out.println("server: Message read from client: " + output);
                            if ("Bye Bye".equals(output)) {
                                client.close();
                                System.out.println("server: The Client messages are complete; close the session.");
                            }
                        }
                        itr.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    static class ClientRunnable implements Runnable {

        @Override
        public void run() {
            try {
                InetSocketAddress hostAddress = new InetSocketAddress("localhost", 8080);
                SocketChannel channel = SocketChannel.open(hostAddress);
                String message = "Hello World!";
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
                channel.write(buffer);
                Thread.sleep(1000);
                String end = "Bye Bye";
                ByteBuffer endBuffer = ByteBuffer.wrap(end.getBytes(StandardCharsets.UTF_8));
                channel.write(endBuffer);
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
