(function() {
  'use strict';

  angular
    .module('mantl')
    .config(routerConfig);

  /** @ngInject */
  function routerConfig($stateProvider, $urlRouterProvider) {
    $stateProvider
      .state('login', {
        url: '/login',
        templateUrl: 'app/login/login.html',
        controller: 'LoginController',
        controllerAs: 'login'
      })
      .state('main', {
        url: '/main',
        abstract: true,
        templateUrl: 'app/main/main.html',
        controller: 'MainController',
        controllerAs: 'main'
      })
      .state('main.user-list', {
        url: '/user-list',
        templateUrl: 'app/main/user-list/user-list.html',
        controller: 'UserListController',
        controllerAs: 'userList'
      })
      .state('main.audit', {
        url: '/audit',
        templateUrl: 'app/main/audit/audit.html',
        controller: 'AuditController',
        controllerAs: 'audit'
      })
      .state('main.settings', {
        url: '/settings',
        templateUrl: 'app/main/settings/settings.html',
        controller: 'SettingsController',
        controllerAs: 'settings'
      });

    $urlRouterProvider.otherwise('login');
  }

})();
