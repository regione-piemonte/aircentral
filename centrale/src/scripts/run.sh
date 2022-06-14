#!/bin/sh

export LANG=it_IT.UTF-8
export LD_LIBRARY_PATH=bin
cd `dirname $0`
mkdir -p log
# nice --adjustment=-1
exec java\
 -Xmx256m\
 -cp\
 bin/castor-core-1.3.3.jar\
:bin/castor-xml-1.3.3.jar\
:bin/commons-dbcp-1.4.jar\
:bin/commons-lang-2.6.jar\
:bin/commons-logging-1.1.3.jar\
:bin/commons-pool-1.5.4.jar\
:bin/gwt-servlet-2.7.0.jar\
:bin/jcommon-1.0.15.jar\
:bin/jetty-6.1.26.jar\
:bin/jetty-util-6.1.26.jar\
:bin/jfreechart-1.0.12.jar\
:bin/log4j-1.2.16.jar\
:bin/servlet-api-2.5.jar\
:bin/slf4j-api-1.5.11.jar\
:bin/slf4j-log4j12-1.5.11.jar\
:bin/xercesImpl-2.9.0.jar\
:bin/jdbc.jar\
:bin/auth-dao-1.0.0.jar\
:bin/auth-model-1.0.0.jar\
:bin/dbman-1.0.1.jar\
:bin/periferico-common-1.0.0.jar\
:bin/centrale.jar\
 -Djava.awt.headless=true\
 -Dcentrale.regexp="/ariaweb/cop/unico/"\
 -Dcentrale.commonconfig.disable=false\
 -Dcentrale.dbaria.minimumUpdateDateForDataTransfer=201601010000\
 it.csi.centrale.Centrale > log/startup.log 2>&1
