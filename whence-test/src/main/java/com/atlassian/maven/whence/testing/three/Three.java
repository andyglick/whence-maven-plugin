package com.atlassian.maven.whence.testing.three;

import com.atlassian.maven.whence.testing.Zero;
import com.atlassian.maven.whence.testing.one.One;
import com.atlassian.maven.whence.testing.two.Two;

@SuppressWarnings("unused")
public class Three {

    static {
        Zero.zero();
        One.one();
        Two.two();
    }

    public static void three() {
    }

}
