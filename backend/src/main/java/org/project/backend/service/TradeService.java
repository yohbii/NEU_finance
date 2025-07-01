package org.project.backend.service;

import org.project.backend.DTO.HttpResponse;
import org.project.backend.entity.TradeRebalanceTask;
import org.project.backend.entity.TradeOrder;
import org.project.backend.entity.TradeOrderError;
import org.project.backend.entity.TradeDelivery;
import org.project.backend.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

/**
 * 交易管理Service
 */
@Service
public class TradeService {

    @Autowired
    private TradeRebalanceTaskMapper tradeRebalanceTaskMapper;
    @Autowired
    private TradeOrderMapper tradeOrderMapper;
    @Autowired
    private TradeOrderErrorMapper tradeOrderErrorMapper;
    @Autowired
    private TradeDeliveryMapper tradeDeliveryMapper;

    // ========================= 1. 组合调仓相关 =========================

    /**
     * 创建组合调仓任务（批量或单户）
     */
    @Transactional
    public HttpResponse<Long> createRebalanceTask(TradeRebalanceTask task, List<TradeOrder> orderList) {
        int taskRes = tradeRebalanceTaskMapper.insert(task);
        if (taskRes <= 0 || task.getId() == null) {
            return new HttpResponse<>(-1, null, "调仓任务创建失败", "插入调仓任务失败");
        }
        // 批量创建调仓交易单
        if (orderList != null && !orderList.isEmpty()) {
            for (TradeOrder order : orderList) {
                order.setTaskId(task.getId());
                order.setStatus("INIT");
                order.setCreateTime(new Date());
                order.setUpdateTime(new Date());
                int orderRes = tradeOrderMapper.insert(order);
                if (orderRes <= 0) {
                    return new HttpResponse<>(-1, null, "调仓交易单生成失败", "部分交易单插入失败");
                }
            }
        }
        return new HttpResponse<>(0, task.getId(), "ok", null);
    }

    public HttpResponse<TradeRebalanceTask> findRebalanceTaskById(Long id) {
        TradeRebalanceTask task = tradeRebalanceTaskMapper.selectById(id);
        if (task == null) {
            return new HttpResponse<>(-1, null, "调仓任务不存在", "未找到指定任务");
        }
        return new HttpResponse<>(0, task, "ok", null);
    }

    public HttpResponse<List<TradeRebalanceTask>> findRebalanceTaskByCombo(Long comboId) {
        List<TradeRebalanceTask> list = tradeRebalanceTaskMapper.selectByComboId(comboId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无调仓任务", "指定组合尚无调仓任务");
        }
        return new HttpResponse<>(0, list, "ok", null);
    }


    // ========================= 2. 交易单管理 =========================

    public HttpResponse<List<TradeOrder>> findOrdersByTaskId(Long taskId) {
        List<TradeOrder> orders = tradeOrderMapper.selectByTaskId(taskId);
        if (orders == null || orders.isEmpty()) {
            return new HttpResponse<>(-1, null, "没有查到调仓交易单", "无交易单记录");
        }
        return new HttpResponse<>(0, orders, "ok", null);
    }

    public HttpResponse<List<TradeOrder>> findOrdersByAccount(Long accountId) {
        List<TradeOrder> orders = tradeOrderMapper.selectByAccountId(accountId);
        if (orders == null || orders.isEmpty()) {
            return new HttpResponse<>(-1, null, "该账户无交易单", "未发现相关交易单");
        }
        return new HttpResponse<>(0, orders, "ok", null);
    }

    @Transactional
    public HttpResponse<Void> updateOrderStatus(Long orderId, String status, String failReason) {
        TradeOrder order = tradeOrderMapper.selectById(orderId);
        if (order == null) {
            return new HttpResponse<>(-1, null, "未找到交易单", null);
        }
        order.setStatus(status);
        order.setFailReason(failReason);
        order.setUpdateTime(new Date());
        int updated = tradeOrderMapper.update(order);
        return updated > 0 ?
                new HttpResponse<>(0, null, "ok", null) :
                new HttpResponse<>(-1, null, "交易单状态更新失败", null);
    }

    // ========================= 3. 差错处理/重下单 =========================

    @Transactional
    public HttpResponse<Long> recordOrderError(TradeOrderError error) {
        int res = tradeOrderErrorMapper.insert(error);
        if (res > 0 && error.getId() != null) {
            return new HttpResponse<>(0, error.getId(), "ok", null);
        }
        return new HttpResponse<>(-1, null, "提交差错处理记录失败", null);
    }

    public HttpResponse<List<TradeOrderError>> findErrorsByOrder(Long orderId) {
        List<TradeOrderError> errors = tradeOrderErrorMapper.selectByOrderId(orderId);
        if (errors == null || errors.isEmpty()) {
            return new HttpResponse<>(-1, null, "无差错记录", "本单暂无差错记录");
        }
        return new HttpResponse<>(0, errors, "ok", null);
    }

    @Transactional
    public HttpResponse<Void> processOrderError(Long errorId, String processStatus, String newFundCode) {
        TradeOrderError error = tradeOrderErrorMapper.selectById(errorId);
        if (error == null) {
            return new HttpResponse<>(-1, null, "差错记录不存在", null);
        }
        error.setProcessStatus(processStatus);
        error.setNewFundCode(newFundCode);
        error.setUpdateTime(new Date());
        int result = tradeOrderErrorMapper.update(error);
        return result > 0 ? new HttpResponse<>(0, null, "ok", null) : new HttpResponse<>(-1, null, "处理失败", null);
    }

    // ========================= 4. 交割单管理 =========================

    public HttpResponse<List<TradeDelivery>> findDeliveriesByOrder(Long orderId) {
        List<TradeDelivery> list = tradeDeliveryMapper.selectByOrderId(orderId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "无交割记录", null);
        }
        return new HttpResponse<>(0, list, "ok", null);
    }

    public HttpResponse<List<TradeDelivery>> findDeliveriesByAccount(Long accountId) {
        List<TradeDelivery> list = tradeDeliveryMapper.selectByAccountId(accountId);
        if (list == null || list.isEmpty()) {
            return new HttpResponse<>(-1, null, "该账户无交割", null);
        }
        return new HttpResponse<>(0, list, "ok", null);
    }

    @Transactional
    public HttpResponse<Long> insertDelivery(TradeDelivery delivery) {
        int res = tradeDeliveryMapper.insert(delivery);
        if (res > 0 && delivery.getId() != null) {
            return new HttpResponse<>(0, delivery.getId(), "ok", null);
        }
        return new HttpResponse<>(-1, null, "交割记录入库失败", null);
    }

    // ================== 一键下单/回撤（仅供展示实现思路） ==================
    // 一键下单：遍历某任务所有INIT单改为“已下单”，可扩展为实际发送至后台投顾/撮合系统
    @Transactional
    public HttpResponse<Void> batchSubmitTrade(Long taskId) {
        List<TradeOrder> orders = tradeOrderMapper.selectByTaskId(taskId);
        if (orders == null || orders.isEmpty()) {
            return new HttpResponse<>(-1, null, "无匹配交易单", null);
        }
        for (TradeOrder order : orders) {
            if ("INIT".equals(order.getStatus())) {
                order.setStatus("SUBMITTED");
                order.setUpdateTime(new Date());
                tradeOrderMapper.update(order);
            }
        }
        return new HttpResponse<>(0, null, "全部已提交", null);
    }

    // 一键回撤：任务下所有“未成交”状态设为“ROLLBACK”
    @Transactional
    public HttpResponse<Void> batchRollback(Long taskId) {
        List<TradeOrder> orders = tradeOrderMapper.selectByTaskId(taskId);
        if (orders == null || orders.isEmpty()) {
            return new HttpResponse<>(-1, null, "无匹配交易单", null);
        }
        for (TradeOrder order : orders) {
            if (!"DONE".equals(order.getStatus())) {
                order.setStatus("ROLLBACK");
                order.setUpdateTime(new Date());
                tradeOrderMapper.update(order);
            }
        }
        return new HttpResponse<>(0, null, "操作完成", null);
    }
}