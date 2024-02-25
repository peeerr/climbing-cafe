package com.peeerr.climbing.dto.post.request;

import lombok.Setter;

@Setter
public class PostSearchCondition {

    private String title;
    private String content;

    public String getTitle() {
        return title != null ? title : null;
    }

    public String getContent() {
        return content != null ? content : null;
    }

}
