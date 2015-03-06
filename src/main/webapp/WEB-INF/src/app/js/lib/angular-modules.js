// taken from http://www.hiddentao.com/archives/2013/11/04/an-improved-angular-module-split-your-modules-into-multiple-files/
(function(angular) {
    var origMethod = angular.module;
    angular.modules = {};
    /**
     * Register/fetch a module.
     *
     * @param name {string} module name.
     * @param reqs {array} list of modules this module depends upon.
     * @param configFn {function} config function to run when module loads (only applied for the first call to create this module).
     * @returns {*} the created/existing module.
     */
    angular.module = function(name, reqs, configFn) {
        reqs = reqs || [];
        var module = null;

        if (angular.modules[name]) {
            module = origMethod(name);
            module.requires.push.apply(module.requires, reqs);
        } else {
            module = origMethod(name, reqs, configFn);
            angular.modules[name] = module;
        }

        return module;
    };
})(angular);
