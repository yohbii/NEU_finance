from fastapi import APIRouter, HTTPException
from app.models.factor import DerivativeFactorCreateRequest, FactorCreateResponse
from app.services import factor_service

router = APIRouter()

@router.post("/derivative", response_model=FactorCreateResponse)
def create_derivative_factor(
    *,
    factor_data: DerivativeFactorCreateRequest
):
    try:
        # 调用服务层的函数来处理业务逻辑
        # 将API层和业务逻辑层分离是一个好的实践
        new_factor_id = factor_service.create_new_derivative_factor(
            name=factor_data.new_factor_name,
            description=factor_data.description,
            base_factors_info=factor_data.base_factors
        )
        return {
            "status": "success",
            "factor_id": new_factor_id,
            "message": f"Derivative factor '{factor_data.new_factor_name}' created successfully."
        }
    except Exception as e:
        # 如果服务层出现任何错误，捕获异常并返回一个标准的HTTP错误响应
        raise HTTPException(status_code=400, detail=str(e))