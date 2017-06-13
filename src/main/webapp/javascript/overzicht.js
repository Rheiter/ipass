$(function() {
	init();
});

function init() {
	$("#rooster").empty();
	loadBewonerTaken();
	loadBewoners();
}

function loadBewonerTaken() {
	date = new Date(sessionStorage.date);
	date = date.getFullYear() + "/" + ("0" + (date.getMonth() + 1)).slice(-2) + "/" + ("0" + date.getDate()).slice(-2);
	var uri = "restservices/bewoner-taak/next10?date=" + date;
	$.ajax(uri, {
		type: "get",
		success: function(data) {
			console.log(data);
			$.each(data, function (i, week) {
				$("#rooster").append("<tr><td>" + week.datum + "</td></tr>");
				$.each(week.taken, function (j, bewonerTaak) {
					if (bewonerTaak.gedaan == true) {
						var gedaan = "gedaan";
					} else {
						var gedaan = "nietGedaan";
					}
					$("#rooster tr:last-child").append("<td class='temp " + gedaan + "' title='" + bewonerTaak.omschrijving + "'>" + bewonerTaak.taak + "</td>");
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
		success: function(data) {
			$("#rooster").append("<tr id='headerRow'></tr>")
			$("#headerRow").append("<th>Deadline</th>");
			$.each(data, function(i, bewoner) {
				$("#headerRow").append("<th>" + bewoner.gebruikersnaam + "</th>");
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
	if (Object.keys(data).length > 0 && gedaan == false) {
		console.log("in de functie");
		data.gedaan = !(data.gedaan);
		var uri = "restservices/bewoner-taak";
		$.ajax(uri, {
			type: "put",
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