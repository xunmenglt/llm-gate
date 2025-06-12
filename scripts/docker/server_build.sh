#!/bin/bash

# 获取当前脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(dirname "$(dirname "$SCRIPT_DIR")")"
PROJECT_NAME="llm-gate-server"

# 设置变量
ITEM_DIR="$ROOT_DIR/$PROJECT_NAME"

# 进入根目录
cd "$ROOT_DIR" || exit 1

echo "Building $ITEM_DIR project..."

# 进入项目目录
cd "$ITEM_DIR" || exit 1

# 执行 Maven 构建命令
mvn clean package -Dmaven.test.skip=true

# 检查 Maven 构建结果
if [ $? -eq 0 ]; then
    echo "$ITEM_DIR build successful!"
else
    echo "$ITEM_DIR build failed!"
    exit 1
fi

# 查找 JAR 文件路径
JAR_PATH=$(find xunmeng-admin/target -name "*.jar" | head -n 1)

# 检查是否找到 JAR
if [ ! -f "$JAR_PATH" ]; then
    echo "No JAR file found at expected path: xunmeng-admin/target/"
    exit 1
fi

# 重命名 JAR 文件
mv "$JAR_PATH" "$PROJECT_NAME.jar"

if [ $? -eq 0 ]; then
    echo "JAR file renamed successfully to $PROJECT_NAME.jar"
else
    echo "Failed to rename JAR file."
    exit 1
fi

# 返回根目录
cd "$ROOT_DIR" || exit 1

# 移动 JAR 文件到根目录并重命名为 codura-server.jar
mv "$ITEM_DIR/$PROJECT_NAME.jar" "$ROOT_DIR/$PROJECT_NAME.jar"

if [ $? -eq 0 ]; then
    echo "JAR file moved to root directory successfully."
else
    echo "Failed to move JAR file."
    exit 1
fi

echo "Build process for $ITEM_DIR completed."
