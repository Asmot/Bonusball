

import * as THREE from 'libs/three.min.js'

function MyShape() {

  var shape;
  var x = 100;
  var y = 100;
  var z = 0;
  var color = "#00ff00";
  var radius = 1;
  var vx, vy;
  var maxSpeed =   10;
  var maxRadius = 4;
  var minRadius = 2;

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
    radius = minRadius + randomInt(maxRadius - minRadius);
    x = randomInt(radius + c_width - 100);
    y = randomInt(radius + c_height - 100);

    color = randomHexColor2();
    

    var vx_ = Math.random() * maxSpeed - maxSpeed;
    var vy_ = Math.random() * maxSpeed - maxSpeed;

    this.setVXY(vx_, vy_);

    var geometry = new THREE.CircleGeometry(radius);
    var material = new THREE.MeshBasicMaterial({
      color: color,
    });

    shape = new THREE.Mesh(geometry, material);
  }

  this.getShape = function () {
    return shape;
  }


  function randomHexColor2() { 
    var color0 = "#DEB887"
    var color1 = "#FAEBD7"
  
    var rate = 100;

    var num = randomInt(100);
    if (num < rate) {
      return color0;
    } else {
      return color1;    
    }
  
    
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

  this.drawMyShape = function () {
    // console.log("drawMyShape call " + x + " " + y + " " + radius)
       shape.position.x = x;
       shape.position.y = y;
       shape.position.z = z;
       
   
  }
}


module.exports = {
  MyShape: MyShape
}