package com.caobolun.framework.mq;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

/**
 * 消息包装类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageWrapper<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务 key
     */
    private String keys;

    /**
     * 业务载荷
     */
    private T body;

    /**
     * 唯一标识，用于客户端幂等验证
     */
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();

    /**
     * 消息发送时间
     */
    @Builder.Default
    private Long timestamp = System.currentTimeMillis();
}
