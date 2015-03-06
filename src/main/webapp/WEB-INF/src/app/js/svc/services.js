angular.module('hipstack.services', [])
    //.provider('$unwrap', function UnwrapProvider(){
    //    this.$get = function(){
    //        return function(res){
    //            return res.data;
    //        }
    //    }
    //})

    .provider('User', function(){
        this.$get = function($http, $urls, $unwrap){
            function parseResponse(user, data){
                angular.extend(user, data);
                user.createdDate = moment.utc(user.createdDate);
                user.lastModifiedDate = moment.utc(user.lastModifiedDate);
            }

            var User = function(data){
                parseResponse(this, data);
            };

            User.prototype = {
                update: function(){
                    var that = this;
                    var payload = _.pick(that, 'id,password,firstName,lastName,displayName'.split(','));
                    return $http.put( $urls.users.one({id: that.id}), payload )
                        .then($unwrap)
                        .then(function(data){
                            parseResponse(that, data);
                            return that;
                        });
                }
            };

            return User;
        };
    })

    .provider('SecurityService', function securityServiceProvider(){
        this.$get = function($http, $urls, $q, User){
            var svc = {
                  getUser: function () {
                    return $q.when(this.user);
                }
                , setUser: function (u) {
                    this.user = new User(u);
                    return $q.when(this.user);
                }
                , loginOrRegister: function (username, password) {
                    var model = {
                        email: username,
                        password: password
                    };
                    return $http.post($urls.auth.login(), model, {ignoreAuthModule: true})
                        .then(function (res) {
                            user = new User(res.data);
                            switch (res.status) {
                                case 200:
                                    console.log('logged in!');
                                    break;

                                case 201:
                                    console.log('created!');
                                    break;

                                default:
                                    console.warn('Unknown state', res.status, res.data);
                                    break;
                            }
                            return user;
                        });
                }
            };
            return svc;
        };
    })
;
