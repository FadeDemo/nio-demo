package org.fade.demo.niodemo.jdknio;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * 编码解码例子
 *
 * @author fade
 * @date 2022/06/27
 */
public class EncodeDecodeExample {

    public static void main(String[] args) throws CharacterCodingException {
        Charset cs = StandardCharsets.UTF_8;
        CharsetDecoder decoder = cs.newDecoder();
        CharsetEncoder encoder = cs.newEncoder();
        String st = "Example of Encode and Decode in Java NIO.";
        @SuppressWarnings("DuplicatedCode")
        ByteBuffer bb = ByteBuffer.wrap(st.getBytes());
        CharBuffer cb = decoder.decode(bb);
        ByteBuffer newBuffer = encoder.encode(cb);
        while (newBuffer.hasRemaining()) {
            char ca = (char) newBuffer.get();
            System.out.print(ca);
        }
        newBuffer.clear();
    }

}
