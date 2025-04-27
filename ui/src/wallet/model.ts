export type WalletDashboardData = {
  chart: WalletChart;
  savings: WalletSavings;
  planned: WalletPlanned;
  detail: WalletDetail[];
}

export type WalletChart = {
  balance: WalletChartBalance[];
  last_year_expenses: Map<string, number>;
  ytd_expenses: Map<string, number>;
  pie: string
}

export type WalletChartBalance = {
  date: number;
  sum: number;
}

export type WalletSavings = {
  dbs: number;
  bca: number;
}

export type WalletPlanned = {
  sgd: number;
  idr: number;
}

export type WalletDetail = {
  id: number;
  date: number;
  name: string;
  category: string;
  currency: string;
  amount: number;
  done: boolean;
  account: string;
}

export type WalletPortoData = {
  stocks: WalletStock[]
}

export type WalletStock = {
  id: string,
  current_price: number
  share: number
  liability: number
  equity: number
  net_profit_current_year: number
  net_profit_previous_year: number
  eip_best_buy?: number
  eip_rating?: string
  eip_risks?: string
}

export type WalletAlert = {
  display: string
  text: string
}