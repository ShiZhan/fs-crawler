#coding=utf-8
"""Seed.Daemon -- Daemon program for SEED storage, 
client, name server and data server all-in-one.

S3 client with this module:

    c = S3.AWSAuthConnection("", "", server="localhost", port=8888,
                             is_secure=False)
    c.create_bucket("mybucket")
    c.put("mybucket", "mykey", "a value")
    print c.get("mybucket", "mykey").body

"""
import bisect
import datetime
import hashlib
import os
import os.path
import urllib

from tornado import escape
from tornado import httpserver
from tornado import ioloop
from tornado import web
from tornado.util import bytes_type

class Daemon(web.Application):
    """Seed.Daemon -- Providing an S3-like storage server based on local files.

    If bucket depth is given, we break files up into multiple directories
    to prevent hitting file system limits for number of files in each
    directories. 1 means one level of directories, 2 means 2, etc.
    """
    def __init__(self, port=10001, root_directory='/tmp/s3', bucket_depth=0):
        web.Application.__init__(self, [
            (r"/", RootHandler),
            (r"/([^/]+)/(.+)", ObjectHandler),
            (r"/([^/]+)/", BucketHandler),
        ])
        self.port = port
        self.directory = os.path.abspath(root_directory)
        if not os.path.exists(self.directory):
            os.makedirs(self.directory)
        self.bucket_depth = bucket_depth

    def run(self):
        """run server loop on the given port at the given path."""
        print 'Serving HTTP on 0.0.0.0 port %d ...' % self.port
        http_server = httpserver.HTTPServer(self)
        http_server.listen(self.port)
        ioloop.IOLoop.instance().start()


class BaseRequestHandler(web.RequestHandler):
    SUPPORTED_METHODS = ("PUT", "GET", "DELETE")

    def render_xml(self, value):
        assert isinstance(value, dict) and len(value) == 1
        self.set_header("Content-Type", "application/xml; charset=UTF-8")
        name = value.keys()[0]
        parts = []
        parts.append('<' + escape.utf8(name) +
                     ' xmlns="http://doc.s3.amazonaws.com/2006-03-01">')
        self._render_parts(value.values()[0], parts)
        parts.append('</' + escape.utf8(name) + '>')
        self.finish('<?xml version="1.0" encoding="UTF-8"?>\n' +
                    ''.join(parts))

    def _render_parts(self, value, parts=[]):
        if isinstance(value, (unicode, bytes_type)):
            parts.append(escape.xhtml_escape(value))
        elif isinstance(value, int) or isinstance(value, long):
            parts.append(str(value))
        elif isinstance(value, datetime.datetime):
            parts.append(value.strftime("%Y-%m-%dT%H:%M:%S.000Z"))
        elif isinstance(value, dict):
            for name, subvalue in value.iteritems():
                if not isinstance(subvalue, list):
                    subvalue = [subvalue]
                for subsubvalue in subvalue:
                    parts.append('<' + escape.utf8(name) + '>')
                    self._render_parts(subsubvalue, parts)
                    parts.append('</' + escape.utf8(name) + '>')
        else:
            raise Exception("Unknown S3 value type %r", value)

    def _object_path(self, bucket, object_name):
        if self.application.bucket_depth < 1:
            return os.path.abspath(os.path.join(
                self.application.directory, bucket, object_name))
        hash = hashlib.md5(object_name).hexdigest()
        path = os.path.abspath(os.path.join(
            self.application.directory, bucket))
        for i in range(self.application.bucket_depth):
            path = os.path.join(path, hash[:2 * (i + 1)])
        return os.path.join(path, object_name)


class RootHandler(BaseRequestHandler):
    def get(self):
        names = os.listdir(self.application.directory)
        buckets = []
        for name in names:
            path = os.path.join(self.application.directory, name)
            info = os.stat(path)
            buckets.append({
                "Name": name,
                "CreationDate": datetime.datetime.utcfromtimestamp(
                    info.st_ctime),
            })
        self.render_xml({"ListAllMyBucketsResult": {
            "Buckets": {"Bucket": buckets},
        }})


class BucketHandler(BaseRequestHandler):
    def get(self, bucket_name):
        prefix = self.get_argument("prefix", u"")
        marker = self.get_argument("marker", u"")
        max_keys = int(self.get_argument("max-keys", 50000))
        path = os.path.abspath(os.path.join(self.application.directory,
                                            bucket_name))
        terse = int(self.get_argument("terse", 0))
        if not path.startswith(self.application.directory) or \
           not os.path.isdir(path):
            raise web.HTTPError(404)
        object_names = []
        for root, dirs, files in os.walk(path):
            for file_name in files:
                object_names.append(os.path.join(root, file_name))
        skip = len(path) + 1
        for i in range(self.application.bucket_depth):
            skip += 2 * (i + 1) + 1
        object_names = [n[skip:] for n in object_names]
        object_names.sort()
        contents = []

        start_pos = 0
        if marker:
            start_pos = bisect.bisect_right(object_names, marker, start_pos)
        if prefix:
            start_pos = bisect.bisect_left(object_names, prefix, start_pos)

        truncated = False
        for object_name in object_names[start_pos:]:
            if not object_name.startswith(prefix):
                break
            if len(contents) >= max_keys:
                truncated = True
                break
            object_path = self._object_path(bucket_name, object_name)
            c = {"Key": object_name}
            if not terse:
                info = os.stat(object_path)
                c.update({
                    "LastModified": datetime.datetime.utcfromtimestamp(
                        info.st_mtime),
                    "Size": info.st_size,
                })
            contents.append(c)
            marker = object_name
        self.render_xml({"ListBucketResult": {
            "Name": bucket_name,
            "Prefix": prefix,
            "Marker": marker,
            "MaxKeys": max_keys,
            "IsTruncated": truncated,
            "Contents": contents,
        }})

    def put(self, bucket_name):
        path = os.path.abspath(os.path.join(
            self.application.directory, bucket_name))
        if not path.startswith(self.application.directory) or \
           os.path.exists(path):
            raise web.HTTPError(403)
        os.makedirs(path)
        self.finish()

    def delete(self, bucket_name):
        path = os.path.abspath(os.path.join(
            self.application.directory, bucket_name))
        if not path.startswith(self.application.directory) or \
           not os.path.isdir(path):
            raise web.HTTPError(404)
        if len(os.listdir(path)) > 0:
            raise web.HTTPError(403)
        os.rmdir(path)
        self.set_status(204)
        self.finish()


class ObjectHandler(BaseRequestHandler):
    def get(self, bucket, object_name):
        object_name = urllib.unquote(object_name)
        path = self._object_path(bucket, object_name)
        if not path.startswith(self.application.directory) or \
           not os.path.isfile(path):
            raise web.HTTPError(404)
        info = os.stat(path)
        self.set_header("Content-Type", "application/unknown")
        self.set_header("Last-Modified", datetime.datetime.utcfromtimestamp(
            info.st_mtime))
        object_file = open(path, "r")
        try:
            self.finish(object_file.read())
        finally:
            object_file.close()

    def put(self, bucket, object_name):
        object_name = urllib.unquote(object_name)
        bucket_dir = os.path.abspath(os.path.join(
            self.application.directory, bucket))
        if not bucket_dir.startswith(self.application.directory) or \
           not os.path.isdir(bucket_dir):
            raise web.HTTPError(404)
        path = self._object_path(bucket, object_name)
        if not path.startswith(bucket_dir) or os.path.isdir(path):
            raise web.HTTPError(403)
        directory = os.path.dirname(path)
        if not os.path.exists(directory):
            os.makedirs(directory)
        object_file = open(path, "w")
        object_file.write(self.request.body)
        object_file.close()
        self.finish()

    def delete(self, bucket, object_name):
        object_name = urllib.unquote(object_name)
        path = self._object_path(bucket, object_name)
        if not path.startswith(self.application.directory) or \
           not os.path.isfile(path):
            raise web.HTTPError(404)
        os.unlink(path)
        self.set_status(204)
        self.finish()
