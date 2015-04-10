/**
 * 
 */

var app = angular.module('vocabularyApp', ['ngRoute', 'ngResource', 'ngTable']);

//This configures the routes and associates each route with a view and a controller
app.config(function ($routeProvider) {
      $routeProvider
        .when('/search',
            {
                controller: 'SearchController',
                templateUrl: 'searchVocabulary.html'
            })
        //Define a route that has a route parameter in it (:customerID)
        .when('/other',
            {
                controller: 'OtherController',
                templateUrl: 'Other.html'
            })
        .otherwise({ redirectTo: '/search' });
});
