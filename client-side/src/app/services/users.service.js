(function () {
  'use strict';

  angular
    .module('mantl')
    .factory('UsersService', UsersService);

  /** @ngInject */
  function UsersService($resource, ENV, $log) {

    var usersData = {
      users: []
    };
    var url = ENV.usersApi + 'users';

    return {
      getData: getData,
      fetch: fetch,
      remove: remove,
      addNew: addNew,
      update: update
    };

    function getData() {
      return usersData;
    }

    function fetch() {
      $resource(url).query({}, onSuccess);

      function onSuccess(users) {
        if (!angular.isArray(users)) {
          $log.debug('Expected array. Got:', users);
          return;
        }

        usersData.users = users;
        $log.debug('XHR Success: GET ' + url, users);
      }
    }

    function remove(id) {
      var requestUrl;

      if (!id) {
        return;
      }

      requestUrl = url + '/' + id;

      $resource(requestUrl).remove({}, onSuccess);

      function onSuccess() {
        fetch();
        $log.debug('XHR Success: DELETE ' + requestUrl);
      }
    }

    function addNew(user, successCallback, errorCallback) {
      if (!user) {
        return;
      }

      $log.debug('Adding user: ' + angular.toString(user));
      $resource(url).save(user, onSuccess, onError);

      function onSuccess(data) {
        $log.debug('XHR Success: POST: ' + url, data);
        fetch();
        successCallback && successCallback();
      }

      function onError(error) {
        errorCallback && errorCallback(error.data);
      }
    }

    function update(id, user, successCallback) {
      var requestUrl;

      if (!id || !user) {
        return;
      }

      requestUrl = url + '/' + id;
      $log.debug('Updating user with id ' + id + ' to: ', user);

      $resource(requestUrl, null, {
        update: {method: 'PUT'}
      }).update(user, onSuccess);

      function onSuccess(data) {
        $log.debug('XHR Success: PUT: ' + requestUrl, data);
        successCallback && successCallback();
      }
    }
  }
})();
