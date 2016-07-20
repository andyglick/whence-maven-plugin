This Maven plugin can help you answer the question "Where is this code coming from?"

It uses [BND](http://aqute.biz/Code/bnd) under the covers to reverse engineer the maven dependencies of a project.

It has two report modes, a tree mode and a flat mode.

By default it only shows exports but you can make it specify all package information

# Parameters

* -Dtype=[ tree | flat ]
* -Ddetail=[ exports | all ]


# Tree Mode

mvn com.atlassian.maven:whence-maven-plugin:whence -Ddetail=all -Dstyle=tree

produces output like :

    [INFO]    +- joda-time:joda-time:jar:1.6:provided
    [INFO]    |      exports (3)
    [INFO]    |      	org.joda.time
    [INFO]    |      	org.joda.time.chrono
    [INFO]    |      	org.joda.time.format
    [INFO]    |      imports (0)
    [INFO]    |      contains (22)
    [INFO]    |      	org.joda.time
    [INFO]    |      	org.joda.time.base
    [INFO]    |      	org.joda.time.chrono
    [INFO]    |      	org.joda.time.convert
    [INFO]    |      	org.joda.time.field
    [INFO]    |      	org.joda.time.format
    [INFO]    |      	org.joda.time.tz
    [INFO]    |      	org.joda.time.tz.data.Africa
    [INFO]    |      	org.joda.time.tz.data.America
    [INFO]    |      	org.joda.time.tz.data.America.Argentina
    [INFO]    |      	org.joda.time.tz.data.America.Indiana
    [INFO]    |      	org.joda.time.tz.data.America.Kentucky
    [INFO]    |      	org.joda.time.tz.data.America.North_Dakota
    [INFO]    |      	org.joda.time.tz.data.Antarctica
    [INFO]    |      	org.joda.time.tz.data.Asia
    [INFO]    |      	org.joda.time.tz.data.Atlantic
    [INFO]    |      	org.joda.time.tz.data.Australia
    [INFO]    |      	org.joda.time.tz.data
    [INFO]    |      	org.joda.time.tz.data.Etc
    [INFO]    |      	org.joda.time.tz.data.Europe
    [INFO]    |      	org.joda.time.tz.data.Indian
    [INFO]    |      	org.joda.time.tz.data.Pacific
    [INFO]    |      references (6)
    [INFO]    |      	java.lang
    [INFO]    |      	java.io
    [INFO]    |      	java.util
    [INFO]    |      	java.security
    [INFO]    |      	java.lang.ref
    [INFO]    |      	java.text


# Flat Mode

mvn com.atlassian.maven:whence-maven-plugin:whence -Ddetail=all -Dstyle=flat

produces output like :

    [INFO]    joda-time:joda-time:jar:1.6:provided exports org.joda.time
    [INFO]    joda-time:joda-time:jar:1.6:provided exports org.joda.time.chrono
    [INFO]    joda-time:joda-time:jar:1.6:provided exports org.joda.time.format
    [INFO]    joda-time:joda-time:jar:1.6:provided imports 0 packages
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.base
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.chrono
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.convert
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.field
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.format
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Africa
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.America
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.America.Argentina
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.America.Indiana
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.America.Kentucky
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.America.North_Dakota
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Antarctica
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Asia
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Atlantic
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Australia
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Etc
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Europe
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Indian
    [INFO]    joda-time:joda-time:jar:1.6:provided contains org.joda.time.tz.data.Pacific
    [INFO]    joda-time:joda-time:jar:1.6:provided references java.io
    [INFO]    joda-time:joda-time:jar:1.6:provided references java.lang
    [INFO]    joda-time:joda-time:jar:1.6:provided references java.lang.ref
    [INFO]    joda-time:joda-time:jar:1.6:provided references java.security
    [INFO]    joda-time:joda-time:jar:1.6:provided references java.text
    [INFO]    joda-time:joda-time:jar:1.6:provided references java.util