# AI Stack Demo Kubernetes 部署指南

本指南说明如何在K3S上部署AI Stack Demo应用。

## 前提条件

- 已安装K3S集群
- kubectl已配置正确

## 部署步骤

1. 创建命名空间和部署所有资源：

```bash
kubectl apply -k ./k8s
```

2. 检查部署状态：

```bash
kubectl get pods -n ai-stack-demo
kubectl get services -n ai-stack-demo
kubectl get ingress -n ai-stack-demo
```

3. 获取访问URL：

```bash
echo "应用访问地址: http://$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}')"
```

## 应用架构

- 前端: Vue.js应用，通过Nginx服务
- 后端: Spring Boot应用，提供REST API
- 所有服务通过Ingress暴露

## 卸载应用

```bash
kubectl delete -k ./k8s
``` 