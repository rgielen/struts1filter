struts1filter
=============

A request parameter filter solution for Struts 1 [CVE-2014-0114](https://issues.apache.org/jira/browse/STR-3220) 
based on the work of [Alvaro Munoz and the HP Fortify team](http://h30499.www3.hp.com/t5/HP-Security-Research-Blog/Protect-your-Struts1-applications/ba-p/6463188#.VDqkCdTLeT4).

To use this filter, add the following filter declaration along with appropriate mapping to the web.xml descriptor
of the Struts 1 application to protect:

```
<filter>
    <filter-name>ParamWrapperFilter</filter-name>
    <filter-class>net.rgielen.struts1.filter.ParamWrapperFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>ParamWrapperFilter</filter-name>
    <servlet-name>YOUR ACTION SERVLET</servlet-name>
</filter-mapping>
```

The filter comes with a default regular expression to match harmful parameter names,
 which might be overridden by explicit configuration:
 
```
<filter>
    <filter-name>ParamWrapperFilter</filter-name>
    <filter-class>net.rgielen.struts1.filter.ParamWrapperFilter</filter-class>
    <init-param>
        <param-name>excludeParams</param-name>
        <param-value>(.*\.|^|.*|\[('|"))(c|C)lass(\.|('|")]|\[).*</param-value>
    </init-param>
</filter>
...
```

The filter is still under development but will soon be released to Maven Central.
