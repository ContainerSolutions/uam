(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('AuditController', AuditController);

  /** @ngInject */
  function AuditController(UsersService) {
    var vm = this;

    vm.usersData = angular.copy(UsersService.getData());
    vm.searchQuery = '';
  }
})();
