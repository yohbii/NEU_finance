from fastapi import APIRouter
from app.api.v1.endpoints import factors, strategies, tasks

api_router = APIRouter()

api_router.include_router(factors.router, prefix="/factors", tags=["Factors"])

api_router.include_router(strategies.router, prefix="/strategies", tags=["Strategies"])

api_router.include_router(tasks.router, prefix="/tasks", tags=["Tasks"])
