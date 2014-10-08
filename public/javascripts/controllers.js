
var controllers = angular.module('WhatToStudy.controllers', ['WhatToStudy.services','ngSanitize']);

controllers.controller('WhatToStudy',
    ['$scope', '$timeout', 'RandomCourseService',
    function($scope, $timeout, randomCourseService) {

    $scope.initialSearch = true;
    $scope.loading = false;
    $scope.results = null;

    $scope.tellMe = function() {
        $scope.loading = true;
        $scope.results = null;

        randomCourseService.loadRandomCourse(function(course) {
            $timeout(function() {
                $scope.loading = false;
                $scope.results = course;
                $scope.initialSearch = false;
            }, Math.random()*5000);
        });
    }

    $scope.good = function() {

    }

}]);