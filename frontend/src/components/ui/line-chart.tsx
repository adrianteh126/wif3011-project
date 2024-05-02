import React from 'react'
import { Line } from 'react-chartjs-2'
import { Chart, CategoryScale, LinearScale, PointElement, LineElement } from "chart.js"
import type { ChartData, ChartOptions } from 'chart.js'
import 'chart.js/auto';
import ChartDataLabels from "chartjs-plugin-datalabels"

interface LineProps {
    options?: ChartOptions<'line'>
    data: ChartData<'line'>
}

Chart.register(
    LinearScale, 
    CategoryScale,
    PointElement,
    LineElement,
    ChartDataLabels
);

const options : ChartOptions<'line'> = {
    plugins: {
      legend: {
        display: true
      },
      title: {
        display: true,
        text: "Elapsed time of each Algorithm",
        color: "#000000",
        font: {
          size: 13
        },
        align: "start"
      },
      datalabels: {
        display: true,
        color: "black"
      }
    }
  }

const LineChart = ({ data } : LineProps) => {
    return (
        <Line data={data} options={options} plugins={[ChartDataLabels]}/>
    )
}

export { LineChart }