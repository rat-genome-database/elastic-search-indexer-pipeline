###Genome Indexer PROD Environment
echo 'Genome Indexer .. Environment..' $1
/home/rgddata/pipelines/ESIndexer/run.sh reindex $1  genome GenomeInfo 2>&1 | mailx -s "[REED] Genome Indexer PROD ok" rgd.developers@mcw.edu
###Chromosome Indexer
echo 'Chromosome Indexer .. Environment..' $1
/home/rgddata/pipelines/ESIndexer/run.sh reindex $1 chromosome Chromosomes 2>&1 | mailx -s "[REED] Chromosome Indexer PROD ok" rgd.developers@mcw.edu
