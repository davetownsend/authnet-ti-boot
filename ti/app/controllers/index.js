$.authSubmitButton.addEventListener('touchend', function() {
  var amount = $.x_amount.value;
  // server endpoint in the Spring Boot app that will return Fingerprint data
  var accessURI = 'http://{your host}/boot/authnet/access?amount=' + amount; // TODO
  get(accessURI, function(data) {
    var form = buildForm(data);
    // standard Authorize.net sandbox test URI
    var authNet = 'https://test.authorize.net/gateway/transact.dll';
    // DPM POST to Authorize.net
    post(form, authNet);
  });
});


var get = function(uri, cb) {
  var client = Ti.Network.createHTTPClient();
  client.onload = function() {
    var data = JSON.parse(this.responseText);
    console.log(data);
    cb(data);
  };
  client.onerror = function(e) {
    console.log('ERROR: ' + e.error);
  };
  client.open('GET', uri);
  client.setRequestHeader('Content-Type', 'application/json');
  client.send();
};


var post = function(form, uri) {
  var client = Ti.Network.createHTTPClient();
  client.open("POST", uri);
  client.onload = function() {
    if (this.status == '200') {
      console.log('- POST to Authorize.net successful!!');
      console.log(this.responseText);
      var data = JSON.parse(this.responseText);
      Alloy.createController('capture', {
            "transId": data.transId,
            "amount": $.x_amount.value
          }
      ).getView().open();
    } else {
      alert('Credit card auth failed. ' + this.status + " " + this.response);
    }
  };
  client.onerror = function(e) {
    console.log('Connection error: ' + e.error);
    alert('Connection issue with payment system.');
  };
  client.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  client.send(form);
};


var buildForm = function(data) {
  return {
    x_card_num: $.x_card_num.value,
    x_card_code: $.x_card_code.value,
    x_exp_date: $.x_exp_date.value,
    x_invoice_num: new Date().getTime(),
    x_relay_url: 'https://{your-host}/boot/relaycapture/relay', // TODO
    x_login: 'Your Authorize.net API Login ID', // TODO
    x_fp_sequence: String(data.sequence),
    x_fp_timestamp: String(data.timestamp),
    x_fp_hash: String(data.hash),
    x_version: '3.1',
    x_method: 'CC',
    x_type: 'AUTH_ONLY',
    x_amount: String($.x_amount.value),
    x_test_request: 'FALSE'
  };
};

$.index.open();
