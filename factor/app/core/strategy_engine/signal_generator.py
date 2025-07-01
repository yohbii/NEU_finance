import numpy as np
import pandas as pd
from app.core.factor_engine import calculator

def generate_sma_crossover_signal(price_df: pd.DataFrame, params: dict) -> pd.Series:
    fast_sma = calculator.calculate_sma(price_df['close'], window=params['fast_window'])
    slow_sma = calculator.calculate_sma(price_df['close'], window=params['slow_window'])
    
    signal = pd.Series(np.where(fast_sma > slow_sma, 1, 0), index=price_df.index)
    return signal