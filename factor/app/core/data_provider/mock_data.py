import pandas as pd
import numpy as np
from datetime import date, timedelta

def get_price_data(
    symbol: str, 
    start_date: date, 
    end_date: date,
    initial_price: float = 100.0
) -> pd.DataFrame:
    print(f"数据提供器: 为 {symbol} 生成从 {start_date} 到 {end_date} 的模拟数据...")
    
    date_range = pd.date_range(start=start_date, end=end_date, freq='B') # 'B' for business day
    n_days = len(date_range)
    
    price_changes = 1 + np.random.randn(n_days) * 0.02  # 每日价格波动
    close_prices = initial_price * price_changes.cumprod()
    
    data = pd.DataFrame(index=date_range)
    data['close'] = close_prices
    data['open'] = data['close'] * (1 - np.random.uniform(-0.01, 0.01, n_days))
    data['high'] = data[['open', 'close']].max(axis=1) * (1 + np.random.uniform(0, 0.01, n_days))
    data['low'] = data[['open', 'close']].min(axis=1) * (1 - np.random.uniform(0, 0.01, n_days))
    
    data['volume'] = np.random.randint(1_000_000, 10_000_000, n_days)
    
    data['high'] = data[['high', 'open', 'close']].max(axis=1)
    data['low'] = data[['low', 'open', 'close']].min(axis=1)
    
    print("数据提供器: 数据生成完毕。")
    return data[['open', 'high', 'low', 'close', 'volume']]
