$(function () {
	init();
});

function init() {
	$("#schulden").empty();
	loadSchulden();
}

$("#uitkeren").click(function() {
	var schulden = false;
	$.each($("td"), function(i, td) {
		if (i > 0 && td.innerHTML !== "\u20AC0") {
		}
	});
	console.log(schulden);
	if (schulden == false) {
		alert("Er zijn op het moment geen schulden.");
	}
	else if (confirm("Weet je zeker dat je wilt uitbetalen?")) {
		var uri = "restservices/bewoners/schuld/uitkeren";
		$.ajax(uri, {
			type: "put",
			beforeSend: function (xhr) {
				var token = sessionStorage.getItem("sessionToken");
				xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
			},
			success: function(data) {
				init();
				$("#verdeling").append("<tr><th>Bewoner</th><th>Te betalen/ontvangen</th></tr>");
				$("uitkeren").hide();
				$.each(data, function(i, bewoner) {
					$.each(bewoner.betalingen, function(j, betaling) {
						if (j == 0) {
							$("#verdeling").append("<tr><td>" + bewoner.bewoner + "</td><td>" + betaling + "</td></tr>");
						} else {
							$("#verdeling").append("<tr><td></td><td>" + betaling + "</td></tr>");
						}
					});
				});
			},
			error: function() {
				console.log("error");
			}
		});
	}
});

function loadSchulden() {
	var uri = "restservices/bewoners";
	$.ajax(uri, {
		type: "get",
		beforeSend: function (xhr) {
			var token = window.sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function(data) {
			
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