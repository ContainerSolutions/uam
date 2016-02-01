(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('UsersController', UsersController);

  /** @ngInject */
  function UsersController($mdDialog, $mdToast, UsersService, TemplatesService) {
    var vm = this;

    vm.searchQuery = '';

    vm.selected = {};
    vm.selectedBackupCopy = {};
    vm.userForm = {};
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
    vm.save = save;

    function retireUser(ev) {
      var confirm = $mdDialog.confirm({
        title: 'Confirm User Dismissal',
        textContent: 'Are you sure you want to delete user ' + vm.selected.firstName + ' ' + vm.selected.lastName + '?',
        ariaLabel: 'Retire user',
        targetEvent: ev,
        ok: 'Retire',
        cancel: 'Cancel'
      });

      $mdDialog.show(confirm).then(function () {
        UsersService.remove(vm.selected.id);
      });
    }

    function save() {
      switch (vm.selectedAccount) {
        case 0:
          updateUserGeneralInfo();
          break;
      }
    }

    function updateUserGeneralInfo() {
      if (!vm.userForm.$valid) {
        angular.forEach(vm.userForm.$error.required, function (field) {
          field.$setTouched()
        });

        return;
      }

      UsersService.update(vm.selectedBackupCopy.id, vm.selected, onSuccess);

      function onSuccess() {
        vm.selectedBackupCopy = angular.copy(vm.selected);
        $mdToast.show(
          $mdToast.simple()
            .content('User info has been successfully updated!')
            .position('top right')
            .hideDelay(3000)
        );
      }
    }
  }
})();
