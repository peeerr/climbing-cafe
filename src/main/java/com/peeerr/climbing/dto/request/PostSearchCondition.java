package com.peeerr.climbing.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static PostSearchCondition of(String title, String content) {
        return new PostSearchCondition(title, content);
    }

}
