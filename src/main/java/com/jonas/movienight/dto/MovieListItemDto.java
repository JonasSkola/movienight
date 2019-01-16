package com.jonas.movienight.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Jonas Karlsson on 2019-01-09.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieListItemDto {

    @JsonProperty("imdbID")
    private String id;

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Year")
    private String year;

    @JsonProperty("Type")
    private String type;

}