(function () {
  'use strict';

  angular
    .module('uam')
    .directive('uamAdAccountInfo', uamAdAccountInfo);

  /** @ngInject */
  function uamAdAccountInfo() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/ad-account-info/ad-account-info.html',
      scope: {
        selectedUser: '='
      },
      controller: AdAccountInfoController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function AdAccountInfoController(AdService, $mdDialog, $scope) {
      var vm = this;

      vm.data = AdService.getData();

      vm.createAccount = createAccount;
      vm.deleteAccount = deleteAccount;

      init();

      function init() {
        var destroyUserListener = $scope.$watch(function () {
          return vm.selectedUser && vm.selectedUser.id;
        }, function () {
          var id = vm.selectedUser && vm.selectedUser.id;

          if (id) {
            AdService.getAccount(id);
          } else {
            AdService.dataToDefault();
          }
        });

        $scope.$on('$destroy', destroyUserListener);
      }

      function createAccount() {
        var user = angular.copy(vm.selectedUser);

        delete user.selected;

        AdService.createAccount(user);
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
          AdService.removeAccount(vm.selectedUser.id);
        });
      }
    }
  }

})();
