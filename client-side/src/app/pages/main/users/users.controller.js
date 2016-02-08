(function () {
  'use strict';

  angular
    .module('uam')
    .controller('UsersController', UsersController);

  /** @ngInject */
  function UsersController(TemplatesService) {
    var vm = this;

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
