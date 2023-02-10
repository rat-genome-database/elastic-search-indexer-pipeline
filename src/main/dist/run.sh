. /etc/profile
APPNAME=ESIndexer
APPDIR=/home/rgddata/pipelines/$APPNAME
SERVER=`hostname -s | tr '[a-z]' '[A-Z]'`
EMAIL_LIST=jthota@mcw.edu
if [ "$SERVER" = "REED" ]; then
  EMAIL_LIST=mtutaj@mcw.edu,jthota@mcw.edu,jdepons@mcw.edu
fi
cd $APPDIR
pwd
DB_OPTS="-Dspring.config=$APPDIR/../properties/default_db.xml"


#LOG4J_OPTS="-Dlog4j.configuration=file://$APPDIR/properties/log4j.properties"
#export ES_INDEXER_OPTS="$DB_OPTS $LOG4J_OPTS"
##bin/$APPNAME "$@" | tee run.log

java -Dspring.config=$APPDIR/../properties/default_db2.xml \
    -Dlog4j.configurationFile=file://$APPDIR/properties/log4j2.xml \
    -jar lib/${APPNAME}.jar "$@" > run.log 2>&1

mailx -s "[$SERVER] ES Index Pipeline OK" $EMAIL_LIST < run.log

