(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('AuditController', AuditController);

  /** @ngInject */
  function AuditController() {
    var vm = this;

    vm.selected = {};
    vm.selectedBackupCopy = {};
    vm.searchQuery = '';
  }
})();
