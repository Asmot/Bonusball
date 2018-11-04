function text2Matrix(text, width_for_text, height_for_text, width_map, height_map, wx, callback) {
  console.log(text)
  var canvasId = "canvas_for_text"
  // 使用 wx.createContext 获取绘图上下文 context

  var ctx = wx.createCanvasContext(canvasId)

  var width = width_for_text;
  var height = height_for_text;
  var readyToShowText = text;

  ctx.fillStyle = '#fff';
  // ctx.setFontSize(14);
  ctx.font = "14px SimSun";
  ctx.textBaseline = 'top';
  ctx.clearRect(0, 0, width, height);
  ctx.fillText(readyToShowText, 0, 0);
  ctx.draw()

  wx.canvasGetImageData({
    canvasId: canvasId,
    x: 0,
    y: 0,
    width: width,
    height: height,
    success(res) {
      // console.log(res.width) // 100
      // console.log(res.height) // 100
      // console.log(res.data instanceof Uint8ClampedArray) // true
      // console.log(res.data.length) // 100 * 100 * 4


      var imgData = res.data;
      var result = [];
      for (var y = 0; y < height; y++) {
        for (var x = 0; x < width; x++) {
          var i = y * width + x;
          if (imgData[i * 4 + 3]) {
            result.push([x, y]);
          }
        }
      }


      // 转换为 适配屏幕的点
      result = map2center(result, width_map, height_map);

      callback(result);

    }
  })

  //计算完成之后清空
  // ctx.clearRect(0, 0, width, height);



}


//把文字映射到中心
function map2center(result, width, height) {
  var width = width;
  var height = height;

  var rect = getTextRect(result);
  var textLeft = rect[0];
  var textTop = rect[1];
  var textWidth = rect[2]; // - rect[0] + 1;
  var textHeight = rect[3]; // - rect[1] + 1;

  var len = result.length;

  var result_tmp = [];

  for (var i = 0; i < len; i++) {
    var x = result[i][0] * (width / (2 * textWidth)) + width / 4;
    var y = result[i][1] * (height / (2 * textHeight)) + height / 4;
    result[i] = [x, y];
  }
  return result;
}

//获取文字的宽度和高度，即宽度和高度的最大值
function getTextRect(result) {
  //minx,miny,maxx,maxy
  var rect = [0, 0, 0, 0];
  rect[0] = result[0][0];
  rect[1] = result[0][1];
  for (var i = 0; i < result.length; i++) {
    var x = result[i][0];
    var y = result[i][1];

    if (x < rect[0]) {
      rect[0] = x;
    }
    if (y < rect[1]) {
      rect[1] = y;
    }

    if (x > rect[2]) {
      rect[2] = x;
    }
    if (y > rect[3]) {
      rect[3] = y;
    }
  }
  return rect;
}




//导出对外的接口
module.exports = {
  text2Matrix: text2Matrix
}