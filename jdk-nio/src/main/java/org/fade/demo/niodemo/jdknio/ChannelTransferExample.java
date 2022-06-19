package org.fade.demo.niodemo.jdknio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * channel传输例子
 *
 * @author fade
 * @date 2022/06/19
 */
public class ChannelTransferExample {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {
        URL firstUrl = ChannelExample.class.getClassLoader().getResource("test.txt");
        FileInputStream firstIn = new FileInputStream(firstUrl.getFile());
        FileChannel firstInChannel = firstIn.getChannel();
        URL secondUrl = ChannelExample.class.getClassLoader().getResource("channel-transfer.txt");
        FileInputStream secondIn = new FileInputStream(secondUrl.getFile());
        FileChannel secondInChannel = secondIn.getChannel();
        FileOutputStream firstOut = new FileOutputStream("channel-transfer-out-1.text");
        FileChannel firstOutChannel = firstOut.getChannel();
        firstInChannel.transferTo(0, firstInChannel.size(), firstOutChannel);
        FileOutputStream secondOut = new FileOutputStream("channel-transfer-out-2.text");
        FileChannel secondOutChannel = secondOut.getChannel();
        secondOutChannel.transferFrom(secondInChannel, 0, secondInChannel.size());
    }

}
