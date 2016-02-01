(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('NewUserDialogController', NewUserDialogController);

  /** @ngInject */
  function NewUserDialogController($mdDialog, $timeout, UsersService) {
    var vm = this;
    var userModel = {
      firstName: '',
      lastName: '',
      email: ''
    };

    vm.user = angular.copy(userModel);
    vm.error = '';
    vm.formValid = false;

    vm.close = close;
    vm.save = save;

    function close() {
      $mdDialog.hide();

      $timeout(function () {
        vm.user = angular.copy(userModel);
      }, 300);
    }

    function save() {
      if (!vm.form.$valid) {
        angular.forEach(vm.form.$error.required, function (field) {
          field.$setTouched()
        });

        return;
      }

      vm.error = '';
      UsersService.addNew(vm.user, close, onError);

      function onError(msg) {
        vm.error = msg;
      }
    }
  }
})();
