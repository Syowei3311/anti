package dna.persistence.commons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Aliang on 2017/7/26.
 */
public class RequestBodyParse {

    private static Logger logger = LoggerFactory.getLogger(RequestBodyParse.class);

    private static final Map<Class, String> baseClass = new HashMap<Class, String>() {
        {
            put(int.class, "parseInt");
            put(short.class, "parseShort");
            put(long.class, "parseLong");
            put(byte.class, "parseByte");
            put(float.class, "parseFloat");
            put(double.class, "parseDouble");
            put(boolean.class, "parseBoolean");
            put(Integer.class, "parseInt");
            put(Short.class, "parseShort");
            put(Long.class, "parseLong");
            put(Byte.class, "parseByte");
            put(Float.class, "parseFloat");
            put(Double.class, "parseDouble");
            put(Boolean.class, "parseBoolean");
        }
    };

    public static <T> T parseObject(String text, Class<T> clazz, Type type) throws Exception {
        if (StringUtils.isEmpty(text)) {
            if (clazz.isPrimitive()) {
                throw new IllegalArgumentException("param[" + text + "] cannot cast to " + clazz.getName());
            } else {
                return null;
            }
        }
        if (baseClass.containsKey(clazz)) {
            return (T) clazz.getMethod(baseClass.get(clazz), String.class).invoke(clazz.newInstance(), new Object[]{text});
        } else if (clazz == String.class) {
            return (T) text;
        } else if (clazz == Character.class || clazz == char.class) {
            if (text.length() > 1) {
                throw new IllegalArgumentException("param[" + text + "] cannot cast to " + clazz.getName());
            } else {
                return (T) ((Object) text.charAt(0));
            }
        } else {
            return JSONObject.parseObject(text, type);
        }
    }

    public class HelloService {
        public void testParse(Object[] obs, List<Map<String, Integer>> lmap) {

        }
    }

    public static void main(final String[] args) {
        Method[] methods = HelloService.class.getMethods();
        Method testMethod;
        for (Method m : methods) {
            if (m.getName().equals("testParse")) {
                List<String> list = new ArrayList<String>() {{
                    add(null);
                    add("a");
                    add("b");
                    add("c");
                }};
                String jsonList = JSONArray.toJSONString(list);
                String jsonLmap = "[{\"name\":1,\"dhh\":233},{\"name\":2,\"dhh\":143}]";
                String jsonObj = "[\"string\",123,1.21,{\"name\":\"zsl\"}]";
                System.out.println(jsonList);
//                List<Map<String, Integer>> lmap = JSONObject.parseObject(jsonLmap, m.getGenericParameterTypes()[1]);
//                for (Map<String, Integer> a : lmap) {
//                    System.out.println(a.get("name") + ":" + a.get("dhh"));
//                }
                Object[] objects = JSONObject.parseObject(jsonObj, m.getGenericParameterTypes()[0]);
                for (Object o : objects) {
                    System.out.println(o.toString());
                }
                Map<String, String> mm = (Map) objects[3];
                System.out.println(mm.get("name"));
                System.out.println(m.getParameterTypes()[0] + "" + m.getGenericParameterTypes()[0]);
            }
            System.out.println(HelloService.class.getSuperclass() + "-" + HelloService.class.getSimpleName() + ":" + m.getName() + ":" + JSON.toJSONString(m.getParameterTypes()) + "-" + m.getReturnType() + "-" + m.getGenericReturnType());
        }
    }

}
