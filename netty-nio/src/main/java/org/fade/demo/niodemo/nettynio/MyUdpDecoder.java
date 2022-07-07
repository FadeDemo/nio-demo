package org.fade.demo.niodemo.nettynio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * udp译码器
 *
 * @author fade
 * @date 2022/07/06
 */
public class MyUdpDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private static final Logger LOG = LoggerFactory.getLogger(MyUdpDecoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) {
        ByteBuf buf = msg.content();
        String s = buf.toString(StandardCharsets.UTF_8);
//        buf.readerIndex(buf.readerIndex() + buf.readableBytes());
        LOG.info(s);
    }

}
