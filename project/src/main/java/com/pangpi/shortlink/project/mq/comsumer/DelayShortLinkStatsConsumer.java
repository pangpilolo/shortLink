/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pangpi.shortlink.project.mq.comsumer;

import com.pangpi.shortlink.convention.exception.ServiceException;
import com.pangpi.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.pangpi.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.pangpi.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import static com.pangpi.shortlink.project.common.constant.RedisKeyConstant.DELAY_QUEUE_STATS_KEY;


/**
 * 延迟记录短链接统计组件
 * 公众号：马丁玩编程，回复：加群，添加马哥微信（备注：link）获取项目资料
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DelayShortLinkStatsConsumer implements InitializingBean {

    private final RedissonClient redissonClient;
    private final ShortLinkService shortLinkService;
    private final MessageQueueIdempotentHandler queueIdempotentHandler;

    public void onMessage() {
        Executors.newSingleThreadExecutor(
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("delay_short-link_stats_consumer");
                    thread.setDaemon(true);
                    return thread;
                }
        ).execute(() -> {
            RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(DELAY_QUEUE_STATS_KEY);
            RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            for (; ; ) {
                try {
                    ShortLinkStatsRecordDTO message = delayedQueue.poll();
                    if (message != null) {
                        String keys = message.getKeys();
                        if (queueIdempotentHandler.isMessageBeingConsumed(keys)) {
                            if (queueIdempotentHandler.isAccomplish(keys)) {
                                return;
                            } else {
                                throw new ServiceException("消息未完成流程，需要消息队列重试");
                            }
                        }
                        try {
                            // 消费消息逻辑，待处理
                            shortLinkService.shortLinkStats(message);
                        } catch (Throwable throwable) {
                            queueIdempotentHandler.delMessageProcessed(keys);
                        }
                        queueIdempotentHandler.setAccomplish(keys);
                    }
                    // 如果消息轮询时没拿到，则休息0.5秒
                    LockSupport.parkUntil(500);
                } catch (Throwable throwable) {
                }
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // onMessage();
    }
}
