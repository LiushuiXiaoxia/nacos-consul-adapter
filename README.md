# Nacos Consul Adapter (for Prometheus)

当使用Nacos作为注册中心时通过`nacos-consul-adapter`能够使prometheus自动发现Nacos中的服务

## Restrictions
这个适配器只实现了prometheus使用consul_sd_config配置时需要的http接口，具体实现的接口如下：
- /v1/agent/self 返回默认的datacenter
- /v1/catalog/services 返回nacos中的服务列表
- /v1/catalog/service/{service} 返回服务实例
- /v1/health/service/{service} 返回服务健康状态

## Requirements

兼容高版本

- Java 1.8+
- SpringBoot: 2.2.1.RELEASE
- SpringCloud: Hoxton.SR8
- Prometheus: 2.20.1

## Prometheus
在prometheus配置文件中使用`consul_sd_configs`配置adapter地址

```
- job_name: 'nacos-prometheus'
  metrics_path: '/actuator/prometheus'
  consul_sd_configs:
  - server: 'localhost:8080'
    services: []
```

## 参考项目

[eureka-consule-adapter](https://github.com/twinformatics/eureka-consul-adapter)

[Consul catalog Api](https://www.consul.io/api-docs/catalog)

[Consul health Api](https://www.consul.io/api-docs/health)

[xuande/nacos-consul-adapter](https://github.com/xuande/nacos-consul-adapter)