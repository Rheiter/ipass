if (sessionStorage.date == null) {
	sessionStorage.date = new Date();
}

$("#logo").click(function() {
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
			var sessionDate = new Date(sessionStorage.date);
			sessionDate = ("0" + sessionDate.getDate()).slice(-2) + "/" + ("0" + (sessionDate.getMonth() + 1)).slice(-2) + "/" + sessionDate.getFullYear();
			$("#datumSpan").text(sessionDate);
		},
		error: function() {
			console.log("error");
		}
	});
	
	date.setDate(date.getDate() + 7);
	sessionStorage.date = (date);
	init();
});