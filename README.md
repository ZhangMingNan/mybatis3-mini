### mybatis3-mini
    首先实现了对mapper接口创建代理对象的功能.
    存在的问题是每次getMapper都会创建一个 MapperProxy 对象
    这是不合理的,因此可以使用工厂模式来创建MapperProxy对象
    在工厂中可以缓存MapperProxy对象.
    完全复制MyBatis中parsing,io模块的代码,由于这两个模块的代码是基础模块,通用性很强,目前没有必要自己写.
