import pandas as pd
import uuid
from typing import List
from app.models.factor import BaseFactorInfo

# create new factor service
def create_new_derivative_factor(name: str, description: str, base_factors_info: List[BaseFactorInfo]) -> str:
    print(f"开始创建衍生因子: {name}")

    total_weight = sum(info.weight for info in base_factors_info)
    if not (0.999 < total_weight < 1.001): # 考虑浮点数精度
        # raise ValueError("Weights of base factors must sum to 1.")
        print(f"Warning: Weights sum to {total_weight}, not 1.")

    base_factor_data = {}
    for factor_info in base_factors_info:
        print(f"  - 加载基础因子 '{factor_info.factor_id}' 的数据...")
        # 模拟加载数据，生成一个pandas Series
        base_factor_data[factor_info.factor_id] = pd.Series(
            data=[(i % 10) * factor_info.weight for i in range(100)],
            index=pd.to_datetime(pd.date_range(start='2022-01-01', periods=100))
        )

    derivative_series = pd.Series(0.0, index=base_factor_data[base_factors_info[0].factor_id].index)
    for factor_info in base_factors_info:
        derivative_series += base_factor_data[factor_info.factor_id] * factor_info.weight

    print("衍生因子计算完成:")
    print(derivative_series.head())

    new_factor_id = f"factor_{uuid.uuid4()}"
    print(f"新因子已保存，ID为: {new_factor_id}")

    return new_factor_id