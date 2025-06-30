import time
import uuid
import random
from datetime import datetime
from typing import Dict, Any

from app.models.strategy import BacktestRequest, BacktestMetrics

TASKS_DB: Dict[str, Dict[str, Any]] = {}

def run_backtest_simulation(task_id: str, request: BacktestRequest):
    print(f"任务 {task_id}: 开始执行回测，策略ID为 {request.strategy_id}")
    
    TASKS_DB[task_id]["status"] = "running"
    TASKS_DB[task_id]["started_at"] = datetime.now()

    try:
        time.sleep(15) 

        if random.random() < 0.9: # 90%的概率成功
            result_metrics = BacktestMetrics(
                annualized_return=random.uniform(0.05, 0.25),
                cumulative_return=random.uniform(0.1, 0.8),
                max_drawdown=random.uniform(-0.3, -0.05),
                sharpe_ratio=random.uniform(0.5, 2.5),
                calmar_ratio=random.uniform(0.2, 1.5)
            )
            TASKS_DB[task_id]["status"] = "success"
            TASKS_DB[task_id]["result"] = result_metrics.dict()
            print(f"任务 {task_id}: 回测成功")
        else: 
            raise ValueError("模拟错误：无法加载所需数据")

    except Exception as e:
        error_message = f"回测执行失败: {e}"
        TASKS_DB[task_id]["status"] = "failed"
        TASKS_DB[task_id]["error_message"] = error_message
        print(f"任务 {task_id}: {error_message}")
    
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
    
    background_tasks.add_task(run_backtest_simulation, task_id, request)
    
    print(f"已提交新任务，ID为: {task_id}")
    return task_id