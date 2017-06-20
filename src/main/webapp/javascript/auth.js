var uri = "restservices/authentication/check";
$.ajax(uri, {
	type: "get",
	beforeSend: function (xhr) {
		var token = sessionStorage.getItem("sessionToken");
		xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
	},
	success: function() {
		var currentPage = window.location.href;
		if (currentPage.endsWith("ipass/") ||
				currentPage.endsWith("index.html")) {
			window.location.replace("overzicht.html");
		}
	},
	error: function(xhr) {
		var currentPage = window.location.href;
		if (!(currentPage.endsWith("ipass/")) &&
				!(currentPage.endsWith("index.html"))) {
			window.location.replace("index.html");
		}
	}
});

$("#logout").click(function () {
	if (confirm("Weet je zeker dat je wilt uitloggen?")) {
		sessionStorage.removeItem("sessionToken");
		sessionStorage.removeItem("gebruikersnaam");
		window.location.replace("index.html");
	}
});

$("#welkomSpan").text(sessionStorage.getItem("gebruikersnaam"));
var sessionDate = new Date(sessionStorage.date);
sessionDate = ("0" + sessionDate.getDate()).slice(-2) + "/" + ("0" + (sessionDate.getMonth() + 1)).slice(-2) + "/" + sessionDate.getFullYear();
$("#datumSpan").text(sessionDate);