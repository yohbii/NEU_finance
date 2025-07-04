import pandas as pd
import numpy as np
import traceback
import datetime as dt
import math
from typing import Union
import re

def run_user_code(code: str, price_df: pd.DataFrame, params: dict) -> pd.Series:
    if not isinstance(code, str) or not code.strip():
        raise ValueError('代码必须是非空字符串')
    
    if not isinstance(price_df, pd.DataFrame):
        raise TypeError('price_df 必须是 pandas.DataFrame 类型')
    
    if price_df.empty:
        raise ValueError('price_df 不能为空')
    
    if not isinstance(params, dict):
        raise TypeError('params 必须是字典类型')
    
    try:
        local_env = {}
        try:
            compiled_code = compile(code, '<string>', 'exec')
            exec(compiled_code, local_env, local_env)
        except (SyntaxError, NameError) as e:
            raise ValueError(f'代码编译或执行失败: {str(e)}')
        # 检查是否定义了 generate_signal 函数
        if 'generate_signal' not in local_env:
            raise ValueError('策略代码中必须定义 generate_signal(price_df, params) 函数')
        generate_signal_func = local_env['generate_signal']
        if not callable(generate_signal_func):
            raise ValueError('generate_signal 必须是一个可调用的函数')
        
        # 调用用户函数
        try:
            signal = generate_signal_func(price_df, params)
        except Exception as e:
            raise ValueError(f'generate_signal 函数执行出错: {str(e)}')
        
        # 验证返回值
        if signal is None:
            raise ValueError('generate_signal 函数不能返回 None')
        
        if not isinstance(signal, pd.Series):
            raise ValueError('generate_signal 必须返回 pandas.Series')
        
        if signal.empty:
            raise ValueError('生成的信号序列不能为空')
        
        if not signal.index.equals(price_df.index):
            raise ValueError('信号序列索引必须与价格数据对齐')
        
        return signal
        
    except SyntaxError as e:
        raise ValueError(f'代码语法错误: {str(e)}')
    except NameError as e:
        raise ValueError(f'代码中使用了未定义的变量或函数: {str(e)}')
    except Exception as e:
        error_msg = f'代码执行出错: {str(e)}'
        if hasattr(e, '__traceback__'):
            error_msg += f'\n详细错误信息: {traceback.format_exc()}'
        raise ValueError(error_msg)