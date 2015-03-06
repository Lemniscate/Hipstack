'use strict';


if( !window.location.hash.length ){
    window.location.hash = '/';
}

var prefix = 'hipstack';
var deps = [
    'ui', 'ui.bootstrap', 'ui.router', 'ngSanitize'
];
// dynamically load any modules that have the same prefix as this module
angular.forEach(angular.modules, function(e){
    if(e.name.length > prefix.length && e.name.substring(0, prefix.length + 1) == prefix + '.'){
        deps.push(e.name);
    }
});

/* App Module */
var app = angular.module(prefix + '.app', deps)
            .constant('CONFIG', {
                templatesPrefix: window.APP_GLOBALS.templatesPrefix
            });

app.config(function (CONFIG, $urlRouterProvider, $stateProvider, $urlsProvider) {
        $urlRouterProvider.otherwise('/404');

        var tpl = CONFIG.templatesPrefix;
        $stateProvider
            .state('/', {
                url: '/',
                templateUrl: tpl + 'main.tpl.html',
                controller: 'MainController'
            })
            .state('notFound', {
                url: '/404',
                templateUrl: tpl + '404.tpl.html'
            })
        ;

        // $urlsProvider.setPrefix('/appContext');
    })
    .run(function($rootScope, $location, $state, $http, $sce, SecurityService) {

        $rootScope.security = SecurityService;
        if( APP_GLOBALS.user ){
            $rootScope.security.setUser(APP_GLOBALS.user);
        }
    })

    .controller('MainController', function($rootScope, $scope, $http, $urls, $state) {
        if( $rootScope.security.user ){
            return $state.go('events');
        }

        $scope.fn = {};
        $scope.model = {};
        $scope.view = {};

        $scope.fn.getStarted = function(){
            return $rootScope.security.loginOrRegister( $scope.model.email, $scope.model.password)
                .then(function(user){
                    $rootScope.security.setUser(user);
                    $state.go(user.displayName ? 'events' : 'users.profile');
                }, function(res){
                    console.warn('Invalid credentials', res);
                    $scope.view.failedLoginMessage = res.data.message;
                });
        };
    })

;
