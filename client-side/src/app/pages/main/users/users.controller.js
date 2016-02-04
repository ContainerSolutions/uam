(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('UsersController', UsersController);

  /** @ngInject */
  function UsersController(TemplatesService) {
    var vm = this;

    vm.selected = {};
    vm.selectedUserBackupCopy = {};
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
