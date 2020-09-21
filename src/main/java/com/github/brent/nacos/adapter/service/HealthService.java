/**
 * The MIT License
 * Copyright (c) 2019 Brent
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.github.brent.nacos.adapter.service;

import com.github.brent.nacos.adapter.data.ChangeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import rx.Single;

import java.util.*;
import java.util.function.Supplier;

/**
 * Returns Services and List of Service with its last changed
 */
@Component
public class HealthService {

    @Autowired
    private DiscoveryClient discoveryClient;

    public Single<ChangeItem<List<Map<String, Object>>>> getServiceHealth(String appName, long waitMillis, Long index) {
        return returnDeferred(waitMillis, index, () -> {
            List<ServiceInstance> instances = discoveryClient.getInstances(appName);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

            if (instances == null) {
                return Collections.emptyList();
            } else {
                Set<ServiceInstance> instSet = new HashSet<ServiceInstance>(instances);
                for (ServiceInstance instance : instSet) {
                    Map<String, Object> ipObj = new HashMap<String, Object>();
                    ipObj.put("Node", node(instance));
                    ipObj.put("Service", service(instance));
                    ipObj.put("Checks", checks(instance));
                    list.add(ipObj);
                }
                return list;
            }
        });
    }

    private Map<String, Object> node(ServiceInstance instance) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ID", instance.getInstanceId());
        map.put("Notes", "");
        map.put("Address", instance.getHost());
        return map;
    }

    private Map<String, Object> service(ServiceInstance instance) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("ID", instance.getInstanceId());
        map.put("Service", instance.getServiceId());
        map.put("Address", instance.getHost());
        map.put("Port", instance.getPort());
        return map;
    }

    private List<Map<String, Object>> checks(ServiceInstance instance) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Node", instance.getInstanceId());
        map.put("Service", "");
        map.put("Address", instance.getHost());

        return Arrays.asList(map);
    }

    private <T> Single<ChangeItem<T>> returnDeferred(long waitMillis, Long index, Supplier<T> fn) {
        return Single.just(new ChangeItem<>(fn.get(), new Date().getTime()));
    }
}
