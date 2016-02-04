(function () {
  'use strict';

  angular
    .module('uam')
    .directive('uamUserSelect', uamUserSelect);

  /** @ngInject */
  function uamUserSelect() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/user-select/user-select.html',
      scope: {
        selected: '=',
        selectedBackupCopy: '='
      },
      controller: UserSelectController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function UserSelectController(UsersService, $mdDialog) {
      var vm = this;

      vm.searchQuery = '';
      vm.data = UsersService.getData();

      vm.updateSelected = updateSelected;
      vm.addUser = addUser;

      UsersService.fetch();

      function updateSelected(user) {
        UsersService.clearSelected();

        vm.selected = user;
        vm.selectedBackupCopy = angular.copy(vm.selected);
        vm.selected.selected = true;
      }

      function addUser(ev) {
        $mdDialog.show({
          targetEvent: ev,
          templateUrl: 'app/components/new-user-dialog/new-user-dialog.html'
        });
      }
    }
  }

})();
