(function () {
  'use strict';

  angular
    .module('uam')
    .directive('uamGappsAccountInfo', uamGappsAccountInfo);

  /** @ngInject */
  function uamGappsAccountInfo() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/gapps-account-info/gapps-account-info.html',
      scope: {
        selectedUser: '=',
        userBackupCopy: '='
      },
      controller: GappsAccountInfoController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function GappsAccountInfoController() {

    }
  }

})();
