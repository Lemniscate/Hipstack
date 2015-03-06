var   gulp = require('gulp')
    , glob = require('glob')
    , gutil = require('gulp-util')
;

// TODO this is pretty nappo. let's find a better solution
var globalModules = [];

var taskOrig = gulp.task;

var monkeyPatcher = {
    init: function(module) {
        gulp.task = function (name, arg1, arg2, arg3) {
            if (arg1 instanceof Array) {
                var changed = [];
                for (var i in arg1) {
                    changed.push(module + '_' + arg1[i]);
                }
                arg1 = changed;
            }
            taskOrig.call(gulp, module + '_' + name, arg1, arg2, arg3);
        }
    },
    destroy: function(){
        gulp.task = taskOrig;
    }
};

// include all our modules
glob.sync("gulp/*.js").forEach(function(e){
    MODULE = /gulp\/([^\.js]*)/g.exec(e)[1];
    monkeyPatcher.init(MODULE);
    require('./' + e);
    monkeyPatcher.destroy();
    gutil.log( 
          gutil.colors.green('Successfully configured [') 
        + gutil.colors.yellow(MODULE)
        + gutil.colors.green('] at')
        , gutil.colors.yellow(e)
    );
    globalModules.push(MODULE + '_build');
});

gulp.task('default', globalModules, function(){
    console.log('Finished building all the things');
    gutil.beep();
});




