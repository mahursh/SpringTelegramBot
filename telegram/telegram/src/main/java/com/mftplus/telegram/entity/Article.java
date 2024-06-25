package com.mftplus.telegram.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Article {
    private String title;
    private String description;
    private String articleUrl;
    private String imageUrl;
    private String thumbUrl;
}
