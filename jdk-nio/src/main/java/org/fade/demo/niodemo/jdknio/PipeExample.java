package org.fade.demo.niodemo.jdknio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * 管道例子
 *
 * @author fade
 * @date 2022/06/27
 */
public class PipeExample {

    public static void main(String[] args) throws IOException {
        // 创建管道
        Pipe pipe = Pipe.open();
        // 往sink channel写数据
        Pipe.SinkChannel skChannel = pipe.sink();
        String td = "Data is successfully sent for checking the java NIO Channel Pipe.";
        ByteBuffer bb = ByteBuffer.allocate(512);
        bb.clear();
        bb.put(td.getBytes());
        bb.flip();
        while (bb.hasRemaining()) {
            skChannel.write(bb);
        }
        // 从source channel中读数据
        Pipe.SourceChannel sourceChannel = pipe.source();
        sourceChannel.configureBlocking(false);
        bb = ByteBuffer.allocate(512);
        while (sourceChannel.read(bb) > 0) {
            bb.flip();
            while (bb.hasRemaining()) {
                char c = (char) bb.get();
                System.out.print(c);
            }
            bb.clear();
        }
        sourceChannel.close();
        skChannel.close();
    }

}
