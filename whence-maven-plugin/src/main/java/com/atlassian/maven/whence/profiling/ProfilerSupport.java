package com.atlassian.maven.whence.profiling;

import org.apache.maven.plugin.logging.Log;

import java.io.File;

public class ProfilerSupport {

    public static void waitForProfiler(Log log, int waitMS) {
        Long then = now();
        String homeDir = System.getProperty("user.home");
        File profilerFile = new File(homeDir, "profiler.wait");
        if (profilerFile.exists()) {
            long initialLastModified = profilerFile.lastModified();
            long lastLogTime = then;
            while (true) {
                if (elapsedTime(lastLogTime) > 1000) {
                    log.warn("Waiting for ~/profiler.wait to change...");
                    lastLogTime = now();
                }
                sleep(100);
                long lastModified = profilerFile.lastModified();
                if (elapsedTime(then) > waitMS) {
                    log.warn(String.format("~/profiler.wait was not touched within within %d ms... Continuing on anyway.", waitMS));
                }
                if (lastModified > initialLastModified) {
                    break;
                }
            }
        }

    }

    private static long now() {
        return System.currentTimeMillis();
    }

    private static long elapsedTime(long then) {
        return (now() - then);
    }

    private static void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
