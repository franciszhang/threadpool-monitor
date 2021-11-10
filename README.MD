### 概述

### 快速开始
#### 1.在 threadpool-monitor-agent-manager 工程下 使用命令 mvn clean package 生成jar包
#### 2.在 threadpool-monitor-agent 工程下 使用命令 mvn clean package 生成jar包
#### 3.在 example-monitor 工程下 加上jvm参数 -javaagent:dest/threadpool-monitor-agent-1.0.2.jar -Dthreadpool.dashboard.server=127.0.0.1:8088 并启动
#### 4.在 threadpool-monitor-dashboard 工程下启动
#### 5.打开 http://localhost:8088/ ，查看机器列表

