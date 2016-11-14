# SolrJ Text Tagger

This provides extensions to SolrJ for using [SolrTextTagger](https://github.com/OpenSextant/SolrTextTagger).

Intended Usage

    String content; //The content to tag
    
    //NOTE: TaggerParams can be reused for multiple requests
    TagParams params = new TagParams();
    params.setOverlaps(Overlaps.LONGEST_DOMINANT_RIGHT);
    params.setFields("id", "title", "cat");
    
    TagRequest request = new TagRequest(params, 
        new StringStream(content, "text/plain"));
    //request.setPath("/my-custom-tagger-path"); //default is "/tag"
    try {
        TagResponse response = request.process(client);
        for(Tag tag : response.getTags()){
            int start = tag.getStart();
            int end = tag.getEnd();
        String mention = content.subString(start, end);
            for(SolrDocument doc : tag.getDocs()){
                String title = (String)doc.getFirstValue("title");
                String cat = (String)doc.getFirstValue("cat");
            }
        }
    } catch (SolrServerException e) {
        //TODO: Error Handling
    }

