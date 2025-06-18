	var options = { 
			series: [netMargin, consultanse,commission], 
			chart: { type: "pie", height: 240 }, 
			labels: ["Net Margin", "Consultant expense","Commission"], 
			colors: ["#23a073", "#546de6","#f46a6a"], 
			legend: { show: !1 }, 
			plotOptions: { 
				pie: 
				{ 
					donut:{ size: "70%" } 
				} 
			} 
		};
	
	(chart2 = new ApexCharts(document.querySelector("#donut-chart"), options)).render();

	function setValue(value){
		if(value != undefined && value != null)
			return value.toFixed(2);
		else 
			return "";
	}
	chartView();
	function chartView(){
		var aa1 = {
		        chart: { height: 359, type: "bar",stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
		        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
		        dataLabels: { enabled: !1 },
		        series: [
		        	{ name: "Net Margin", data: userNetMargins },
		            { name: "Expense", data: userexpenses },
		            { name: "Commission", data: usercommission },
		        ],
		        xaxis: { categories: xChart },
		        colors: ["#34c38f","#556ee6","#f46a6a"],
		        legend: { show: false },
		        fill: { opacity: 1 },
		        yaxis: {
		      	  labels: {
		      		  formatter: function(val, index) {
		      		        return "$ " + setValue(val);
		      		      }
		      	  },
		      	}
		       
		    };
		
		chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
		chart.render();
	};
	function changeChartUserWise(){
		chart.destroy ();
		$("#changeChartUserWise").removeClass("btn-outline-primary");
		$("#changeChartUserWise").addClass("btn-primary");
		$("#changeChartMonthly").removeClass("btn-primary");
		$("#changeChartMonthly").addClass("btn-outline-primary");
		var aa = {
				chart: { height: 359, type: "bar", stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
				plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
				dataLabels: { enabled: !1 },
				series: [
					{ name: "Net Margin", data: netMargins },
					{ name: "Expense", data: expenses },
					{ name: "Commission", data: commissions },
				],
					xaxis: { categories: users },
					colors: ["#34c38f","#556ee6","#f46a6a"],
					legend: { show: false },
					fill: { opacity: 1 },
					yaxis: {
			        	  labels: {
			        		  formatter: function(val, index) {
			        		        return "$ " + setValue(val);
			        		      }
			        	  },
			        	},
		};
		
		chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa);
		chart.render();
		
		$("input[name=revenue][value=Revenue]").prop("checked", true);
	}
	
	function changeChartMonthly(){
		$("#changeChartMonthly").removeClass("btn-outline-primary");
		$("#changeChartMonthly").addClass("btn-primary");
		$("#changeChartUserWise").removeClass("btn-primary");
		$("#changeChartUserWise").addClass("btn-outline-primary");
		var aa1 = {
		        chart: { height: 359, type: "bar",stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
		        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
		        dataLabels: { enabled: !1 },
		        series: [
		        	{ name: "Net Margin", data: userNetMargins },
		            { name: "Expense", data: userexpenses },
		            { name: "Commission", data: usercommission },
		        ],
		        xaxis: { categories: xChart },
		        colors: ["#34c38f","#556ee6","#f46a6a"],
		        legend: { show: false },
		        fill: { opacity: 1 },
		        yaxis: {
		      	  labels: {
		      		  formatter: function(val, index) {
		      		        return "$ " + setValue(val);
		      		      }
		      	  },
		      	}
		       
		    };
		
		chart.destroy ();
		chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
		chart.render();
		
		$("input[name=revenue][value=Revenue]").prop("checked", true);
	}

	
	// toggleSeries accepts a single argument which should match the series name you're trying to toggle
	function toggleSeries(checkbox) {
		
		if($("#changeChartMonthly").hasClass( "btn-primary" )){
			chartSet(checkbox);
		} else {
			chartSetUser(checkbox);
		}
	}

	function chartSetUser(checkbox){
		if(checkbox.value == 'Revenue'){
			
			var aa = {
					chart: { height: 359, type: "bar", stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
					plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
					dataLabels: { enabled: !1 },
					series: [
						{ name: "Net Margin", data: netMargins },
						{ name: "Expense", data: expenses },
						{ name: "Commission", data: commissions },
					],
						xaxis: { categories: users },
						colors: ["#34c38f","#556ee6","#f46a6a"],
						legend: { show: false },
						fill: { opacity: 1 },
						yaxis: {
				        	  labels: {
				        		  formatter: function(val, index) {
				        		        return "$ " + setValue(val);
				        		      }
				        	  },
				        	},
			};
			
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa);
			chart.render();
			
			$(".userHoursChartTitle").text("Revenue");
		}
		if(checkbox.value == 'Gross Margin'){
			$(".userHoursChartTitle").text("Gross Margin");
			var aa = {
					chart: { height: 359, type: "bar", stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
					plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
					dataLabels: { enabled: !1 },
					series: [
						{ name: "Gross Margin", data: grossMargins },
					],
						xaxis: { categories: users },
						colors: ["#f1b44c"],
						legend: { show: false },
						fill: { opacity: 1 },
						yaxis: {
				        	  labels: {
				        		  formatter: function(val, index) {
				        		        return "$ " + setValue(val);
				        		      }
				        	  },
				        	},
			};
			
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa);
			chart.render();
		}
		if(checkbox.value == 'Net Margin'){
			$(".userHoursChartTitle").text("Net Margin");
			
			var aa = {
					chart: { height: 359, type: "bar", stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
					plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
					dataLabels: { enabled: !1 },
					series: [
						{ name: "Net Margin", data: netMargins },
					],
						xaxis: { categories: users },
						colors: ["#34c38f"],
						legend: { show: false },
						fill: { opacity: 1 },
						yaxis: {
				        	  labels: {
				        		  formatter: function(val, index) {
				        		        return "$ " + setValue(val);
				        		      }
				        	  },
				        	},
			};
			
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa);
			chart.render();
		}
		if(checkbox.value == 'Expense'){
			$(".userHoursChartTitle").text("Expense");
			var aa = {
					chart: { height: 359, type: "bar", stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
					plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
					dataLabels: { enabled: !1 },
					series: [
						{ name: "Ws2Paytax Expense", data: expensesWs2P },
						{ name: "Consultant Expense", data: expensesCon },
						{ name: "Other Expense", data: expensesOther },
					],
						xaxis: { categories: users },
						colors: ["#6d84f5","#3348af","#04135a"],
						legend: { show: false },
						fill: { opacity: 1 },
						yaxis: {
				        	  labels: {
				        		  formatter: function(val, index) {
				        		        return "$ " + setValue(val);
				        		      }
				        	  },
				        	},
			};
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa);
			chart.render();
		}
		if(checkbox.value == 'Commission'){
			$(".userHoursChartTitle").text("Commission");
			var aa = {
					chart: { height: 359, type: "bar", stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
					plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
					dataLabels: { enabled: !1 },
					series: [
			        	{ name: "BDM Commission", data: commissionsBDM },
			        	{ name: "ACM Commission", data: commissionsACM },
			            { name: "Rec Commission", data: commissionsREC },
			        ],
						xaxis: { categories: users },
						colors: ["#f46a6a","#9c1d06","#c52b0f"],
						legend: { show: false },
						fill: { opacity: 1 },
						yaxis: {
				        	  labels: {
				        		  formatter: function(val, index) {
				        		        return "$ " + setValue(val);
				        		      }
				        	  },
				        	},
			};
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa);
			chart.render();
		}
	}
	
	
	function chartSet(checkbox){
		if(checkbox.value == 'Revenue'){
			
			var aa1 = {
			        chart: { height: 359, type: "bar",stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
			        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
			        dataLabels: { enabled: !1 },
			        series: [
			        	{ name: "Net Margin", data: userNetMargins },
			            { name: "Expense", data: userexpenses },
			            { name: "Commission", data: usercommission },
			        ],
			        xaxis: { categories: xChart },
			        colors: ["#34c38f","#556ee6","#f46a6a"],
			        legend: { show: false },
			        fill: { opacity: 1 },
			        yaxis: {
			      	  labels: {
			      		  formatter: function(val, index) {
			      		        return "$ " + setValue(val);
			      		      }
			      	  },
			      	}
			       
			    };
			
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
			chart.render();
			
			$(".userHoursChartTitle").text("Revenue");
		}
		if(checkbox.value == 'Gross Margin'){
			$(".userHoursChartTitle").text("Gross Margin");
			var aa1 = {
			        chart: { height: 359, type: "bar",stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
			        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
			        dataLabels: { enabled: !1 },
			        series: [
			            { name: "Gross Margin", data: userGrossMargins },
			        ],
			        xaxis: { categories: xChart },
			        colors: ["#f1b44c"],
			        legend: { show: false },
			        fill: { opacity: 1 },
			        yaxis: {
			      	  labels: {
			      		  formatter: function(val, index) {
			      		        return "$ " + setValue(val);
			      		      }
			      	  },
			      	}
			       
			    };
			
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
			chart.render();
		}
		if(checkbox.value == 'Net Margin'){
			$(".userHoursChartTitle").text("Net Margin");
			
			var aa1 = {
			        chart: { height: 359, type: "bar",stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
			        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
			        dataLabels: { enabled: !1 },
			        series: [
			        	{ name: "Net Margin", data: userNetMargins },
			        ],
			        xaxis: { categories: xChart },
			        colors: ["#34c38f"],
			        legend: { show: false },
			        fill: { opacity: 1 },
			        yaxis: {
			      	  labels: {
			      		  formatter: function(val, index) {
			      		        return "$ " + setValue(val);
			      		      }
			      	  },
			      	}
			       
			    };
			
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
			chart.render();
		}
		if(checkbox.value == 'Expense'){
			$(".userHoursChartTitle").text("Expense");
			var aa1 = {
			        chart: { height: 359, type: "bar",stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
			        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
			        dataLabels: { enabled: !1 },
			        series: [
			        	{ name: "Consultant Expense", data: userExpenseCon },
			        	{ name: "Ws2Paytax Expense", data: userExpenseW2p },
			            { name: "Other Expense", data: userExpenseOther },
			        ],
			        xaxis: { categories: xChart },
			        colors: ["#6d84f5","#3348af","#04135a"],
			        legend: { show: false },
			        fill: { opacity: 1 },
			        yaxis: {
			      	  labels: {
			      		  formatter: function(val, index) {
			      		        return "$ " + setValue(val);
			      		      }
			      	  },
			      	}
			       
			    };
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
			chart.render();
		}
		if(checkbox.value == 'Commission'){
			$(".userHoursChartTitle").text("Commission");
			var aa1 = {
			        chart: { height: 359, type: "bar",stacked:!0, toolbar: { show: !1 }, zoom: { enabled: !0 } },
			        plotOptions: { bar: { horizontal: !1, columnWidth: "15%", endingShape: "rounded" } },
			        dataLabels: { enabled: !1 },
			        series: [
			        	{ name: "BDM Commission", data: userCommissionsBDM },
			        	{ name: "ACM Commission", data: userCommissionsACM },
			            { name: "Rec Commission", data: userCommissionsREC },
			        ],
			        xaxis: { categories: xChart },
			        colors: ["#f46a6a","#9c1d06","#c52b0f"],
			        legend: { show: false },
			        fill: { opacity: 1 },
			        yaxis: {
			      	  labels: {
			      		  formatter: function(val, index) {
			      		        return "$ " + setValue(val);
			      		      }
			      	  },
			      	}
			       
			    };
			chart.destroy ();
			chart = new ApexCharts(document.querySelector("#stacked-column-chart-hours"), aa1);
			chart.render();
		}
	}

