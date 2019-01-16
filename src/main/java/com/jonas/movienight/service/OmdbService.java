package com.jonas.movienight.service;

import com.jonas.movienight.dto.MovieDto;
import com.jonas.movienight.dto.MovieListDto;
import com.jonas.movienight.entity.MovieEntity;
import com.jonas.movienight.repository.MovieRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * Created by Jonas Karlsson on 2019-01-09.
 */
@Service
public class OmdbService {

    private final Logger logger = LoggerFactory.getLogger(OmdbService.class);

    private final ModelMapper modelMapper;
    private final MovieRepository movieEntityRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public OmdbService(ModelMapper modelMapper, MovieRepository movieEntityRepository,
                       RestTemplate restTemplate) {
        this.modelMapper = modelMapper;
        this.movieEntityRepository = movieEntityRepository;
        this.restTemplate = restTemplate;
    }

    public MovieDto getMovieById(String id) {

        MovieEntity movieEntity = movieEntityRepository.getById(id);

        if (movieEntity != null) {
            return modelMapper.map(movieEntity, MovieDto.class);
        } else {
            logger.info("Movie with ID: [{}] was not found in database.", id);
            movieEntity = restTemplate
                    .getForObject("http://www.omdbapi.com/?i=" + id + "&apikey=7f951636", MovieEntity.class);
            if (movieEntity != null && movieEntity.getId() != null) {
                System.out.println(movieEntity.getId());
                logger.info("Found movie with ID: [{}] from omdb API, saving to database.", id);
                movieEntityRepository.save(movieEntity);
                return modelMapper.map(movieEntity, MovieDto.class);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find movie with ID: " + id);
        }
    }

    public MovieListDto getMoviesByName(String search) {

        MovieListDto movieListDto = restTemplate
                .getForObject("http://www.omdbapi.com/?s=" + search + "&apikey=7f951636", MovieListDto.class);

        if (movieListDto == null || movieListDto.getMovieList().size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Could not find any movies with title containing: " + search);
        } else {
            return movieListDto;
        }
    }

}
