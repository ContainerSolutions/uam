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
        data: '='
      },
      controller: UserSelectController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function UserSelectController() {
      var vm = this;

      vm.searchQuery = '';
      vm.updateSelected = updateSelected;

      function updateSelected(index) {
        var users = vm.data.users,
          selectedUser = users[index];

        for (var i = 0, len = users.length; i < len; i++) {
          users[i].selected = false;
        }

        selectedUser.selected = true;
        vm.data.selected = selectedUser;
      }
    }
  }

})();
