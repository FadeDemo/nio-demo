package org.fade.demo.niodemo.nettynio;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * udp接收方
 *
 * @author fade
 * @date 2022/07/06
 */
public class MyUdpReceiver {

    public static void main(String[] args) {
        int workerThreadCount = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);
        EventLoopGroup group = new NioEventLoopGroup(workerThreadCount, new DefaultThreadFactory("Udp-Receiver", true));
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST,true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    @Override
                    protected void initChannel(NioDatagramChannel ch) {
                        ch.pipeline().addLast(new MyUdpDecoder());
                    }

                });
        try {
            // fixme 一条消息打印两次？
            bootstrap.bind(7758).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }

}
