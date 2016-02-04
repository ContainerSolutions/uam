(function () {
  'use strict';

  angular
    .module('uam')
    .directive('uamGitAccountInfo', uamGitAccountInfo);

  /** @ngInject */
  function uamGitAccountInfo() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/git-account-info/git-account-info.html',
      scope: {
        selectedUser: '=',
        userBackupCopy: '='
      },
      controller: GitAccountInfoController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function GitAccountInfoController() {

    }
  }

})();
