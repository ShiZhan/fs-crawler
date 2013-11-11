#!/usr/bin/env bash
curl http://ontoware.org/swrc/swrc/SWRCOWL/swrc_updated_v0.7.1.owl -o swrc.owl
curl http://xmlns.com/foaf/spec/index.rdf -o foaf.rdf
curl https://raw.github.com/structureddynamics/Bibliographic-Ontology-BIBO/master/bibo.xml.owl -o bibo.owl
