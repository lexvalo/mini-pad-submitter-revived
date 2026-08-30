rem test.bat, test all filters
rem only works if set debugging=true in each module you wish to test.
rem these are not command line utilities. Just tests to show you
rem how to use the code and to prove the filter works.
rem ---------------------------------------
rem all dirs
java com.mindprod.filter.AllDirectoriesFilter
rem ---------------------------------------
rem all files
java com.mindprod.filter.AllFilesFilter
rem ---------------------------------------
rem cl*.java
java com.mindprod.filter.ClamFilter
rem ---------------------------------------
rem everything
java com.mindprod.filter.EverythingFilter
rem ---------------------------------------
rem "filter.use", "Just.bat", "notthere.txt"
java com.mindprod.filter.ListFilter
rem ---------------------------------------
rem find all *.java and *.class files with names longer than 12 characters,
rem and longer than 2000 bytes,
rem but don't count MultiFilter.java, case insensitive.
java com.mindprod.filter.MultiFilter
rem ---------------------------------------
rem get files that have not been modified in the last hour.
java com.mindprod.filter.RecentFilter
rem ---------------------------------------
rem all files beginning with F but not ending in .java
java com.mindprod.filter.RegexFilter "F.*"  ".*\.java"
rem ---------------------------------------
rem get filenames 12 chars or less long.
java com.mindprod.filter.FilenameLengthFilter
rem ---------------------------------------
rem get files 2000 bytes or longer
java com.mindprod.filter.FileLengthFilter
rem -30-
