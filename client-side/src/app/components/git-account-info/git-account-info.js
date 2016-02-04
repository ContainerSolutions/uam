(function () {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlGitAccountInfo', mantlGitAccountInfo);

  /** @ngInject */
  function mantlGitAccountInfo() {
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
