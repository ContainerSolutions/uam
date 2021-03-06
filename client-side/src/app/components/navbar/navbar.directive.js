(function() {
  'use strict';

  angular
    .module('uam')
    .directive('uamNavbar', uamNavbar);

  /** @ngInject */
  function uamNavbar() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/navbar/navbar.html',
      scope: {},
      controller: NavbarController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function NavbarController($rootScope, $state) {
      var vm = this;

      vm.user = {
        name: 'John Galt'
      };
      vm.activeState = $state.current.name;

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
