angular.module('hipstack.urls', [])
.provider('$unwrap', function UnwrapProvider(){
    this.$get = function(){
        return function(res){
            return res.data;
        }
    }
})
.provider('$urls', function UrlsProvider(){
    var prefix = '';

    this.setPrefix = function(p){
        prefix = p;
    };

    this.$get = ['CONFIG', function urlProviderFactory(){
        var results = {
            users:{
                'one': '/api/users/:id'
            },
            auth: {
                'login': '/api/auth/login'
            }
        };

        function curry(tpl){
            return function(data){
                var result = tpl;
                _.each(data, function(value, key){
                    result = result.replace(':' + key, value);
                });
                return result;
            }
        }

        function map(obj){
            _.each(obj, function(e){
                _.each(e, function(url, key){
                    switch(typeof(url)){
                        case 'string':
                            return e[key] = curry(prefix + url);

                        case 'object':
                            return map(e);

                        default:
                            throw new Error('Could not map type ' + key + ' of type ' + typeof(url))
                    }

                })
            });
        }
        map(results);

        return results;
    }];
});
