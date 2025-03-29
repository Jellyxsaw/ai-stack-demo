# AI Stack 项目 Docker 部署说明

## 项目结构

- `frontend`: Vue.js 前端项目
- `src`: Spring Boot 后端项目

## 使用 Docker 部署

### 前置条件

- 安装 [Docker](https://www.docker.com/get-started)
- 安装 [Docker Compose](https://docs.docker.com/compose/install/)

### 如何部署

1. 克隆本仓库到本地

```bash
git clone <仓库地址>
cd ai-stack-t2
```

2. 使用 Docker Compose 构建并启动容器

```bash
docker-compose up -d
```

3. 访问应用

- 前端：http://localhost
- 后端 API：http://localhost:8080
- Swagger API 文档：http://localhost:8080/swagger-ui.html

### 停止服务

```bash
docker-compose down
```

### 重新构建和启动

```bash
docker-compose up -d --build
```

### 查看日志

```bash
# 查看所有容器的日志
docker-compose logs

# 查看特定服务的日志
docker-compose logs frontend
docker-compose logs backend
docker-compose logs db

# 实时查看日志
docker-compose logs -f
```

## 容器说明

- `ai-stack-frontend`: 运行 Vue.js 前端应用，使用 Nginx 服务
- `ai-stack-backend`: 运行 Spring Boot 后端应用
- `ai-stack-db`: PostgreSQL 数据库

## 数据持久化

PostgreSQL 数据存储在 Docker 数据卷 `postgres_data` 中，确保数据不会在容器重启后丢失。 