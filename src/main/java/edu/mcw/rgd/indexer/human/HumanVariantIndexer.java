package edu.mcw.rgd.indexer.human;


import edu.mcw.rgd.dao.impl.MapDAO;

import edu.mcw.rgd.datamodel.Chromosome;
import edu.mcw.rgd.datamodel.Sample;

import edu.mcw.rgd.process.Utils;
import org.apache.log4j.Logger;


import java.util.*;


/**
 * Created by jthota on 11/8/2019.
 */
public class HumanVariantIndexer {
 /*   private Thread t;
    private String objectType;
    private String index;
    private String line;
    private String[] header;
    private boolean processLinesWithMissingADDP=true;
    private boolean processVariantsSameAsRef ;
    private int strainCount;
    public static Map<String, Sample> sampleIdMap;
    public HumanVariantIndexer(){}
    public HumanVariantIndexer(String indexName,String line, int strainCount, String[] header){

        index=indexName;
        processLinesWithMissingADDP = true;
        processVariantsSameAsRef = false;
        this.line=line;
        this.strainCount=strainCount;
        this.header=header;
        this.index=indexName;
    }

    public void run() {
        Logger log= Logger.getLogger("main");
        String[] v = line.split("[\\t]", -1);

        if (v.length == 0 || v[0].length() == 0 || v[0].charAt(0) == '#')
            //skip lines with "#"
            return;

        // validate chromosome
        String chr = null;
        try {
            chr = getChromosome(v[0]);
        } catch (Exception e) {
            e.printStackTrace();

        }
    //    log.info(Thread.currentThread().getName() + ":CHR- " + chr + " started " + new Date());
        // skip lines with invalid chromosomes (chromosome length must be 1 or 2
        if( chr==null || chr.length()>2 ) {
            return;
        }

        // variant pos
        int pos = Integer.parseInt(v[1]);
        String rsId=v[2];
        String refNuc = v[3];
        String alleles = v[4];
     //   log.info(Thread.currentThread().getName() + ":rsId " + rsId + " started " + new Date());
        // get index of GQ - genotype quality
        String[] format = v[8].split(":");
        int ADindex = readADindex(format);
        int DPindex = readDPindex(format);
        if( ADindex < 0 || DPindex<0 ) {
            if( !processLinesWithMissingADDP ) {
                return;
            }
        }

        // rgdid and hgvs name
        Integer rgdId = null;
        String hgvsName = null;
        String id = v[2];
        if( !Utils.isStringEmpty(id) && id.startsWith("RGDID:")) {
            // sample ID field for ClinVar:
            // RGDID:8650299;NM_001031836.2(KCNU1):c.2736+27C>T
            int semicolonPos = id.indexOf(';');
            if( semicolonPos>0 ) {
                rgdId = Integer.parseInt(id.substring(6, semicolonPos));
                hgvsName = id.substring(semicolonPos+1);
            } else {
                System.out.println("missing semicolon");
            }
        }
        List<String> strains= new ArrayList<>();
        for( int i=9; i<9+strainCount && i<v.length; i++ ) {
            //     System.out.println(i);
            String strain = header[i];
            String genotype= v[i].substring(0,3);;
            try {

                if( !genotype.equals("./.") && !genotype.equals("0/0") && !genotype.trim().equals("") )
                    strains.add(strain);

            }catch (Exception e){
                System.out.println("POSITION: "+ pos +"\tGENOTYPE:" +genotype);
                e.printStackTrace();
            }


        }

        try {
            processStrains(chr, pos, refNuc, alleles, rgdId, hgvsName,rsId, strains);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    void processStrains(String chr, int pos, String refNuc, String alleleString, Integer rgdId, String hgvsName,String rsId, List<String> strains) throws Exception {


        // read counts for all alleles, as determined by genotype
        int[] readCount = null;
        // format is in 0/1:470,63:533:99:507,0,3909
        int readDepth = 0;

        if( processLinesWithMissingADDP ) {
            readDepth = 9;
            readCount = new int[] {9, 9, 9, 9, 9, 9, 9, 9};
        }


        int totalDepth = 0;

        if( processLinesWithMissingADDP ) {
            totalDepth = 9;
        }


        // for the single Reference to multiple Variants
        int alleleCount = getAlleleCount(alleleString);
        String[] alleles = (refNuc+","+alleleString).split(",");


        VariantLoad3 loader= new VariantLoad3();
        List<CommonFormat2Line> lines= new ArrayList<>();
        // for every allele, including refNuc
        for (String allele: alleles ) {
            // skip the line variant if it is the same with reference (unless an override is specified)
            if( !processVariantsSameAsRef && refNuc.equals(allele) ) {
                continue;
            }

            CommonFormat2Line line = new CommonFormat2Line();
            line.setChr(chr);
            line.setPos(pos);
            line.setRefNuc(refNuc);
            line.setVarNuc(allele);
            line.setRsId(rsId);
            line.setCountA(getReadCountForAllele("A", alleles, readCount));
            line.setCountC(getReadCountForAllele("C", alleles, readCount));
            line.setCountG(getReadCountForAllele("G", alleles, readCount));
            line.setCountT(getReadCountForAllele("T", alleles, readCount));
            if( totalDepth>0 )
                line.setTotalDepth(totalDepth);
            line.setAlleleDepth(getReadCountForAllele(allele, alleles, readCount));
            line.setAlleleCount(alleleCount);
            line.setReadDepth(readDepth);
            line.setRgdId(rgdId);
            line.setHgvsName(hgvsName);
            //    line.setStrains(sb.toString());
            line.setStrainList(strains);
            //    this.writer.writeLine(line);

            loader.processLine(line);
            // incrementVariantCount(strain, chr);
        }

    }
    int getReadCountForAllele(String allele, String[] alleles, int[] readCount) {

        for( int i=0; i<alleles.length; i++ ) {
            if( alleles[i].equals(allele) )
                return readCount[i];
        }
        return 0;
    }

    int getAlleleCount(String s) {
        int alleleCount = 1;
        for( int i=0; i<s.length(); i++ ) {
            if( s.charAt(i)==',' )
                alleleCount++;
        }
        return alleleCount;
    }

    int readADindex(String[] format) {

        // format : "GT:AD:DP:GQ:PL"
        for( int i=0; i<format.length; i++ ) {
            if( format[i].equals("AD") ) {
                return i;
            }
        }

        // try CLCAD2
        for( int i=0; i<format.length; i++ ) {
            if( format[i].equals("CLCAD2") ) {
                return i;
            }
        }
        return -1;
    }

    int readDPindex(String[] format) {

        // format : "GT:AD:DP:GQ:PL"
        // determine which position separated by ':' occupies
        for( int i=0; i<format.length; i++ ) {
            if( format[i].equals("DP") ) {
                return i;
            }
        }
        return -1;
    }
    String getChromosome(String chr) throws Exception {

        String c = getChromosomeImpl(chr);
        if( c!=null && c.equals("M") ) {
            c = "MT";
        }
        return c;
    }
    String getChromosomeImpl(String chr) throws Exception {
        // chromosomes could be provided as 'NC_005100.4'
        if( chr.startsWith("NC_") ) {
            return getChromosomeFromDb(chr);
        }

        chr = chr.replace("chr", "").replace("c", "");
        // skip lines with invalid chromosomes (chromosome length must be 1 or 2
        if( chr.length()>2 || chr.contains("r") || chr.equals("Un") ) {
            return null;
        }
        return chr;
    }
    String getChromosomeFromDb(String refseqNuc) throws Exception {
        String chr = _chromosomeMap.get(refseqNuc);
        if( chr==null ) {
            MapDAO dao = new MapDAO();
            Chromosome c = dao.getChromosome(refseqNuc);
            if( c!=null ) {
                chr = c.getChromosome();
                _chromosomeMap.put(refseqNuc, chr);
            }
        }
        return chr==null ? null : chr;
    }
    Map<String,String> _chromosomeMap = new HashMap<>();
  /*  public static void main(String[] args) throws Exception {
        SampleDAO sdao= new SampleDAO();
        IndexAdmin admin=new IndexAdmin();
        admin.createIndex("variant_mappings", "variant_human");
        sdao.setDataSource(DataSourceFactory.getInstance().getCarpeNovoDataSource());
        List<Sample> samples= sdao.getSamplesByMapKey(17);
        Map<String, Sample> sampleIdMap= new HashMap<>();
        for(Sample s:samples){
            String analysisName=s.getAnalysisName();
            String substr;
            if(analysisName.contains(":")){
              substr  = analysisName.substring(analysisName.indexOf("(")+1, analysisName.indexOf(":"));
            }else {
                if(analysisName.contains(")"))
                 substr  = analysisName.substring(analysisName.indexOf("(")+1, analysisName.indexOf(")"));
               else
                substr  = analysisName.substring(analysisName.indexOf("(")+1);

            }
         //   System.out.println(substr);
           sampleIdMap.put(substr, s);
        }
       HumanVariantIndexer.sampleIdMap=sampleIdMap;
       File file= new File(args[0]);
        HumanVariantIndexer indexer=new HumanVariantIndexer();
        BufferedReader reader;
        if( file.getName().endsWith(".txt.gz") ) {
            reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
        } else {
            reader = new BufferedReader(new FileReader(file));
        }
        String line;
        int lineCount=0;
        String[] header = null;
        int strainCount = 0;
        while((line=reader.readLine())!=null ) {
            // skip comment line
            if( line.startsWith("#") ){
                header = line.substring(1).split("[\\t]", -1);
                strainCount = header.length - 9;
            }
              //  continue;
            else if(lineCount<2){
            //   System.out.println(line);
                indexer.processLine(line, strainCount, header);
                lineCount++;
            }



        }

        // cleanup
        reader.close();
    }*/

}
