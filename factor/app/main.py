from fastapi import FastAPI
from app.api.strategy import router as strategy_router

app = FastAPI(title="智能投顾策略测试平台")

app.include_router(strategy_router, prefix="/api/strategy", tags=["StrategyTest"])

@app.get("/")
def read_root():
    return {"message": "欢迎使用智能投顾策略测试平台！"}
