(function () {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlTransferSelect', mantlTransferSelect);

  /** @ngInject */
  function mantlTransferSelect() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/transfer-select/transfer-select.html',
      scope: {
        possibleOptionsTitle: '=',
        selectedOptionsTitle: '=',
        templates: '=',
        possibleOptions: '=',
        selectedOptions: '='
      },
      controller: mantlTransferSelectController,
      controllerAs: 'vm',
      bindToController: true
    };

    /** @ngInject */
    function mantlTransferSelectController($timeout) {
      var vm = this;

      vm.addOptionToSelected = addOptionToSelected;
      vm.removeOptionFromSelected = removeOptionFromSelected;

      function addOptionToSelected(index) {
        $timeout(function () {
          var option = vm.possibleOptions[index];
          vm.possibleOptions.splice(index, 1);
          vm.selectedOptions.push(option);
        }, 150)
      }

      function removeOptionFromSelected(index) {
        $timeout(function () {
          var option = vm.selectedOptions[index];
          vm.selectedOptions.splice(index, 1);
          vm.possibleOptions.push(option);
        }, 150);
      }
    }
  }

})();
