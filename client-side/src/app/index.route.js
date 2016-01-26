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
        templateUrl: 'app/pages/login/login.html',
        controller: 'LoginController',
        controllerAs: 'login'
      })
      .state('main', {
        url: '/main',
        abstract: true,
        templateUrl: 'app/pages/main/main.html',
        controller: 'MainController',
        controllerAs: 'main'
      })
      .state('main.user-list', {
        url: '/user-list',
        templateUrl: 'app/pages/main/user-list/user-list.html',
        controller: 'UserListController',
        controllerAs: 'userList'
      })
      .state('main.audit', {
        url: '/audit',
        templateUrl: 'app/pages/main/audit/audit.html',
        controller: 'AuditController',
        controllerAs: 'audit'
      })
      .state('main.settings', {
        url: '/settings',
        templateUrl: 'app/pages/main/settings/settings.html',
        controller: 'SettingsController',
        controllerAs: 'settings'
      });

    $urlRouterProvider.otherwise('login');
  }

})();
