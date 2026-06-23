package com.caobolun.framework.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SSE发送器封装类
 */
@Slf4j
public class SSEEmitterSender {

    /**
     *  发送器实例
     */
    private final SseEmitter emitter;

    /**
     * 连接关闭状态标识，使用原子布尔类型保证线程安全
     * true 表示连接已关闭，false 表示连接仍然活跃
     */
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * Spring的SseEmitter实例，用于SSE通信
     * @param emitter
     */
    public SSEEmitterSender(SseEmitter emitter){
        this.emitter = emitter;
        // 链接完成时，调用close设置为true
        emitter.onCompletion(() -> closed.set(true));
        // 链接超时时，调用close设置为true
        emitter.onTimeout(() -> closed.set(true));
        // 链接报错时，调用close设置为true
        emitter.onError(e -> closed.set(true));
    }

    /**
     * 发送消息
     * @param eventName
     * @param data
     */
    public void sendEvent(String eventName, Object data){
        if(closed.get()){
            return;
        }
        try{
            if(eventName == null){
                emitter.send(data);
                return;
            }
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (Exception e){
            fail(e);
        }
    }


    /**
     * 正常完成并关闭SSE
     */
    public void complete(){
        if(closed.compareAndExchange(false, true)){
            emitter.complete();
        }
    }

    /**
     *
     * @param throwable
     */
    public void fail(Throwable throwable){
        closeWithError(throwable);
        log.warn("SSE 发送失败", throwable);
    }

    /**
     * 以异常方式关闭链接
     * @param throwable
     */
    private void closeWithError(Throwable throwable){
        // 使用原子操作，确保只关闭一次
        if(closed.compareAndExchange(false, true)){
            emitter.completeWithError(throwable);
        }
    }

}
