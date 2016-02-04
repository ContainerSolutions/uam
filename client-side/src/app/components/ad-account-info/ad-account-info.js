(function () {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlADAccountInfo', mantlADAccountInfo);

  /** @ngInject */
  function mantlADAccountInfo() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/ad-account-info/ad-account-info.html',
      scope: {
        selectedUser: '=',
        userBackupCopy: '='
      },
      controller: ADAccountInfoController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function ADAccountInfoController() {

    }
  }

})();
