var api = {
  user: {
    login: function(username, password, rememberMe) {
      return axios.post('/api/users/login', { username: username, password: password, rememberMe: rememberMe });
    },
    deductMoney: function(pw, amount) {
      return axios.put('/api/users/money/deduct', null, { params: { pw: pw, amount: amount } });
    }
  },
  address: {
    list: function() {
      return axios.get('/api/addresses');
    },
    getById: function(id) {
      return axios.get('/api/addresses/' + id);
    }
  },
  item: {
    getById: function(id) {
      return axios.get('/api/items/' + id);
    },
    page: function(params) {
      return axios.get('/api/items/page', { params: params });
    },
    deductStock: function(itemId, num) {
      return axios.put('/api/items/stock/deduct', null, { params: { itemId: itemId, num: num } });
    }
  },
  search: {
    list: function(params) {
      return axios.get('/api/search/list', { params: params });
    },
    filters: function(params) {
      return axios.post('/api/search/filters', params);
    }
  },
  cart: {
    add: function(itemId, name, spec, price, image, num) {
      return axios.post('/api/carts', {
        itemId: itemId, name: name, spec: spec, price: price, image: image, num: num || 1
      });
    },
    list: function() {
      return axios.get('/api/carts');
    },
    update: function(cart) {
      return axios.put('/api/carts', cart);
    },
    delete: function(id) {
      return axios.delete('/api/carts/' + id);
    },
    deleteByIds: function(ids) {
      return axios.delete('/api/carts', { params: { ids: ids.join(',') } });
    }
  },
  order: {
    create: function(data) {
      return axios.post('/api/orders', data);
    },
    getById: function(id) {
      return axios.get('/api/orders/' + id);
    },
    list: function() {
      return axios.get('/api/orders/list');
    },
    markPaySuccess: function(orderId) {
      return axios.put('/api/orders/' + orderId);
    }
  },
  pay: {
    apply: function(data) {
      return axios.post('/api/pay-orders', data);
    },
    payByBalance: function(payOrderId, pw) {
      return axios.post('/api/pay-orders/' + payOrderId, { pw: pw });
    },
    queryByBizOrderNo: function(bizOrderNo) {
      return axios.get('/api/pay-orders/biz/' + bizOrderNo);
    }
  }
};