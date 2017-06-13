$("#submit").click(function() {
			var uri = "restservices/taken";
			var data = $("#form").serialize();
			$.ajax(uri, {
				type: "post",
				data: data, 
				success: function(response) {
					console.log(response);
					$("#added").text("Taak \"" + response.naam + "\" is toegevoegd. ");
				},
				error: function() {
					console.log("error");
					$("#added").text("Taak \"" + response.naam + "\" kon niet toegevoegd worden. ");
				}
			});
		});