
var services = angular.module('WhatToStudy.services', []);

services.service('RandomCourseService', ['$http', function($http) {
    return {
        loadRandomCourse: function(callback) {
            $http.get('/courses', {
                params: {
                    random: true
                }
            })
            .success(function(course) {
                callback(course);
            });
        }
    }
}]);