(function() {
  'use strict';

  angular
    .module('mantl')
    .config(config);

  /** @ngInject */
  function config($logProvider, ENV, $httpProvider, cfpLoadingBarProvider) {
    $logProvider.debugEnabled(ENV.debugEnabled);

    $httpProvider.interceptors.push('HttpInterceptors');

    $httpProvider.defaults.withCredentials = true;
    $httpProvider.defaults.useXDomain = true;
    $httpProvider.defaults.headers.common.Accept = 'application/json';
    $httpProvider.defaults.headers.common['Content-Type'] = 'application/json';
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

    cfpLoadingBarProvider.includeSpinner = false;
    cfpLoadingBarProvider.latencyThreshold = 200;

  }

})();
