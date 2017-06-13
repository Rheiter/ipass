$(function() {
	loadTaken();
});

function loadTaken() {
	$("#confirm").hide();
	var uri = "restservices/taken";
	$.ajax(uri, {
		type: "get",
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
		success: function(response) {
			loadTaken();
			$("#removed").text("De taak \"" + $(".selected").data().naam + "\" is verwijderd. ");
		},
		error: function(response) {
			$("#removed").text("Kon de taak \"" + $(".selected").data().naam + "\" niet verwijderen. ");
		}
	});
});

$("#taken").on("click", ".taak", function() {
	$(".selected").removeClass("selected");
	$(this).addClass("selected");
	$("#chosen").text($(this).data().naam);
	$("#confirm").show();
});