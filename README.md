TriGraM
=======

Introduction
------------

Triple Graph based Meta-data storage - TriGraM

A brief history
---------------

The original motivation is described in
[my thesis](http://cdmd.cnki.com.cn/Article/CDMD-10487-1012268216.htm).

Preliminary version was developed as a simple XQuery program during 2011.2\~2011.8,
and can be run from [XQilla](http://xqilla.sourceforge.net/HomePage) command line.
The input source file is from [DMTF CIM](http://www.dmtf.org/standards/cim) model
and must be compiled by OpenPegasus, and the basic idea is to use translated
semantic model in helping storage management.

However, the XQuery-based version has many limitations, such as costly XML processing,
program portability and hard to maintain.

In late 2011, switch the whole tool-chain to Python and
[RDFLib](https://github.com/RDFLib/rdflib), Python is easy to code, and RDFLib has
a neat interface for semantic model manipulation. The new project named [SEED](#seed),
its functionality had been expanded to efficient meta-data storage for distributed
file systems.

In late 2012, scalability became an important issue, on both the code and triples,
so the program framework switched once more, to [Scala](http://www.scala-lang.org/) +
[Jena](http://jena.apache.org/). Scala provides the language-level parallelism,
and Jena is a comprehensive framework for both model manipulation and persistent
storage, the triple storage is [TDB](http://jena.apache.org/documentation/tdb/).

### SEED

Storage of **Extemporal Ensemble Device**

Organize commodity storage devices with minimum cost to build a loose-coupled system,
which features dynamic {meta-data manager|storage server|client} nodes,
and fast deployment.

How to use
----------

1.  Preparations

    * deploy [sbt](https://github.com/harrah/xsbt/wiki), which is required by
      most scala and java projects.

    * open sbt console in project root directory, run `update` to update
      project dependencies, then the following commands, or run them directly
      from OS shell.

    * [optional] use sbt `eclipse` to generate eclipse project,
      include `.project` and `.classpath`, this is based on sbt plugin:
      [sbteclipse](https://github.com/typesafehub/sbteclipse).

    * run sbt `compile`.

    * run sbt `copy-dep` to collect all project dependencies into
      `target\scala-2.10\lib\`.

2.  Run main program

    Use scripts in project root to run the program, `trigram` for \*NIX platform and
    `trigram.cmd` for Windows.

    Online help: `trigram -h|--help`

3.  CIM base model

    Before generating models for resources such as directory, checksums, archives etc.,
    there is a base model that should be created first, it is derived from the DMTF CIM
    model, which is mentioned previously.

    Download CIM schema XML version from official web site and extract, then use
    `trigram -m cim(ex) <CIM in XML>` to do the translation, generate the CIM model.
    **cim** for all-in-one version, **cimex** for individual class models, all will be
    put into "cim" directory.

    NOTE: the default location can be altered by **CIM_DATA**.

    Also update the CIM vocabulary if needed, by using `trigram -V <CIM in XML>`,
    which will be used in other modelers, especially those for modeling
    computer devices, components and their associations.

    * CIM_All.owl: all-in-one version, complete huge model.

    * CIM_Base.owl ...: individual class models, which is comprised of thousands of
      interrelated sub-models for use by dedicated modeling domain. The class models
      can be imported when required through **OWL.imports**, provide modeling flexibility.

4.  Modeling

    use `trigram -m <modeler with options>` to invoke the corresponding modeler,
    translate designated resources into semantic models.

    NOTE: some modelers, related to file system modeling, are using file URI to create
    individuals, so the absolute file path must be globally unique, it is important to
    set full path in modeler parameters, for them to generate consistent URIs.

5.  Model combining and importing

    Several interrelated models can be put together as a single model file for using
    in particular tasks, for example, the directory, checksum and archive models are
    associated through path names. For combining those models:

    * `trigram -c <models>`, to generate a combined model

    * `trigram -i <models>`, to importing them into the same repository.

    Triple database location is defined in 'TGM_DATA' and defaults to
    '<current working directory>/.trigram'.

6.  CIM based modeling

    For example, 'dir(ex)/arc' modelers will use CIM vocabulary to generate models,
    which contain **OWL:imports** leads to CIM class models, generated by **cimex**.
    Those imported class models can be gathered by `trigram -g <model>` for
    independent use without the CIM repository and triple database.

    The purpose is to merge required sub-models into one aggregated model.
    The merged model can than be easily inferred, imported or transferred.

    CIM class models can also be imported into triple database for use with the models
    already stored in it.

7.  SPARQL execution

    * use `trigram -q <SPARQL query>` for query.

    * use `trigram -u <SPARQL update>` for update.

    * or use `trigram` to enter command shell, and mode <query|update> to
      switch between query and update modes.

8.  Helper scripts in test directory

    * (accessory project) GenerateProtegeCatalog.scala

      scan designated directory and create a catalog file for Protege to load
      so that the models can be easily imported and processed.

    * (accessory project) PrepareTestDirectory.scala

      create a set of files and directories for testing.

    * cim/get-cim.sh

      show how to get the latest CIM schema from official web site

    * test/checksum-collect.sh

      collect checksum from designated directory to CSV file
      'test/checksum-names' can be used to carry the concept and property names

    * test/get-acad.sh

      download a set of meta-model for academic literature (for future use).

9.  Use [sbt assembly](https://github.com/sbt/sbt-assembly) to create a portable all-in-one JAR.

    Use `java -jar target\scala-2.10\trigram-assembly-1.0.jar` to run.

NOTE:

1.  `trigram` will search dependencies in `target\scala-2.10\lib\` (step 4).

2.  The sbt ivy cache should be located in a folder without spaces in its name.

3.  ivy cache relocation can be done through adding these parameters to
    sbt loader:

    JAVA\_OPTS=" -Dsbt.ivy.home=d:/java/sbt/.ivy2/
    -Dsbt.global.base=d:/java/sbt/.sbt/ "\$JAVA\_OPTS

Design
------

-   System architecture, communication and model draft:

    `doc/design.odg`

-   Brief introduction:

    `doc/oam.odt`

-   Command line interface:

    provides the following entries:

    0. default entry: enter console

    1. '-h' show help

    2. '-v' show version

    3. '-i' import specified model into local triple storage

    4. '-q' query local storage

    5. '-u' update local storage

    6. '-c' combine multiple models

    7. '-V' generate CIM vocabulary from CIM schema XML 

    8. '-g' gather imported CIM class models into designated model

    9. '-m' invoke modeler 'MODELER' to translate specified source

Related Work
------------

Distributed File Systems: [GFS](http://labs.google.com/papers/gfs.html),
[HDFS](http://hadoop.apache.org/index.html), [Ceph](http://ceph.com/),
[Tahoe-LAFS](https://tahoe-lafs.org/trac/tahoe-lafs),
[Kosmos Distributed Filesystem](http://code.google.com/p/kosmosfs/),
[Quantcast File System](https://github.com/quantcast/qfs),
[Storage@home](http://cs.stanford.edu/people/beberg/Storage@home2007.pdf)

Triple Store: [Sesame](http://www.openrdf.org/) and
[Alibaba](http://www.openrdf.org/alibaba.jsp), more can be found in [W3C
LargeTripleStores](http://www.w3.org/wiki/LargeTripleStores)

Author
======

[ShiZhan](http://shizhan.github.com/) (c) 2013 [Apache License Version
2.0](http://www.apache.org/licenses/)
