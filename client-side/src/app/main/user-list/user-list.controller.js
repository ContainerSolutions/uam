(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('UserListController', UserListController);

  /** @ngInject */
  function UserListController() {
    var vm = this;

    vm.awesomeThings = [];
  }
})();
