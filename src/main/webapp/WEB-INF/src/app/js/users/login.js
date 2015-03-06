angular.module('hipstack.login', ['hipstack.config'])
    .config(function(CONFIG, $stateProvider){ //$stateProvider){
        $stateProvider
            .state('login',{
                url: '/login',
                templateUrl: CONFIG.templatesPrefix + 'users/register.tpl.html',
                controller: 'LoginController'
            });
    })

    .controller('LoginController', function($scope, $http, $urls, $unwrap) {

        $scope.fn = {};
        $scope.model = {};

        $scope.fn.login = function(model){
            return $http.post( $urls.users.register(), model)
                .then($unwrap)
                .then(function(res){
                    console.log('success', res);
                }, function(err){
                    console.log('fail', err);
                });
        };
    })

;
