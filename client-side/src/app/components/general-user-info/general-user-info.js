(function () {
  'use strict';

  angular
    .module('uam')
    .directive('uamGeneralUserInfo', uamGeneralUserInfo);

  /** @ngInject */
  function uamGeneralUserInfo() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/general-user-info/general-user-info.html',
      scope: {
        selectedUser: '=',
        userBackupCopy: '='
      },
      controller: GeneralUserInfoController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function GeneralUserInfoController($mdDialog, $mdToast, UsersService) {
      var vm = this;

      vm.userForm = {};

      vm.retireUser = retireUser;
      vm.updateUserInfo = updateUserInfo;
      vm.discard = discard;

      function retireUser(ev) {
        var confirm = $mdDialog.confirm({
          title: 'Confirm User Dismissal',
          textContent: 'Are you sure you want to delete user ' + vm.selectedUser.firstName + ' ' + vm.selectedUser.lastName + '?',
          ariaLabel: 'Retire user',
          targetEvent: ev,
          ok: 'Retire',
          cancel: 'Cancel'
        });

        $mdDialog.show(confirm).then(function () {
          UsersService.remove(vm.userBackupCopy.id, onSuccess);
        });

        function onSuccess() {
          vm.selected = vm.userBackupCopy = {};
        }
      }

      function updateUserInfo() {
        var user;

        if (!vm.userForm.$valid) {
          angular.forEach(vm.userForm.$error.required, function (field) {
            field.$setTouched()
          });

          return;
        }

        user = angular.copy(vm.selectedUser);
        delete user.selected;

        UsersService.update(vm.userBackupCopy.id, user, onSuccess);

        function onSuccess() {
          vm.userBackupCopy = angular.copy(vm.selectedUser);
          $mdToast.show(
            $mdToast.simple()
              .content('User info has been successfully updated!')
              .position('top right')
              .hideDelay(3000)
          );
        }
      }

      function discard() {
        //todo: for each property of selected user set correpsonding property of backup user
      }
    }
  }

})();
