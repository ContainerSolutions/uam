(function () {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlUserSelect', mantlUserSelect);

  /** @ngInject */
  function mantlUserSelect() {
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

      function updateSelected(index) {
        vm.selected = vm.data.users[index];
        vm.selectedBackupCopy = angular.copy(vm.selected);
        vm.selectedIndex = index;
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
