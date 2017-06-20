$("#form").validate({
   rules: {
	   naam: {
		   required: true,
		   minlength: 2,
		   maxlength: 20,
		   letterswithbasicpunc: true
	   },
	   omschrijving: {
		   required: false,
		   maxlength: 400,
		   letterswithbasicpunc: true
	   },
	   boete: {
		   required: true,
		   number:true,
		   min: 0,
		   max: 9999
	   },
   },
   messages: {
	   naam: {
		   required: "Dit veld is verplicht.<br>",
		   minlength: "Gebruik minstens 2 karakters.<br>",
		   maxlength: "Gebruik maximaal 20 karakters.<br>",
		   letterswithbasicpunc: "Gebruik alleen letters en interpunctie.<br>"
	   },
	   omschrijving: {
		   maxlength: "Gebruik maximaal 400 karakters.<br>",
		   letterswithbasicpunc: "Gebruik alleen letters en interpunctie.<br>"
	   },
	   boete: {
		   required: "Dit veld is verplicht.<br>",
		   number: "Vul een geldig getal in.<br>",
		   min: "Negatieve waarden niet toegestaan.<br>",
		   max: "Boetes mogen niet hoger zijn dan \u20AC9999.<br> "
	   }
   }
});

$("#submit").click(function() {
	if ($("#form").valid() == true) {
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
	}
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