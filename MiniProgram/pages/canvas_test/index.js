//index.js



//获取应用实例
const app = getApp()

var mouseX = 0;
var mouseY = 0;

Page({
  data: {
    width: 500,
    height: 300,
    size: 100,
    counter: 1,
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },

  onLoad: function() {

  },
  onTap: function(e) {
    
    console.log(e.detail.x + " " + e.detail.y)
    mouseX = e.detail.x;
    mouseY = e.detail.y;
  },
  onReady: function(e) {

    var DEBUG_LOG = false;


    var index_counter = 0;

    //全局变量，存放所有的点
    var circles = [];
    var width = this.data.width;
    var height = this.data.height;
    var size = this.data.size;


    mouseX = width / 2;
    mouseY = height / 2;

    console.log(width + " " + height+ " " + size)

    for(var i =0; i < size;i++) {
      var cir = new MyShape();
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


        if (DEBUG_LOG)
          console.log("circle prepare pos " + cir.getX() + " " + cir.getY() + " " + cir.getVX() + " " + cir.getVY())

        var dX = cir.getX() - mouseX;
        var dY = cir.getY() - mouseY;

        var d = Math.sqrt(dX * dX + dY * dY);
        dX = d > 0 ? dX / d : 0;
        dY = d > 0 ? dY / d : 0;

        //修改速度
        //离触摸点越远速度越小
        if (d < toDist) {
          var toAcc = (1 - (d / toDist)) * width * 0.0014;
          var vx =  cir.getVX() - dX * toAcc;
          var vy = cir.getVY() - dY * toAcc;

          cir.setVXY(vx, vy)
        }


        // cir.vx *= 0.96;
        // cir.vy *= 0.96;

        cir.setVXY(cir.getVX() * 0.96, cir.getVY() * 0.96)
        if (DEBUG_LOG)
          console.log("circle after pos " + cir.getX() + " " + cir.getY() + " " + cir.getVX() + " " + cir.getVY() + " " + cir.getVX() * 0.96)
        //速度修复，太慢了需要处理一下
        var avgVX = Math.abs(cir.getVX());
        var avgVY = Math.abs(cir.getVY());
        var avgV = (avgVX + avgVY) * 0.5;

        
        if (avgVX < 0.001) {
          // cir.vx *= Math.random(); // / Integer.MAX_VALUE * 3;//float(mRandom.nextInt()) / Integer.MAX_VALUE * 3;
          cir.setVXY(cir.getVX() * Math.random(), cir.getVY());

        }
        if (avgVY < 0.001) {
          // cir.vx *= Math.random(); // / Integer.MAX_VALUE * 3;
          cir.setVXY(cir.getVX(), cir.getVY() * Math.random());
        }


        //移动
        // cir.x += cir.vx;
        // cir.getY() += cir.vy;
        cir.setPos(cir.getX() + cir.getVX(), cir.getY() + cir.getVY())

        if (DEBUG_LOG)
          console.log("circle pos " + cir.getX() + " " + cir.getY())

        //边界判断
        if (cir.getX() > width - cir.getRadius()) {
          cir.setPos(width - cir.getRadius(), cir.getY());
          cir.setVXY(cir.getVX() * -1, cir.getVY());
          // cir.vx *= -1;
        } else if (cir.getX() < cir.getRadius()) {
          // cir.getX() = cir.getRadius();
          // cir.vx *= -1;
          cir.setPos(cir.getRadius(), cir.getY());
          cir.setVXY(cir.getVX() * -1, cir.getVY());
        }
        if (cir.getY() > height - cir.getRadius()) {
          // cir.getY() = height - cir.getRadius();
          // cir.vy *= -1;
          cir.setPos(cir.getX(), height - cir.getRadius());
          cir.setVXY(cir.getVX(), cir.getVY() * -1);
        } else if (cir.getY() < cir.getRadius()) {
          // cir.getY() = cir.getRadius();
          // cir.vy *= -1;
          cir.setPos(cir.getX(), cir.getRadius());
          cir.setVXY(cir.getVX(), cir.getVY() * -1);
        }




        cir.drawMyShape(context)
      }
      context.draw()

    

    }

    setInterval(onDraw, 16)



  },
  drawCirlce1: function() {
    console.log("drawCircle1 call")

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


function MyShape() {
  var x = 100;
  var y = 100;
  var color = "#00ff00";
  var r, g, b;
  var radius = 10;
  var maxSpeed = 10;
  var vx, vy;
  var maxRadius = 15;

  this.getX = function () {
    return x;
  }
  this.getY = function () {
    return y;
  }

  this.setPos = function (x_, y_) {
    x = x_;
    y = y_;
  }

  this.getVX = function () {
    return vx;
  }
  this.getVY = function () {
    return vy;
  }

  this.setVXY = function (vx_, vy_) {
    vx = vx_;
    vy = vy_;
  }

  this.getRadius = function () {
    return radius;
  }



  this.randomInit = function (c_width, c_height) {
    //出初始
    radius = randomInt(maxRadius - 2) + 2;
    x = randomInt(radius + c_width - 100);
    y = randomInt(radius + c_height - 100);

    // 初始位置
    r = Math.random();
    g = Math.random();
    b = Math.random();
    color = randomHexColor();

    //vx = randomInt(2 * maxSpeed) - maxSpeed;
    //vy = randomInt(2 * maxSpeed) - maxSpeed;
    vx = Math.random() * 2 * maxSpeed - maxSpeed;
    vy = Math.random() * 2 * maxSpeed - maxSpeed;
  }


  function randomHexColor() { //随机生成十六进制颜色
    var hex = Math.floor(Math.random() * 16777216).toString(16); //生成ffffff以内16进制数
    while (hex.length < 6) { //while循环判断hex位数，少于6位前面加0凑够6位
      hex = '0' + hex;
    }
    return '#' + hex; //返回‘#'开头16进制颜色
  }

  // Returns a random integer from 0 to range - 1.
  function randomInt(range) {
    return Math.floor(Math.random() * range);
  }

  this.drawMyShape = function (context) {

    // console.log("drawMyShape call " + x + " " + y + " " + radius)

    context.setFillStyle(color)
    // context.beginPath();
    // context.arc(x, y, radius, 0, 2 * Math.PI, true)
    // context.closePath()
    // context.fill()

    context.setStrokeStyle(color)
    context.beginPath();
    context.arc(x, y, radius, 0, 2 * Math.PI, true)
    context.closePath()
    context.stroke()
  }
}