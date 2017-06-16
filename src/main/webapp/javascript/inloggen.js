$("#loginButton").click(function(event) {
		var data = $("#loginForm").serialize();
		$.post("restservices/authentication", data, function(response) {
		sessionStorage.setItem("sessionToken", response);
		sessionStorage.setItem("gebruikersnaam", $("#gebruikersnaam").val());
		window.location.replace("overzicht.html");
		}).fail(function(jqXHR, textStatus, errorThrown) {
			console.log(textStatus);
			console.log(errorThrown);
			alert("Wrong username/password!");
		});
	});