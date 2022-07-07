package org.fade.demo.niodemo.nettynio;

import java.util.concurrent.*;

/**
 * 线程池工具类
 *
 * @author fade
 * @date 2022/07/04
 */
public class MyThreadPoolUtil {

    private static final String POOL_NAME = "business";

    private static final ThreadPoolExecutor BUSINESS = new ThreadPoolExecutor(10, 200, 5,
            TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
            x -> new Thread(x, POOL_NAME));

    /**
     * <p>执行{@link Runnable}</p>
     * @param runnable {@link Runnable}
     * */
    public static void execute(Runnable runnable) {
        BUSINESS.execute(runnable);
    }

    /**
     * <p>提交{@link Runnable}</p>
     * @param runnable {@link Runnable}
     * @return {@link Future}
     * */
    public static Future<?> submit(Runnable runnable) {
        return BUSINESS.submit(runnable);
    }

    /**
     * <p>提交{@link Callable}</p>
     * @param callable {@link Callable}
     * @return {@link Future}
     * */
    public static <T> Future<T> submit(Callable<T> callable) {
        return BUSINESS.submit(callable);
    }

}
