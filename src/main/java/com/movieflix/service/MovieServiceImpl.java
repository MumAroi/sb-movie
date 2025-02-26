package com.movieflix.service;

import com.movieflix.dto.MovieDto;
import com.movieflix.entities.Movie;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.movieflix.repositories.MovieRepository;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {
        String uploadFileName = fileService.uploadFile(path, file);
        movieDto.setPoster(uploadFileName);
        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster());

        Movie saveMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/api/v1/files/" + uploadFileName;

        MovieDto response = new MovieDto(
                saveMovie.getId(),
                saveMovie.getTitle(),
                saveMovie.getDirector(),
                saveMovie.getStudio(),
                saveMovie.getMovieCast(),
                saveMovie.getReleaseYear(),
                saveMovie.getPoster(),
                posterUrl);

        return response;
    }

    @Override
    public MovieDto getMovie(Integer id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie no found!"));

        String posterUrl = baseUrl + "/api/v1/files/" + movie.getPoster();

        MovieDto response = new MovieDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl);

        return response;
    }

    @Override
    public List<MovieDto> getAllMovies() {
        List<Movie> movies = movieRepository.findAll();

        List<MovieDto> movieDtos = new ArrayList<>();

        for (Movie movie : movies) {
            String posterUrl = baseUrl + "/api/v1/files/" + movie.getPoster();

            MovieDto response = new MovieDto(
                    movie.getId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl);

            movieDtos.add(response);

        }

        return movieDtos;
    }

    @Override
    public MovieDto updateMovie(Integer id, MovieDto movieDto, MultipartFile file) throws IOException {
        Movie mv = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found!"));

        String fileName = mv.getPoster();
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }

        movieDto.setPoster(fileName);

        Movie movie = new Movie(
                mv.getId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudio(),
                movieDto.getMovieCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster());

        Movie updatedMovie = movieRepository.save(movie);

        String posterUrl = baseUrl + "/api/v1/files/" + fileName;

        MovieDto response = new MovieDto(
                updatedMovie.getId(),
                updatedMovie.getTitle(),
                updatedMovie.getDirector(),
                updatedMovie.getStudio(),
                updatedMovie.getMovieCast(),
                updatedMovie.getReleaseYear(),
                updatedMovie.getPoster(),
                posterUrl);

        return response;
    }

    @Override
    public String deleteMovie(Integer id) throws IOException {
        Movie mv = movieRepository.findById(id).orElseThrow(() -> new RuntimeException("Movie not found!"));
        Integer movieId = mv.getId();

        Files.deleteIfExists(Paths.get(path + File.separator + mv.getPoster()));

        movieRepository.delete(mv);

        return "Movie deleted with id: " + movieId;
    }
}