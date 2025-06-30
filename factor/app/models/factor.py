from pydantic import BaseModel, Field
from typing import List, Dict


# factor基类
class BaseFactorInfo(BaseModel):
    factor_id: str = Field(..., description="基础因子的唯一标识符")
    weight: float = Field(..., description="该因子在合成时所占的权重")

class DerivativeFactorCreateRequest(BaseModel):
    new_factor_name: str = Field(..., description="新创建的衍生因子的名称")
    description: str = Field("", description="对这个因子的描述")
    base_factors: List[BaseFactorInfo] = Field(..., description="用于合成的基础因子列表及其权重")

class FactorCreateResponse(BaseModel):
    status: str = "success"
    factor_id: str
    message: str
