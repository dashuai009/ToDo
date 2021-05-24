# ToDo
一个todo软件，使用了sqlite

# 截图

# 特点
1. 使用room持久性库访问数据库
2. 具体实现还是很丑，有launch还有GlobalScope.launch，而且会创建两个Room.dataBaseBuilder,大概率使用一会之后就会崩溃
3. 去掉了插入一条todo之后的Toast提示，暂时不会搞
