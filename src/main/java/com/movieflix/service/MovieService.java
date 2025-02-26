package com.movieflix.service;

import org.springframework.web.multipart.MultipartFile;
import com.movieflix.dto.MovieDto;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovie(Integer id);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(Integer id, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie(Integer id) throws IOException;
}