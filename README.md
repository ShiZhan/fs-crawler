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
