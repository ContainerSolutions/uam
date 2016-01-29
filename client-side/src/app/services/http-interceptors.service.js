(function () {
  'use strict';

  angular
    .module('mantl')
    .service('HttpInterceptors', HttpInterceptors);

  /** @ngInject */
  function HttpInterceptors($q, $log, $injector) {

    return {
      request: function (config) {
        config.timeout = 15000;
        return config;
      },

      response: function (response) {
        return response;
      },

      responseError: function (rejection) {
        var $mdToast = $injector.get('$mdToast');
        var msg = rejection.errorText || 'Unknown Error';

        $log.debug(rejection);

        $mdToast.show(
          $mdToast.simple()
            .content(msg)
            .position('top right')
            .hideDelay(3000)
        );

        switch (rejection.status) {
          case 401:
            //call something like $injector.get('UserService').logout();
            break;
        }

        return $q.reject(rejection);
      }
    };
  }
})();
