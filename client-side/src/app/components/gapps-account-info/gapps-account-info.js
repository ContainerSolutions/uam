(function () {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlGappsAccountInfo', mantlGappsAccountInfo);

  /** @ngInject */
  function mantlGappsAccountInfo() {
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
