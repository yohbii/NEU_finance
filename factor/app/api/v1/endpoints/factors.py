from fastapi import APIRouter, HTTPException
from models.factor import DerivativeFactorCreateRequest, FactorCreateResponse
from services import factor_service

router = APIRouter()

@router.post("/derivative", response_model=FactorCreateResponse)
def create_derivative_factor(
    *,
    factor_data: DerivativeFactorCreateRequest
):
    try:
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
        raise HTTPException(status_code=400, detail=str(e))