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
        selected: '='
      },
      controller: UserSelectController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function UserSelectController(UsersService) {
      var vm = this;

      vm.searchQuery = '';
      vm.data = UsersService.getData();
      vm.loadersData = UsersService.getLoadersData();
      vm.updateSelected = updateSelected;

      UsersService.fetchUsers();

      function updateSelected(index) {
        vm.selected = vm.data.users[index];
        vm.selectedIndex = index;
      }
    }
  }

})();
