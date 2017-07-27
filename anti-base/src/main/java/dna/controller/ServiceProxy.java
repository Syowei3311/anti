package dna.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dna.persistence.commons.AppContextHolder;
import dna.persistence.commons.RequestBodyParse;
import dna.remote.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;


/**
 * Created by Aliang on 2017/7/20.
 */
@CrossOrigin(origins = "*")
@RestController
public class ServiceProxy {
    private static Logger logger = LoggerFactory.getLogger(ServiceProxy.class);
    @Autowired
    private HelloService helloService;

    @RequestMapping(value = "/*.jsonRequest", method = {RequestMethod.POST})
    public void rpcConRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            final PrintWriter resBody = response.getWriter();
            BufferedReader br = request.getReader();
            String tmp;
            StringBuilder reqContent = new StringBuilder("");
            while ((tmp = br.readLine()) != null) {
                reqContent.append(tmp);
            }
            logger.debug(reqContent.toString());
            JSONObject jsonData = JSONObject.parseObject(reqContent.toString());
            String beanName = jsonData.getString("serviceId");
            String methodName = jsonData.getString("method");
            String body = jsonData.getString("body");
            logger.debug("body:{}", body);
            String[] params = JSON.parseArray(body).toArray(new String[0]);
            Object bean = AppContextHolder.getBean(beanName);
            Method[] methods = AppContextHolder.getType(beanName).getMethods();
            Method noParamMethod = null;
            Boolean exactMethod = false;
            Object result = null;
            for (Method m : methods) {
                if (methodName.equals(m.getName())) {
                    m.setAccessible(true);
                    Type[] gpt = m.getGenericParameterTypes();
                    Class[] pc = m.getParameterTypes();
                    Object[] ops = new Object[gpt.length];
                    if (gpt.length == 0) {
                        noParamMethod = m;
                    } else if (gpt.length == params.length) {
                        exactMethod = true;
                        for (int i = 0; i < gpt.length; i++) {
                            ops[i] = RequestBodyParse.parseObject(params[i], pc[i], gpt[i]);
                        }
                    } else {
                        continue;
                    }
                    if (exactMethod) {
                        if (m.getReturnType() != void.class) {
                            result = m.invoke(bean, ops);
                            resBody.write(JSON.toJSONString(result));
                        } else {
                            m.invoke(bean, ops);
                        }
                        break;
                    }
                }
            }
            if (!exactMethod) {
                if (noParamMethod != null) {
                    if (noParamMethod.getReturnType() != void.class) {
                        result = noParamMethod.invoke(bean, new Object[]{});
                        resBody.write(JSON.toJSONString(result));
                    } else {
                        noParamMethod.invoke(bean, new Object[]{});
                    }
                } else {
                    throw new NoSuchMethodException("can not find method[" + methodName + "] with service[" + beanName + "]");
                }
            }
            logger.info(JSON.toJSONString(result));
        } catch (Exception e) {
            logger.error("", e);
            response.setStatus(500);
            response.getWriter().write(e.getMessage());
        } finally {
        }
    }
}
