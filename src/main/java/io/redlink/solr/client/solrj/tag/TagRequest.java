package io.redlink.solr.client.solrj.tag;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;

/**
 * A request for the <a href="https://github.com/OpenSextant/SolrTextTagger">SolrTextTagger</a> request handler
 * <p>
 * <b>Usage:</b>
 * <pre>
 *  String content; //The content to tag
 *  SolrClient client; //The client for the Solr Servier configured with SolrTextTagger request handler
 *  
 *  //NOTE: TaggerParams can be reused for multiple requests
 *  TagParams params = new TagParams();
 *  params.setOverlaps(Overlaps.LONGEST_DOMINANT_RIGHT);
 *  params.setFields("id", "title", "cat");
 *  
 *  TagRequest request = new TagRequest(params, 
 *      new StringStream(content, "text/plain"));
 *  //request.setPath("/my-custom-tagger-path"); //default is "/tag"
 *  try {
 *      TagResponse response = request.process(client);
 *      for(Tag tag : response.getTags()){
 *          int start = tag.getStart();
 *          int end = tag.getEnd();
 *      String mention = content.subString(start, end);
 *          for(SolrDocument doc : tag.getDocs()){
 *              String title = (String)doc.getFirstValue("title");
 *              String cat = (String)doc.getFirstValue("cat");
 *          }
 *      }
 *  } catch (SolrServerException e) {
 *      //do error handling here
 *  }
 * </pre>
 * 
 * @author Rupert Westenthaler
 *
 */
public class TagRequest extends SolrRequest<TagResponse> {

    private static final long serialVersionUID = -6416805321692746218L;

    private static final String DEFAULT_PATH = "/tag";
    private SolrParams params;
    private Set<ContentStream> contentStreams;
    private String docIdField = "id"; //ID is used by the default Solr schema

    public TagRequest(SolrParams params, ContentStream content) {
        super(METHOD.POST, DEFAULT_PATH);
        this.params = params;
        this.contentStreams = Collections.singleton(content);
    }

    public TagRequest(String path) {
        super(METHOD.POST, path);
    }

    public String getDocIdField() {
        return docIdField;
    }
    
    public void setDocIdField(String docIdField) {
        this.docIdField = docIdField;
    }
    
    @Override
    public SolrParams getParams() {
        return params;
    }

    @Override
    public Collection<ContentStream> getContentStreams() throws IOException {
        return contentStreams;
    }

    @Override
    protected TagResponse createResponse(SolrClient client) {
        TagResponse response = new TagResponse(client);
        response.setDocIdField(docIdField);
        return response;
    }

}