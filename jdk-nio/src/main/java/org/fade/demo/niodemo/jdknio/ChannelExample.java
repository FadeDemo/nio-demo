package org.fade.demo.niodemo.jdknio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 通道例子
 *
 * @author fade
 * @date 2022/06/19
 */
public class ChannelExample {

    public static void main(String[] args) throws Exception {
        URL url = ChannelExample.class.getClassLoader().getResource("test.txt");
        FileInputStream in = new FileInputStream(url.getFile());
        FileChannel inChannel = in.getChannel();
        FileOutputStream out = new FileOutputStream("channel.text");
        FileChannel outChannel = out.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (inChannel.read(buffer) != -1) {
            buffer.flip();
            if (buffer.hasRemaining()) {
                outChannel.write(buffer);
            }
            buffer.clear();
        }
        outChannel.close();
        inChannel.close();
    }

}
