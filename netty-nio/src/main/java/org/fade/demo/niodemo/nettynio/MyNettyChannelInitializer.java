package org.fade.demo.niodemo.nettynio;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author fade
 * @date 2022/07/04
 */
public class MyNettyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline= socketChannel.pipeline();
        //这里以HTTP服务器为例
        pipeline.addLast("http_decoder", new HttpRequestDecoder());
        pipeline.addLast("http_aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("http_encoder", new HttpResponseEncoder());
        pipeline.addLast("http_chunked", new ChunkedWriteHandler());
        pipeline.addLast("http_defined", new MyHttpHandler());
    }

}
