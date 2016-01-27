(function() {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlNavbar', mantlNavbar);

  /** @ngInject */
  function mantlNavbar() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/navbar/navbar.html',
      scope: {},
      controller: NavbarController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function NavbarController($rootScope) {
      var vm = this;

      vm.user = {
        name: 'John Galt'
      };

      vm.links = [
        {
          state: 'main.users',
          text: 'User List'
        },
        {
          state: 'main.audit',
          text: 'Audit'
        },
        {
          state: 'main.settings',
          text: 'Application Settings'
        }
      ];

      /*eslint-disable */
      $rootScope.$on('$stateChangeSuccess', function (event, toState) {
        /*eslint-enable */
        vm.activeState = toState.name;
      });
    }
  }

})();
