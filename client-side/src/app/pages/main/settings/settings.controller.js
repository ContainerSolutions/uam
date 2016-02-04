(function () {
  'use strict';

  angular
    .module('uam')
    .controller('SettingsController', SettingsController);

  /** @ngInject */
  function SettingsController(TemplatesService) {
    var vm = this;

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
