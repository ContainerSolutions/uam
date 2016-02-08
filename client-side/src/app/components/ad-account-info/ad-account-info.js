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
        selectedUser: '='
      },
      controller: ADAccountInfoController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function ADAccountInfoController(ADService, $mdDialog, $scope) {
      var vm = this;

      vm.data = ADService.getData();

      vm.createAccount = createAccount;
      vm.deleteAccount = deleteAccount;

      init();

      function init() {
        var destroyUserListener = $scope.$watch(function () {
          return vm.selectedUser && vm.selectedUser.id;
        }, function () {
          var id = vm.selectedUser && vm.selectedUser.id;

          if (id) {
            ADService.getAccount(id);
          } else {
            ADService.dataToDefault();
          }
        });

        $scope.$on('$destroy', destroyUserListener);
      }

      function createAccount() {
        var user = angular.copy(vm.selectedUser);

        delete user.selected;

        ADService.createAccount(user);
      }

      function deleteAccount(ev) {
        var confirm = $mdDialog.confirm({
          title: 'Confirm Account Deletion',
          textContent: 'Are you sure you want to delete this AD account?',
          ariaLabel: 'Delete Account',
          targetEvent: ev,
          ok: 'Delete',
          cancel: 'Cancel'
        });

        $mdDialog.show(confirm).then(function () {
          ADService.removeAccount(vm.selectedUser.id);
        });
      }
    }
  }

})();
