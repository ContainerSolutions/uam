(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('SettingsController', SettingsController);

  /** @ngInject */
  function SettingsController() {
    var vm = this;

    vm.awesomeThings = [];
  }
})();
