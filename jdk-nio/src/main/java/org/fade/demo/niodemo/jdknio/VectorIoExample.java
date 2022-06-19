package org.fade.demo.niodemo.jdknio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 向量io例子
 *
 * @author fade
 * @date 2022/06/19
 */
public class VectorIoExample {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        FileOutputStream out = new FileOutputStream("vectorIo.txt");
        FileChannel outChannel = out.getChannel();
        ByteBuffer firstBuffer = ByteBuffer.allocate(8);
        ByteBuffer secondBuffer = ByteBuffer.allocate(400);
        firstBuffer.asIntBuffer().put(420);
        secondBuffer.asCharBuffer().put("hello nio");
        outChannel.write(new ByteBuffer[] {firstBuffer, secondBuffer});
        FileInputStream in = new FileInputStream("vectorIo.txt");
        FileChannel inChannel = in.getChannel();
        firstBuffer.clear();
        secondBuffer.clear();
        inChannel.read(new ByteBuffer[] {firstBuffer, secondBuffer});
        assert firstBuffer.asIntBuffer().get() == 420;
        assert "hello nio".equals(secondBuffer.asCharBuffer().toString());
    }

}
