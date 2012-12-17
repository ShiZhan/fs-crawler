#!/usr/bin/env python
# -*- coding: UTF-8 -*-
"""SEED -- A storage system based on 'Extemporal Ensemble' Devices

Program aims at minimum deployment effort and dependencies, easy to manage
and scale. """

#
#  Copyright 2012 Shi.Zhan
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

import re
import argparse
from shell import Shell
from daemon import Daemon

def main():
    """SEED main program"""

    parser = argparse.ArgumentParser()

    parser.add_argument(
        '-v', '--version',
        action='version',
        version='%(prog)s 1.0')

    parser.add_argument(
        '-D', '--daemon',
        action='store_true',
        dest='daemon',
        default=False,
        help='start daemon on specified port',
        )

    parser.add_argument(
        '-S', '--shell',
        action='store_true',
        dest='shell',
        default=False,
        help='start command line interface on specified server and port'
        )

    parser.add_argument(
        '-s', '--server',
        action='store',
        dest='server',
        default='127.0.0.1',
        help='connect server on specified IP address',
        )

    parser.add_argument(
        '-p', '--port',
        action='store',
        dest='port',
        type=int,
        default=10001,
        help='connect/listen to specified port',
        )

    options = parser.parse_args()

    # valid server, port
    valid_server = \
        re.search(
            '^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])(\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])){3}',
            options.server
            )
    valid_port = (options.port > 1000 and options.port < 65535)
    if not (valid_server and valid_port):
        print "invalid parameter, \
            should be as '-s 127.0.0.1 -p 10001 (between 1000 and 65535)'."
        exit(1)

    # which way to go ... start daemon or open shell?
    if options.shell:
        # run the shell @ host
        Shell(options.server).cmdloop()

    elif options.daemon:
        # run the daemon on specified root directory, bucket depth and port
        Daemon("/cygdrive/r/s3", 0, options.port).run()


if __name__ == '__main__':
    main()
