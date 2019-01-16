package com.jonas.movienight.repository;

import com.jonas.movienight.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Jonas Karlsson on 2019-01-09.
 */
@Repository
public interface MovieRepository extends JpaRepository<MovieEntity, String> {

    MovieEntity getById(String id);
}
