var options = {
        chart: { height: 300, type: "bar", toolbar: { show: !1 } },
        plotOptions: { bar: { horizontal: !1, columnWidth: "14%", endingShape: "rounded" } },
        dataLabels: { enabled: !1 },
        stroke: { show: !0, width: 2, colors: ["transparent"] },
        series: [{ name: "Net Margin", data: chartData }],
        xaxis: { categories: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"] },
        yaxis: { title: { text: "$ (Net Margin)" } },
        fill: { opacity: 1 },
        colors: ["#556ee6"],
    },
    chart = new ApexCharts(document.querySelector("#revenue-chart"), options);
chart.render();
