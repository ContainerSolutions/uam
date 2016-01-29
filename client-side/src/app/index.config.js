(function() {
  'use strict';

  angular
    .module('mantl')
    .config(config);

  /** @ngInject */
  function config($logProvider, ENV) {
    $logProvider.debugEnabled(ENV.debugEnabled);
  }

})();
