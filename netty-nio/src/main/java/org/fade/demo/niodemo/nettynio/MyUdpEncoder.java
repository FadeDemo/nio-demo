package org.fade.demo.niodemo.nettynio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * udp编码器
 *
 * @author fade
 * @date 2022/07/06
 */
public class MyUdpEncoder extends MessageToMessageEncoder<String> {

    private static final InetSocketAddress ADDRESS = new InetSocketAddress("255.255.255.255", 7758);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = channelHandlerContext.alloc().buffer(bytes.length);
        buffer.writeBytes(bytes);
        DatagramPacket datagramPacket = new DatagramPacket(buffer, ADDRESS);
        list.add(datagramPacket);
    }

}
