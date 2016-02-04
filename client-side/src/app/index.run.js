(function() {
  'use strict';

  angular
    .module('uam')
    .run(runBlock);

  /** @ngInject */
  function runBlock($log) {
    $log.debug('runBlock end');
  }

})();
