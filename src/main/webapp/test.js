window.onload = () => {
	window.getCharts = (urlCallback) => {
		console.log(Highcharts);
		chart =  $("#chart").highcharts()
		var chartSVG = chart.getSVG({
			exporting: {
				sourceWidth: chart.chartWidth,
				sourceHeight: chart.chartHeight
			}
		});

		var canvas = document.createElement('canvas');
		canvas.width = chart.chartWidth;
		canvas.height = chart.chartHeight;
        canvg(canvas, chartSVG)
		graph = canvas.toDataURL('image/jpg');

        canvas = document.getElementById("map").getElementsByTagName("canvas")[0]
        map = canvas.toDataURL('image/jpg');

		Wicket.Ajax.post({ ep : {base : graph, map : map}, u: urlCallback});

	}

}
