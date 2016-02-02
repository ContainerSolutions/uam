(function () {
  'use strict';

  angular
    .module('mantl')
    .factory('EventsService', EventsService);

  /** @ngInject */
  function EventsService(ENV, $log, $resource) {

    var url = ENV.api + 'events/';
    var data = {
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

      requestUrl = url + userID;

      $log.debug('Fetching event for user ' + userID);
      $resource(requestUrl).query({}, onSuccess, onError);

      function onSuccess(events) {
        $log.debug('XHR Success: GET ' + requestUrl, events);
        data.events = events;
      }

      //temporarily return mock data while back end is not ready
      function onError() {
        data.events = [
          {
            datetime: new Date() - 45,
            application: 'AD',
            action: 'Ololo',
            executor: 'Alyosha',
            requestNumber: Math.floor(Math.random() * 10000)
          },
          {
            datetime: new Date() + 10,
            application: 'Jira',
            action: 'Ololo2',
            executor: 'Gena',
            requestNumber: Math.floor(Math.random() * 10000)
          }
        ];
      }
    }
  }
})();
