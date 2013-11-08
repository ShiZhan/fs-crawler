#!/usr/bin/env bash
find $1 -type f -print0 | xargs -0 md5sum | grep -v -e "^md5sum" | cut -b 1-32,34- > output.csv