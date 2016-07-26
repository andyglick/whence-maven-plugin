package com.atlassian.maven.whence;

import org.apache.maven.plugin.logging.Log;

@SuppressWarnings("unused")
public class MvnLog {

    private final Log log;
    private final boolean verbose;

    MvnLog(Log log, boolean verbose) {
        this.log = log;
        this.verbose = verbose;
    }

    public void verbose(CharSequence content) {
        if (verbose) {
            log.info(content);
        }
    }

    public void debug(CharSequence content) {
        log.debug(content);
    }

    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    public void debug(CharSequence content, Throwable error) {
        log.debug(content, error);
    }

    public void warn(CharSequence content, Throwable error) {
        log.warn(content, error);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void info(CharSequence content, Throwable error) {
        log.info(content, error);
    }

    public void info(CharSequence content) {
        log.info(content);
    }

    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    public void error(Throwable error) {
        log.error(error);
    }

    public void warn(Throwable error) {
        log.warn(error);
    }

    public void error(CharSequence content, Throwable error) {
        log.error(content, error);
    }

    public void info(Throwable error) {
        log.info(error);
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void error(CharSequence content) {
        log.error(content);
    }

    public void debug(Throwable error) {
        log.debug(error);
    }

    public void warn(CharSequence content) {
        log.warn(content);
    }
}
