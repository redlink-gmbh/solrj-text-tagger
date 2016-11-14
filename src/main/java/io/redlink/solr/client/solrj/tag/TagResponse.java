package io.redlink.solr.client.solrj.tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.SolrResponseBase;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A response as sent by the <a href="https://github.com/OpenSextant/SolrTextTagger">SolrTextTagger</a> request handler
 * <p>
 * <b>Usage:</b>
 * <pre>
 *  String content; //The content to tag
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
 *      //TODO: Error Handling
 *  }
 * </pre>
 * 
 * @author Rupert Westenthaler
 *
 */
public class TagResponse extends SolrResponseBase {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private static final long serialVersionUID = 3978938148590733417L;

    private SolrClient solrClient;

    private NamedList<Object> _header;
    private SolrDocumentList _results;
    private List<NamedList<Object>> _tags;
    
    protected Map<Object, SolrDocument> docMap;
    protected List<Tag> tags = Collections.emptyList();

    private String docIdField;


    /**
     * Utility constructor to set the solrServer and namedList
     */
    public TagResponse( NamedList<Object> res , SolrClient solrClient){
        this.setResponse( res );
        this.solrClient = solrClient;
    }

    public TagResponse(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setResponse( NamedList<Object> res ) {
        super.setResponse( res );
        for( int i=0; i<res.size(); i++ ) {
            String n = res.getName( i );
            if( "responseHeader".equals( n ) ) {
              _header = (NamedList<Object>) res.getVal( i );
            } else if("tags".equals(n)){
                this._tags = (List<NamedList<Object>>) res.getVal( i );
            } else if( "response".equals( n ) ) {
                _results = (SolrDocumentList) res.getVal( i );
            }
        }
        if(_results != null){
            docMap = new HashMap<>();
            _results.stream().forEach( doc -> {
                Object id = doc.get(docIdField);
                assert id != null;
                if(id == null){
                    log.warn("NULL value for document id field value. This indicates that {} is"
                            + "not the document id field of the schema! Please do set the correct"
                            + "field name via TagRequest#setDocIdField(..)", docIdField);
                }
                SolrDocument old = docMap.put(id, doc);
                assert old == null;
                if(old != null){
                    log.warn("Multiple result document with id='{}'. This indicates that {} is"
                            + "not the document id field of the schema! Please do set the correct"
                            + "field name via TagRequest#setDocIdField(..)", id, docIdField);
                }
            });
        }
        if(_tags != null){
            this.tags = new ArrayList<Tag>(_tags.size());
            for(NamedList<Object> tag : _tags){
                this.tags.add(new Tag(tag));
            }
        } else {
            this.tags = Collections.emptyList();
        }
    }
    
    /**
     * The Document ID field is required for assigning
     * {@link #getResults()} with the IDs of the {@link #getTags()}.
     * The default <code>id</code> will work for most solr schema.
     * However if this is not set correctly the {@link Tag#getDocs()}
     * method will not work correctly.
     * @return the document ID field name
     */
    protected void setDocIdField(String docIdField) {
        this.docIdField = docIdField;
        
    }
    /**
     * Getter for the document ID field name. This is required to
     * assign {@link #getResults()} to {@link Tag}s.
     * @return the document id field name
     */
    public String getDocIdField() {
        return docIdField;
    }
    
    public NamedList<Object> getHeader() {
        return _header;
      }

    public List<Tag> getTags() {
        return tags;
    }
    
    public SolrDocument getDocument(String id){
        return null;
    }
    
    public SolrDocumentList getResults() {
        return _results;
      }

    public <T> List<T> getBeans(Class<T> type){
        return solrClient == null ?
          new DocumentObjectBinder().getBeans(type,_results):
          solrClient.getBinder().getBeans(type, _results);
      }

    public final class Tag {
        
        Integer _start;
        Integer _end;
        List<String> _ids;
        
        @SuppressWarnings("unchecked")
        Tag(NamedList<Object> res){
            for( int i=0; i<res.size(); i++ ) {
                String n = res.getName( i );
                switch (n) {
                case "startOffset":
                    _start = (Integer)res.getVal(i);
                    break;
                case "endOffset":
                    _end = (Integer)res.getVal(i);
                    break;
                case "ids":
                    _ids = (List<String>)res.getVal(i);
                    break;
                default:
                    break;
                }
            }
        }
        
        public int getStart(){
            return _start == null ? -1 : _start.intValue();
        }
        
        public int getEnd(){
            return _end == null ? -1 : _end.intValue();
        }
        
        public List<String> getIds(){
            return _ids;
        }
        
        public List<SolrDocument> getDocs(){
            return _ids.stream()
                .map(id -> docMap.get(id))
                .filter(doc -> doc != null)
                .collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return "Tag [span:" + _start + ".." + _end + ", docs=" + _ids + "]";
        }
        
    }
    
    
}