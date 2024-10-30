# DO NOT EDIT THIS FILE - See: https://jetty.org/docs/

[description]
Enables Experimental CompressionHandler for dynamic compression for the entire server.
Enable a one or more compression libs too. (compression-gzip, compression-brotli, or compression-zstandard)

[tags]
server
handler
compression
experimental

[depend]
server

[lib]
lib/compression/jetty-compression-server-${jetty.version}.jar

[xml]
etc/jetty-compression-handler.xml

