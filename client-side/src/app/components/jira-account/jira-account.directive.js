(function () {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlJiraAccount', mantJiraAccount);

  /** @ngInject */
  function mantJiraAccount() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/jira-account/jira-account.html',
      scope: {
        selectedUser: '='
      },
      controller: JiraAccountController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function JiraAccountController(JiraService, $mdDialog, $scope) {
      var vm = this;

      vm.data = JiraService.getData();
      vm.createAccount = createAccount;
      vm.deleteAccount = deleteAccount;

      $scope.$watch(function () {
        return vm.selectedUser && vm.selectedUser.id;
      }, function () {
        var id = vm.selectedUser && vm.selectedUser.id;

        if (id) {
          JiraService.getAccount(id);
        }
      });

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
