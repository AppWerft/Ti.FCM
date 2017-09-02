// first in stall gcm by `sudo npm --global install gcm`

var GCM = require('gcm').GCM;

const Tokens = {
	"lenovo" : "fyFGMoVFfEM:APA91bH6NP3NqJiVLVCttV8ffPnCjGZLWUgMowRRXviawQYKb_BpfEwRxC4vKv_fX228TdFntno7U_SBL-pcTfqcM3fKr0w2LQsewvos48uRZeUJBGpm3PDrFNVKnUqH6bTa7fQLIvgV",
	"LG6" : ""
};

const apiKey = 'AIzaSyC0j6XzIlORap6L3OHnLtZKxealAZLAGZ4';
// from tiapp.xml
const gcm = new GCM(apiKey);

Object.getOwnPropertyNames(Tokens).forEach(function(key) {
	if (Tokens[key]) {
		console.log("send to " + key);
		gcm.send({
			registration_id : Tokens[key], // required
			collapse_key : 'Collapse key',
			'title' : 'Mein Titel',
			'mesage' : 'Meine Botschaft'
		}, function(err, messageId) {
			if (err) {
				console.log("Something has gone wrong!");
			} else {
				console.log("Sent with message ID: ", messageId);
			}
		});
	}
});

