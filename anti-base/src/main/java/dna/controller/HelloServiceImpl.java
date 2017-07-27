package dna.controller;

import dna.remote.HelloService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Aliang on 2017/7/20.
 */
@Service("helloService")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHi(String str) {
        return "receive" + str;
    }

    public String ss(int str) {
        return "receive" + str;
    }

    public void testParse(Object[] obs, List<Map<String, Integer>> lmap) {

    }
}
