package com.atlassian.maven.whence.testing.one;

import com.atlassian.maven.whence.testing.Zero;

@SuppressWarnings("unused")
public class One {
    static {
        Zero.zero();
    }

    public static void one() {}

}
