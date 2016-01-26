(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('UserListController', UserListController);

  /** @ngInject */
  function UserListController() {
    var vm = this;

    //mock data
    vm.searchQuery = '';
    vm.users = [
      {
        name: 'Petia'
      },
      {
        name: 'Vasia'
      },
      {
        name: 'Lyosha'
      },
      {
        name: 'Misha'
      }
    ];
  }
})();
