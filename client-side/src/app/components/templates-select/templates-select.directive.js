(function () {
  'use strict';

  angular
    .module('mantl')
    .directive('mantlTemplatesSelect', mantlTemplatesSelect);

  /** @ngInject */
  function mantlTemplatesSelect() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/templates-select/templates-select.html',
      scope: {
        templates: '=',
        possibleOptionsTitle: '=',
        selectedOptionsTitle: '='
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
      vm.updateSelectedTemplate = updateSelectedTemplate;

      function addOptionToSelected(index) {
        $timeout(function () {
          var option = vm.selectedTemplate.possibleOptions[index];
          vm.selectedTemplate.possibleOptions.splice(index, 1);
          vm.selectedTemplate.selectedOptions.push(option);
        }, 150)
      }

      function removeOptionFromSelected(index) {
        $timeout(function () {
          var option = vm.selectedTemplate.selectedOptions[index];
          vm.selectedTemplate.selectedOptions.splice(index, 1);
          vm.selectedTemplate.possibleOptions.push(option);
        }, 150);
      }

      function updateSelectedTemplate(index) {
        vm.selectedTemplate = vm.templates[index];
      }
    }
  }

})();
