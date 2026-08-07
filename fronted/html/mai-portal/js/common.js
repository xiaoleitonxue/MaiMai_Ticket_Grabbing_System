axios.defaults.timeout = 10000;

axios.interceptors.request.use(
  config => {
    var token = sessionStorage.getItem("token");
    if (token) {
      config.headers['authorization'] = token;
    }
    return config;
  },
  function(err) {
    return Promise.reject(err);
  }
);

axios.interceptors.response.use(
  function(response) {
    return response;
  },
  function(err) {
    if (err.response && (err.response.status === 401 || err.response.status === 403)) {
      sessionStorage.removeItem("token");
      sessionStorage.removeItem("user-info");
      location.href = "/login.html";
    }
    return Promise.reject(err);
  }
);
const util = {
  isLogin(){
    return !!sessionStorage.getItem("user-info");
  },
  getUserInfo(){
    try {
      return JSON.parse(sessionStorage.getItem("user-info"));
    } catch(e) {
      return null;
    }
  },
  setUserInfo(info){
    sessionStorage.setItem("user-info", JSON.stringify(info));
  },
  logout(){
    sessionStorage.removeItem("user-info")
    sessionStorage.removeItem("token")
    location.href = "/login.html";
  },
  getUrlParam(name) {
    let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    let r = window.location.search.substr(1).match(reg);
    if (r != null) {
      return decodeURI(r[2]);
    }
    return "";
  },
  formatPrice(val) {
    if (val == null || val === '') return '0.00';
    var num = Number(val);
    if (isNaN(num)) return '0.00';
    return (num / 100).toFixed(2);
  },
  formatDate(val) {
    if (!val) return '';
    var d = new Date(val);
    if (isNaN(d.getTime())) return val;
    var y = d.getFullYear();
    var m = ('0' + (d.getMonth() + 1)).slice(-2);
    var day = ('0' + d.getDate()).slice(-2);
    var h = ('0' + d.getHours()).slice(-2);
    var min = ('0' + d.getMinutes()).slice(-2);
    var s = ('0' + d.getSeconds()).slice(-2);
    return y + '-' + m + '-' + day + ' ' + h + ':' + min + ':' + s;
  },
  getOrderStatusText(status) {
    var map = { 1: '待付款', 2: '已付款', 3: '已发货', 4: '已完成', 5: '已取消', 6: '已评价' };
    return map[status] || '未知';
  },
  getOrderStatusClass(status) {
    var map = { 1: 'status-pending', 2: 'status-paid', 3: 'status-shipped', 4: 'status-done', 5: 'status-cancel', 6: 'status-finish' };
    return map[status] || '';
  },
  getPaymentTypeText(type) {
    var map = { 1: '支付宝', 2: '微信', 3: '余额支付' };
    return map[type] || '其他';
  },
  toast(msg, type) {
    type = type || 'info';
    var bgColors = { success: '#10b981', error: '#ef4444', warning: '#f59e0b', info: '#6366f1' };
    var div = document.createElement('div');
    div.textContent = msg;
    div.style.cssText = 'position:fixed;top:20px;left:50%;transform:translateX(-50%);z-index:9999;'
      + 'padding:12px 28px;border-radius:8px;color:#fff;font-size:14px;font-weight:600;'
      + 'background:' + (bgColors[type] || bgColors.info) + ';box-shadow:0 8px 24px rgba(0,0,0,0.15);'
      + 'animation:toastIn 0.3s ease;';
    document.body.appendChild(div);
    setTimeout(function() {
      div.style.opacity = '0';
      div.style.transition = 'opacity 0.3s';
      setTimeout(function() { document.body.removeChild(div); }, 300);
    }, 2000);
  },
  confirm(msg) {
    return new Promise(function(resolve) {
      var overlay = document.createElement('div');
      overlay.style.cssText = 'position:fixed;top:0;left:0;right:0;bottom:0;background:rgba(0,0,0,0.4);z-index:9998;';
      var box = document.createElement('div');
      box.style.cssText = 'position:fixed;top:50%;left:50%;transform:translate(-50%,-50%);z-index:9999;'
        + 'background:#fff;border-radius:12px;padding:32px;min-width:320px;box-shadow:0 20px 60px rgba(0,0,0,0.2);text-align:center;';
      box.innerHTML = '<div style="font-size:16px;font-weight:600;margin-bottom:24px;color:#0f172a;">' + msg + '</div>'
        + '<div style="display:flex;gap:12px;justify-content:center;">'
        + '<button id="confirm-cancel" style="padding:8px 24px;border:1px solid #e2e8f0;border-radius:8px;background:#fff;cursor:pointer;font-size:14px;">取消</button>'
        + '<button id="confirm-ok" style="padding:8px 24px;border:none;border-radius:8px;background:#6366f1;color:#fff;cursor:pointer;font-size:14px;">确定</button>'
        + '</div>';
      overlay.appendChild(box);
      document.body.appendChild(overlay);
      document.getElementById('confirm-ok').onclick = function() {
        document.body.removeChild(overlay);
        resolve(true);
      };
      document.getElementById('confirm-cancel').onclick = function() {
        document.body.removeChild(overlay);
        resolve(false);
      };
    });
  },
  store: {
    set(key, obj) {
      sessionStorage.setItem(key, JSON.stringify(obj))
    },
    get(key) {
      try {
        return JSON.parse(sessionStorage.getItem(key))
      } catch(e) {
        return null;
      }
    },
    del(key) {
      sessionStorage.removeItem(key)
    }
  }
}