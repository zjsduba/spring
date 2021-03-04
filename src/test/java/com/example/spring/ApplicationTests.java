package com.example.spring;

import com.example.spring.beans.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

class ApplicationTests {

    @Test
    void contextLoads() {

        BeanFactory beanFactory=new XmlBeanFactory(new ClassPathResource("beanFactoryTest.xml"));
        Book book = (Book) beanFactory.getBean("book");
        System.out.println(book);
    }

}
