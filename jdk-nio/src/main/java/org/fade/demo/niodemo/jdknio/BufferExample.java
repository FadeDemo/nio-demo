package org.fade.demo.niodemo.jdknio;

import java.nio.ByteBuffer;

/**
 * 缓冲区例子
 *
 * @author fade
 * @date 2022/06/19
 */
public class BufferExample {

    public static void main(String[] args) {
        // 分配缓冲区
        ByteBuffer allocate = ByteBuffer.allocate(1024);
        // get读缓冲区
        byte b = allocate.get();
        // 从缓冲区读入通道略

        // put写缓冲区
        allocate.put((byte) 12);
        // 从通道写入缓冲区略
    }

}
