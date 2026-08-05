# 生产部署文档

## 一、准备

1. 一台服务器（推荐 2核4G，香港节点免备案）
2. 一个域名，解析两个子域名到服务器 IP：
   - `blog.example.com` → 博客前台
   - `admin.example.com` → 管理后台
   （可选）`minio.example.com` → 图片存储
3. 服务器安装 Docker：
   ```bash
   curl -fsSL https://get.docker.com | sh
   ```

## 二、构建与上传

在本地（或服务器）构建产物：

```bash
sh deploy/build.sh
```

然后把整个项目（或至少 aurora-springboot/target、aurora-vue/*/dist、deploy 目录）传到服务器。

## 三、配置环境变量

```bash
cp deploy/.env.example .env
vim .env   # 修改密码、域名、JWT 密钥
```

> `.env` 不要提交到 git。

## 四、启动

```bash
docker compose -f deploy/docker-compose.prod.yml up -d
docker compose -f deploy/docker-compose.prod.yml ps   # 查看状态，等全部 healthy
```

首次启动 MySQL 会用 `sql/aurora.sql` 初始化全新数据库（含初始管理员账号）。

> **已有数据迁移**：如果你想把本地数据库的数据带过去，在本地执行
> `mysqldump -uroot -paurora123 aurora > backup.sql`，传到服务器后导入；
> 这种情况下不要用 aurora.sql 初始化（首次启动时把该挂载行注释掉或先删数据卷）。

## 五、MinIO 桶公开读（必须）

图片要能被浏览器直接访问，需要把 `aurora` 桶设为公开读：

```bash
docker exec aurora-minio mc alias set local http://localhost:9000 <MINIO_ROOT_USER> <MINIO_ROOT_PASSWORD>
docker exec aurora-minio mc anonymous set download local/aurora
```

同时确认 `.env` 里 `MINIO_ENDPOINT` 是公网可访问地址（例如 `https://minio.example.com` 或 `http://服务器IP:9000`）。

## 六、HTTPS（可选但推荐）

用 certbot 申请免费证书：

```bash
docker run --rm -p 80:80 -v /etc/letsencrypt:/etc/letsencrypt certbot/certbot certonly \
  --standalone -d blog.example.com -d admin.example.com
```

然后把 `deploy/nginx/nginx.conf` 里的 server_name 改成你的真实域名，并加上 443 监听与证书：

```nginx
listen 443 ssl;
ssl_certificate     /etc/letsencrypt/live/blog.example.com/fullchain.pem;
ssl_certificate_key /etc/letsencrypt/live/blog.example.com/privkey.pem;
```

并在 nginx 容器挂载证书目录：`/etc/letsencrypt:/etc/letsencrypt:ro`。

> 如果走 HTTPS，图片地址也要是 HTTPS（MINIO_ENDPOINT 用 minio 子域名或加证书），否则浏览器会拦截混合内容。

## 七、QQ 登录（待申请）

当前 QQ appid 是占位符。申请到自己的 QQ 互联 appid 后：

1. 后端 `.env` 里 `QQ_APP_ID` 换成你的 appid
2. `aurora-vue/aurora-blog/public/index.html` 里 QQ SDK 的 `data-appid` 和 `data-redirecturi` 改成你自己的
3. 在 QQ 互联后台配置回调域名

未申请前，可在网站管理里把 QQ 登录关闭（`qqLogin: 0`），或提示访客暂未开放。

## 八、备份

```bash
# 数据库每日备份
docker exec aurora-mysql sh -c 'exec mysqldump -uroot -p$MYSQL_ROOT_PASSWORD aurora' > backup_$(date +%F).sql
# MinIO 图片目录是 docker volume，可定期备份或挂载到宿主机目录
```
