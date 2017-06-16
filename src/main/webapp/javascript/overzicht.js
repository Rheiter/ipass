console.log("hier komt de gebruikersnaam:");
console.log(sessionStorage.getItem("gebruikersnaam"));

$(function() {
	init();
});

function init() {
	$("#rooster").empty();
	$("#schulden").empty();
	loadBewoners();
	loadBewonerTaken();
}

function loadBewonerTaken() {
	date = new Date(sessionStorage.date);
	date = date.getFullYear() + "/" + ("0" + (date.getMonth() + 1)).slice(-2) + "/" + ("0" + date.getDate()).slice(-2);
	var uri = "restservices/bewoner-taak?date=" + date;
	$.ajax(uri, {
		type: "get",
		beforeSend: function (xhr) {
			var token = sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function(data) {
			$.each(data, function (i, week) {
				$("#rooster").append("<tr><td>" + week.datum + "</td></tr>");
				$.each(week.taken, function (j, bewonerTaak) {
					if (bewonerTaak.gedaan == true) {
						var gedaan = "gedaan";
					} else if (bewonerTaak.gedaan == false) {
						var gedaan = "nietGedaan";
					}
					$("#rooster tr:last-child").append("<td class='temp "+ gedaan + "' title='Boete: \u20AC" + bewonerTaak.boete + "&#013Omschrijving: " + bewonerTaak.omschrijving + "'>" + bewonerTaak.taak + "</td>");
					if (i == 0) {
						var jsonObj = bewonerTaak;
						jsonObj["datum"] = week.datum;
						$(".temp").data(jsonObj);
						$(".temp").removeClass("temp");
					}
				});
			});
		},
		error: function() {
			console.log("error")
		}
	});
}

function loadBewoners() {
	var uri = "restservices/bewoners";
	$.ajax(uri, {
		type: "get",
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function(data) {
			$("#rooster").append("<tr id='roosterHeaderRow'></tr>")
			$("#roosterHeaderRow").append("<th>Deadline</th>");
			$.each(data, function(i, bewoner) {
				$("#roosterHeaderRow").append("<th>" + bewoner.gebruikersnaam + "</th>");
			});
			
			$("#schulden").append("<tr id='schuldenHeaderRow'></tr>")
			$("#schuldenHeaderRow").append("<th></th>");
			$("#schulden").append("<tr><td>Schuld</td></tr>");
			
			$.each(data, function(i, bewoner) {
				$("#schuldenHeaderRow").append("<th>" + bewoner.gebruikersnaam + "</th>");
				$("#schulden tr:last-child").append("<td>\u20AC" + bewoner.schuld + "</td>");
			});
		},
		error: function() {
			console.log("error")
		}
	});
}

$("table").on("click", "td", function() {
	var td = $(this);
	data = td.data();
	var gedaan = data.gedaan;
	console.log(gedaan);
	if (Object.keys(data).length > 0
			&& gedaan == false
			&& data.bewoner == sessionStorage.getItem("gebruikersnaam")) {
		console.log("in de functie");
		data.gedaan = !(data.gedaan);
		var uri = "restservices/bewoner-taak";
		$.ajax(uri, {
			type: "put",
			beforeSend: function (xhr) {
				var token = window.sessionStorage.getItem("sessionToken");
				xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
			},
			data: JSON.stringify(data),
			success: function(data) {
				td.removeClass("nietGedaan");
				td.addClass("gedaan");
			},
			error: function(data) {
				console.log("error")
			}
		});
	}
});