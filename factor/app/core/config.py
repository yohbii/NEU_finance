import os
from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    PROJECT_NAME: str = "Smart Investment Advisor Quant Engine"
    API_V1_STR: str = "/api/v1"

    DATA_SOURCE_TYPE: str = "mock" # 'mock' 或 'database'

    DEFAULT_INITIAL_CAPITAL: float = 1_000_000.0

    class Config:
        case_sensitive = True
        env_file = ".env"

settings = Settings()