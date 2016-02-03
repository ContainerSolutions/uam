(function () {
  'use strict';

  angular
    .module('mantl')
    .factory('UsersService', UsersService);

  /** @ngInject */
  function UsersService($resource, ENV, $log) {

    var usersData = {
      fetching: false,
      users: []
    };
    var url = ENV.usersApi + 'users';

    return {
      getData: getData,
      fetch: fetch,
      remove: remove,
      addNew: addNew,
      update: update,
      clearSelected: clearSelected
    };

    function getData() {
      clearSelected();
      return usersData;
    }

    function fetch() {
      usersData.fetching = true;
      $resource(url).query({}, onSuccess, onError);

      function onSuccess(users) {
        if (!angular.isArray(users)) {
          $log.debug('Expected array. Got:', users);
          onError();
          return;
        }

        usersData.users = users;
        usersData.fetching = false;
        $log.debug('XHR Success: GET ' + url, users);
      }

      function onError() {
        usersData.fetching = false;
      }
    }

    function remove(id, successCallback) {
      var requestUrl;

      if (!id) {
        return;
      }

      requestUrl = url + '/' + id;

      $resource(requestUrl).remove({}, onSuccess);

      function onSuccess() {
        fetch();
        successCallback && successCallback();
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

    function clearSelected() {
      angular.forEach(usersData.users, function (user) {
        user.selected = false;
      });
    }
  }
})();
