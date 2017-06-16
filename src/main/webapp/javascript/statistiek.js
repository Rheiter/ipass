google.charts.load('current', {packages: ['corechart', 'bar', 'line']});

persoonlijkeData = [];
algemeneData = [];

function loadPersoonlijkeData() {
	var uri = "restservices/statistiek/bewoner/" + sessionStorage.getItem("gebruikersnaam");
	$.ajax(uri, {
		type: "get",
		beforeSend: function (xhr) {
			var token = sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function (data) {
			console.log(data);
			persoonlijkeData = data;
			google.charts.setOnLoadCallback(drawPersoonlijkeStatistiek);
		},
		error: function(data) {
			console.log("error");
		}
	});
}

function loadAlgemeneData() {
	var uri = "restservices/statistiek";
	$.ajax(uri, {
		type: "get",
		beforeSend: function (xhr) {
			var token = sessionStorage.getItem("sessionToken");
			xhr.setRequestHeader( 'Authorization', 'Bearer ' + token);
		},
		success: function (data) {
			algemeneData.push(['Datum','Gedaan', {type: 'string', role:'tooltip', p: {html: true}}]);
			$.each(data, function(i, bt) {
				var date = new Date(bt[0], bt[1], bt[2]);
				var dateOptions = {year: 'numeric', month: 'long', day: 'numeric' };
				var tooltipString = "<b>" + date.toLocaleDateString("nl-NL",dateOptions) + "</b><br>" + "Gedaan: <b>" + bt[3] +"</b>";
				algemeneData.push([new Date(bt[0], bt[1], bt[2]), bt[4], tooltipString]);
				google.charts.setOnLoadCallback(drawStatistiek);
			});
		},
		error: function(data) {
			console.log("error");
		}
	});
}

loadPersoonlijkeData();
console.log(persoonlijkeData);
loadAlgemeneData();
console.log(algemeneData);

function drawPersoonlijkeStatistiek() {
      var data = google.visualization.arrayToDataTable(persoonlijkeData);

      var options = {
      chartArea:{left:'15%',top:10,width:'50%',height:'75%'},
      animation: {duration: 1000, easing: 'out', startup: true},
        title: 'Motivation and Energy Level Throughout the Day',
        isStacked: 'percent',
        hAxis: {
          title: 'Taak'
        },
        vAxis: {
          title: 'Percentage',
            ticks: [0, .25, .5, .75, 1]
        }
      };

      var chart = new google.visualization.ColumnChart(document.getElementById('chart_div'));
      chart.draw(data, options);
    }

function drawStatistiek() {
    var data = google.visualization.arrayToDataTable(algemeneData);

    var options = {
      title: 'Percentage gedaan',
      animation: {duration: 1000, easing: 'out', startup: true},
      vAxis: {
    	  minValue: 0,
    	  maxValue: 100,
    	  format: "#'%'"
      },
      tooltip: {isHtml: true},
      legend: { position: 'bottom' }
    };

    var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

    chart.draw(data, options);
  }