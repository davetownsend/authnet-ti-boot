var args = arguments[0] || {};

var post = function(uri, transId, amount) {
  var client = Ti.Network.createHTTPClient();
  console.log('- Submitting PRIOR_AUTH_CAPTURE to Authorize.net | transId: ' + transId + ' | amount: ' + amount);
  client.open("POST", uri);
  client.onload = function() {
    if (this.status == '200') {
      console.log('- Capture successfull!');
      console.log(this.responseText);
      $.captureSuccessLabel.show();
      $.captureCloseButton.show();
    } else {
      alert('Credit card capture failed. ' + this.status + " " + this.response);
    }
  };
  client.onerror = function(e) {
    console.log('- Connection error: ' + e.error);
    alert('Connection issue with payment system.');
  };
  client.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
  client.send(JSON.stringify({
        "transId": transId,
        "amount": amount
      }
  ));
};


$.captureSubmitButton.addEventListener('touchend', function() {
  var transId = args.transId;
  var amount = args.amount;
  var captureURI = 'http://{your host}/boot/relaycapture/capture'; // TODO
  post(captureURI, transId, amount);
});


$.captureCloseButton.addEventListener('touchend', function() {
  Alloy.createController('index').getView().open();
});

$.captureSuccessLabel.hide();
$.captureCloseButton.hide();