(function () {
  'use strict';

  angular
    .module('uam')
    .directive('uamADAccountInfo', uamADAccountInfo);

  /** @ngInject */
  function uamADAccountInfo() {
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
