angular.module('hipstack.register', ['hipstack.config'])
    .config(function(CONFIG, $stateProvider){ //$stateProvider){
        console.log('Register', CONFIG);
        $stateProvider
            .state('register',{
                url: '/register',
                templateUrl: CONFIG.templatesPrefix + 'users/register.tpl.html',
                controller: 'RegisterController'
            });
    })

    .controller('RegisterController', function($scope, $http, $urls, $unwrap) {

        $scope.fn = {};
        $scope.model = {};

        $scope.fn.register = function(model){
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
