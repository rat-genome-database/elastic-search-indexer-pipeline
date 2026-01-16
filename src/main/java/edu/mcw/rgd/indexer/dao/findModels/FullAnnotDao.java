package edu.mcw.rgd.indexer.dao.findModels;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mcw.rgd.dao.impl.*;
import edu.mcw.rgd.datamodel.*;
import edu.mcw.rgd.datamodel.annotation.Evidence;
import edu.mcw.rgd.datamodel.ontology.Annotation;
import edu.mcw.rgd.datamodel.ontologyx.Term;
import edu.mcw.rgd.datamodel.ontologyx.TermDagEdge;
import edu.mcw.rgd.datamodel.ontologyx.TermSynonym;
import edu.mcw.rgd.indexer.model.findModels.ModelIndexObject;

import edu.mcw.rgd.process.Utils;
import edu.mcw.rgd.services.ClientInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.xcontent.XContentType;

import java.io.IOException;
import java.util.*;
import java.util.Map;

/**
 * Created by jthota on 3/3/2020.
 */
public class FullAnnotDao {

    public AnnotationDAO adao= new AnnotationDAO();
    public OntologyXDAO xdao= new OntologyXDAO();
    public StrainDAO strainDAO=new StrainDAO();
    public AliasDAO aliasDAO=new AliasDAO();
    public AssociationDAO associationDAO=new AssociationDAO();


}