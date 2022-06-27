package org.fade.demo.niodemo.jdknio;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 字符集例子
 * @author fade
 * @date 2022/06/27
 */
public class CharsetExample {

    public static void main(String[] args) {
        Charset cs = StandardCharsets.UTF_8;
        System.out.println(cs.displayName());
        System.out.println(cs.canEncode());
        String st = "Welcome, it is Charset test Example.";
        ByteBuffer bytebuffer = ByteBuffer.wrap(st.getBytes());
        CharBuffer charbuffer = cs.decode(bytebuffer);
        ByteBuffer newBytebuffer = cs.encode(charbuffer);
        while (newBytebuffer.hasRemaining()) {
            char ca = (char) newBytebuffer.get();
            System.out.print(ca);
        }
        newBytebuffer.clear();
    }

}
