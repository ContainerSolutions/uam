(function () {
  'use strict';

  angular
    .module('uam')
    .directive('uamUserForm', uamUserForm);

  /** @ngInject */
  function uamUserForm() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/user-form/user-form.html',
      scope: {
        user: '=',
        form: '='
      },
      controller: UserFormController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function UserFormController() {
      var vm = this;

      vm.user = vm.user || {};
    }
  }

})();
