(function () {
  'use strict';

  angular
    .module('mantl')
    .factory('EventsService', EventsService);

  /** @ngInject */
  function EventsService(ENV, $log, $resource) {

    var url = ENV.usersApi + 'users/';
    var data = {
      loading: false,
      events: []
    };

    return {
      getData: getData,
      fetchEvents: fetchEvents
    };

    function getData() {
      return data;
    }

    function fetchEvents(userID) {
      var requestUrl;

      if (!userID) {
        return;
      }

      requestUrl = url + userID + '/events';
      data.loading = true;

      $log.debug('Fetching event for user ' + userID);
      $resource(requestUrl).query({}, onSuccess, onError);

      function onSuccess(events) {
        if (!angular.isArray(events)) {
          $log.debug('Expected array of events, got:', events);
          onError();
          return;
        }

        $log.debug('XHR Success: GET ' + requestUrl, events);
        data.events = events;
        data.loading = false;
      }

      function onError() {
        data.events = [];
        data.loading = false;
      }
    }
  }
})();
