#!/usr/bin/env bash
# <directory> <output>
find $1 -type f -print0 | xargs -0 md5sum | awk 'BEGIN { FS = "*" }; "$0 !~ /^md5sum/" {print $2 "*" $1}' > $2