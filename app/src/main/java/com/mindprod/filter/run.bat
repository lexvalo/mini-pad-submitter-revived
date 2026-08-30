@echo off
@echo °±²Û run.bat: run Filter as an application Û²±°
Rem adjust drive letter before use:
E:
cd com\mindprod\filter
rem add any parameters on the tail end of the next line.
java.exe %JAVA_OPTIONS -ea -jar filter.jar
rem -30-
