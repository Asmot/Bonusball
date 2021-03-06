//index.js

var TextUtil = require('TextUtil.js');
var shape = require('MyShape.js');

//获取应用实例
const app = getApp()

var mouseX = 0;
var mouseY = 0;

var circles = [];

/**
 * 记录生成字是否正在运行
 */
var isFormTextRunning = false;

var lifeTimer;


function startShowText(text, width_for_text, height_for_text, width_map, height_map, wx) {
  // text.split()

  if (!isFormTextRunning) {
    // 添加判断避免重复处理
    isFormTextRunning = true;
    console.log("ready to show " + text);
    var readyToShowText = text;
    TextUtil.text2Matrix(readyToShowText, width_for_text, height_for_text, width_map, height_map, wx, TextGeneragetCallback);
  } else {
    console.log("FormText is running ");
  }
}


/**
 * 生成文字的回调
 */
function TextGeneragetCallback(points) {

  formText(points);
  setTimeout(stopFormText, 5000);
}

// 停止生成汉字
function stopFormText() {
  for (var i = 0; i < circles.length; i++) {
    var cir = circles[i];
    cir.setTargetEable(false);
  }

  // 完成汉字展示
  isFormTextRunning = false;

}

//开始成汉字
function formText(points) {
  // console.log(points)
  for (var i = 0; i < circles.length; i++) {
    var cir = circles[i];

    var targetX = points[i % points.length][0];
    var targetY = points[i % points.length][1];


    var offset_value = 14;
    var offset_x = Math.floor(Math.random() * offset_value) - offset_value / 2;
    var offset_y = Math.floor(Math.random() * offset_value) - offset_value / 2;

    cir.setTargetPos(targetX + offset_x, targetY + offset_y);
    cir.setTargetEable(true);

  }
}


Page({
  data: {
    width: 500,
    height: 300,
    width_for_text: 100,
    height_for_text: 20,
    inputValue: '面',
    size: 300,
    counter: 1,
    userInfo: {},
    hasUserInfo: false,
    DEBUG_LOG: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },

  onLoad: function() {

  },

  // 手势相关
  // 最后一次单击事件点击发生时间
  lastTapTime: 0,

  /// 双击
  onTap: function(e) {
    var that = this
    // 当前点击的时间
    var currentTime = e.timeStamp
    var lastTapTime = that.lastTapTime
    // 更新最后一次点击时间
    that.lastTapTime = currentTime

    // 如果两次点击时间在300毫秒内，则认为是双击事件
    if (currentTime - lastTapTime < 300) {
      // console.log("双击")

      // 双击事件，打乱位置
      for (var i = 0; i < circles.length; i++) {
        var cir = circles[i];
        var speed = 50;
        var vx = Math.floor(Math.random() * 2 * speed) - speed;
        var vy = Math.floor(Math.random() * 2 * speed) - speed;
        cir.setVXY(vx, vy)
      }

      return;
    }
    // console.log("单击")

    // 单击事件
    mouseX = e.detail.x;
    mouseY = e.detail.y;
  
  },

  onTouchMove: function(e) {
    // 按住移动
    mouseX = e.touches[0].x;
    mouseY = e.touches[0].y;
  },

  onLongTap: function(e) {
    var text = this.data.inputValue;
    startShowText(text, this.data.width_for_text, this.data.height_for_text, this.data.width, this.data.height, wx);
  },
  ///////////////////////////

  // 按钮点击事件
  onButtonClick: function(e) {
    //
    // var text = this.data.inputValue;
    // TextUtil.text2Matrix(text, this.data.width_for_text, this.data.height_for_text, this.data.width, this.data.height, wx, TextGeneragetCallback);    
    this.onLongTap()
  },


  ////
  onTextChanged: function(input) {
    console.log(input.detail.value);
    this.setData({
      inputValue: input.detail.value
    });
  },

  ///
  onReady:function(e) {

  },

  onShow: function(e) {

    var myWidth = this.data.width;
    var myHeight = this.data.height;

    wx.getSystemInfo({
      success: function(res) {
        console.log(res)
        myWidth = res.windowWidth;
        myHeight = res.windowHeight;
      },
    });

    this.setData({
      width: myWidth,
      height: myHeight
    })


    var DEBUG_LOG = this.data.DEBUG_LOG;


    var index_counter = 0;

    //全局变量，存放所有的点
    var width = this.data.width;
    var height = this.data.height;
    var size = this.data.size;


    mouseX = width / 2;
    mouseY = height / 2;

    console.log(width + " " + height + " " + size)

    for (var i = 0; i < size; i++) {
      var cir = new shape.MyShape();
      cir.randomInit(width, height)
      circles.push(cir)
    }

    var onDraw = function() {
      // 使用 wx.createContext 获取绘图上下文 context
      var context = wx.createCanvasContext('canvasid01')

      // console.log(this.data.counter);

      //循环绘制
      for (var i = 0; i < circles.length; i++) {
        var cir = circles[i];



        //移动到触摸点		
        var toDist = width * 1.86;
        var stirDist = width * 0.125;
        var blowDist = width * 0.5;

        var dX = cir.getX() - mouseX;
        var dY = cir.getY() - mouseY;

        // 判断是否往目标位置移动，默认所有的点会往手势点击位置移动
        if (cir.getTargetEable()) {
          dX = cir.getX() - cir.getTargetX();
          dY = cir.getY() - cir.getTargetY();
        }


        var d = Math.sqrt(dX * dX + dY * dY);
        dX = d > 0 ? dX / d : 0;
        dY = d > 0 ? dY / d : 0;

        //修改速度
        //离触摸点越远速度越小
        if (d < toDist) {
          var toAcc = (1 - (d / toDist)) * width * 0.0014;
          var vx = cir.getVX() - dX * toAcc;
          var vy = cir.getVY() - dY * toAcc;

          cir.setVXY(vx, vy)
        }


        // cir.vx *= 0.96;
        // cir.vy *= 0.96;

        cir.setVXY(cir.getVX() * 0.96, cir.getVY() * 0.96)

        //速度修复，太慢了需要处理一下
        var avgVX = Math.abs(cir.getVX());
        var avgVY = Math.abs(cir.getVY());
        var avgV = (avgVX + avgVY) * 0.5;


        if (avgVX < 0.001) {
          cir.setVXY(cir.getVX() * Math.random(), cir.getVY());
        }
        if (avgVY < 0.001) {
          cir.setVXY(cir.getVX(), cir.getVY() * Math.random());
        }


        //移动
        cir.setPos(cir.getX() + cir.getVX(), cir.getY() + cir.getVY())

        if (DEBUG_LOG)
          console.log("circle pos " + cir.getX() + " " + cir.getY())

        //边界判断
        if (cir.getX() > width - cir.getRadius()) {
          cir.setPos(width - cir.getRadius(), cir.getY());
          cir.setVXY(cir.getVX() * -1, cir.getVY());

        } else if (cir.getX() < cir.getRadius()) {

          cir.setPos(cir.getRadius(), cir.getY());
          cir.setVXY(cir.getVX() * -1, cir.getVY());
        }
        if (cir.getY() > height - cir.getRadius()) {
          cir.setPos(cir.getX(), height - cir.getRadius());
          cir.setVXY(cir.getVX(), cir.getVY() * -1);
        } else if (cir.getY() < cir.getRadius()) {
          cir.setPos(cir.getX(), cir.getRadius());
          cir.setVXY(cir.getVX(), cir.getVY() * -1);
        }




        cir.drawMyShape(context)
      }
      context.draw()



    }

    lifeTimer = setInterval(onDraw, 16)



  },
  onHide: function() {
    circles = [];
    clearInterval(lifeTimer)
  },

  drawCirlce1: function() {

    // 使用 wx.createContext 获取绘图上下文 context
    var context = wx.createCanvasContext('canvasid01')


    // for (var i = 0; i < 5; i++) {

    console.log("this a test " + i);
    context.setFillStyle("#00ff00")
    context.setLineWidth(2)
    context.beginPath();
    context.arc(120, 80 + i * 20, 5, 0, 2 * Math.PI, true)
    context.closePath()
    context.fill()


    // }

    context.draw()

  },
  queryCartList: function() {
    console.log('ok')
  }
})