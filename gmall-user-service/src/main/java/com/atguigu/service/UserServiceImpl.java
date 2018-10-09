package com.atguigu.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.movie.MovieService;
import com.atguigu.gmall.user.Movie;

@Service
    public class UserServiceImpl implements MovieService {

        @Override
        public Movie getMovie(String id) {
            return new Movie("1","西游记");
        }
    }
