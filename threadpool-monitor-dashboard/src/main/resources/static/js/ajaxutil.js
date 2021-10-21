var AjaxUtil = {
  post : function(url, data, callback) {
    $.ajax({
      url : url,
      type : 'post',
      contentType : 'application/json',
      data : JSON.stringify(data),
      success : function(data) {
        if (callback) {
          callback(data);
        }
      },
      error : function(data) {
        console.log(data);
      }
    });
  },
  get : function(url, data, callback) {
    $.ajax({
      url : url,
      type : 'get',
      contentType : 'application/json',
      data : JSON.stringify(data),
      success : function(data) {
        callback(data);
      },
      error : function(data) {
        console.log(data);
      }
    });
  },
  acrossPost : function(url, data, callback) {
    $.ajax({
      url : url,
      type : 'get',
      contentType : 'application/json',
      data : JSON.stringify(data),
      success : function(data) {
        callback(data);
      },
      error : function(data) {
        console.log(data);
      }
    });
  }
};