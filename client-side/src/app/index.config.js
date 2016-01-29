(function() {
  'use strict';

  angular
    .module('mantl')
    .config(config);

  /** @ngInject */
  function config($logProvider, ENV, $httpProvider) {
    $logProvider.debugEnabled(ENV.debugEnabled);

    $httpProvider.interceptors.push('HttpInterceptors');

    $httpProvider.defaults.withCredentials = true;
    $httpProvider.defaults.useXDomain = true;
    $httpProvider.defaults.headers.common.Accept = 'application/json';
    $httpProvider.defaults.headers.common['Content-Type'] = 'application/json';
    $httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

  }

})();
