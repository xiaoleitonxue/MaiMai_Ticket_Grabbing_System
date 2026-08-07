# MaiMai Ticket Grabbing System（麦麦抢票系统）

## 项目简介

麦麦抢票系统是一个基于 **Spring Cloud 微服务架构** 的电商票务抢购平台。系统采用前后端分离架构，后端使用 Spring Boot + Spring Cloud Alibaba 技术栈，支持高并发场景下的商品浏览、购物车、下单、支付等完整业务流程。

---

## 技术架构

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| JDK | 11 | 运行环境 |
| Spring Boot | 2.7.12 | 基础框架 |
| Spring Cloud | 2021.0.3 | 微服务治理 |
| Spring Cloud Alibaba | 2021.0.4.0 | 微服务生态（Nacos、Sentinel、Seata） |
| Spring Cloud Gateway | - | API 网关 |
| Spring Cloud OpenFeign | - | 服务间远程调用 |
| MyBatis-Plus | 3.5.3.1 | ORM 框架 |
| MySQL | 8.0.23 | 关系型数据库 |
| RabbitMQ | - | 消息队列（异步解耦、延迟消息） |
| Elasticsearch | 7.x | 全文搜索引擎 |
| Nacos | - | 服务注册与配置中心 |
| Sentinel | - | 流量控制与熔断降级 |
| Seata | - | 分布式事务 |
| Spring Security + JWT | - | 认证与授权 |
| Knife4j | - | API 接口文档 |
| Hutool | 5.8.11 | 工具类库 |
| Lombok | 1.18.20 | 代码简化 |

### 前端技术栈

| 技术 | 用途 |
|------|------|
| Vue.js | 管理后台页面框架 |
| Element UI | 管理后台 UI 组件库 |
| HTML/CSS/JS | 用户门户页面 |
| Nginx | 前端静态资源服务器 |

---

## 项目结构

```
MaiMai_Ticket_Grabbing_System/
├── backed/                          # 后端微服务模块（Maven 父工程）
│   ├── pom.xml                      # 父 POM（依赖管理）
│   ├── mai-gateway/                 # API 网关服务
│   ├── mai-common/                  # 公共模块（工具类、配置、异常、拦截器）
│   ├── mai-api/                     # Feign 远程调用接口模块
│   ├── user-service/                # 用户服务
│   ├── item-service/                # 商品服务
│   ├── cart-service/                # 购物车服务
│   ├── trade-service/               # 订单/交易服务
│   ├── pay-service/                 # 支付服务
│   └── search-service/              # 搜索服务
├── fronted/                         # 前端资源
│   ├── conf/                        # Nginx 配置
│   └── html/
│       ├── mai-portal/              # 用户门户页面
│       └── mai-admin/               # 管理后台页面
└── .gitignore
```

---

## 微服务模块详解

### 1. mai-gateway（API 网关）

**端口**：`8080`

网关是整个系统的统一入口，基于 **Spring Cloud Gateway** 构建，负责：

- **路由转发**：将请求根据路径路由到对应的微服务
- **身份认证**：通过 `AuthGlobalFilter` 全局过滤器，解析 JWT Token 并将用户 ID 透传到下游服务
- **请求拦截**：支持白名单路径配置，排除不需要认证的接口（如登录、注册）
- **JWT 解析**：利用 RSA 密钥对解析 Token

核心文件：
- [GatewayApplication.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-gateway/src/main/java/com/mai/gateway/GatewayApplication.java) — 启动类
- [AuthGlobalFilter.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-gateway/src/main/java/com/mai/gateway/filter/AuthGlobalFilter.java) — 全局认证过滤器
- [MyGlobalFilter.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-gateway/src/main/java/com/mai/gateway/filter/MyGlobalFilter.java) — 自定义全局过滤器
- [JwtTool.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-gateway/src/main/java/com/mai/gateway/utils/JwtTool.java) — JWT 工具类

---

### 2. mai-common（公共模块）

**模块说明**：提供各微服务共享的基础组件，通过 Spring Boot 自动装配机制被其他模块引用。

**目录结构**：

```
mai-common/src/main/java/com/mai/common/
├── advice/
│   └── CommonExceptionAdvice.java   # 全局异常处理切面
├── config/
│   ├── JsonConfig.java              # JSON 序列化配置
│   ├── Knife4jConfig.java           # Swagger/Knife4j 接口文档配置
│   ├── MqConfig.java                # RabbitMQ 消息转换器配置
│   ├── MqConsumeErrorAutoConfiguration.java  # 消息消费异常自动配置
│   ├── MvcConfig.java               # Spring MVC 拦截器配置
│   └── MyBatisConfig.java           # MyBatis-Plus 分页插件配置
├── constants/
│   └── MqConstants.java             # 消息队列常量（交换机、路由键）
├── domain/
│   ├── PageDTO.java                 # 通用分页响应 DTO
│   ├── PageQuery.java               # 通用分页查询参数
│   └── R.java                       # 统一响应结果封装
├── exception/                       # 自定义异常体系
│   ├── BadRequestException.java     # 错误请求异常
│   ├── BizIllegalException.java     # 业务非法异常
│   ├── CommonException.java         # 通用异常基类
│   ├── DbException.java             # 数据库异常
│   ├── ForbiddenException.java      # 禁止访问异常
│   └── UnauthorizedException.java   # 未授权异常
├── interceptor/
│   └── UserInfoInterceptor.java     # 用户信息拦截器（从请求头获取用户ID）
└── utils/
    ├── BeanUtils.java               # 对象拷贝工具
    ├── CollUtils.java               # 集合工具类
    ├── Convert.java                 # 类型转换工具
    ├── CookieBuilder.java           # Cookie 构建工具
    ├── UserContext.java             # 用户上下文（ThreadLocal）
    └── WebUtils.java                # Web 工具类
```

**核心机制**：

- **统一响应**：`R<T>` 类封装了 `code`、`msg`、`data` 三个字段，所有接口返回统一格式
- **用户上下文传递**：`UserContext` 基于 ThreadLocal 存储当前请求用户 ID，网关解析 JWT 后将用户 ID 放入请求头 `user-info`，下游微服务通过 `UserInfoInterceptor` 拦截器取出并存入 ThreadLocal
- **全局异常处理**：`CommonExceptionAdvice` 使用 `@RestControllerAdvice` 捕获所有异常，统一转换为 `R` 响应格式

---

### 3. mai-api（Feign 远程调用接口）

**模块说明**：定义所有微服务间的 Feign 远程调用接口，实现服务间解耦。各业务服务通过引入此模块来调用其他服务。

**Feign 客户端列表**：

| 客户端 | 目标服务 | 功能 |
|--------|----------|------|
| [CartClient](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-api/src/main/java/com/mai/api/client/CartClient.java) | cart-service | 删除购物车商品 |
| [ItemClient](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-api/src/main/java/com/mai/api/client/ItemClient.java) | item-service | 查询商品、扣减库存 |
| [PayClient](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-api/src/main/java/com/mai/api/client/PayClient.java) | pay-service | 查询支付单 |
| [TradeClient](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-api/src/main/java/com/mai/api/client/TradeClient.java) | trade-service | 标记订单已支付 |
| [UserClient](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/mai-api/src/main/java/com/mai/api/client/UserClient.java) | user-service | 扣减余额 |

**特性**：

- 每个 Feign 客户端都配置了对应的 **Fallback 降级工厂**（如 `ItemClientFallback`），结合 Sentinel 实现熔断降级
- 通过 `DefaultFeignConfig` 统一注册降级实现，并配置请求拦截器传递用户上下文

---

### 4. user-service（用户服务）

**端口**：`8084`

**功能**：用户认证、余额管理、收货地址管理。

**API 接口**：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户登录 | POST | `/users/login` | 用户名密码登录，返回 JWT Token |
| 扣减余额 | PUT | `/users/money/deduct` | 支付时扣减用户余额 |
| 查询地址 | GET | `/addresses/{id}` | 根据 ID 查询收货地址 |
| 地址列表 | GET | `/addresses` | 查询当前用户地址列表 |

**核心实现**：

- **登录认证**：`UserServiceImpl.login()` 使用 BCrypt 密码加密 + RSA 密钥对生成 JWT Token，返回用户 ID、用户名、余额等信息
- **余额扣减**：`UserServiceImpl.deductMoney()` 通过自定义 SQL 实现原子性余额扣减，防止超扣
- **安全配置**：`SecurityConfig` 配置 BCrypt 密码编码器和 RSA 密钥对加载

**数据表**：`user`（用户表）、`address`（收货地址表）

核心文件：
- [UserController.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/user-service/src/main/java/com/mai/user/controller/UserController.java)
- [UserServiceImpl.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/user-service/src/main/java/com/mai/user/service/impl/UserServiceImpl.java)
- [SecurityConfig.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/user-service/src/main/java/com/mai/user/config/SecurityConfig.java)
- [JwtTool.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/user-service/src/main/java/com/mai/user/utils/JwtTool.java)

---

### 5. item-service（商品服务）

**端口**：`8081`

**功能**：商品 CRUD、库存管理、数据库搜索。

**API 接口**：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 分页查询商品 | GET | `/items/page` | 支持分页查询商品列表 |
| 批量查询商品 | GET | `/items` | 根据 ID 集合批量查询 |
| 查询单个商品 | GET | `/items/{id}` | 根据 ID 查询商品详情 |
| 新增商品 | POST | `/items` | 新增商品 |
| 更新商品 | PUT | `/items` | 更新商品信息 |
| 更新商品状态 | PUT | `/items/status/{id}/{status}` | 商品上下架 |
| 删除商品 | DELETE | `/items/{id}` | 删除商品 |
| 批量扣减库存 | PUT | `/items/stock/deduct` | 下单时扣减库存 |
| 数据库搜索 | GET | `/search/list` | 基于 MySQL 的关键词搜索 |

**核心实现**：

- **库存扣减**：`ItemServiceImpl.deductStock()` 使用 MyBatis 批量执行 SQL 语句，通过 `updateStock` 映射实现原子性的库存扣减（`stock >= num` 条件约束）
- **商品上下架**：更新商品状态后，通过 RabbitMQ 发送消息到 `items.topic` 交换机，通知 `search-service` 同步更新 Elasticsearch 索引
- **数据库搜索**：`SearchController` 基于 MyBatis-Plus 条件构造器，支持关键词模糊匹配、品牌/分类过滤、价格区间筛选

**数据表**：`item`（商品表）

核心文件：
- [ItemController.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/item-service/src/main/java/com/mai/item/controller/ItemController.java)
- [ItemServiceImpl.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/item-service/src/main/java/com/mai/item/service/impl/ItemServiceImpl.java)
- [SearchController.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/item-service/src/main/java/com/mai/item/controller/SearchController.java)

---

### 6. cart-service（购物车服务）

**端口**：`8082`

**功能**：购物车商品的增删改查。

**API 接口**：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 添加到购物车 | POST | `/carts` | 添加商品到购物车 |
| 更新购物车 | PUT | `/carts` | 更新购物车商品数量 |
| 删除单个商品 | DELETE | `/carts/{id}` | 删除购物车中指定商品 |
| 批量删除 | DELETE | `/carts` | 批量删除购物车商品 |
| 查询购物车列表 | GET | `/carts` | 查询当前用户购物车 |

**核心实现**：

- **添加购物车**：`CartServiceImpl.addItem2Cart()` 先检查是否已存在，存在则更新数量，不存在则新增。同时检查购物车是否已满（`cartProperties.getMaxItems()`）
- **购物车列表**：查询购物车后，通过 Feign 调用 `item-service` 获取商品详细信息（名称、价格、图片等）
- **订单创建后清理**：`OrderStatusListener` 监听 RabbitMQ 的 `order.create` 消息，订单创建成功后异步清理购物车中已购买的商品

**数据表**：`cart`（购物车表）

核心文件：
- [CartController.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/cart-service/src/main/java/com/mai/cart/controller/CartController.java)
- [CartServiceImpl.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/cart-service/src/main/java/com/mai/cart/service/impl/CartServiceImpl.java)
- [OrderStatusListener.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/cart-service/src/main/java/com/mai/cart/listener/OrderStatusListener.java)

---

### 7. trade-service（订单/交易服务）

**端口**：`8083`

**功能**：订单创建、支付状态更新、订单取消、库存恢复。

**API 接口**：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 查询订单 | GET | `/orders/{id}` | 根据 ID 查询订单 |
| 创建订单 | POST | `/orders` | 创建新订单 |
| 标记已支付 | PUT | `/orders/{orderId}` | 标记订单支付成功 |

**核心实现**：

- **订单创建流程**（`OrderServiceImpl.createOrder()`）：
  1. 通过 Feign 调用 `item-service` 查询商品信息和价格
  2. 计算订单总金额
  3. 写入订单主表和订单详情表
  4. 发送 RabbitMQ 消息通知 `cart-service` 清理购物车
  5. 通过 Feign 调用 `item-service` 扣减库存
  6. 发送 **延迟消息**（10 秒）到 RabbitMQ，用于订单超时检查

- **分布式事务**：使用 `@GlobalTransactional`（Seata AT 模式）保证订单创建过程中跨服务的库存扣减和订单写入的事务一致性

- **延迟队列机制**：通过 RabbitMQ 的 `x-delayed-message` 插件实现订单超时取消：
  - 订单创建后发送 10 秒延迟消息
  - `OrderDelayMessageListener` 消费延迟消息，检查订单支付状态
  - 已支付则更新订单状态，未支付则调用 `cancelOrder()` 取消订单并恢复库存

- **支付状态监听**：`PayStatusListener` 监听 `pay.success` 消息，收到支付成功通知后更新订单状态

**数据表**：`order`（订单表）、`order_detail`（订单详情表）、`order_logistics`（订单物流表）

核心文件：
- [OrderController.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/trade-service/src/main/java/com/mai/trade/controller/OrderController.java)
- [OrderServiceImpl.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/trade-service/src/main/java/com/mai/trade/service/impl/OrderServiceImpl.java)
- [OrderDelayMessageListener.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/trade-service/src/main/java/com/mai/trade/listener/OrderDelayMessageListener.java)
- [PayStatusListener.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/trade-service/src/main/java/com/mai/trade/listener/PayStatusListener.java)
- [RabbitMQConfig.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/trade-service/src/main/java/com/mai/trade/config/RabbitMQConfig.java)

---

### 8. pay-service（支付服务）

**端口**：`8085`

**功能**：支付单创建、余额支付处理。

**API 接口**：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 生成支付单 | POST | `/pay-orders` | 创建支付单 |
| 余额支付 | POST | `/pay-orders/{id}` | 基于余额进行支付 |
| 查询支付单 | GET | `/pay-orders/biz/{bizOrderNo}` | 根据业务订单号查询支付单 |

**核心实现**：

- **支付单生成**：`PayOrderServiceImpl.applyPayOrder()` 包含幂等性校验，同一业务订单号不会重复创建支付单
- **余额支付流程**（`PayOrderServiceImpl.tryPayOrderByBalance()`）：
  1. 查询支付单状态，校验是否为待支付状态
  2. 通过 Feign 调用 `user-service` 扣减用户余额
  3. 更新支付单状态为支付成功（乐观锁防并发）
  4. 发送 RabbitMQ 消息通知 `trade-service` 更新订单状态

- **分布式事务**：使用 `@GlobalTransactional` 保证余额扣减和支付单状态更新的事务一致性

**支付状态枚举**：
- `NOT_COMMIT`（未提交）
- `WAIT_BUYER_PAY`（待支付）
- `TRADE_SUCCESS`（支付成功）
- `TRADE_CLOSED`（交易关闭）

**数据表**：`pay_order`（支付单表）

核心文件：
- [PayController.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/pay-service/src/main/java/com/mai/pay/controller/PayController.java)
- [PayOrderServiceImpl.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/pay-service/src/main/java/com/mai/pay/service/impl/PayOrderServiceImpl.java)

---

### 9. search-service（搜索服务）

**端口**：`8086`

**功能**：基于 Elasticsearch 的全文搜索、聚合过滤、商品索引同步。

**API 接口**：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 搜索商品 | GET | `/search/list` | 全文搜索商品 |
| 动态过滤器 | POST | `/search/filters` | 获取品牌/分类聚合过滤条件 |

**核心实现**：

- **全文搜索**：`SearchServiceImpl.search()` 使用 Elasticsearch RestHighLevelClient：
  - 关键词匹配（`matchQuery`）
  - 品牌/分类过滤（`termQuery`）
  - 价格区间过滤（`rangeQuery`）
  - 广告加权（`functionScoreQuery`，广告商品权重提升 100 倍）
  - 排序（按更新时间排序）
  - 高亮显示

- **索引同步**：`ItemStatusListener` 监听 RabbitMQ 消息：
  - 商品上架（`item.up`）→ 同步写入 ES 索引
  - 商品下架（`item.down`）→ 从 ES 索引中删除

- **聚合过滤**：`filters()` 方法对品牌和分类字段进行聚合查询，返回可选过滤条件

核心文件：
- [SearchController.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/search-service/src/main/java/com/mai/search/controller/SearchController.java)
- [SearchServiceImpl.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/search-service/src/main/java/com/mai/search/service/Impl/SearchServiceImpl.java)
- [ItemStatusListener.java](file:///d:/lesson/project/MaiMai_Ticket_Grabbing_System/backed/search-service/src/main/java/com/mai/search/listener/ItemStatusListener.java)

---

## 消息队列架构

系统使用 RabbitMQ 实现服务间异步通信，主要涉及以下消息通道：

| 交换机 | 类型 | 路由键 | 消费者 | 用途 |
|--------|------|--------|--------|------|
| `trade.topic` | topic | `order.create` | cart-service | 订单创建后清理购物车 |
| `pay.topic` | topic | `pay.success` | trade-service | 支付成功后更新订单状态 |
| `items.topic` | topic | `item.up` | search-service | 商品上架同步 ES 索引 |
| `items.topic` | topic | `item.down` | search-service | 商品下架删除 ES 索引 |
| `trade.delay.topic` | x-delayed-message | `order.query` | trade-service | 订单超时取消（延迟 10 秒） |

**消息流转示意**：

```
订单创建 → trade.topic:order.create → cart-service 清理购物车
订单创建 → trade.delay.topic:order.query → (10秒后) → trade-service 检查支付状态
余额支付 → pay.topic:pay.success → trade-service 更新订单
商品上架 → items.topic:item.up → search-service 同步 ES
商品下架 → items.topic:item.down → search-service 删除 ES 索引
```

---

## 核心业务流程

### 下单流程

```
用户 → Gateway → trade-service
                    │
                    ├─ 1. Feign 查询 item-service 获取商品信息
                    ├─ 2. 计算总价，写入订单表 + 订单详情表
                    ├─ 3. MQ 通知 cart-service 清理购物车
                    ├─ 4. Feign 调用 item-service 扣减库存
                    └─ 5. MQ 延迟消息（10秒后检查支付状态）
```

### 支付流程

```
用户 → Gateway → pay-service
                    │
                    ├─ 1. 幂等性校验，创建支付单
                    ├─ 2. Feign 调用 user-service 扣减余额
                    ├─ 3. 更新支付单状态（乐观锁）
                    └─ 4. MQ 通知 trade-service 更新订单
```

### 认证流程

```
用户 → Gateway → AuthGlobalFilter
                    │
                    ├─ 白名单路径 → 直接放行
                    └─ 需要认证 → 解析 JWT → 用户ID 放入请求头 → 路由到下游服务
                                                      │
                                        user-info → UserInfoInterceptor → UserContext (ThreadLocal)
```

---

## 分布式事务方案

系统采用 **Seata AT 模式** 处理跨服务分布式事务：

- **订单创建**（`OrderServiceImpl.createOrder()`）：`@GlobalTransactional` 保证订单写入、库存扣减的事务一致性
- **余额支付**（`PayOrderServiceImpl.tryPayOrderByBalance()`）：`@GlobalTransactional` 保证余额扣减和支付单状态更新的事务一致性
- **订单取消**（`OrderServiceImpl.cancelOrder()`）：`@GlobalTransactional` 保证订单取消和库存恢复的事务一致性

Seata 配置文件中指定了 AT 模式：`seata.data-source-proxy-mode: AT`

---

## 服务治理

### Nacos（服务注册与配置中心）

- 所有微服务启动时向 Nacos 注册
- 支持服务发现与负载均衡
- 统一配置管理

### Sentinel（流量控制）

- Feign 集成 Sentinel，支持熔断降级
- 每个 Feign 客户端都有对应的 Fallback 降级实现
- Sentinel Dashboard 地址：`localhost:8090`

---

## 数据库设计

### 核心数据表

| 数据库 | 表名 | 所属服务 | 说明 |
|--------|------|----------|------|
| mai-user | `user` | user-service | 用户表（用户名、密码、余额、状态） |
| mai-user | `address` | user-service | 收货地址表 |
| mai-item | `item` | item-service | 商品表（名称、价格、库存、品牌、分类） |
| mai-cart | `cart` | cart-service | 购物车表（用户ID、商品ID、数量） |
| mai-trade | `order` | trade-service | 订单表（用户ID、总金额、状态、支付时间） |
| mai-trade | `order_detail` | trade-service | 订单详情表（商品名称、价格、数量） |
| mai-trade | `order_logistics` | trade-service | 订单物流表 |
| mai-pay | `pay_order` | pay-service | 支付单表（支付单号、业务订单号、金额、状态） |

---

## 环境配置

### 配置文件说明

每个微服务都有以下配置文件：

- `application.yaml` — 通用配置（服务端口、Feign、Sentinel、Seata 等）
- `application-dev.yaml` — 开发环境配置（数据库连接、Nacos、Redis 等）
- `application-local.yaml` — 本地环境配置
- `bootstrap.yml` — 引导配置（Nacos 配置中心地址等）

### 运行环境要求

| 组件 | 要求 |
|------|------|
| JDK | 11+ |
| Maven | 3.6+ |
| MySQL | 8.0+ |
| RabbitMQ | 3.8+（需安装 delayed-message-exchange 插件） |
| Elasticsearch | 7.x |
| Nacos | 2.x |
| Sentinel | 1.8.x |

### 启动顺序

1. 启动基础设施：MySQL、Nacos、RabbitMQ、Elasticsearch、Sentinel Dashboard
2. 启动基础服务：`mai-gateway`
3. 启动业务服务：`user-service` → `item-service` → `cart-service` → `trade-service` → `pay-service` → `search-service`

---

## 前端说明

### mai-portal（用户门户）

位于 `fronted/html/mai-portal/`，纯 HTML/CSS/JS 实现，包含：
- 商品列表页
- 商品详情页
- 购物车页
- 登录页

### mai-admin（管理后台）

位于 `fronted/html/mai-admin/`，基于 Vue.js + Element UI 实现，包含：
- 商品管理（`items.html`）
- 用户管理（`users.html`）

### Nginx 配置

前端通过 Nginx 提供静态资源服务，配置文件位于 `fronted/conf/nginx.conf`。

---

## 项目亮点

1. **微服务架构**：基于 Spring Cloud Alibaba 全家桶，实现服务注册发现、配置管理、流量控制、分布式事务
2. **异步解耦**：使用 RabbitMQ 实现服务间异步通信，包括订单创建后清理购物车、支付成功后更新订单、商品上下架同步 ES 索引
3. **延迟队列**：利用 RabbitMQ 延迟插件实现订单超时自动取消，避免库存锁定
4. **分布式事务**：Seata AT 模式确保跨服务数据一致性
5. **全文搜索**：基于 Elasticsearch 实现商品全文搜索，支持广告加权、聚合过滤、高亮显示
6. **安全认证**：JWT + RSA 非对称加密，网关统一认证，ThreadLocal 传递用户上下文
7. **乐观锁**：支付单状态更新使用乐观锁（状态条件判断）防止并发问题
8. **幂等性**：支付单创建支持幂等校验，防止重复创建
9. **熔断降级**：Feign + Sentinel 实现服务熔断降级，提高系统容错能力