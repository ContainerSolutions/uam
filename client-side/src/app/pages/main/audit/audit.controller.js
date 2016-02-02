(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('AuditController', AuditController);

  /** @ngInject */
  function AuditController(EventsService, $scope) {
    var vm = this;

    vm.selectedUser = {};
    vm.selectedUserBackupCopy = {};
    vm.searchQuery = '';

    vm.eventData = EventsService.getData();

    $scope.$watch(function () {
      return vm.selectedUser && vm.selectedUser.id;
    }, function () {
      vm.eventData.events = [];
      EventsService.fetchEvents(vm.selectedUser.id);
    });
  }
})();
