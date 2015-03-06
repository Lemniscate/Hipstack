angular.module('hipstack.events', ['hipstack.config'])
    .config(function(CONFIG, $stateProvider){ //$stateProvider){
        $stateProvider
            .state('events',{
                url: '/events',
                templateUrl: CONFIG.templatesPrefix + 'events/events.tpl.html',
                controller: 'EventsController'
            });
    })

    .controller('EventsController', function($rootScope, $scope, $state) {
        var user = $rootScope.security.user;
        if( !user ){
            return $state.go('/');
        }
        if( !user.displayName ){
            return $state.go('users.profile', {infoRequired: true});
        }
    })

;
