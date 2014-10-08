
var whatToStudy = angular.module('WhatToStudy', ['ngRoute', 'WhatToStudy.controllers', 'WhatToStudy.services']);

whatToStudy.config(['$routeProvider', function($routeProvider) {

    $routeProvider.when('/', {
        templateUrl: 'assets/html/what_to_study.html',
        controller: 'WhatToStudy'
    }).otherwise({
        redirectTo: '/'
    });
}]);