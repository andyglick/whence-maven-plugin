package com.atlassian.maven.whence.testing.two;

import com.atlassian.maven.whence.testing.Zero;
import com.atlassian.maven.whence.testing.one.One;

@SuppressWarnings("unused")
public class Two {
    static {
        Zero.zero();
        One.one();
    }

    public static void two() {
    }

}
