# 业务调度器构建流程

运行步骤：
1.构建镜像：

```bash
   docker build -t uvperday:0.0.11 .
```

2.运行容器

```bash
   docker run -it -d --rm --name uvperday uvperday:0.0.11
```

3.进入容器

```bash
   docker exec -it uvperday /bin/bash
```

4.查看日志

```bash
   docker logs -f -t --tail 100 uvperday
```