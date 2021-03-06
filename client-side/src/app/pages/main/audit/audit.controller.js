(function () {
  'use strict';

  angular
    .module('uam')
    .controller('AuditController', AuditController);

  /** @ngInject */
  function AuditController(EventsService, $scope) {
    var vm = this;

    vm.selectedUser = {};
    vm.selectedUserBackupCopy = {};
    vm.searchQuery = '';
    vm.eventData = EventsService.getData();

    init();

    function init() {
      var destroyUserListener = $scope.$watch(function () {
        return vm.selectedUser && vm.selectedUser.id;
      }, function () {
        vm.eventData.events = [];
        EventsService.fetchEvents(vm.selectedUser.id);
      });

      $scope.$on('$destroy', destroyUserListener);
    }
  }
})();
