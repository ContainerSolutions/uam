(function () {
  'use strict';

  angular
    .module('uam')
    .factory('JiraService', JiraService);

  /** @ngInject */
  function JiraService($resource, ENV, $log) {
    var url = ENV.jiraApi + 'account';
    var accountData = {};

    dataToDefault();

    return {
      getData: getData,
      dataToDefault: dataToDefault,
      getAccount: getAccount,
      createAccount: createAccount,
      removeAccount: removeAccount
    };

    function dataToDefault() {
      accountData.accountExists = false;
      accountData.loading = false;
      accountData.account = {};
    }

    function getData() {
      return accountData;
    }

    function getAccount(id) {
      var requestUrl;

      if (!id) {
        return;
      }

      requestUrl = url + '/' + id;

      accountData.loading = true;
      $resource(requestUrl).get({}, onSuccess, onError);

      function onSuccess(response) {
        if (!angular.isObject(response)) {
          $log.debug('Expected object. Got:', response);
          onError(response);
          return;
        }

        if (response.errorMessages && response.errorMessages.length) {
          onError(response);
          return;
        }

        accountData.account = response;
        accountData.accountExists = true;
        accountData.loading = false;
        $log.debug('XHR Success: GET ' + requestUrl, response);
      }

      function onError(error) {
        accountData.accountExists = false;
        accountData.account = {};
        accountData.loading = false;
        $log.debug('XHR FAIL: GET ' + requestUrl, error);
      }
    }

    function createAccount(account, successCallback, errorCallback) {
      if (!account) {
        return;
      }

      $log.debug('Adding account: ', account);
      accountData.loading = true;
      $resource(url).save(account, onSuccess, onError);

      function onSuccess(data) {
        $log.debug('XHR Success: POST: ' + url, data);
        accountData.account = account;
        accountData.accountExists = true;
        accountData.loading = false;
        successCallback && successCallback();
      }

      function onError(error) {
        accountData.loading = false;
        errorCallback && errorCallback(error.data);
      }
    }

    function removeAccount(id) {
      var requestUrl;

      if (!id) {
        return;
      }

      requestUrl = url + '/' + id;
      accountData.loading = true;
      $resource(requestUrl).remove({}, onSuccess, onError);

      function onSuccess() {
        accountData.account = {};
        accountData.accountExists = false;
        accountData.loading = false;
        $log.debug('XHR Success: DELETE ' + requestUrl);
      }

      function onError() {
        accountData.loading = false;
      }
    }
  }
})();
