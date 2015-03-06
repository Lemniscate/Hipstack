angular.module('hipstack.users', ['hipstack.config'])
    .config(function(CONFIG, $stateProvider){
        $stateProvider
            .state('users',{
                url: '/users',
                templateUrl: CONFIG.templatesPrefix + 'users/users.tpl.html'
            })
            .state('users.profile',{
                url: '/profile?infoRequired',
                templateUrl: CONFIG.templatesPrefix + 'users/profile.tpl.html',
                controller: 'ProfileController'
            });
        ;
    })

    .controller('ProfileController', function($rootScope, $scope, $state, $stateParams) {

        if(!$rootScope.security.user){
            return $state.go('/');
        }

        $scope.fn = {};
        $scope.view = {
            infoRequired: $stateParams.infoRequired
        };
        $scope.model = _.pick($rootScope.security.user, ['firstName', 'lastName', 'displayName']);


        $scope.fn.update = function(){
            var u = $rootScope.security.user;
            u.firstName = $scope.model.firstName;
            u.lastName = $scope.model.lastName;
            u.displayName = $scope.model.displayName;
            return u.update().then(function(res){
                $state.go('events');
            });
        };

    })

;
