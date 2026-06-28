package com.caobolun.bootstrap.rag.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 记忆配置校验器
 * 校验摘要相关配置的合理性
 */
public class MemoryConfigValidator implements ConstraintValidator<ValidMemoryConfig, MemoryProperties> {

    /**
     * 校验方法
     * @param properties 需要校验的配置属性
     * @param context 校验上下文对象，用于自定义错误提示、关闭默认提示、自定义违规信息
     *
     * @return 校验结果，true表示校验通过，false表示校验失败
     */
    @Override
    public boolean isValid(MemoryProperties properties, ConstraintValidatorContext context) {
        // 如果配置属性为空，则不进行校验，返回true
        if(properties == null){
            return true;
        }
        // 如果启用了摘要功能，则需要校验摘要相关配置的合理性
        if(Boolean.TRUE.equals(properties.getSummaryEnabled())){
            Integer summaryStartTurns = properties.getSummaryStartTurns();
            Integer historyKeepTurns = properties.getHistoryKeepTurns();

            // 摘要触发轮数必须大于保留轮数
            if(summaryStartTurns <= historyKeepTurns){
                // 关闭框架默认违规提示，这里要动态拼接两个配置的实际数值，所以禁用默认提示模板，完全自定义错误文案
                context.disableDefaultConstraintViolation();
                // 构建自定义错误提示
                context.buildConstraintViolationWithTemplate(
                        String.format(
                                "当开启摘要功能的时候，历史对话保留轮数 HistoryKeepTurns : %d 必须小于等于开始摘要轮数 SummaryStartTurns : %d,"+
                                        "否则将永远不会触发校验，建议配置至少: summaryStartTurns = historyKeepTurns + 1",
                                historyKeepTurns, summaryStartTurns)
                ).addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
