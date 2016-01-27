(function () {
  'use strict';

  angular
    .module('mantl')
    .factory('UsersService', UsersService);

  /** @ngInject */
  function UsersService() {

    //mock data
    var data = {
      selected: {},
      users: [
        {
          firstName: 'Petia',
          lastName: 'Ivanov',
          id: Math.floor(Math.random() * 10000),
          email: Math.floor(Math.random() * 10000) + '@dio-soft.com',
          events: [
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
          ]
        },
        {
          firstName: 'Vasia',
          lastName: 'Ivanov',
          id: Math.floor(Math.random() * 10000),
          email: Math.floor(Math.random() * 10000) + '@dio-soft.com',
          events: [
            {
              datetime: new Date() - 124,
              application: 'General',
              action: 'Olofhd hisulo',
              executor: 'Vasia',
              requestNumber: Math.floor(Math.random() * 10000)
            },
            {
              datetime: new Date() + 10,
              application: 'GIT',
              action: 'Olofd hdkjsf kfksd2o2',
              executor: 'Misha',
              requestNumber: Math.floor(Math.random() * 10000)
            }
          ]
        },
        {
          firstName: 'Lyosha',
          lastName: 'Ivanov',
          id: Math.floor(Math.random() * 10000),
          email: Math.floor(Math.random() * 10000) + '@dio-soft.com',
          events: [
            {
              datetime: new Date() + 320,
              application: 'Git',
              action: 'fnds  fggsfgs',
              executor: 'Sergey',
              requestNumber: Math.floor(Math.random() * 10000)
            },
            {
              datetime: new Date() + 120,
              application: 'Jira',
              action: 'Ololo2',
              executor: 'Gena',
              requestNumber: Math.floor(Math.random() * 10000)
            }
          ]
        },
        {
          firstName: 'Misha',
          lastName: 'Ivanov',
          id: Math.floor(Math.random() * 10000),
          email: Math.floor(Math.random() * 10000) + '@dio-soft.com',
          events: [
            {
              datetime: new Date(),
              application: 'General',
              action: ' fsf sgdhfg skj',
              executor: 'Alyosha',
              requestNumber: Math.floor(Math.random() * 10000)
            },
            {
              datetime: new Date() + 100,
              application: 'Jira',
              action: ' fjksd fgsdfh jskdgfkj',
              executor: 'Gena',
              requestNumber: Math.floor(Math.random() * 10000)
            }
          ]
        }
      ]
    };

    return {
      getData: getData
    };

    function getData() {
      return data;
    }
  }
})();
