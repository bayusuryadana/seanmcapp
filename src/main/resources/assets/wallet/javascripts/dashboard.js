function colorMapping(label) {
  switch(label) {
    case "Daily": return "#7ed6a5";
    case "Rent": return "#fcc468";
    case "Zakat": return "#4acccd";
    case "Travel": return "#9fd2d6";
    case "Fashion": return "#e3e3e3";
    case "IT Stuff": return "#f4c0fc";
    case "Misc": return "#ef8157";
    case "Wellness": return "#d6cfc6";
    case "Funding": return "#9e9e9e";
    default: return "#212121";
  }
}

function expensePie(label, data, color) {
  new Chart(document.getElementById('expensePie').getContext("2d"), {
    type: 'pie',
    data: {
      labels: label,
      datasets: [{
        backgroundColor: color,
        borderWidth: 1,
        data: data
      }]
    },
  });
}

function buildDataset(data, color, tag) {
  return {
    label: tag,
    data: data,
    fill: false,
    backgroundColor: color,
    borderColor: color,
    pointRadius: 2,
    pointHoverRadius: 2,
    pointBorderWidth: 4,
  }
}

let chartOptions = {
  legend: {
    display: false,
    position: 'top'
  }
};

function buildChart(chartType, target, label, datasets1, datasets2, segment) {
  let ctx = document.getElementById(target);

  let myChartData = new Chart(ctx, {
    type: chartType,
    data: {
      labels: label,
      datasets: datasets1,
    },
    options: chartOptions
  });

  $("#"+segment+"-1").click(function() {
    let data = myChartData.config.data;
    data.datasets = datasets1;
    data.labels = label;
    myChartData.update();
  });
  $("#"+segment+"-2").click(function() {
    let data = myChartData.config.data;
    data.datasets = datasets2;
    data.labels = label;
    myChartData.update();
  });
}
