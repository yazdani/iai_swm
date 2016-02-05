@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  iai_swm startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

@rem Add default JVM options here. You can also use JAVA_OPTS and IAI_SWM_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windowz variants

if not "%OS%" == "Windows_NT" goto win9xME_args
if "%@eval[2+2]" == "4" goto 4NT_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*
goto execute

:4NT_args
@rem Get arguments from the 4NT Shell from JP Software
set CMD_LINE_ARGS=%$

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\iai_swm-0.1.0.jar;%APP_HOME%\lib\rosjava-0.2.1.jar;%APP_HOME%\lib\knowrob_common-0.1.0.jar;%APP_HOME%\lib\jeromq-0.3.5.jar;%APP_HOME%\lib\json-lib-2.4-jdk15.jar;%APP_HOME%\lib\apache_xmlrpc_common-0.2.1.jar;%APP_HOME%\lib\apache_xmlrpc_server-0.2.1.jar;%APP_HOME%\lib\apache_xmlrpc_client-0.2.1.jar;%APP_HOME%\lib\message_generation-0.2.1.jar;%APP_HOME%\lib\rosjava_test_msgs-0.2.1.jar;%APP_HOME%\lib\rosgraph_msgs-1.11.1.jar;%APP_HOME%\lib\geometry_msgs-1.11.8.jar;%APP_HOME%\lib\nav_msgs-1.11.8.jar;%APP_HOME%\lib\tf2_msgs-0.5.12.jar;%APP_HOME%\lib\dnsjava-2.1.1.jar;%APP_HOME%\lib\com.springsource.org.apache.commons.logging-1.1.1.jar;%APP_HOME%\lib\com.springsource.org.apache.commons.net-2.0.0.jar;%APP_HOME%\lib\guava-12.0.jar;%APP_HOME%\lib\vecmath-1.3.1.jar;%APP_HOME%\lib\owlapi-distribution-3.5.0.jar;%APP_HOME%\lib\org.semanticweb.hermit-1.3.8.4.jar;%APP_HOME%\lib\jpl-3.1.4-alpha.jar;%APP_HOME%\lib\commons-beanutils-1.8.0.jar;%APP_HOME%\lib\commons-collections-3.2.1.jar;%APP_HOME%\lib\commons-lang-2.5.jar;%APP_HOME%\lib\ezmorph-1.0.6.jar;%APP_HOME%\lib\com.springsource.org.apache.commons.httpclient-3.1.0.jar;%APP_HOME%\lib\ws-commons-util-1.0.1.jar;%APP_HOME%\lib\netty-3.5.2.Final.jar;%APP_HOME%\lib\com.springsource.org.apache.commons.codec-1.3.0.jar;%APP_HOME%\lib\com.springsource.org.apache.commons.io-1.4.0.jar;%APP_HOME%\lib\commons-pool-1.6.jar;%APP_HOME%\lib\com.springsource.org.apache.commons.lang-2.4.0.jar;%APP_HOME%\lib\gradle_plugins-0.2.1.jar;%APP_HOME%\lib\std_msgs-0.5.9.jar;%APP_HOME%\lib\actionlib_msgs-1.11.8.jar;%APP_HOME%\lib\junit-3.8.2.jar;%APP_HOME%\lib\jsr305-1.3.9.jar;%APP_HOME%\lib\owlapi-apibinding-3.5.0.jar;%APP_HOME%\lib\owlapi-api-3.5.0.jar;%APP_HOME%\lib\owlapi-tools-3.5.0.jar;%APP_HOME%\lib\owlapi-impl-3.5.0.jar;%APP_HOME%\lib\owlapi-parsers-3.5.0.jar;%APP_HOME%\lib\owlapi-oboformat-3.5.0.jar;%APP_HOME%\lib\owlapi-contract-3.5.0.jar;%APP_HOME%\lib\axiom-api-1.2.14.jar;%APP_HOME%\lib\axiom-c14n-1.2.14.jar;%APP_HOME%\lib\axiom-impl-1.2.14.jar;%APP_HOME%\lib\axiom-dom-1.2.14.jar;%APP_HOME%\lib\automaton-1.11-8.jar;%APP_HOME%\lib\xml-apis-1.0.b2.jar;%APP_HOME%\lib\geronimo-activation_1.1_spec-1.1.jar;%APP_HOME%\lib\geronimo-javamail_1.4_spec-1.7.1.jar;%APP_HOME%\lib\jaxen-1.1.4.jar;%APP_HOME%\lib\geronimo-stax-api_1.0_spec-1.0.1.jar;%APP_HOME%\lib\apache-mime4j-core-0.7.2.jar;%APP_HOME%\lib\woodstox-core-asl-4.1.4.jar;%APP_HOME%\lib\stax2-api-3.1.1.jar;%APP_HOME%\lib\commons-logging-1.1.3.jar;%APP_HOME%\lib\org.osgi.core-1.4.0.jar

@rem Execute iai_swm
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %IAI_SWM_OPTS%  -classpath "%CLASSPATH%" org.ros.RosRun %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable IAI_SWM_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%IAI_SWM_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
