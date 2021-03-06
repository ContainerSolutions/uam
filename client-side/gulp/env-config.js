'use strict';

var gulp = require('gulp');
var gulpNgConfig = require('gulp-ng-config');

exports.setVariables = function (destination) {
  gulp.src('./env-config.json')
    .pipe(gulpNgConfig('uam.ENV', {
      environment: destination
    }))
    .pipe(gulp.dest('./src/app'));
};
