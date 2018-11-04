
function MyShape() {
  var x = 100;
  var y = 100;
  var color = "#00ff00";
  var radius = 10;
  var maxSpeed = 10;
  var vx, vy;
  var maxRadius = 15;

  // 是否需要往目标位置移动,默认关闭
  var isTargetEable = false;
  // 目标位置,如果启动了目标位置，则会往目标位置移动，记录在这里是因为每个shape可以有不同的目标位置
  var targetX = 0;
  var targetY = 0;

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

  this.getTargetX = function () {
    return targetX;
  }
  this.getTargetY = function () {
    return targetY;
  }

  this.setTargetPos = function (x_, y_) {
    targetX = x_;
    targetY = y_;
  }


  this.getTargetEable = function () {
    return isTargetEable;
  }
  this.setTargetEable = function (enable) {
    isTargetEable = enable;
  }



  this.randomInit = function (c_width, c_height) {
    //出初始
    radius = randomInt(maxRadius - 2) + 2;
    x = randomInt(radius + c_width - 100);
    y = randomInt(radius + c_height - 100);

    color = randomHexColor();

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
    context.beginPath();
    context.arc(x, y, radius, 0, 2 * Math.PI, true)
    context.closePath()
    context.fill()

    // context.setStrokeStyle(color)
    // context.beginPath();
    // context.arc(x, y, radius, 0, 2 * Math.PI, true)
    // context.closePath()
    // context.stroke()
  }
}


module.exports = {
  MyShape: MyShape
}