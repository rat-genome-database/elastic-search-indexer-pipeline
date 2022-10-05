###Chromosome Indexer
#echo 'Chromosome Indexer .. Environment..' $1
#/home/rgddata/pipelines/ESIndexer/run.sh reindex prod chromosome Chromosomes 2>&1 | mailx -s "[REED] Chromosome Indexer PROD ok" rgd.developers@mcw.edu
#
###Genome Indexer PROD Environment
#echo 'Genome Indexer .. Environment..'$1
# /home/rgddata/pipelines/ESIndexer/run.sh reindex prod  genome GenomeInfo 2>&1 | mailx -s "[REED] Genome Indexer PROD ok" rgd.developers@mcw.edu
#
###Find Models indexer
#
echo 'Models Indexer.. Environment..'+ $1
/home/rgddata/pipelines/ESIndexer/run.sh reindex $1 models Models 2>&1
#
###Phenominer indexer
#
echo 'Phenominer Indexer .. Environment..'+ $1
/home/rgddata/pipelines/ESIndexer/run.sh reindex $1  phenominer Phenominer 2>&1
#
### Search Indexer
#
echo 'Search Indexer.. Environment.. ' $1
/home/rgddata/pipelines/ESIndexer/run.sh reindex $1 search Genes Qtls Strains Sslps Variants GenomicElements Reference Annotations 2>&1 | mailx -s "[REED] Search Indexer $1 ok" rgd.developers@mcw.edu
