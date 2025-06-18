//Superwiser
/**
 * get user basic details
 * @param id
 * @returns
 */
function bacisDetails(name,id) {
	$.ajax({
		type : "GET",
		url : context + "supervisor/basic-detail/"+id,
		success: function(data){
			$('#user-details-modal').replaceWith(data);
			$('#view_title_name').text(name);
		}
	});
}

var options1 = { 
		series: [approvedFile, newFile, rejectFile], 
		chart: { type: "pie", height: 240 }, 
		labels: ["Approved Time Sheet", "Submitted  Time Sheet","Rejected Time Sheet"], 
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


var aa = {
        chart: { height: 359, type: "bar",stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
        dataLabels: { enabled: !1 },
        series: [
            { name: "C2C Consultant", data: c2cUserList },
            { name: "W2 Consultant", data: ptaxUserList },
//            { name: "All Consultant", data: totalUserList },
        ],
        xaxis: { categories: xChart },
        colors: ["#f1b44c", "#0f78ea"],
        legend: { show: true },
        fill: { opacity: 1 },
        markers: {
            size: 5,
            hover: {
                size: 1,
                sizeOffset: 3
              }
        },
        stroke: {
        	  curve: 'smooth',
        },
		fill: {
			type: 'gradient' / 'solid' / 'pattern'
		},
        
    },
	chart1 = new ApexCharts(document.querySelector("#stacked-column-chart-1"), aa);
	
	chart1.render();
	
	function setValue(value){
		if(value % 1 == 0){
			return value.toFixed(0);
		} else {
			return "";
		}
		
	}
//	checkLegends1();
	
	function checkLegends1() {
		
		chart1.showSeries('C2C Consultant');
		chart1.showSeries('W2 Consultant');
//		chart1.showSeries('All Consultant');
		
	//	$(".consultantAll123").prop("checked",true);
	}
	
	function toggleSeries(checkbox) {
		if(checkbox.value == 'C2C Consultant'){
			chart1.showSeries('C2C Consultant');
			chart1.hideSeries('W2 Consultant');
			chart1.hideSeries('All Consultant');
			
		} else if(checkbox.value == 'W2 Consultant'){
			chart1.showSeries('W2 Consultant');
			chart1.hideSeries('C2C Consultant');
			chart1.hideSeries('All Consultant');
			
		} else if(checkbox.value == 'All Consultant'){
			chart1.showSeries('All Consultant');
			chart1.hideSeries('W2 Consultant');
			chart1.hideSeries('C2C Consultant');
		}
	}

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
        colors: ["#50a5f1", "#f1b44c","#556ee6"],
        legend: { show: false },
        fill: { opacity: 1 },
    }
	chart2 = undefined;
//    chart2 = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
//	chart2.render();
//	
//	checkLegends2();
	
	function checkLegends2() {
		
		chart2.showSeries('Daily hours');
		chart2.hideSeries('Extra hours');
		chart2.hideSeries('Vacation');
		
		$(".revenue123").prop("checked",true);
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
	
	function changeChart(valueChart){
		$("#consultantChartCard").toggleClass('hide');
		$("#hoursChartCard").toggleClass('hide');
		
		if(valueChart == "CONSULTANTCHARTCARD"){
			if(chart2 != undefined)
				chart2.destroy();
			chart1 = new ApexCharts(document.querySelector("#stacked-column-chart-1"), aa);
			chart1.render();
			checkLegends1();
		} else {
			chart1.destroy();
			chart2 = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
			chart2.render();
			checkLegends2();
		}
	}
	
