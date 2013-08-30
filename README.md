trigram
=======

Intro
-----

Triple Graph based Metadata storage - TriGraM

A brief history
---------------

The original motivation is described in [my
thsis](http://cdmd.cnki.com.cn/Article/CDMD-10487-1012268216.htm).

Preliminary version was developed as a simple XQuery program during
2011.2\~2011.8, and can be run from xquilla command line, the input
source file is from [DMTF CIM](http://www.dmtf.org/standards/cim)
model and must be compiled by OpenPegasus,
and the basic idea is to use translated semantic model in helping
storage resource management.

In late 2011, switch the whole toolchain to Python and
[RDFLib](https://github.com/RDFLib/rdflib) for flexible development,
called SEED, aims at efficient metadata storage for distributed file
systems.

In late 2012, considering the scalability issues, the main framework
switched once more, to [Scala](http://www.scala-lang.org/) +
[Jena](http://jena.apache.org/), for an easy integration with
[TDB](http://jena.apache.org/documentation/tdb/).

### SEED

Storage of **Extemporal Ensemble Device**

Organize commodity storage devices with minimum cost to build a
loose-coupled system, which features dynamic
metadata-manager/storage-server/client nodes, and fast deployment.

How to use
----------

1.  deploy [sbt](https://github.com/harrah/xsbt/wiki) with
    [sbteclipse](https://github.com/typesafehub/sbteclipse), open sbt
    console in project root directory, type "update", wait until all the
    dependencies are resolved.

2.  use "eclipse" to generate eclipse project, include ".project" and
    ".classpath".

3.  use "compile" or "package".

4.  use "copy-dependencies" to collect all jars into "target\scala-2.10\lib\".

5.  use loaders in "bin" to execute, "trigram" for \*NIX and
    "trigram.cmd" for Windows.

6.  use "translator" to translate various resources to semantic models,
    the models can then be imported into trigram.

    translate cim model first, for that will be used in other computer system models.

    CIM_All.owl: all-in-one version, complete huge model.

    CIM_Base.owl ...: model group, can be imported when required.

7.  SPARQL

8.  use "thinker" to do inference on semantic models (Work In Progress).

NOTE:

1.  the loaders will search dependencies in "target\scala-2.10\lib\" (step 4).

2.  The sbt ivy cache should be located in a folder without spaces in
    its name.

3.  ivy cache relocation can be done through adding these parameters to
    sbt loader:

    > JAVA\_OPTS=" -Dsbt.ivy.home=d:/java/sbt/.ivy2/
    > -Dsbt.global.base=d:/java/sbt/.sbt/ "\$JAVA\_OPTS

Design
------

-   System architecture, communication and model draft

    > doc/design.fodg

Related Work
------------

Distributed File Systems: [GFS](http://labs.google.com/papers/gfs.html),
[HDFS](http://hadoop.apache.org/index.html), [Ceph](http://ceph.com/),
[Tahoe-LAFS](https://tahoe-lafs.org/trac/tahoe-lafs),
[Storage@home](http://cs.stanford.edu/people/beberg/Storage@home2007.pdf)

Triple Store: [Sesame](http://www.openrdf.org/) and
[Alibaba](http://www.openrdf.org/alibaba.jsp), more can be found in [W3C
LargeTripleStores](http://www.w3.org/wiki/LargeTripleStores)

Author
======

[ShiZhan](http://shizhan.github.com/) (c) 2012 [Apache License Version
2.0](http://www.apache.org/licenses/)
