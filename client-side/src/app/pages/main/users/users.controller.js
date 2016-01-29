(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('UsersController', UsersController);

  /** @ngInject */
  function UsersController(TemplatesService, $mdDialog) {
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

    vm.retireUser = retireUser;

    function retireUser(ev) {
      //var confirm = $mdDialog.confirm()
      //  .title('Confirm User Dismissal')
      //  .textContent('Are you sure you want to delete user ' + vm.selected.firstName + ' ' + vm.selected.lastName + '?')
      //  .ariaLabel('Retire user')
      //  .targetEvent(ev)
      //  .ok('Retire')
      //  .cancel('Cancel');

      var confirm = $mdDialog.confirm({
        title: 'Confirm User Dismissal',
        textContent: 'Are you sure you want to delete user ' + vm.selected.firstName + ' ' + vm.selected.lastName + '?',
        ariaLabel: 'Retire user',
        targetEvent: ev,
        ok: 'Retire',
        cancel: 'Cancel'
      });

      $mdDialog.show(confirm).then(function () {

      });
    }
  }
})();
