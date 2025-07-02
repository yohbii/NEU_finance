import pandas as pd
import numpy as np
from datetime import datetime
from app.core.data_provider.mock_data import get_price_data
from app.utils.sandbox import run_user_code

def run_strategy_code(code: str, params: dict, symbol: str, start_date: str, end_date: str, initial_capital: float):
    price_df = get_price_data(symbol, pd.to_datetime(start_date), pd.to_datetime(end_date))
    # 2. 沙箱执行用户策略，期望返回信号（Series: 1买入, 0空仓, -1卖出）
    signal = run_user_code(code, price_df, params)
    capital = initial_capital
    position = 0
    cash = capital
    equity_curve = []
    for date, row in price_df.iterrows():
        if signal.loc[date] == 1 and position == 0:
            position = cash / row['close']
            cash = 0
        elif signal.loc[date] == -1 and position > 0:
            cash = position * row['close']
            position = 0
        equity = cash + position * row['close']
        equity_curve.append(equity)
    equity_curve = np.array(equity_curve)
    # 4. 指标计算
    returns = pd.Series(equity_curve).pct_change().fillna(0)
    cumulative_return = equity_curve[-1] / capital - 1
    annualized_return = (1 + cumulative_return) ** (252 / len(price_df)) - 1
    max_drawdown = ((np.maximum.accumulate(equity_curve) - equity_curve) / np.maximum.accumulate(equity_curve)).max()
    sharpe_ratio = returns.mean() / (returns.std() + 1e-8) * np.sqrt(252)
    return {
        "cumulative_return": float(cumulative_return),
        "annualized_return": float(annualized_return),
        "max_drawdown": float(max_drawdown),
        "sharpe_ratio": float(sharpe_ratio),
        "equity_curve": equity_curve.tolist(),
        "dates": [date.strftime('%Y-%m-%d') for date in price_df.index]
    }