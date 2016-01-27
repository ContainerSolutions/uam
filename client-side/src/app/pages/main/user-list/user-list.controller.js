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
