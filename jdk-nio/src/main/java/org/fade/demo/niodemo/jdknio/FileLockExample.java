package org.fade.demo.niodemo.jdknio;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 文件锁例子
 *
 * @author fade
 * @date 2022/06/28
 */
public class FileLockExample {

    private static final Path PATH;

    static {
        try {
            PATH = Paths.get(ChannelExample.class.getClassLoader().getResource("test.txt").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 200, 5,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                x -> new Thread(x, "FileLock-example-" + UUID.randomUUID()));
        executor.execute(new WriteRunnable());
        Thread.sleep(1000);
//        executor.execute(new ReadRunnable());
        executor.shutdown();
    }

    static class WriteRunnable implements Runnable {

        @Override
        public void run() {
            // 看起来不是很好用
            // File locks are held on behalf of the entire Java virtual machine.
            // They are not suitable for controlling access to a file by multiple threads within the same virtual machine.
            // 似乎还和操作系统有关
            // todo 等待寻找合适的方法进行测试
            String input = "* end of the file.";
            System.out.println("Input string to the test file is: " + input);
            ByteBuffer buf = ByteBuffer.wrap(input.getBytes());
            Path pt;
            try {
                pt = Paths.get(ChannelExample.class.getClassLoader().getResource("test.txt").toURI());
                FileChannel fc = FileChannel.open(pt, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                System.out.println("File channel is open for write and Acquiring lock...");
                FileLock lock = fc.lock();
                System.out.println("The Lock is shared: " + lock.isShared());
                fc.write(buf);
                Thread.sleep(20000);
                // Releases the Lock?
                fc.close();
                System.out.println("Content Writing is complete. Therefore close the channel and release the lock.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    static class ReadRunnable implements Runnable {

        @Override
        public void run() {
            try {
                FileReader filereader = new FileReader(PATH.toString());
                BufferedReader bufferedreader = new BufferedReader(filereader);
                String tr = bufferedreader.readLine();
                System.out.println("The Content of testout-file.txt file is: ");
                while (tr != null) {
                    System.out.println("    " + tr);
                    tr = bufferedreader.readLine();
                }
                filereader.close();
                bufferedreader.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
