(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('UsersController', UsersController);

  /** @ngInject */
  function UsersController(UsersService, TemplatesService) {
    var vm = this;

    vm.searchQuery = '';

    vm.selected = {};
    vm.templates = angular.copy(TemplatesService.getTemplates());
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
  }
})();
