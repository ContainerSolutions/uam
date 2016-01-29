(function () {
  'use strict';

  angular
    .module('mantl')
    .factory('UsersService', UsersService);

  /** @ngInject */
  function UsersService($resource, ENV, $log) {

    //mock data
    var usersData = {
      users: []
    };
    var loadersData = {
      fetchingUsers: false,
      fetchingEvents: false,
      addingUser: false,
      deletingUser: false
    };

    return {
      getData: getData,
      getLoadersData: getLoadersData,
      fetchUsers: fetchUsers
    };

    function getData() {
      return usersData;
    }

    function getLoadersData() {
      return loadersData;
    }

    function fetchUsers() {
      var url = 'users';

      loadersData.fetchingUsers = true;
      $resource(ENV.api + url).query({}, onSuccess, onError);

      function onSuccess(users) {
        if (!angular.isArray(users)) {
          onError({data: 'Expected array. Got ' + angular.toJson(users)});
          return
        }

        usersData.users = users;
        loadersData.fetchingUsers = false;
        $log.debug('XHR Success: GET ' + ENV.api + url);
      }

      function onError(error) {
        loadersData.fetchingUsers = false;
        $log.debug('XHR FAIL: GET ' + ENV.api + url + '\n' + error.data);
      }
    }
  }
})();
