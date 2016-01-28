(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('SettingsController', SettingsController);

  /** @ngInject */
  function SettingsController() {
    var vm = this;

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
