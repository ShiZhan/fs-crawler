#!/usr/bin/env bash
# <directory> <output>
#find $1 test/ -type f -exec md5sum -t {} \; | sed 's/  /*/' | awk 'BEGIN { FS = "*" }; "$0 !~ /^md5sum/" {print $2 "*" $1}' > $2
#find $1 -type f -print0 | xargs -0 md5sum -t | sed 's/  /*/' | awk 'BEGIN { FS = "*" }; "$0 !~ /^md5sum/" {print $2 "*" $1}' > $2
#find $1 -type f -print0 | xargs -0 md5sum -t | sed 's/  /*/' | awk -v prefix=$2 'BEGIN { FS = "*" }; "$0 !~ /^md5sum/" {print prefix $2 "*" $1}' > $3
find $1 -type f -print0 | xargs -0 md5sum -t | sed 's/  /*/' > $2