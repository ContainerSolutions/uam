(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('UsersController', UsersController);

  /** @ngInject */
  function UsersController(UsersService) {
    var vm = this;

    vm.searchQuery = '';

    //mock data
    vm.usersData = angular.copy(UsersService.getData());
    vm.selectedAccount = 0;
    vm.accounts = [
      {
        name: 'General'
      },
      {
        name: 'GApps'
      },
      {
        name: 'Git'
      },
      {
        name: 'Jira'
      },
      {
        name: 'AD'
      }
    ];
    vm.templates = [
      {
        name: 'Template1'
      },
      {
        name: 'Template2'
      },
      {
        name: 'Template3'
      },
      {
        name: 'Template4'
      }
    ];
    vm.possibleOptions = [
      {
        name: 'test1'
      },
      {
        name: 'test2'
      },
      {
        name: 'test3'
      },
      {
        name: 'test4'
      }
    ];
    vm.selectedOptions = [
      {
        name: 'test5'
      },
      {
        name: 'test6'
      },
      {
        name: 'test7'
      },
      {
        name: 'test8'
      }
    ];
  }
})();
