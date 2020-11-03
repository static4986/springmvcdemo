package mvnFramework.context;

import mvnFramework.annotation.ControllerOther;
import mvnFramework.annotation.ServiceOther;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class DispatcherServletOther extends HttpServlet {


    //配置文件
    private Properties properties;

    //要加载得类全限定名
    private Set<String> classPath;

    //ico容器
    private Map<String, Object> iocMap = new HashMap<>();

    @Override
    public void init(ServletConfig config) throws ServletException {

        //1.加载配置文件
        doLoadConfig(config);

        //2.扫描包路径
        doScanPackage();

        //3.类实例化，放入ioc容器
        doInstantiate();

        //4.依赖注入
        doAssembleDependence();

        //5.加载处理器映射器、处理器适配器、视图解析器
        doHandlerResolver();

        System.out.println("容器初始化完成");

        //6.开始任务
        super.init(config);
    }

    private void doHandlerResolver() {
    }

    /**
     * 依赖注入
     */
    private void doAssembleDependence() {
    }

    /**
     * 加载类实例
     */
    private void doInstantiate() {
        for (String s : classPath) {
            try {
                Class<?> aClass = Class.forName(s);
                try {
                    Object singleton = aClass.getConstructor().newInstance();
                    //判断注解类型，Controller直接用类名首字母小写，Service要考虑实现得接口
                    if (aClass.isAnnotationPresent(ControllerOther.class)) {
                        String simpleName = aClass.getSimpleName();
                        String firstLowString = firstLow(simpleName);
                        iocMap.put(firstLowString,singleton);
                    }else if(aClass.isAnnotationPresent(ServiceOther.class)){
                        //判断注解是否有value值
                        ServiceOther serviceOther = aClass.getAnnotation(ServiceOther.class);
                        String alias = serviceOther.value();
                        if(StringUtils.isNotBlank(alias)){
                            iocMap.put(alias,singleton);
                        }else {
                            iocMap.put(firstLow(aClass.getSimpleName()),singleton);
                        }
                        //实现的所有接口，实例化放入容器
                        Class<?>[] interfaces = aClass.getInterfaces();
                        for (Class<?> interfaceName : interfaces){
                            String parentName = firstLow(interfaceName.getSimpleName());
                            iocMap.put(parentName,singleton);
                        }
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 首字母转换小写
     * */
    private String firstLow(String simpleName) {
        char[] chars = simpleName.toCharArray();
        if (chars[0] >= 'A' && chars[0] <= 'Z') {
            chars[0] += 32;
            //chars[0] ^= 0x20; 这种方式也可以
        }
        return chars.toString();
    }

    /**
     * 扫描包路径下得所有类
     */
    private void doScanPackage() {
        String packageScan = properties.getProperty("packageScan");
        //当前文件夹路径获取
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        File file = new File(path + packageScan.replaceAll(".", "/"));
        //递归查询文件
        doLoadFile(file);
    }

    /**
     * 递归查询文件夹下文件
     */
    private void doLoadFile(File file) {
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File fileChildren = files[i];
            if (fileChildren.isDirectory()) {
                doLoadFile(fileChildren);
            } else if (fileChildren.getName().endsWith(".java")) {
                String filePath = fileChildren.getAbsolutePath() + fileChildren.getName().substring(0, fileChildren.getName().lastIndexOf("."));
                filePath = filePath.replaceAll("/", ".");
                classPath.add(filePath);
            } else {
                continue;
            }
        }
    }

    /**
     * 加载配置文件
     */
    private void doLoadConfig(ServletConfig config) {
        String contextConfigLocation = config.getInitParameter("contextConfigLocation");
        InputStream is = DispatcherServletOther.class.getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }


}
