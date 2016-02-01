(function () {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlUserForm', mantlUserForm);

  /** @ngInject */
  function mantlUserForm() {
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
