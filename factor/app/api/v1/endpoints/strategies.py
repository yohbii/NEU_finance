from fastapi import APIRouter, BackgroundTasks
from models.strategy import BacktestRequest, TaskResponse

from services import backtest_service

router = APIRouter()

@router.post("/backtest", response_model=TaskResponse)
def run_strategy_backtest(
    *,
    request: BacktestRequest,
    background_tasks: BackgroundTasks
):
    task_id = backtest_service.start_backtest_task(
        request=request, 
        background_tasks=background_tasks
    )
    
    return TaskResponse(task_id=task_id)

