package com.jonas.movienight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Jonas Karlsson on 2019-01-16.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private String startTime;

    private String endTime;

    private String movieName;

}
