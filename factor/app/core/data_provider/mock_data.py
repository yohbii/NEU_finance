import pandas as pd
import numpy as np
from datetime import date, timedelta
import tushare as ts

ts.set_token('52ac0b64061be1aaa289b0f19a34cac82aea6c4d01869d36b26f866b')

def get_price_data(
    symbol: str, 
    start_date: date, 
    end_date: date,
    initial_price: float = 100.0
) -> pd.DataFrame:
    pro = ts.pro_api()
    print(f"从Tushare获取数据: 获取 {symbol} 从 {start_date} 到 {end_date} 的数据")
    df = pro.daily(ts_code=symbol, start_date=start_date.strftime('%Y%m%d'), end_date=end_date.strftime('%Y%m%d'))
    
    # date_range = pd.date_range(start=start_date, end=end_date, freq='B') # 'B' for business day
    # n_days = len(date_range)
    
    # price_changes = 1 + np.random.randn(n_days) * 0.02  # 每日价格波动
    # close_prices = initial_price * price_changes.cumprod()
    
    # data = pd.DataFrame(index=date_range)
    # data['close'] = close_prices
    # data['open'] = data['close'] * (1 - np.random.uniform(-0.01, 0.01, n_days))
    # data['high'] = data[['open', 'close']].max(axis=1) * (1 + np.random.uniform(0, 0.01, n_days))
    # data['low'] = data[['open', 'close']].min(axis=1) * (1 - np.random.uniform(0, 0.01, n_days))
    
    # data['volume'] = np.random.randint(1_000_000, 10_000_000, n_days)
    
    # data['high'] = data[['high', 'open', 'close']].max(axis=1)
    # data['low'] = data[['low', 'open', 'close']].min(axis=1)
    
    # print("数据提供器: 数据生成完毕。")
    # result = data[['open', 'high', 'low', 'close', 'volume']]
    # if isinstance(result, pd.Series):
    #     result = result.to_frame().T
    return df
