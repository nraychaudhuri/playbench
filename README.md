playbench
=========

contains a stripped down (ie. 0 scala dependency) version of play 2.0 with some generated source files thrown in for good measure

download and install sbt 0.12
=============================
https://github.com/harrah/xsbt/

run
===

2.9.2
=====

```sbt12 -Dscala.version=2.9.2 clean compile```


2.10
====
```sbt12 -Dscala.version=2.10.0-SNAPSHOT clean compile```

this will download and install the latest snapshot from https://oss.sonatype.org/content/repositories/snapshots

how to run it with a local snapshot
===================================

replace 2.10.0-SNAPSHOT jars in ~/.ivy2/cache/org.scala-lang