import * as THREE from 'libs/three.min.js'

var TextUtil = require('TextUtil.js');
var shape = require('MyShape.js');

let ctx = canvas.getContext('webgl')

let scene
let renderer
let camera
var shapes = []

var mouseX = 0;
var mouseY = 0;

var circles = [];

var canvasWidth = 200;
var canvasHeight =300;

var width = 200;
var height = 300;
var width_for_text = 100;
var height_for_text = 20;
var inputValue = '面';
var size = 100;
var DEBUG_LOG = false;


// 手势相关
// 最后一次单击事件点击发生时间
var lastTapTime = 0

/**
 * 游戏主函数
 */
export default class Main {
  constructor() {
    this.start();

    //事件
    wx.onTouchStart(((e) => {
      let x = e.touches[0].clientX
      let y = e.touches[0].clientY
      y = height - y;


      // console.log(" touch " + x + " " + y, width, height, canvasWidth, canvasHeight)

      
      var that = this
      // 当前点击的时间
      var currentTime = e.timeStamp
      var lastTapTime = that.lastTapTime
      // 更新最后一次点击时间
      that.lastTapTime = currentTime

      // 如果两次点击时间在300毫秒内，则认为是双击事件
      if (currentTime - lastTapTime < 300) {
         console.log("双击")

        // 双击事件，打乱位置
        for (var i = 0; i < circles.length; i++) {
          var cir = circles[i];
          var speed = 30;
          var vx =Math.random() * 2 * speed - speed;
          var vy = Math.random() * 2 * speed - speed;
          cir.setVXY(vx, vy)
        }

        return;
      }
       console.log("单击")

      // 单击事件
      mouseX = x;
      mouseY = y;

    }).bind(this))
    wx.onTouchMove(((e) => {

      let x = e.touches[0].clientX
      let y = e.touches[0].clientY

      if (this.touched) {

      }

    }).bind(this))

    wx.onTouchEnd(((e) => {
      this.touched = false
    }).bind(this))
  }




  start() {

    canvasWidth = canvas.width;
    canvasHeight = canvas.height;

    var scale = canvasWidth / canvasHeight;

    width = canvasWidth;
    height = width / scale;

    mouseX = width / 2;
    mouseY = height / 2;

    console.log(width + " " + height)

    // 初始化
    scene = new THREE.Scene()

    var left = 0;
    var right = canvasWidth;
    var top = 0;
    var bottom = canvasHeight;
    camera = new THREE.PerspectiveCamera(90, scale, 1, 1000);
    camera.position.set(width /2, height/2, width);
    camera.lookAt(width / 2, height / 2,0);
    scene.add(camera);
    

    var geometry = new THREE.CubeGeometry(width, height, 0.1);
    var material = new THREE.MeshBasicMaterial({
            color: 0xffff00,
      wireframe: true
    });
    // cube 是一个可以填充的形状
    var cube = new THREE.Mesh(geometry, material);

    cube.position.x = width/2;
    cube.position.y = height/2;
    cube.position.z = 0;

    scene.add(cube);

    for (var i = 0; i < size; i++) {
      // cube 是一个可以填充的形状
      var cir = new shape.MyShape();
      cir.randomInit(width, height)
      circles.push(cir)
      scene.add(cir.getShape());
    }

    renderer = new THREE.WebGLRenderer({
      // 将canvas交给renderer 一个渲染的容器
      context: ctx,
      // 平滑， 抗锯齿 输出的画面会进行优化，不会带毛边
      antialias: true
    });
    // 设置renderer的样子
    renderer.setSize(width, height);

    this.loop();
  }


  /**
   * canvas重绘函数
   * 每一帧重新绘制所有的需要展示的元素
   */
  render() {
    renderer.render(scene, camera);
  }

  // 游戏逻辑更新主函数
  update() {
    // for (var i = 0; i < shapes.length; i++) {
    //   var shape = shapes[i];
    //   shape.rotation.x += 0.1;
    //   shape.rotation.y += 0.1;

    //   shape.position.x = Math.floor(Math.random() * 10);
    //   shape.position.y = Math.floor(Math.random() * 10);
    // }

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

      cir.drawMyShape();
    }
  }

  // 实现游戏帧循环
  loop() {
    this.update()
    this.render()

    window.requestAnimationFrame(this.loop.bind(this), canvas)
  }
}