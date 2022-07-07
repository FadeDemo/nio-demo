package org.fade.demo.niodemo.nettynio;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * udp发送方
 *
 * @author fade
 * @date 2022/07/06
 */
public class MyUdpSender {

    public static void main(String[] args) {
        int workerThreadCount = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);
        EventLoopGroup group = new NioEventLoopGroup(workerThreadCount, new DefaultThreadFactory("Udp-Sender", true));
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST,true)
                .handler(new ChannelInitializer<NioDatagramChannel>() {

                    @Override
                    protected void initChannel(NioDatagramChannel ch) {
                        ch.pipeline().addLast(new MyUdpEncoder());
                    }

                });
        try {
            Channel channel = bootstrap.bind(0).sync().channel();
            MyThreadPoolUtil.submit(() -> {
                String msg = "message in udp format";
                channel.writeAndFlush(msg);
            });
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }

}
