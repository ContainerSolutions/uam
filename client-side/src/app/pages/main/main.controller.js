(function () {
  'use strict';

  angular
    .module('mantl')
    .controller('MainController', MainController);

  /** @ngInject */
  function MainController() {
    var vm = this;

    vm.awesomeThings = [];
  }
})();
