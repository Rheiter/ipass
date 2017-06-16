$("#submit").click(function() {
	var uri = "restservices/taken";
	var data = $("#form").serialize();
	$.ajax(uri, {
		type: "post",
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		data: data, 
		success: function(response) {
			var date = new Date(sessionStorage.date);
			date = date.getFullYear() + "/" + ("0" + (date.getMonth() + 1)).slice(-2) + "/" + ("0" + date.getDate()).slice(-2);
			updateThisWeek(response.taakID, date);
			$("#added").text("Taak \"" + response.naam + "\" is toegevoegd. ");
		},
		error: function() {
			console.log("error");
			$("#added").text("Taak \"" + response.naam + "\" kon niet toegevoegd worden. ");
		}
	});
});

function updateThisWeek(taakID, sysdate) {
	var uri = "restservices/bewoner-taak/nieuwe-taak/" + taakID;
	$.ajax(uri, {
		type: "put",
		data: sysdate,
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function() {
			console.log("Taak toegevoegd");
		},
		error: function() {
			console.log("error");
		}
	});
}