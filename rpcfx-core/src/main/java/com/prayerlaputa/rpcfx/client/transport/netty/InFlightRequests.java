/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.prayerlaputa.rpcfx.client.transport.netty;

import com.prayerlaputa.rpcfx.api.RpcfxResponseFuture;
import com.prayerlaputa.rpcfx.client.Rpcfx;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 此处代码来自：https://github.com/liyue2008/simple-rpc-framework
 * 用于处理在途请求。
 *
 * @author LiYue
 * Date: 2019/9/20
 */
@Slf4j
public class InFlightRequests implements Closeable {
    private final static long TIMEOUT_SEC = 1000L;
    private final Semaphore semaphore = new Semaphore(10);
    private final Map<Long, RpcfxResponseFuture> futureMap = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;
    public InFlightRequests() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures, TIMEOUT_SEC, TIMEOUT_SEC, TimeUnit.SECONDS);
    }

    public void put(RpcfxResponseFuture responseFuture) throws InterruptedException, TimeoutException {
        if(semaphore.tryAcquire(TIMEOUT_SEC, TimeUnit.SECONDS)) {
            futureMap.put(responseFuture.getRequestId(), responseFuture);
        } else {
            throw new TimeoutException();
        }
    }

    private void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> {
            if( System.nanoTime() - entry.getValue().getTimestamp() > TIMEOUT_SEC * 1000000000L) {
                semaphore.release();
                log.info("removeTimeoutFutures remove: reqId={}.", entry.getKey());
                return true;
            } else {
                return false;
            }
        });
    }

    public RpcfxResponseFuture remove(long requestId) {
        RpcfxResponseFuture future = futureMap.remove(requestId);
        if(null != future) {
            semaphore.release();
        }
        return future;
    }

    @Override
    public void close() {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
