(function() {
  'use strict';

  angular
    .module('mantl')
    .config(config);

  /** @ngInject */
  function config($logProvider) {

    //enable log based on ENV variable
    $logProvider.debugEnabled(true);
  }

})();
