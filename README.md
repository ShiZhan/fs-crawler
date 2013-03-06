trigram
=======

Intro
------

Triple Graph based Metadata storage

A brief history
----------------

The motivation is described [here](http://cdmd.cnki.com.cn/Article/CDMD-10487-1012268216.htm).

Original version was developed as a simple XQuery program during 2011.2~2011.8, and can be run from xquilla command line, the input source file must be compiled by OpenPegasus.

In late 2011, switch the whole toolchain to Python and [RDFLib](https://github.com/RDFLib/rdflib) for easier development.

In late 2012, considering the scalability issues, the main framework switched once more, to [Scala](http://www.scala-lang.org/) + [Jena](http://jena.apache.org/).

How to use
-----------

1. deploy [sbt](https://github.com/harrah/xsbt/wiki) with [sbteclipse](https://github.com/typesafehub/sbteclipse), open sbt console in project root directory, wait until all the dependencies are resolved.

2. use "eclipse" to generate eclipse project, include ".project" and ".classpath".

3. use "compile" or "package".

4. use loaders in "bin" to execute program, "trigram" for *NIX and "trigram.cmd" for Windows.

NOTE:

1. the loaders will read classpath from .classpath file generated in step 1.

2. The ivy cache should be located in a folder without spaces in its name.

3. 2 can be done through passing these parameters to sbt loader:

        JAVA_OPTS=" -Dsbt.ivy.home=d:/java/sbt/.ivy2/ -Dsbt.global.base=d:/java/sbt/.sbt/ "$JAVA_OPTS 

