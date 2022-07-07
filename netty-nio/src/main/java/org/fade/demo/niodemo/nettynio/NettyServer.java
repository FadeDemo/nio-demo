package org.fade.demo.niodemo.nettynio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * netty服务器
 *
 * @author fade
 * @date 2022/07/03
 */
public class NettyServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    public static void main(String[] args) {
        int bossThreadCount = 1;
        int workerThreadCount = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossThreadCount,
                new DefaultThreadFactory("Boss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThreadCount,
                new DefaultThreadFactory("Worker", true));
        LOG.info("Thread count-> NettyServerBoss {} , NettyWorker {}", bossThreadCount, workerThreadCount);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .childOption(ChannelOption.SO_SNDBUF, 4 * 1024)
                    .childOption(ChannelOption.SO_RCVBUF, 4 * 1024)
                    .childHandler(new MyNettyChannelInitializer());
            LOG.info("try to bind all ports for netty server");
            List<ChannelFuture> bindFutures = new ArrayList<>();
            bindFutures.add(bootstrap.bind(10086).sync());
            LOG.info("succeed to bind port {} by netty server", 10086);
            // Wait until all the server socket is closed.
            // 单个端口处问题-不影响其它端口-尽可能减少损失
            for (ChannelFuture future : bindFutures) {
                try {
                    // 这里会一直等待
                    future.channel().closeFuture().sync();
                } catch (Throwable e) {
                    LOG.error("error due to {}", e.toString());
                }
            }
        } catch (Exception e) {
            LOG.error("error due to {}", e.toString());
        } finally {
            // 先关闭boss 再关闭worker
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            LOG.error("the server is closed , please review the log with ERROR label");
            System.exit(-1);
        }
    }

}
