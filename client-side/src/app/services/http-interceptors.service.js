(function () {
  'use strict';

  angular
    .module('mantl')
    .service('HttpInterceptors', HttpInterceptors);

  /** @ngInject */
  function HttpInterceptors($q, $log) {

    return {
      request: function (config) {
        config.timeout = 15000;
        return config;
      },

      response: function (response) {
        return response;
      },

      responseError: function (rejection) {
        $log.debug(rejection);

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
