package org.fade.demo.niodemo.nettynio;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * http处理程序
 *
 * @author fade
 * @date 2022/07/04
 */
public class MyHttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        MyThreadPoolUtil.submit(() -> {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK);
            response.headers().set("content-type", "application/json");
            response.headers().set("connection", "close");
            String json = "{\"time\":";
            json += System.currentTimeMillis();
            json += "}";
            response.content().writeBytes(json.getBytes());
            channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        });
    }

}
