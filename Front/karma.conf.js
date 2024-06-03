module.exports = function (config) {
  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma'),
      require('karma-jasmine-html-reporter'),
      require('@angular-devkit/build-angular/plugins/karma'),
      require('karma-sonarqube-reporter'),
    ],
    client: {
      jasmine: {},
      clearContext: false
    },
    jasmineHtmlReporter: {
      suppressAll: true
    },
    coverageReporter: {
      type: 'lcovonly',
      dir: 'coverage/',
      subdir: '.',
      file: 'lcov.info',
      exclude: ['src/app/model/**', 'src/app/open-api/**', 'src/app/environments/**', '**/*.html', '**/*.scss', 'src/app/layout/**', 'src/app/assets/**','src/assets/**'
      ,'src/environments/**','src/open-api/**','src/**.ts','src/**.json']
    },
    sonarqubeReporter: {
      basePath: 'src/app', // test files folder
      filePattern: '**/*spec.ts', // test files glob pattern
      encoding: 'utf-8', // test files encoding
      outputFolder: 'reports', // report destination
      legacyMode: false, // report for Sonarqube < 6.2 (disabled)
      reportName: function (metadata) {
        return 'sonarqube_report.xml';
      },
    },
    reporters: ['progress', 'coverage', 'kjhtml','sonarqube'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: ['Chrome'],
    singleRun: false,
    restartOnFileChange: true
  });
};
