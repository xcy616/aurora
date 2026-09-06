#!/usr/bin/env bash
set -euo pipefail

# 数据库图片地址迁移脚本
#
# 本地数据库里存的图片地址是 http://localhost:9000/aurora/...
# 上线后浏览器访问不到 localhost，需要把地址统一替换成生产 MinIO 的公网地址。
#
# 用法（在服务器上、compose 启动之后执行）：
#   sh deploy/migrate-images.sh https://liliyuan.top/minio
#   如果库里存的是 IP 前缀（例如 http://1.2.3.4:9000），第二个参数传旧前缀：
#   sh deploy/migrate-images.sh https://liliyuan.top/minio http://1.2.3.4:9000

NEW_BASE="${1:?用法: sh deploy/migrate-images.sh https://liliyuan.top/minio [旧地址前缀]}"
OLD_BASE="${2:-http://localhost:9000}"

# 读取 .env（compose 会从 deploy/ 目录读取，因此优先找 deploy/.env）
if [ -f deploy/.env ]; then
  ENV_FILE="deploy/.env"
elif [ -f .env ]; then
  ENV_FILE=".env"
else
  ENV_FILE=""
fi

if [ -n "$ENV_FILE" ]; then
  set -a
  # shellcheck disable=SC1091
  source "$ENV_FILE"
  set +a
fi

MYSQL_PWD="${MYSQL_ROOT_PASSWORD:-aurora123}"

echo "==> 将数据库中的图片地址从 $OLD_BASE 替换为 $NEW_BASE"

docker exec -i aurora-mysql mysql -uroot -p"$MYSQL_PWD" aurora <<SQL
UPDATE t_article        SET article_cover = REPLACE(article_cover, '$OLD_BASE', '$NEW_BASE') WHERE article_cover LIKE '%$OLD_BASE%';
UPDATE t_article        SET article_content = REPLACE(article_content, '$OLD_BASE', '$NEW_BASE') WHERE article_content LIKE '%$OLD_BASE%';
UPDATE t_user_info      SET avatar        = REPLACE(avatar,        '$OLD_BASE', '$NEW_BASE') WHERE avatar        LIKE '%$OLD_BASE%';
UPDATE t_friend_link    SET link_avatar   = REPLACE(link_avatar,   '$OLD_BASE', '$NEW_BASE') WHERE link_avatar   LIKE '%$OLD_BASE%';
UPDATE t_talk           SET images        = REPLACE(images,        '$OLD_BASE', '$NEW_BASE') WHERE images        LIKE '%$OLD_BASE%';
UPDATE t_website_config SET config        = REPLACE(config,        '$OLD_BASE', '$NEW_BASE') WHERE config        LIKE '%$OLD_BASE%';
UPDATE t_photo          SET photo_src     = REPLACE(photo_src,     '$OLD_BASE', '$NEW_BASE') WHERE photo_src     LIKE '%$OLD_BASE%';
UPDATE t_photo_album    SET album_cover   = REPLACE(album_cover,   '$OLD_BASE', '$NEW_BASE') WHERE album_cover   LIKE '%$OLD_BASE%';
UPDATE t_about          SET content       = REPLACE(content,       '$OLD_BASE', '$NEW_BASE') WHERE content       LIKE '%$OLD_BASE%';
SQL

echo "==> 完成"
echo "如还有 linhaojun / picsum / talkxj 等外链图片，请在后台重新上传（前台头像、文章封面、友链头像），"
echo "或手动把图片传到 MinIO 后更新对应 URL。"
