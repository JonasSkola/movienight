package com.jonas.movienight.controller;

import com.jonas.movienight.dto.MovieDto;
import com.jonas.movienight.dto.MovieListDto;
import com.jonas.movienight.service.OmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Jonas Karlsson on 2019-01-09.
 */
@RestController
@RequestMapping("/omdb")
public class OmdbController {

    private final OmdbService omdbService;

    @Autowired
    public OmdbController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    @GetMapping("/{id}")
    public MovieDto getMovieById(@PathVariable String id){
        return omdbService.getMovieById(id);
    }

    @GetMapping
    public MovieListDto getMoviesBySearch(@RequestParam String s){
        return omdbService.getMoviesByName(s);
    }
}