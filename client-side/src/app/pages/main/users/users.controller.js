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
    vm.selectedUserBackupCopy = {};
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
        UsersService.remove(vm.selected.id, onSuccess);
      });

      function onSuccess() {
        vm.selected = vm.selectedUserBackupCopy = {};
      }
    }

    function save() {
      switch (vm.selectedAccount) {
        case 0:
          updateUserGeneralInfo();
          break;
      }
    }

    function updateUserGeneralInfo() {
      var user;

      if (!vm.userForm.$valid) {
        angular.forEach(vm.userForm.$error.required, function (field) {
          field.$setTouched()
        });

        return;
      }

      user = angular.copy(vm.selected);
      delete user.selected;

      UsersService.update(vm.selectedUserBackupCopy.id, user, onSuccess);

      function onSuccess() {
        vm.selectedUserBackupCopy = angular.copy(vm.selected);
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
