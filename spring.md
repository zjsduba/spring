**核心类**  
`1.DefaultListableBeanFactory`  
    1.xmlBeanFactory extends DefaultListableBeanFactory    
    2.AliasRegistry：定义对alias（别名）的简单增删改查等操作  
    3.SimpleAliasRegistry：主要使用map作为alias的缓存，并对接口AliasRegistry进行实现  
    4.SingletonBeanRegistry：定义对单例的注册和获取  
    5.BeanFactory：定义获取bean及bean的各种属性  
    6.DefaultSingletonBeanRegistry：对接口SingletonBeanRegistry各函数的实现  
    7.FactoryBeanRegistrySupport：在DefaultSingletonBeanRegistry基础上增加对FactoryBean的特殊处理  
    8.HierarchicalBeanFactory:继承BeanFactory，也就是在BeanFactory定义的功能的基础上增加了对parentFactory的支持  
    9.ConfigurableBeanFactory：提供配置Factory的各种方法  
    10.ListableBeanFactory：根据各种条件获取bean的配置清单  
    11.AbstractBeanFactory：综合FactoryBeanRegistrySupport和ConfigurableBeanFactory的功能  
    12.AutoWireCapableBeanFactory：提供创建bean，自动注入，初始化以及应用bean的后处理器  
    13.AbstractAutoWireCapableBeanFactory：综合AbstractBeanFactory并对接口AutoWireCapableBeanFactory进行实现  
    14.ConfigurableListableBeanFactory：BeanFactory配置清单，指定忽略类型和接口等  
    15.DefaultListableBeanFactory：综合上面所有功能，主要是对bean注册后的处理  
![Image text](image/DefaultListableBeanFactory层次结构图.png)

`2.xmlBeanDefinitionReader`  
    1.ResourceLoader：定义资源加载器，主要应用于根据给定的资源文件地址返回对应的Resource  
    2.BeanDefinitionReader：主要定义资源文件读取并转化为BeanDefinition的各个功能  
    3.EnvironmentCapable：定义获取Environment方法  
    4.DocumentLoader：定义从资源文件加载到转换为Document的功能  
    5.AbstractBeanDefinitionReader：对EnvironmentCapable，BeanDefinitionReader类定义的功能进行实现  
    BeanDefinitionDocumentReader：定义读取document并注册BeanDefinition功能  
    6.BeanDefinitonParserDelegate：定义解析element的各种方法 
    7.大致流程：  
            1.通过继承自AbstractBeanDefinitionReader中的方法，来使用ResourceLoader将资源文件路径转换为对应的Resource文件  
            2.通过DocumentLoader对resource文件进行转换，将Resource文件转为Document文件  
            3.通过实现接口BeanDefinitionDocumentReader的DefaultBeanDefinitionDocumentReader类对Document进行解析，并使用  
            BeanDefinitionParserDelegate对Element进行解析  
**容器的基础：xmlBeanFactory**  
    1.BeanFactory beanFactory=new XmlBeanFactory(new ClassPathResource("beanFactoryTest.xml"));  
        1.逻辑处理顺序：首先调用ClasspathResource的构造函数构造Resource资源文件的实例对象。然后调用XmlBeanFactory的构造函数进行初始化  
    2.Resource资源文件：配置文件读取是通过ClassPathResource进行封装的  
        1.在java中，将不同来源的资源抽象成URL,通过注册不同的handler来处理不同来源的资源的读取逻辑，所有spring将资源抽象成了Resource  
        2.InputStreamResource封装任何能返回InputStream的类，例如File,Classpath下的资源，ByteArray：只有一个方法：getInputStream    
        3.FileSystemResource：文件资源，使用FileInputStream对文件进行实例化  
        4.ClasspathResource：classpath资源，通过class或者classLoader提供的底层方法进行调用  
        5.URLResource：URL资源  
        6.ByteArrayResource：Byte数组资源  
        例：Resource resource=new ClassPathResource("beanFactoryTest.xml);  
    3.XmlBeanFactory的初始化过程：  
        1.使用Resource实例作为构造函数参数进行初始化  
            super(parentBeanFactory);  
                1.跟踪代码到父类AbstractAutowireCapableBeanFactory的构造函数，调用了ignoreDependencyInterface(xx.class)
                    ignoreDependencyInterface主要功能为：自动装配时忽略给定的依赖接口
            this.reader.loadBeanDefinition(resource);资源加载的真正实现（XmlBeanDefinitionReader在这里实现加载数据）
        2.super(parentBeanFactory);
                 
            