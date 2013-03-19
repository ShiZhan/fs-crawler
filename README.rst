.. -*- coding: utf-8 -*-

trigram
=======

Intro
------

Triple Graph based Metadata storage

A brief history
----------------

The motivation is described here_.

Original version was developed as a simple XQuery program during 2011.2~2011.8, and can be run from xquilla command line, the input source file must be compiled by OpenPegasus.

In late 2011, switch the whole toolchain to Python and RDFLib_ for easier development, called SEED_.

In late 2012, considering the scalability issues, the main framework switched once more, to Scala_ + Jena_.

.. _here: http://cdmd.cnki.com.cn/Article/CDMD-10487-1012268216.htm
.. _RDFLib: https://github.com/RDFLib/rdflib
.. _Scala: http://www.scala-lang.org/
.. _Jena: http://jena.apache.org/

SEED
^^^^^

Storage of **Extemporal Ensemble Device**

Organize commodity storage devices with minimum cost to build a loose-coupled system, which features dynamic metadata-manager/storage-server/client nodes, and fast deployment.

How to use
-----------

1. deploy sbt_ with sbteclipse_, open sbt console in project root directory, wait until all the dependencies are resolved.

2. use "eclipse" to generate eclipse project, include ".project" and ".classpath".

3. use "compile" or "package".

4. use loaders in "bin" to execute program, "trigram" for \*\NIX and "trigram.cmd" for Windows.

NOTE:

1. the loaders will read classpath from .classpath file generated in step 1.

2. The ivy cache should be located in a folder without spaces in its name.

3. ivy cache relocation can be done through passing these parameters to sbt loader:

        JAVA_OPTS=" -Dsbt.ivy.home=d:/java/sbt/.ivy2/ -Dsbt.global.base=d:/java/sbt/.sbt/ "$JAVA_OPTS 

.. _sbt: https://github.com/harrah/xsbt/wiki
.. _sbteclipse: https://github.com/typesafehub/sbteclipse

Design
------

* System architecture draft

    doc/design.fodg

* Communication draft

    doc/session.fodg

* Metadata model draft

    doc/model.fodg

Related Work
------------

Various Distributed File Systems: GFS_, HDFS_, Ceph_, `Tahoe-LAFS`_, `Storage@home`_

Author
======

`ShiZhan <http://shizhan.github.com/>`_ (c) 2012 `Apache License Version 2.0 <http://www.apache.org/licenses/>`_ 

.. _GFS: http://labs.google.com/papers/gfs.html
.. _HDFS: http://hadoop.apache.org/index.html
.. _Ceph: http://ceph.com/
.. _`Tahoe-LAFS`: https://tahoe-lafs.org/trac/tahoe-lafs
.. _`Storage@home`: http://cs.stanford.edu/people/beberg/Storage@home2007.pdf
