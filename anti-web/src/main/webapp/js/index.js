(function () {
    //设置服务器地址
    proxy.env.setServerContextUrl("http://localhost:8080/anti-base/");
    proxy.bindServiceMethod({
        beanName: "helloService",  //服务名
        method: "sayH"   //方法名
    })("zsl").then(function (json) { //返回服务结果
        console.log(json);
    }).fail(function (e) { //登陆失败或调用服务失败均到此函数。参见Promise模式。
        console.error(e);
    });
}.call(this));

