$("#menuIcon").click(function() {
	console.log("click");
	$("#menu").toggleClass("show");
});

window.onclick = function(event) {
  if (!event.target.matches('#menuIcon')) {

    var dropdowns = document.getElementsByClassName("dropdown-content");
    var i;
    for (i = 0; i < dropdowns.length; i++) {
      var openDropdown = dropdowns[i];
      if (openDropdown.classList.contains('show')) {
        openDropdown.classList.remove('show');
      }
    }
  }
}