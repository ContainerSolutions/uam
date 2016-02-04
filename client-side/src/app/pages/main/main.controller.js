(function () {
  'use strict';

  angular
    .module('uam')
    .controller('MainController', MainController);

  /** @ngInject */
  function MainController() {
    var vm = this;

    vm.awesomeThings = [];
  }
})();
