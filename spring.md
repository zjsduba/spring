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
            1.super(parentBeanFactory);  
                1.跟踪代码到父类AbstractAutowireCapableBeanFactory的构造函数，调用了ignoreDependencyInterface()  
                    [ignoreDependencyInterface(BeanNameAware.class);  
                    ignoreDependencyInterface(BeanFactoryAware.class);  
                    ignoreDependencyInterface(BeanClassLoaderAware.class);]  
                    ignoreDependencyInterface主要功能为：自动装配时忽略给定的依赖接口
                    Spring的介绍：自动装配时忽略给定的依赖接口，典型应用是通过其他方式解析Application上下文注册依赖，
                                类似于BeanFactory通过BeanFactoryAware进行注入或者ApplicationContext通过ApplicationContextAware进行注入                 
            2.this.reader.loadBeanDefinition(resource);资源加载的真正实现（XmlBeanDefinitionReader在这里实现加载数据）
                1.封装资源文件，当进入XmlBeanDefinitionReader后首先对参数Resource使用EncodedResource的类进行封装
                2.获取输入流。从resource中获取对应的InputStream并构造InputSource。（通过SAX读取XML文件的方式来准备InputStream对象）
                3.通过构造的InputSource实例和Resource实例继续调用doLoadBeanDefinition(inputSource, encodedResource.getResource());
                    1.getValidationModeForResource(resource)：获取对XML文件的验证模式;
                        1.DTD与XSD区别
                            1.DTD:文档类型定义，是一种XML约束模式语言，是XML文件的验证机制，属于XML文件组成的一部分。保证了XML文档格式正确性
                            一个DTD文档包含：元素的定义规则，元素间关系的定义规则，元素可使用的属性，可使用的实体和符合规则
                            2.XSD:XML Schema语言，描述了XML文档的结构。可以用一个指定的XML Schema来验证某个XML文件，以检查该XML文件是否符合其要求
                            文档设计者可以通过XML Schema指定XML文档所允许的结构和内容，并可据此检查XML文档是否是有效的。XML Schema本身是XML文档，它符合XML
                            语法结构。可以用通用的XML解析器解析它。
                                1.需要声明名称空间(xmlns=http://www.Springframework.org/schema/beans)
                                2.还需要指定该名称空间所对应的XML Schema文件的存储位置(xsi:)：通过schemaLocation属性指定名称空间所对应的XML Schema文档的存储位置（包含两部分：一个是名称空间的URL,
                                一个是该名称空间所标识的XML Schema文件位置或URL）
                        2.验证模式的读取
                            1.实现规则：如果设定了验证模式则使用设定的验证模式（可以通过对调用XmlBeanDefinitionReader中的setValidationMode方法进行设定）；
                            否则使用自动检测的方式
                            2.自动检测验证模式的功能是在detectValidationMode方法中实现。在detectValidationMode方法中又将自动检测验证模式的工作委托给了专门处理类
                            XmlValidationModeDetector，调用了XmlValidationModeDetector的validationModeDetector方法
                    2.loadDocument:加载xml文件，并得到对应的Document
                        1.委托DocumentLoader（接口）的DefaultDocumentLoader实现类执行：通过SAX解析xml文档：
                            1.创建DocumentBuilderFactory，通过该factory创建DocumentBuilder，进而解析inputSource来返回Document对象（其中有一个通过getEntityResolver()获取entityResolver）
                            2.EntityResolver：如果SAX应用程序需要实现自定义处理外部实体，则必须实现此接口并使用setEntityResolver方法向SAX驱动器注册一个实例
                            就是说，对于解析一个XML，SAX首先读取该XML文件上的声明，根据声明去寻找对应的DTD定义，以便对文档进行一个验证，默认的寻找规则是通过DTD文件声明的URL地址去下载相应的DTD声明，并进行认证
                            EntityResolver的作用就是项目本身就可以提供一个如何寻找DTD声明的方法，即由程序来实现寻找DTD声明的过程，比如通过将DTD文件放到项目某处，来实现直接将此文档读取并返回给SAX即可。
                    3.registerBeanDefinition(doc,resource)：根据返回的Document注册bean信息
                        1.BeanDefinitionDocumentReader documentReader=createBeanDefinitionDocumentReader():
                        通过DefaultBeanDefinitionDocumentReader实例化BeanDefinitionDocumentReader;
                        2.documentReader.setEnvironment(this.getEnvironment):documentReader设置环境变量
                        3.int countBefore=getRegistry().getBeanDefinitionCount():
                        在实例化BeanDefinitionReader的时候会将BeanDefinitionRegistry传入，默认使用继承自DefaultListableBeanFactory的子类
                        记录统计前BeanDefinition的加载个数
                        4.documentReader.registerBeanDefinitions(doce,createReaderContext(resource)):加载及注册bean
                        5.return getRegistry().getBeanDefinitionCount()-countBefore:记录本次加载的beanDefinition个数
                            
                        
                        
            