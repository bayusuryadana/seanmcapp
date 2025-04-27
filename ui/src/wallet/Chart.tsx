import { useTheme } from '@mui/material/styles';
import { LineChart, Line, XAxis, YAxis, Label, ResponsiveContainer } from 'recharts';
import { Title } from './Title';
import { WalletChartBalance } from './model';
import { Fragment } from 'react';

interface ChartProps {
  data: WalletChartBalance[]
}

export default function Chart(props: ChartProps) {

  const theme = useTheme();

  return (
    <Fragment>
      <Title>Balance</Title>
      <ResponsiveContainer>
        <LineChart
          data={props.data.sort((a,b) => a.date-b.date)}
          margin={{
            top: 16,
            right: 16,
            bottom: 0,
            left: 24,
          }}
        >
          <XAxis
            dataKey="date"
            stroke={theme.palette.text.secondary}
            style={theme.typography.body2}
          />
          <YAxis
            dataKey="sum"
            stroke={theme.palette.text.secondary}
            style={theme.typography.body2}
          >
            <Label
              angle={270}
              position="left"
              style={{
                textAnchor: 'middle',
                fill: theme.palette.text.primary,
                ...theme.typography.body1,
              }}
            >
              Balance (S$)
            </Label>
          </YAxis>
          <Line
            isAnimationActive={false}
            type="monotone"
            dataKey="sum"
            stroke={theme.palette.primary.main}
            dot={false}
          />
        </LineChart>
      </ResponsiveContainer>
    </Fragment>
  );
}