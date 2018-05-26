package com.ly.zmn48644.scripting;

import com.ly.zmn48644.parsing.XNode;
import com.ly.zmn48644.scripting.xmltags.SqlSource;
import com.ly.zmn48644.session.Configuration;

/**
 * Created by zhangmingnan on 2018/5/13.
 */
public interface LanguageDriver {

    SqlSource createSqlSource(Configuration configuration, XNode node);

}
