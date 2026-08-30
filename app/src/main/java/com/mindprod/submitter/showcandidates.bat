rem showcandidates.bat display candidates on website without changing existing order.
E:
cd \com\mindprod\submitter
csvcondense candidates.csv
java com.mindprod.submitter.TidyKeywords candidates.csv
csvalign candidates.csv
java com.mindprod.submitter.Format candidates.csv E:\mindprod\jgloss\include\candidateguts.htmlfrag
E:\com\mindprod\repair\fixcssclasses.exe %configuration -s E:\mindprod\jgloss\include
E:
cd \mindprod\jgloss
call E:\com\mindprod\htmlmacros\one.btm candidates.html
E:
cd \com\mindprod\submitter
rem -30-
