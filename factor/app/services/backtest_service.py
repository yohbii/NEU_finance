import pandas as pd
import uuid
from datetime import datetime
from typing import Dict, Any

from app.models.strategy import BacktestRequest, BacktestMetrics
from app.core.strategy_engine import backtester, metrics, signal_generator

TASKS_DB: Dict[str, Dict[str, Any]] = {}

def run_real_backtest(task_id: str, request: BacktestRequest):
    print(f"任务 {task_id}: 开始执行回测，策略为 '{request.strategy_name}'")
    TASKS_DB[task_id]["status"] = "running"
    TASKS_DB[task_id]["started_at"] = datetime.now()

    try:
        price_list = [p.dict() for p in request.price_data]
        price_df = pd.DataFrame(price_list)
        price_df['date'] = pd.to_datetime(price_df['date'])
        price_df.set_index('date', inplace=True)
        
        if request.strategy_name == 'sma_crossover':
            signal = signal_generator.generate_sma_crossover_signal(price_df, request.strategy_params)
        else:
            raise NotImplementedError(f"策略 '{request.strategy_name}' 尚未实现")
            
        portfolio = backtester.run_vectorized_backtest(
            price_data=price_df,
            signal=signal,
            initial_capital=request.initial_capital
        )
        
        result_metrics_dict = metrics.calculate_performance_metrics(portfolio)
        result_metrics = BacktestMetrics(**result_metrics_dict)
        
        TASKS_DB[task_id]["status"] = "success"
        TASKS_DB[task_id]["result"] = result_metrics.dict()
        print(f"任务 {task_id}: 回测成功")

    except Exception as e:
        error_message = f"回测执行失败: {e}"
        TASKS_DB[task_id]["status"] = "failed"
        TASKS_DB[task_id]["error_message"] = error_message
        print(f"任务 {task_id}: {error_message}", e)
    
    finally:
        TASKS_DB[task_id]["finished_at"] = datetime.now()


def start_backtest_task(request: BacktestRequest, background_tasks) -> str:
    task_id = str(uuid.uuid4())
    TASKS_DB[task_id] = {
        "task_id": task_id,
        "status": "pending",
        "submitted_at": datetime.now(),
        "result": None,
        "error_message": None
    }
    
    background_tasks.add_task(run_real_backtest, task_id, request)
    
    print(f"已提交新任务，ID为: {task_id}")
    return task_id

