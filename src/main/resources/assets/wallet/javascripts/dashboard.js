var chartOptions = {
  legend: {
    display: false,
    position: 'top'
  }
};

function expensePie(label, data) {
  new Chart(document.getElementById('expensePie').getContext("2d"), {
    type: 'pie',
    data: {
      labels: label,
      datasets: [{
        backgroundColor: [
          '#e3e3e3',
          '#4acccd',
          '#fcc468',
          '#ef8157',
          '#7ed6a5',
          '#f4c0fc',
          '#d6cfc6',
          '#9fd2d6',
          '#b9751a'
        ],
        borderWidth: 1,
        data: data
      }]
    },
  });
}

function expenseChart(label, expense) {
  var chartCanvas = document.getElementById("expenseChart");

  function buildDataset(data, color, label) {
    return {
      data: data,
      backgroundColor: color,
      label: label
    }
  }

  var expenseData = {
    labels: label.slice(label.length - 7),
    datasets: [
      buildDataset(expense.daily, '#7ed6a5', 'Daily'),
      buildDataset(expense.rent, '#fcc468', 'Rent'),
      buildDataset(expense.zakat, '#4acccd', 'Zakat'),
      buildDataset(expense.travel, '#9fd2d6', 'Travel'),
      buildDataset(expense.fashion, '#e3e3e3', 'Fashion'),
      buildDataset(expense.itStuff, '#f4c0fc', 'IT Stuff'),
      buildDataset(expense.misc, '#ef8157', 'Misc'),
      buildDataset(expense.wellness, '#d6cfc6', 'Wellness')
    ]
  };

  new Chart(chartCanvas, {
    type: 'bar',
    hover: false,
    data: expenseData,
    options: chartOptions
  });
}

function balanceChart(label, balanceData) {
  var chartCanvas = document.getElementById("balanceChart");

  new Chart(chartCanvas, {
    type: 'line',
    hover: false,
    data: {
      labels: label,
      datasets: [{
        data: balanceData,
        fill: false,
        backgroundColor: 'transparent',
        borderColor: '#4acccd',
        pointRadius: 3,
        pointHoverRadius: 3,
        pointBorderWidth: 6,
      }]
    },
    options: chartOptions
  });
}

function cashFlowChart(label, cashFlowData) {
  var chartCanvas = document.getElementById("cashFlowChart");

  new Chart(chartCanvas, {
    type: 'bar',
    hover: false,
    data: {
      labels: label,
      datasets: [{
        data: cashFlowData,
        backgroundColor: '#fcc468',
      }]
    },
    options: chartOptions
  });
}

function activeInvestChart(label, activeInvestData) {
  var chartCanvas = document.getElementById("activeInvestChart");

  function buildDataset(data, color, label) {
    return {
      label: label,
      data: data,
      fill: false,
      backgroundColor: 'transparent',
      borderColor: color,
      pointRadius: 2,
      pointHoverRadius: 2,
      pointBorderWidth: 4,
    }
  }

  new Chart(chartCanvas, {
    type: 'line',
    hover: false,
    data: {
      labels: label,
      datasets: [
        buildDataset(activeInvestData.amartha, '#f4c0fc', 'amartha'),
        buildDataset(activeInvestData.igrow, '#fcc468', 'igrow'),
        buildDataset(activeInvestData.growpal, '#4acccd', 'growpal')
      ]
    },
    options: chartOptions
  });
}

function investIncomeChart(label, investIncomeData) {
  var chartCanvas = document.getElementById("investIncomeChart");

  new Chart(chartCanvas, {
    type: 'line',
    hover: false,
    data: {
      labels: label,
      datasets: [{
        data: investIncomeData,
        fill: false,
        backgroundColor: 'transparent',
        borderColor: '#7ed6a5',
        pointRadius: 3,
        pointHoverRadius: 3,
        pointBorderWidth: 6,
      }]
    },
    options: chartOptions
  });
}