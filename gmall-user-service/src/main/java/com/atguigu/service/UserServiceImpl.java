package com.atguigu.service;

import com.atguigu.gmall.movie.MovieService;
import com.atguigu.gmall.user.Movie;
import com.atguigu.gmall.user.User;
import com.atguigu.gmall.user.UserSerivce;

    public class UserServiceImpl implements MovieService {

        @Override
        public Movie getMovie(String id) {
            return new Movie("1","西游记");
        }
    }
