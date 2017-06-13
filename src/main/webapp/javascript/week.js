if (sessionStorage.date == null) {
	sessionStorage.date = new Date();
}

$("#weekButton").click(function() {
	var date = new Date(sessionStorage.date);
	date.setDate(date.getDate() + 7);
	sessionStorage.date = (date)
	init();
});