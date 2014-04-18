File System Crawler
===================

Introduction
------------

Crawler for gathering File System Meta-data as triples.

How to use
----------

1.  Preparations

    * deploy [sbt](https://github.com/harrah/xsbt/wiki), which is required by
      most scala and java projects.
    * open sbt console in project root directory, run `update` to update
      project dependencies, then the following commands, or run them directly
      from OS shell.
    * [optional] use sbt `eclipse` to generate eclipse project, based on sbt plugin:
      [sbteclipse](https://github.com/typesafehub/sbteclipse).
    * run sbt `compile`.
    * run sbt `copy-dep` to collect all project dependencies into
      `target\scala-2.10\lib\`.

2.  Run main program

    Use scripts in project root to run the program, `fscrawler` for \*NIX platform and
    `fscrawler.cmd` for Windows.

    Online help: `fscrawler -h`

3.  Modeling

    use `fscrawler -m <modeler with options>` to invoke the corresponding modeler,
    translate designated resources into semantic models.

    NOTE: some modelers, related to file system modeling, are using file URI to create
    individuals, so the absolute file path must be globally unique, it is important to
    set full path in modeler parameters, for them to generate consistent URIs.

4.  Use [sbt assembly](https://github.com/sbt/sbt-assembly) to create a portable all-in-one JAR.

    Use `java -jar target\scala-2.10\FSCrawler-assembly-1.0.jar` to run.

NOTE:

1.  `fscrawler` will search dependencies in `target\scala-2.10\lib\` (step 4).
2.  The sbt ivy cache should be located in a folder without spaces in its name.
3.  ivy cache relocation can be done through adding these parameters to
    sbt loader:

    JAVA\_OPTS=" -Dsbt.ivy.home=d:/java/sbt/.ivy2/
    -Dsbt.global.base=d:/java/sbt/.sbt/ "\$JAVA\_OPTS

Command line interface
----------------------

    provides the following entries:

    1. '-h' show help
    2. '-v' show version
    3. '-m' invoke modeler 'MODELER' to translate specified source

Author
======

[ShiZhan](http://shizhan.github.com/) (c) 2013 [Apache License Version
2.0](http://www.apache.org/licenses/)