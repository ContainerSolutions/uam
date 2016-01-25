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
    function NavbarController() {
      var vm = this;

      vm.user = {
        name: 'John Galt'
      };
    }
  }

})();
