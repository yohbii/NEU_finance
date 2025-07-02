from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Any, Dict
from app.core.runner import run_strategy_code

router = APIRouter()

class StrategyRequest(BaseModel):
    code: str  # 策略Python脚本字符串
    params: Dict[str, Any] = {}
    symbol: str = "MOCK"
    start_date: str = "2023-01-01"
    end_date: str = "2023-12-31"
    initial_capital: float = 1000000.0

@router.post("/run")
def run_strategy(request: StrategyRequest):
    try:
        result = run_strategy_code(
            code=request.code,
            params=request.params,
            symbol=request.symbol,
            start_date=request.start_date,
            end_date=request.end_date,
            initial_capital=request.initial_capital
        )
        return {"success": True, "result": result}
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e)) 