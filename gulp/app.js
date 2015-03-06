/* MODULE - the name of this module */

var gulp = require('gulp'),
    path = require('path'),
    debug = require('gulp-debug'),
    gutil = require('gulp-util'),
    prefix = require('gulp-autoprefixer'),
    minifyCss = require('gulp-minify-css'),
    usemin = require('gulp-usemin'),
    uglify = require('gulp-uglify'),
    compass = require('gulp-compass'),
    minifyHtml = require('gulp-minify-html'),
    livereload = require('gulp-livereload'),
    imagemin = require('gulp-imagemin'),
    ngAnnotate = require('gulp-ng-annotate'),
    jshint = require('gulp-jshint'),
    rev = require('gulp-rev'),
    connect = require('gulp-connect'),
    proxy = require('proxy-middleware'),
    es = require('event-stream'),
    flatten = require('gulp-flatten'),
    clean = require('gulp-clean'),
    replace = require('gulp-replace'),
    browserify = require('gulp-browserify'),
    debug = require('gulp-debug'),
    flatten = require('gulp-flatten'),
    rename = require('gulp-rename'),
    streamqueue = require('streamqueue')
    ;



var bower       = require('gulp-bower');
var concat      = require('gulp-concat');
var less        = require('gulp-less');
var templateCache   = require('gulp-angular-templatecache');
var filesize 	  = require('gulp-filesize');

// Every module should define this for the "build-everything" task
gulp.task( 'build', ['clean', 'copy', 'usemin'], function(){
    gutil.log( gutil.colors.blue('Successfully built ' + MODULE + ' module') );
    gutil.log(
        gutil.colors.blue('Successfully built ')
        + gutil.colors.yellow(MODULE)
        + gutil.colors.blue(' module')
    );
});

function prefixDir(prefix){
    return rename(function(path){
        path.dirname = prefix + path.dirname;
    });
}

var yeoman = {
    tmp: '.tmp/',
    app: 'src/main/webapp/WEB-INF/',
    assets: 'src/main/webapp/WEB-INF/src/' + MODULE,
    dist: 'src/main/webapp/WEB-INF/dist/' + MODULE,
    distClean: [
        'src/main/webapp/WEB-INF/dist/' + MODULE,
        'src/main/webapp/WEB-INF/views/' + MODULE
    ],
    distBase: 'src/main/webapp/WEB-INF',
    test: 'src/test/js/spec/',
    scss: 'src/main/webapp/WEB-INF/src/' + MODULE + '/scss/'
};


gulp.task('clean', function(){
    return gulp.src(yeoman.distClean, {read: false}).
        pipe(clean({force: true}));
});

gulp.task('clean:tmp', function(){
    return gulp.src(yeoman.tmp, {read: false}).
        pipe(clean({force: true}));
});

gulp.task('copy', ['clean'], function(){
    return es.merge(
        gulp.src(yeoman.assets + 'i18n/**').
            pipe(gulp.dest(yeoman.dist + 'i18n/')),
        gulp.src(yeoman.assets + '**/*.{woff,svg,ttf,eot}').
            pipe(flatten()).
            pipe(gulp.dest(yeoman.dist + 'static/fonts/')));
});

gulp.task('images', function(){
    return gulp.src(yeoman.assets + 'images/**').
        pipe(imagemin({optimizationLevel: 5})).
        pipe(gulp.dest(yeoman.dist + 'static/images'));
});

gulp.task('compass', function() {
    return gulp.src(yeoman.scss + '{,*/}*.scss')
        .pipe(gulp.dest('.tmp/scss'))
        //.pipe(debug())
        .pipe(compass({
            project: path.join(__dirname, '..'),
            sass: '.tmp/scss',
            css: '.tmp/css',
            generated_images: '.tmp/images/generated',
            image: yeoman.assets + 'images',
            javascript: yeoman.assets + 'js',
            font: yeoman.assets + 'fonts',
            import_path: 'src/main/webapp/WEB-INF/bower_components',
            relative: false
        }))
        //.pipe(debug())
        .pipe(gulp.dest(yeoman.tmp + 'styles'))
    ;
});

// copy all css to the temp folder
gulp.task('css', function(){
    //return gulp.src(yeoman.assets + '/css/**/*.css')
    //    .pipe(debug())
    //    .pipe(gulp.dest(yeoman.tmp + '/css'));
});

// copy all css in the temp folder to the dist folder
// (note this includes compiled scss from compass)
gulp.task('styles', ['compass', 'css'], function() {
    return gulp.src(yeoman.tmp + '/css/**').
            pipe(gulp.dest(yeoman.dist + '/css/'));
});

gulp.task('html2js', function(){
    return gulp.src(yeoman.assets + '/**/*.tpl.html')
        //.pipe(debug())
        .pipe(minifyHtml({
            empty: true,
            spare: true,
            quotes: true
        }))
        .pipe(templateCache({
            root: '/tpl/',
            module: MODULE
        }))
        .pipe(concat('templates.js'))
        .pipe(gulp.dest( yeoman.tmp + '/templates'))
        .pipe(filesize())
        .pipe(rename('templates.min.js'))
        .pipe(uglify())
        .pipe(gulp.dest(yeoman.dist + '/tpl'))
        .pipe(filesize());
});


gulp.task('usemin', ['html2js', 'images', 'styles'], function(){
    //return gulp.src(yeoman.app + 'WEB-INF/views/{,*/}**.html').
    return gulp.src(yeoman.assets + '/{,*/}/**.view.html')
        
        // strip out the "views" dirname, otherwise we get module/views/js and module/views/views
        .pipe(rename(function(path){
            path.dirname = path.dirname == 'views' ? '' : path.dirname;
        }))
        .pipe(usemin({
            //path: 'src/main/webapp',
            path: yeoman.assets,
            css: [
                prefix.apply(),
                replace(/[0-9a-zA-Z\-_\s\.\/]*\/([a-zA-Z\-_\.0-9]*\.(woff|eot|ttf|svg))/g, '/fonts/$1'),
                //minifyCss(),
                'concat',
                rev()
                , prefixDir("static/")
            ],
            html: [
                prefixDir('dist/' + MODULE + '/views/')
                //, minifyHtml({empty: true, conditionals:true})
            ],
            js: [
                  ngAnnotate()
                , uglify()
                , 'concat'
                , rev()
            ]
        }))
        .pipe(gulp.dest(yeoman.distBase))
        ;
});