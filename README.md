# Bonusball
很多小球模拟流体的效果，并形成汉字

思路
1.设定一个列表 。如果点经过了这些地方 。。。就不走了
效果还不错。。。。不过 貌似有点卡。。。。

可以连续写好几个字。。。

2.判断 小球停下来   在目标 点的方圆 5以内
最后剩几个球 一直找不到自己的位置。。。 所以判断不是所有的球都停下来


问题 
很致命，，，全包围结构。。。中间的就写不了了。。。


3.试试 如果这个地方有点了。。。就把它的screenPoint设置为。。。false
或者 0.5的概率设置


4.最后方案
每个球都设置一个目标位置，它这一生的任务就是尽可能的接近它

=========
bug 连续双击的话 顺序就会被打乱了
如显示 一二三
再双击 应该从头显示一二三   但它有可能是  认为有两个任务  两个一二三  “交替”显示。。。
timer的问题 每次开始写字的时候 创建新的timer就好 把上次留下的清理掉即可
