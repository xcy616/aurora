#!/usr/bin/env bash
set -e

# 在项目根目录执行：sh deploy/build.sh

echo "==> 1/3 打包后端 jar"
cd aurora-springboot
mvn -q package -DskipTests -Dlombok.version=1.18.30
cd ..

echo "==> 2/3 构建博客前台"
cd aurora-vue/aurora-blog
npm run build
cd ../..

echo "==> 3/3 构建管理后台"
cd aurora-vue/aurora-admin
npm run build
cd ../..

echo "构建完成！产物："
echo "  后端  aurora-springboot/target/aurora-springboot-0.0.1.jar"
echo "  博客  aurora-vue/aurora-blog/dist"
echo "  后台  aurora-vue/aurora-admin/dist"
