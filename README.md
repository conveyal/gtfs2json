gtfs2json
=========

A command line tool for converting GTFS into JSON per https://gist.github.com/kpwebb/6690301

mvn package

java -jar target/gtfs2json.jar path/to/gtfs.zip 


Options:  -r  to export a routes directory with route specific json files.



Writes  data to data/ in working directory.

