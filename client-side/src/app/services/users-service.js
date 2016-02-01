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

    return {
      getData: getData,
      fetchUsers: fetchUsers,
      deleteUser: deleteUser
    };

    function getData() {
      return usersData;
    }

    function fetchUsers() {
      var url = 'users';

      $resource(ENV.api + url).query({}, onSuccess);

      function onSuccess(users) {
        if (!angular.isArray(users)) {
          $log.debug('Expected array. Got ' + angular.toJson(users));
          return;
        }

        usersData.users = users;
        $log.debug('XHR Success: GET ' + ENV.api + url);
      }
    }

    function deleteUser(id) {
      var url = 'users/' + id;

      $resource(ENV.api + url).remove({}, onSuccess);

      function onSuccess(data) {
        fetchUsers();
        $log.debug('XHR Success: DELETE ' + ENV.api + url + '\n' + data);
      }
    }
  }
})();
