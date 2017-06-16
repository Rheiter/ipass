$(function() {
	loadTaken();
});

function loadTaken() {
	$("#confirm").hide();
	var uri = "restservices/taken";
	$.ajax(uri, {
		type: "get",
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function(data) {
			$("#taken").empty();
			$.each(data, function(i, taak) {
				$("#taken").append("<li class='taak temp'>" + taak.naam + "</li>");
				$(".temp").data(taak);
				$(".temp").removeClass("temp");
			});
		},
		error: function() {
			console.log("error")
		}
	});
}

$("#delete").click(function() {
	var uri = "restservices/taken/" + $(".selected").data().taakID;
	$.ajax(uri, {
		type: "delete",
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function(response) {
			loadTaken();
			var date = new Date(sessionStorage.date);
			date = date.getFullYear() + "/" + ("0" + (date.getMonth() + 1)).slice(-2) + "/" + ("0" + date.getDate()).slice(-2);
			updateThisWeek(response.taakID, date);
			$("#removed").text("De taak \"" + $(".selected").data().naam + "\" is verwijderd. ");
		},
		error: function(response) {
			$("#removed").text("Kon de taak \"" + $(".selected").data().naam + "\" niet verwijderen. ");
		}
	});
});

$("#update").click(function() {
	var uri = "restservices/taken/" + $(".selected").data().taakID;
	$.ajax(uri, {
		type: "put",
		data: $("#form").serialize(),
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function(response) {
			loadTaken();
			$("#removed").text("De taak \"" + $("#naam").val() + "\" is gewijzigd. ");
		},
		error: function(response) {
			$("#removed").text("Kon de taak \"" + $("#naam").val() + "\" niet wijzigen. ");
		}
	});
});

function updateThisWeek(taakID, sysdate) {
	var uri = "restservices/bewoner-taak/verwijder-taak/" + taakID;
	$.ajax(uri, {
		type: "put",
		data: sysdate,
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function() {
			console.log("Taak verwijderd");
		},
		error: function() {
			console.log("error");
		}
	});
}

$("#taken").on("click", ".taak", function() {
	var data = $(this).data();
	$(".selected").removeClass("selected");
	$(this).addClass("selected");
	$("#chosen").text(data.naam);
	$("#naam").val(data.naam);
	$("#omschrijving").val(data.omschrijving);
	$("#boete").val(data.boete);
	$("#confirm").show();
});