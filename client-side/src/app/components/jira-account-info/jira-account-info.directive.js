(function () {
  'use strict';

  angular
    .module('uam')
    .directive('uamJiraAccountInfo', uamJiraAccountInfo);

  /** @ngInject */
  function uamJiraAccountInfo() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/jira-account-info/jira-account-info.html',
      scope: {
        selectedUser: '='
      },
      controller: JiraAccountInfoController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function JiraAccountInfoController(JiraService, $mdDialog, $scope) {
      var vm = this;

      vm.data = JiraService.getData();
      vm.createAccount = createAccount;
      vm.deleteAccount = deleteAccount;

      var destroyUserListener = $scope.$watch(function () {
        return vm.selectedUser && vm.selectedUser.id;
      }, function () {
        var id = vm.selectedUser && vm.selectedUser.id;

        if (id) {
          JiraService.getAccount(id);
        } else {
          JiraService.dataToDefault();
        }
      });

      $scope.$on('$destroy', destroyUserListener);

      function createAccount() {
        var user = angular.copy(vm.selectedUser);

        user.displayName = user.firstName + ' ' + user.lastName;

        delete user.firstName;
        delete user.lastName;
        delete user.selected;

        JiraService.createAccount(user);
      }

      function deleteAccount(ev) {
        var confirm = $mdDialog.confirm({
          title: 'Confirm Account Deletion',
          textContent: 'Are you sure you want to delete this Jira account?',
          ariaLabel: 'Delete Account',
          targetEvent: ev,
          ok: 'Delete',
          cancel: 'Cancel'
        });

        $mdDialog.show(confirm).then(function () {
          JiraService.removeAccount(vm.selectedUser.id);
        });
      }
    }
  }

})();
