# 3.Interceptor与AOP的差别

## 拦截器Interceptor

* 拦截器拦截以.action结尾的url，拦截Action的访问。Interfactor是基于Java的反射机制（AOP思想）进行实现，不依赖Servlet容器
* 拦截器可以在方法执行之前(preHandle)和方法执行之后(afterCompletion)进行操作，回调操作(postHandle),可以获取执行的方法的名称，请求（HttpServletRequest）
