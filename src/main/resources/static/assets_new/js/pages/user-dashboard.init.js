//Superwiser

var options1 = { 
		series: [approvedFile, newFile, rejectFile], 
		chart: { type: "pie", height: 240 }, 
		labels: ["Approved Time Sheet", "New Time Sheet","Rejected Time Sheet"], 
		colors: ["#556ee6", "#34c38f","#f46a6a"], 
		legend: { show: !1 }, 
		plotOptions: { 
			pie: 
			{ 
				donut:{ size: "70%" } 
			} 
		} 
	};

(chart = new ApexCharts(document.querySelector("#donut-chart-1"), options1)).render();

var aa1 = {
        chart: { height: 359, type: "bar", toolbar: { show: !1 }, zoom: { enabled: !0 } },
        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
        dataLabels: { enabled: !1 },
        series: [
            { name: "Daily hours", data: hours },
            { name: "Extra hours", data: extra },
            { name: "Vacation", data: vacation },
        ],
        xaxis: { categories: xChart },
        colors: ["#50a5f1", "#f1b44c","#546de6"],
        legend: { show: false },
        fill: { opacity: 1 },
    },
    chart2 = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
	chart2.render();
	
	checkLegends2();
	
	function checkLegends2() {
		
		chart2.showSeries('Daily hours');
		chart2.hideSeries('Extra hours');
		chart2.hideSeries('Vacation');
	}
	
	function toggleSeriesHours(checkbox) {
		if(checkbox.value == 'Daily hours'){
			chart2.showSeries('Daily hours');
			chart2.hideSeries('Extra hours');
			chart2.hideSeries('Vacation');
		}
		if(checkbox.value == 'Extra hours'){
			chart2.showSeries('Extra hours');
			chart2.hideSeries('Daily hours');
			chart2.hideSeries('Vacation');
		}
		if(checkbox.value == 'Vacation'){
			chart2.showSeries('Vacation');
			chart2.hideSeries('Extra hours');
			chart2.hideSeries('Daily hours');
		}
	}

