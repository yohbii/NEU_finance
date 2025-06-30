# app/api/v1/endpoints/tasks.py
# 这个模块提供用于查询异步任务状态的API。

from fastapi import APIRouter, HTTPException
from typing import Any

from app.models.strategy import TaskStatusResponse
from app.services.backtest_service import TASKS_DB

router = APIRouter()

@router.get("/{task_id}", response_model=TaskStatusResponse)
def get_task_status(task_id: str):
    task = TASKS_DB.get(task_id)
    if not task:
        raise HTTPException(status_code=404, detail="任务未找到")
    
    return task