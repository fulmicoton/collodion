gulp = require 'gulp'
browserify = require 'browserify'
source = require 'vinyl-source-stream'
browserSync = require 'browser-sync'
less = require 'gulp-less'

ASSET_SRC_DIR = "./src/main/resources/assets/"

gulp.task 'copy-static', ->
    gulp.src [ASSET_SRC_DIR + '/index.html']
        .pipe gulp.dest('./dist')
    gulp.src [ASSET_SRC_DIR + 'js/ext/*.js']
        .pipe gulp.dest('./dist/js/ext')

gulp.task 'build-less', ->
    gulp.src(ASSET_SRC_DIR + 'css/style.less')
        .pipe(less({}))
        .pipe gulp.dest('./dist/css/')

gulp.task 'build-app', ->
    b = browserify({
        entries: [ASSET_SRC_DIR + 'js/main.coffee'],
        transform: ["coffee-reactify"],
        extensions: ['.coffee']
    })
    return b.bundle()
        .pipe source('app.js')
        .pipe gulp.dest('./dist/js')

gulp.task('build-app-watch', ['build-app'], browserSync.reload)

gulp.task 'build', ['copy-static', 'build-less', 'build-app'], ->
    console.log "Building"

gulp.task 'serve', ['build'], ->
    gulp.watch([ASSET_SRC_DIR + '**/*.cjsx', ASSET_SRC_DIR + '**/*.coffee'], ['build-app-watch'])
    gulp.watch([ASSET_SRC_DIR + 'css/style.less'], ['build-less'])
        .on("change", browserSync.reload)
    gulp.watch([ASSET_SRC_DIR + '/index.html', ASSET_SRC_DIR + 'js/ext/*.js'], ['copy-static'])
        .on("change", browserSync.reload)
    browserSync.init
        server: {baseDir: "./dist"}
