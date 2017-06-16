if (sessionStorage.date == null) {
	sessionStorage.date = new Date();
}

$("#weekButton").click(function() {
	var date = new Date(sessionStorage.date);
	var dateString = date.getFullYear() + "/" + ("0" + (date.getMonth() + 1)).slice(-2) + "/" + ("0" + date.getDate()).slice(-2);
	
	var uri = "restservices/bewoners/schuld?date=" + dateString;
	$.ajax(uri, {
		type: "put",
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function(data) {
			console.log("succes");
			console.log(data);
		},
		error: function() {
			console.log("error");
		}
	});
	
	date.setDate(date.getDate() + 7);
	sessionStorage.date = (date);
	console.log(date);
	init();
});