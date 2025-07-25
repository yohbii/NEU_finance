import pandas as pd

def calculate_sma(price_series: pd.Series, window: int) -> pd.Series:
    result = price_series.rolling(window=window, min_periods=1).mean()
    if isinstance(result, pd.DataFrame):
        # 取第一个列（通常不会出现，但防御性处理）
        result = result.iloc[:, 0]
    if not isinstance(result, pd.Series):
        result = pd.Series(result, index=price_series.index)
    return result

def calculate_ema(price_series: pd.Series, window: int) -> pd.Series:
    result = price_series.ewm(span=window, adjust=False, min_periods=1).mean()
    if isinstance(result, pd.DataFrame):
        result = result.iloc[:, 0]
    if not isinstance(result, pd.Series):
        result = pd.Series(result, index=price_series.index)
    return result

def calculate_rsi(price_series: pd.Series, window: int = 14) -> pd.Series:
    delta = price_series.diff()
    gain = delta.clip(lower=0)
    loss = -delta.clip(upper=0)
    
    avg_gain = gain.ewm(com=window - 1, min_periods=window).mean()
    avg_loss = loss.ewm(com=window - 1, min_periods=window).mean()
    
    rs = avg_gain / avg_loss
    rsi = 100 - (100 / (1 + rs))
    if isinstance(rsi, pd.DataFrame):
        rsi = rsi.iloc[:, 0]
    if not isinstance(rsi, pd.Series):
        rsi = pd.Series(rsi, index=price_series.index)
    return rsi

def calculate_macd(price_series: pd.Series, fast_window: int = 12, slow_window: int = 26, signal_window: int = 9):
    ema_fast = calculate_ema(price_series, window=fast_window)
    ema_slow = calculate_ema(price_series, window=slow_window)
    
    macd_line = ema_fast - ema_slow
    signal_line = calculate_ema(macd_line, window=signal_window)
    histogram = macd_line - signal_line
    
    return macd_line, signal_line, histogram