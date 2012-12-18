#coding=utf-8
"""Seed.Shell -- Shell program for SEED storage, 
for accessing storage manually, through command line interface. """
from cmd import Cmd
# curl https://raw.github.com/nephics/python-s3/master/s3/S3.py -o s3client.py
# patch sha to hashlib, change sha to hashlib.sha1.
from s3client import AWSAuthConnection
from s3client import CallingFormat

class Shell(Cmd):
    """Seed.Shell"""
    # cmd internal settings
    intro = "SEED command processor"
    doc_header = 'available commands'
    misc_header = 'misc help'
    undoc_header = 'getting help'
    ruler = '-'

    # command parameters
    parameters = []

    def __init__(self, server="127.0.0.1", port=10001):
        Cmd.__init__(self)
        self.prompt = '[' + server + ':' + str(port) + ']>> '
        self.connection = AWSAuthConnection(
            "", "", server=server, port=port, is_secure=False,
            calling_format=CallingFormat.PATH)
        print self.connection, " connected\n", 

    def __del__(self):
        self.connection.__del__()
        Cmd.__del__(self)

    def do_shell(self, line):
        """Run a shell command"""
        print "running shell command:", line
        # beware of the decode/encode pair, since 'output' may vary between OSes.
        output = os.popen(line).read()
        print output

    def do_ls(self, line):
        """list objects"""
        # for long help, implement 'def help_greet(self):' instead.
        print "objects: ", line

    def do_put(self, line):
        """put objects"""
        parameters = line.split()
        if len(parameters) < 3:
            print "parameter not enough, need '[bucket] [key] [value]'."
        else:
            self.connection.put(parameters[0], parameters[1], parameters[2])
            print "put %s=%s into %s" % (parameters[1], parameters[2], parameters[0])

    def do_get(self, line):
        """get objects"""
        parameters = line.split()
        if len(parameters) < 2:
            print "parameter not enough, need '[bucket] [key]'."
        else:
            item = self.connection.get(parameters[0], parameters[1])
            print "get [bucket: %s], [key: %s]" % (parameters[0], parameters[1])
            print item.body

    def do_create(self, line):
        """create bucket"""
        parameters = line.split()
        if len(parameters) < 1:
            print "parameter not enough, need '[bucket]'."
        else:
            self.connection.create_bucket(parameters[0])
            print "bucket '%s' created" % parameters[0]

    def do_delete(self, line):
        """delete objects"""
        print "objects: ", line

    def do_version(self, line):
        """show SEED remote server version"""
        print "WIP"

    def do_status(self, line):
        """show SEED status"""
        print "status: ", line

    def do_exit(self, line):
        """exit from shell"""
        return True
