package io.redlink.solr.client.solrj.tag;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.SolrParams;

/**
 * {@link SolrParams} as supported by the <a href="https://github.com/OpenSextant/SolrTextTagger">SolrTextTagger</a> 
 * request handler
 * 
 * @author Rupert Westenthaler
 *
 */
public class TagParams extends ModifiableSolrParams {

    private static final String STT_OVERLAPPS = "overlaps";
    private static final String STT_MATCH_TEXT = "matchText";
    private static final String STT_TAGS_LIMIT = "tagsLimit";
    private static final String STT_ROWS = "rows";
    private static final String STT_SKIP_ALT_TOKENS = "skipAltTokens";
    private static final String STT_IGNORE_STOPWORDS = "ignoreStopwords";
    private static final String STT_XML_OFFSET_ADJUST = "xmlOffsetAdjust";
    private static final String STT_HTML_OFFSET_ADJUST = "htmlOffsetAdjust";
    private static final String STT_NON_TAGGABLETAGS = "nonTaggableTags";

    private static final long serialVersionUID = -1796995231499462496L;

    public TagParams() {
        super();
    }

    public enum Overlaps {
        /**
         * Emit all tags
         */
        ALL, 
        /**
         * Don't emit a tag that is completely within another tag (i.e. no subtag).
         */
        NO_SUB, 
        /**
         * Given a cluster of overlapping tags, emit the longest one 
         * (by character length). If there is a tie, pick the right-most. 
         * Remove any tags overlapping with this tag then repeat the algorithm 
         * to potentially find other tags that can be emitted in the cluster.
         */
        LONGEST_DOMINANT_RIGHT
    };

    /**
     * Setter for the the algorithm used to determine which 
     * overlapping tags should be retained, versus being 
     * pruned away. Options are defined in the {@link Overlaps} enumeration
     * @param overlaps the algorithm to be used
     * @return this
     */
    public TagParams setOverlaps(TagParams.Overlaps overlaps) {
        this.set(STT_OVERLAPPS, overlaps.name());
        return this;
    }

    /**
     * Getter for the the algorithm used to determine which 
     * overlapping tags should be retained, versus being 
     * @return the algorithm to be used
     */
    public TagParams.Overlaps getOverlaps() {
        String s = this.get(STT_OVERLAPPS);
        return s == null ? null : Overlaps.valueOf(s);
    }

    /**
     * Setter for state whether to return the matched text 
     * in the tag response. This will trigger the tagger 
     * to fully buffer the input before tagging.
     * @param state the state
     * @return this
     */
    public TagParams setMatchText(boolean state) {
        this.set(STT_MATCH_TEXT, state);
        return this;
    }
    
    /**
     * Getter for the state whether to return the matched text 
     * in the tag response. This will trigger the tagger 
     * to fully buffer the input before tagging.
     * @return the state
     */
    public boolean isMatchText() {
        return this.getBool(STT_MATCH_TEXT);
    }

    /**
     * Setter for the maximum number of tags to return in the response. 
     * Tagging effectively stops after this point. By default this is 1000.
     * @param tagsLimit the limit
     * @return this
     */
    public TagParams setTagsLimit(int tagsLimit) {
        this.set(STT_TAGS_LIMIT, tagsLimit);
        return this;
    }

    /**
     * Getter for the maximum number of tags to return in the response. 
     * Tagging effectively stops after this point. By default this is 1000.
     * @return the limit
     */
    public int getTagsLimit() {
        return this.getInt(STT_TAGS_LIMIT, 1000);
    }

    /**
     * Setter for Solr's standard param to say the maximum number of documents to return, 
     * but defaulting to 10000 for a tag request.
     * @param rows the maximum number of rows to be returned
     * @return this
     */
    public TagParams setRows(int rows) {
        this.set(STT_ROWS, rows);
        return this;
    }
    /**
     * Getter for Solr's standard param to say the maximum number of documents to return, 
     * but defaulting to 10000 for a tag request.
     * @return the maximum number of rows to be returned
     */
    public int getRows() {
        return this.getInt(STT_ROWS, 1000);
    }

    /**
     * Setter for the boolean flag used to suppress errors that can occur if, 
     * for example, you enable synonym expansion at query time in the analyzer, 
     * which you normally shouldn't do. Let this default to false unless you know that 
     * such tokens can't be avoided.
     * @param skipAltTokens if alternative tokens should be ignored or not
     * @return this
     */
    public TagParams setSkipAltTokens(boolean skipAltTokens) {
        this.set(STT_SKIP_ALT_TOKENS, skipAltTokens);
        return this;
    }
    /**
     * Getter for the boolean flag used to suppress errors that can occur if, 
     * for example, you enable synonym expansion at query time in the analyzer, 
     * which you normally shouldn't do. Let this default to false unless you know that 
     * such tokens can't be avoided.
     * @return
     */
    public boolean isSkipAltTokens() {
        return this.getBool(STT_SKIP_ALT_TOKENS);
    }

    /**
     * Setter for the  boolean flag that causes stopwords (or any condition causing 
     * positions to skip like >255 char words) to be ignored as if it wasn't there. 
     * Otherwise, the behavior is to treat them as breaks in tagging on the 
     * presumption your indexed text-analysis configuration doesn't have a 
     * StopWordFilter. By default the indexed analysis chain is checked for the 
     * presence of a StopWordFilter and if found then ignoreStopWords is true 
     * if unspecified. You probably shouldn't have a StopWordFilter configured 
     * and probably won't need to set this param either.
     * @param ignoreStopwords if stop words should be ignored or not
     * @return this
     */
    public TagParams setIgnoreStopwords(boolean ignoreStopwords) {
        this.set(STT_IGNORE_STOPWORDS, ignoreStopwords);
        return this;
    }
    /**
     * Getter for the  boolean flag that causes stopwords (or any condition causing 
     * positions to skip like >255 char words) to be ignored as if it wasn't there. 
     * Otherwise, the behavior is to treat them as breaks in tagging on the 
     * presumption your indexed text-analysis configuration doesn't have a 
     * StopWordFilter. By default the indexed analysis chain is checked for the 
     * presence of a StopWordFilter and if found then ignoreStopWords is true 
     * if unspecified. You probably shouldn't have a StopWordFilter configured 
     * and probably won't need to set this param either.
     * @return the state
     */
    public boolean isIgnoreStopwords() {
        return this.getBool(STT_IGNORE_STOPWORDS);
    }

    /**
     * Setter for the boolean indicating that the input is XML and furthermore 
     * that the offsets of returned tags should be adjusted as necessary to 
     * allow for the client to insert an open and closing element at the positions. 
     * If it isn't possible to do so then the tag will be omitted. You are expected 
     * to configure HTMLStripCharFilter in the schema when using this option. This 
     * will trigger the tagger to fully buffer the input before tagging.
     * @param xmlOffsetAdjust the state
     * @return this
     */
    public TagParams setXmlOffsetAdjust(boolean xmlOffsetAdjust) {
        this.set(STT_XML_OFFSET_ADJUST, xmlOffsetAdjust);
        return this;
    }
    /**
     * Getter for the boolean indicating that the input is XML and furthermore 
     * that the offsets of returned tags should be adjusted as necessary to 
     * allow for the client to insert an open and closing element at the positions. 
     * If it isn't possible to do so then the tag will be omitted. You are expected 
     * to configure HTMLStripCharFilter in the schema when using this option. This 
     * will trigger the tagger to fully buffer the input before tagging.
     * @return the state
     */
    public boolean isXmlOffsetAdjust() {
        return this.getBool(STT_XML_OFFSET_ADJUST);
    }
    
    /**
     * Setter for the boolean flag indicating the the input is HTML and furthermore 
     * that the offsets of returned tags should be adjusted as necessary to 
     * allow for the client to insert an open and closing element at the positions.
     * <p>
     * Similar to xmlOffsetAdjust except for HTML content that may have various issues 
     * that would never work with an XML parser. There needn't be a top level element, 
     * and some tags are known to self-close (e.g. BR). The tagger uses the 
     * Jericho HTML Parser for this feature (dual LGPL & EPL licensed).
     * <p>
     * If disabled this will also disable <code>{@link #isNonTaggableTags()}</code>
     * @param htmlOffsetAdjust the state
     * @return this
     * @see #setNonTaggableTags(boolean)
     */
    public TagParams setHtmlOffsetAdjust(boolean htmlOffsetAdjust) {
        this.set(STT_HTML_OFFSET_ADJUST, htmlOffsetAdjust);
        if(!htmlOffsetAdjust){
            setNonTaggableTags(false);
        }
        return this;
    }
    
    /**
     * Getter for the boolean flag indicating the the input is HTML and furthermore 
     * that the offsets of returned tags should be adjusted as necessary to 
     * allow for the client to insert an open and closing element at the positions.
     * <p>
     * Similar to xmlOffsetAdjust except for HTML content that may have various issues 
     * that would never work with an XML parser. There needn't be a top level element, 
     * and some tags are known to self-close (e.g. BR). The tagger uses the 
     * Jericho HTML Parser for this feature (dual LGPL & EPL licensed).
     * @return the state
     */
    public boolean isHtmlOffsetAdjust() {
        return this.getBool(STT_HTML_OFFSET_ADJUST);
    }
    
    /**
     * Setter for the flag that allows to omits tags that would enclose one of these HTML 
     * elements. Comma delimited, lower-case. For example <code>'a'</code> (anchor) 
     * would be a likely choice so that links the application inserts don't overlap 
     * other links. <p>
     * If enabled this will also enable <code>{@link #isHtmlOffsetAdjust()</code>
     * @param nonTaggableTags
     * @return this
     * @see #setHtmlOffsetAdjust(boolean)
     */
    public TagParams setNonTaggableTags(boolean nonTaggableTags) {
        if(nonTaggableTags){
            setHtmlOffsetAdjust(true);
        }
        this.set(STT_NON_TAGGABLETAGS, nonTaggableTags);
        return this;
    }
    /**
     * Getter for the flag that allows to omits tags that would enclose one of these HTML 
     * elements. Comma delimited, lower-case. For example <code>'a'</code> (anchor) 
     * would be a likely choice so that links the application inserts don't overlap 
     * other links. <i>(only with htmlOffsetAdjust)</i> <p>
     * @return the state
     */
    public boolean isNonTaggableTags() {
        return this.getBool(STT_NON_TAGGABLETAGS);
    }

    /*
     * NOTE: All CommonParams.FL related methods are copied from
     * org.apache.solr.client.solrj.SolrQuery (solrj 6.1.0)
     */
    /**
     * Setter for the list of fields returned for {@link SolrDocument}s
     * referenced by Tags
     * @param fields the fields
     * @return this
     */
    public TagParams setFields(String ... fields) {
        if( fields == null || fields.length == 0 ) {
          this.remove( CommonParams.FL );
          return this;
        }
        StringBuilder sb = new StringBuilder();
        sb.append( fields[0] );
        for( int i=1; i<fields.length; i++ ) {
          sb.append( ',' );
          sb.append( fields[i] );
        }
        this.set(CommonParams.FL, sb.toString() );
        return this;
      }
      /**
       * Adds a field to be returned for {@link SolrDocument}s referenced by Tags
       * @param field the fields name
       * @return this
       */
      public TagParams addField(String field) {
        return addValueToParam(CommonParams.FL, field);
      }

      /**
       * Getter for the fields included for {@link SolrDocument} referenced by
       * Tags
       * @return the field named separated by comma
       */
      public String getFields() {
        String fields = this.get(CommonParams.FL);
        if (fields!=null && fields.equals("score")) {
          fields = "*, score";
        }
        return fields;
      }
      
      private TagParams addValueToParam(String name, String value) {
          String tmp = this.get(name);
          tmp = join(tmp, value, ",");
          this.set(name, tmp);
          return this;
        }
         
      private String join(String a, String b, String sep) {
          StringBuilder sb = new StringBuilder();
          if (a!=null && a.length()>0) {
            sb.append(a);
            sb.append(sep);
          } 
          if (b!=null && b.length()>0) {
            sb.append(b);
          }
          return sb.toString().trim();
        }
}