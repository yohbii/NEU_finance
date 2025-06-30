import pandas as pd

def calculate_sma(price_series: pd.Series, window: int) -> pd.Series:
    if not isinstance(price_series, pd.Series):
        raise TypeError("价格序列必须是 pandas Series。")
    if window <= 0:
        raise ValueError("窗口期必须是正整数。")
        
    return price_series.rolling(window=window, min_periods=1).mean()

def calculate_ema(price_series: pd.Series, window: int) -> pd.Series:
    return price_series.ewm(span=window, adjust=False, min_periods=1).mean()
