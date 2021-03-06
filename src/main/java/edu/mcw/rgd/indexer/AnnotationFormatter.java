
package edu.mcw.rgd.indexer;

import edu.mcw.rgd.datamodel.RgdId;
import edu.mcw.rgd.datamodel.SpeciesType;
import edu.mcw.rgd.datamodel.XDBIndex;
import edu.mcw.rgd.datamodel.XdbId;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.process.Utils;
import edu.mcw.rgd.reporting.HTMLTableReportStrategy;
import edu.mcw.rgd.reporting.Link;
import edu.mcw.rgd.reporting.Record;
import edu.mcw.rgd.reporting.Report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jdepons
 * Date: Dec 6, 2010
 */


public class AnnotationFormatter {


    public String buildTable(List<String> records, int columns) {

        int rowCount=(int) Math.ceil(records.size() / columns) + 1;

        StringBuilder table = new StringBuilder("<table class=\"annotationTable\" width='95%' border=0><tr>");

        for (int i=0; i< records.size(); i++) {
            String str = records.get(i);

            if (i==0) {
                table.append("<td valign='top'><table>");
            }else if ((i % rowCount == 0)) {
                table.append("</table></td><td valign='top'><table>");
            }

            table.append(str);

        }
        table.append("</table></td></tr></table>");

        return table.toString();

    }

    public String createGridFormatAnnotations(List<Annotation> annotationList, int objectId, int columns) throws Exception {

        List<String> records = new ArrayList<String>();

        String evidence= "";
        String termAcc="";
        String annotatedRgdId="";
        String term="";

        // by default, CHEBI annots link to new tabular report
        // other annots link to default list-like report
        String annotUrl = null;

        for (Annotation a : annotationList) {

            // compute url based on first term on the list
            if( annotUrl==null ) {
                if( !a.getTermAcc().startsWith("CHEBI") ) {
                    annotUrl = "/rgdweb/report/annotation/main.html";
                } else {
                    annotUrl = "/rgdweb/report/annotation/table.html";
                }
            }

            if (a.getTermAcc().equals(termAcc)) {
                // same term -- combine evidence codes
                if (!evidence.contains(a.getEvidence())) {
                    evidence += "," + a.getEvidence();
                }
            } else {

                if (!term.equals("")) {
                    records.add("<tr><td><img src='/rgdweb/common/images/bullet_green.png' /></td>"+
                            "<td><a href=\"" + annotUrl + "?term=" + termAcc + "&id=" + annotatedRgdId + "\">" + term +
                            " </a><span style=\"font-size:10px;\">&nbsp;(" + evidence + ")</span></td></tr>");
                }

                termAcc = a.getTermAcc();
                annotatedRgdId = a.getAnnotatedObjectRgdId() + "";
                term = a.getTerm();
                evidence = a.getEvidence();
            }
        }

        records.add("<tr><td><img src='/rgdweb/common/images/bullet_green.png' /></td>" +
                "<td><a href=\"" + annotUrl + "?term=" + termAcc + "&id=" + annotatedRgdId + "\">" + term +
                " </a><span style=\"font-size:10px;\">&nbsp;(" + evidence + ")</span></td></tr>");

        return this.buildTable(records, columns);
    }

    public String createGridFormatAnnotationsTable(List<Annotation> annotationList) throws Exception {
        return createGridFormatAnnotationsTable(annotationList, "RGD");

    }


    public String createGridFormatAnnotationsTable(List<Annotation> annotationList, String site) throws Exception {

        // by default, CHEBI annots link to new tabular report
        // other annots link to default list-like report
        String annotUrl = null;

        Report report = new Report();

        Record rec = new Record();
        rec.append("Term");
        rec.append("Qualifier");
        rec.append("Evidence");
        rec.append("With");
        rec.append("Reference");
        rec.append("Notes");
        rec.append("Source");
        rec.append("Original Reference(s)");

        report.append(rec);

        for (Annotation a : annotationList) {

            // compute url based on first term on the list
            if( annotUrl==null ) {
                if( !a.getTermAcc().startsWith("CHEBI") ) {
                    annotUrl = "/rgdweb/report/annotation/main.html";
                } else {
                    annotUrl = "/rgdweb/report/annotation/table.html";
                }
            }

            rec = new Record();

            String termString = "<a href='" + annotUrl + "?term=" + a.getTermAcc() + "&id=" + a.getAnnotatedObjectRgdId() + "'>" + a.getTerm() + " </a>";
            rec.append(termString);

            if (a.getQualifier() == null) {
                rec.append("&nbsp;");
            } else {
                rec.append(a.getQualifier());
            }
            rec.append(a.getEvidence());

            if (a.getWithInfo() == null) {
                rec.append("&nbsp;");
            } else {
                int objectKey = a.getRgdObjectKey();
                if( Utils.stringsAreEqualIgnoreCase(a.getDataSrc(), "ClinVar") ) {
                    // see comments in ClinVar pipeline annotator
                    if( a.getSpeciesTypeKey()== SpeciesType.HUMAN )
                        objectKey = RgdId.OBJECT_KEY_VARIANTS; // ClinVar gene annotations derived from variant annotations
                    else if( a.getSpeciesTypeKey()== SpeciesType.MOUSE ||  a.getSpeciesTypeKey()== SpeciesType.RAT )
                        objectKey = RgdId.OBJECT_KEY_GENES;
                    else
                        objectKey = 0; // determine the object type by querying the db
                }

                String val = getLinkForWithInfo(a.getWithInfo(), objectKey);

                if (!site.equals("RGD")) {
                    val = val.replaceAll("RGD", site);
                }

                rec.append(val);
            }

            if( a.getRefRgdId()!=null && a.getRefRgdId()>0 ) {
                rec.append("<a href='" + Link.ref(a.getRefRgdId()) + "' title='show reference'>" + a.getRefRgdId() + "</a>");
            }
            else {
                rec.append("&nbsp;");
            }

            // notes: some could be as big as 4k of text; every sentence ends with "; ",
            //  we display only first sentence followed by "..." link
            if( a.getNotes()==null ) {
                rec.append("&nbsp;");
            }
            else {
                String notes;
                int pos = a.getNotes().indexOf("; ");
                if( pos > 0 ) {
                    notes = a.getNotes().substring(0, pos);
                    notes += "; "+makeGeneTermAnnotLink(a.getAnnotatedObjectRgdId(), a.getTermAcc(), "pmore");
                } else
                    notes = a.getNotes();

                rec.append(notes);
            }

            if (!site.equals("RGD")) {
                rec.append(a.getDataSrc().replaceAll("RGD",site));

            }else {
                rec.append(a.getDataSrc());
            }

            if (a.getXrefSource() == null) {
                rec.append("&nbsp;");
            } else {
                // show at most 2 references; if there are more than two, first one is shown and then text 'more ...' is shown
                String[] refs = a.getXrefSource().split("\\|");
                if( refs.length==1 ) {
                    rec.append(makeRefLink(refs[0]));
                }
                else if( refs.length==2 ) {
                    rec.append(makeRefLink(refs[0])+" "+makeRefLink(refs[1]));
                }
                else {
                    // more than 2 links: display only 1st and then link 'more ...'
                    rec.append(makeRefLink(refs[0])+makeGeneTermAnnotLink(a.getAnnotatedObjectRgdId(), a.getTermAcc(), "pmore"));
                }
            }

            report.append(rec);
        }

        return new HTMLTableReportStrategy().format(report);
    }

    String getLinkForWithInfo(String withInfo, int objectKey) throws Exception {

        try {
            if(withInfo.contains("|")){
                String[] multipleInfos = withInfo.split("\\|");
                String withInfoField="";
                for(String info:multipleInfos){
                    withInfoField += "<a href='" + getLinkForWithInfoEx(info, objectKey) + "'>" + info + "</a> ";
                }
                return withInfoField;
            }else{
                return "<a href='" + getLinkForWithInfoEx(withInfo, objectKey) + "'>" + withInfo + "</a>";
            }
        } catch (Exception e) {
            return withInfo;
        }
    }

    String getLinkForWithInfoEx(String withInfo, int objectKey) throws Exception {

        if( withInfo.startsWith("RGD:") ) {
            if( objectKey!=0 )
                return Link.it(Integer.parseInt(withInfo.substring(4)), objectKey);
            else
                return Link.it(Integer.parseInt(withInfo.substring(4)));
        }
        else
            return Link.it(withInfo);
    }

    public String createGridFormatAnnotatedObjects(List<Annotation> annotationList, int columns) throws Exception {

        List<String> records = new ArrayList<>();

        for (Annotation a : annotationList) {

            String objSymbol = Utils.NVL(a.getObjectSymbol(), "NA");
            String objName = Utils.NVL(a.getObjectName(), "NA");

            records.add("<tr><td><img src='/rgdweb/common/images/bullet_green.png' /></td>"+
                        "<td><a href=\"" + Link.it(a.getAnnotatedObjectRgdId(), a.getRgdObjectKey()) + "\" class='geneList" + a.getSpeciesTypeKey() + "'>" + objSymbol +
                        " </a><span style=\"font-size:10px;\">&nbsp;(" + objName + ")</span></td></tr>");
        }

        return this.buildTable(records, columns);
    }

    /**
     * @param  id
     * @return  id
     * @throws Exception
     * if id is a pubmed id, create a link to NCBI pubmed article;
     * else if id is a REF_RGD_ID, create a link to RGD reference report page;
     * otherwise just show the link
     */
    static public String makeRefLink(String id) throws Exception {
        if( id.startsWith("PMID:") ) {
            return XDBIndex.getInstance().getXDB(XdbId.XDB_KEY_PUBMED).getALink(id.substring(5), id);
        }
        else if( id.startsWith("REF_RGD_ID:") ) {
            return "<a href=\""+ Link.ref(Integer.parseInt(id.substring(11)))+"\">"+id+"</a>";
        }
        else {
            return id;
        }
    }

    static String makeGeneTermAnnotLink(int rgdId, String termAcc, String aclass) {

        String text = aclass.equals("imore") ? "&nbsp;&nbsp;&nbsp;" : "more ...";
        String str = " <a class=\""+aclass+"\" href=\"/rgdweb/report/annotation/table.html?id=" + rgdId;
        str += "&term=" + termAcc + "\" title=\"see all interactions and original references for this gene and chemical\">"+text+"</a>";
        return str;
    }

    /**
     * return a subset of annotations matching given aspect
     * @param annotationList list of annotations
     * @param aspect aspect
     * @return a subset of annotations matching given aspect; could be empty list
     */
    public List<Annotation> filterList(List<Annotation> annotationList, String aspect) {

        List<Annotation> returnList = new ArrayList<Annotation>();
        for (Annotation annot : annotationList) {
            if (annot.getAspect().equalsIgnoreCase(aspect)) {
                returnList.add(annot);
            }
        }

        // sort by term, case insensitive
        Collections.sort(returnList, new Comparator<Annotation>() {
            public int compare(Annotation o1, Annotation o2) {
                return Utils.stringsCompareToIgnoreCase(o1.getTerm(), o2.getTerm());
            }
        });
        return returnList;
    }

}
