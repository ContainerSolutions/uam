(function () {
  'use strict';

  angular
    .module('uam')
    .factory('TemplatesService', TemplatesService);

  /** @ngInject */
  function TemplatesService() {

    //mock data
    var templates = [
      {
        id: 'temp1',
        name: 'Template1',
        possibleOptions: [
          {
            id: '1',
            name: '1test1'
          },
          {
            id: '2',
            name: '1test2'
          },
          {
            id: '3',
            name: '1test3'
          },
          {
            id: '4',
            name: '1test4'
          }
        ],
        selectedOptions: [
          {
            id: '5',
            name: '1test5'
          },
          {
            id: '6',
            name: '1test6'
          },
          {
            id: '7',
            name: '1test7'
          },
          {
            id: '8',
            name: '1test8'
          }
        ]
      },
      {
        id: 'temp2',
        name: 'Template2',
        possibleOptions: [
          {
            id: '1',
            name: '2test1'
          },
          {
            id: '2',
            name: '2test2'
          },
          {
            id: '3',
            name: '2test3'
          },
          {
            id: '4',
            name: '2test4'
          }
        ],
        selectedOptions: [
          {
            id: '5',
            name: '2test5'
          },
          {
            id: '6',
            name: '2test6'
          },
          {
            id: '7',
            name: '2test7'
          },
          {
            id: '8',
            name: '2test8'
          }
        ]
      },
      {
        id: 'temp3',
        name: 'Template3',
        possibleOptions: [
          {
            id: '1',
            name: '3test1'
          },
          {
            id: '2',
            name: '3test2'
          },
          {
            id: '3',
            name: '3test3'
          },
          {
            id: '4',
            name: '3test4'
          }
        ],
        selectedOptions: [
          {
            id: '5',
            name: '3test5'
          },
          {
            id: '6',
            name: '3test6'
          },
          {
            id: '7',
            name: '3test7'
          },
          {
            id: '8',
            name: '3test8'
          }
        ]
      }
    ];

    return {
      getTemplates: getTemplates
    };

    function getTemplates() {
      return templates;
    }
  }
})();
