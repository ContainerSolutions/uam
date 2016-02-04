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
        selectedUser: '='
      },
      controller: GeneralUserInfoController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function GeneralUserInfoController($scope, $mdDialog, $mdToast, UsersService) {
      var vm = this;

      vm.userForm = {};
      vm.infoChanged = false;

      vm.retireUser = retireUser;
      vm.updateUserInfo = updateUserInfo;
      vm.discard = discard;

      init();

      function init() {
        var destroyUserListener = $scope.$watch(function () {
          return vm.selectedUser && vm.selectedUser.id;
        }, function () {
          vm.user = angular.copy(vm.selectedUser);
          vm.userBackupCopy = angular.copy(vm.user);
        });

        var destroyFormListener = $scope.$watch('vm.user', function () {
          vm.infoChanged = !angular.equals(vm.user, vm.userBackupCopy);
        }, true);

        $scope.$on('$destroy', function () {
          destroyUserListener();
          destroyFormListener();
        });
      }

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
          vm.selectedUser = vm.user = vm.userBackupCopy = {};
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

        user = angular.copy(vm.user);
        delete user.selected;

        UsersService.update(vm.userBackupCopy.id, user, onSuccess);

        function onSuccess() {
          angular.extend(vm.userBackupCopy, vm.user);
          vm.infoChanged = false;

          $mdToast.show(
            $mdToast.simple()
              .content('User info has been successfully updated!')
              .position('top right')
              .hideDelay(3000)
          );
        }
      }

      function discard() {
        for (var key in vm.user) {
          if (vm.user.hasOwnProperty(key)) {
            vm.user[key] = vm.userBackupCopy[key];
          }
        }
      }
    }
  }

})();
