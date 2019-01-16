package com.jonas.movienight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Jonas Karlsson on 2019-01-09.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {

    private String id;

    private String title;

    private String runtime;

}
